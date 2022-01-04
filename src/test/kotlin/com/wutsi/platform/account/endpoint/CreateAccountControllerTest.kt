package com.wutsi.platform.account.endpoint

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.platform.account.dao.AccountRepository
import com.wutsi.platform.account.dao.PasswordRepository
import com.wutsi.platform.account.dao.PaymentMethodRepository
import com.wutsi.platform.account.dao.PhoneRepository
import com.wutsi.platform.account.dto.CreateAccountRequest
import com.wutsi.platform.account.dto.CreateAccountResponse
import com.wutsi.platform.account.entity.AccountStatus
import com.wutsi.platform.account.error.ErrorURN
import com.wutsi.platform.account.event.AccountCreatedPayload
import com.wutsi.platform.account.event.EventURN
import com.wutsi.platform.core.error.ErrorResponse
import com.wutsi.platform.core.stream.EventStream
import com.wutsi.platform.payment.PaymentMethodProvider
import com.wutsi.platform.payment.PaymentMethodType
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.jdbc.Sql
import org.springframework.web.client.HttpStatusCodeException
import org.springframework.web.client.RestTemplate
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/CreateAccountController.sql"])
public class CreateAccountControllerTest : AbstractSecuredController() {
    @LocalServerPort
    public val port: Int = 0

    @Autowired
    private lateinit var dao: AccountRepository

    @Autowired
    private lateinit var phoneDao: PhoneRepository

    @Autowired
    private lateinit var passwordDao: PasswordRepository

    @MockBean
    private lateinit var eventStream: EventStream

    @Autowired
    private lateinit var paymentMethodDao: PaymentMethodRepository

    private lateinit var rest: RestTemplate
    private lateinit var url: String

    @BeforeEach
    override fun setUp() {
        super.setUp()

        url = "http://localhost:$port/v1/accounts"
        rest = createResTemplate(listOf("user-manage"))
    }

    @Test
    fun `create account`() {
        val request = CreateAccountRequest(
            phoneNumber = "+23774511111",
            language = "fr",
            country = "US",
            displayName = "Ray Sponsible",
            pictureUrl = "http://www.google.ca/img/1.ong",
            business = true
        )
        val response = rest.postForEntity(url, request, CreateAccountResponse::class.java)

        assertEquals(200, response.statusCodeValue)

        val account = dao.findById(response.body.id).get()
        assertEquals(request.displayName, account.displayName)
        assertEquals(request.pictureUrl, account.pictureUrl)
        assertEquals(request.language, account.language)
        assertEquals(request.country, account.country)
        assertEquals(AccountStatus.ACTIVE, account.status)
        assertNotNull(account.created)
        assertNotNull(account.updated)
        assertNull(account.deleted)
        assertTrue(account.business)
        assertFalse(account.retail)
        assertEquals(TENANT_ID, account.tenantId)

        val phone = phoneDao.findById(account.phone?.id).get()
        assertEquals(request.phoneNumber, phone.number)
        assertEquals("CM", phone.country)
        assertNotNull(phone.created)

        val password = passwordDao.findByAccount(account)
        assertTrue(password.isEmpty)

        val payload = argumentCaptor<AccountCreatedPayload>()
        verify(eventStream).publish(eq(EventURN.ACCOUNT_CREATED.urn), payload.capture())
        assertEquals(response.body.id, payload.firstValue.accountId)
        assertEquals(phone.number, payload.firstValue.phoneNumber)
    }

    @Test
    fun `create account with password`() {
        val request = CreateAccountRequest(
            phoneNumber = "+23774511117",
            language = "fr",
            country = "CM",
            displayName = "Ray Sponsible",
            pictureUrl = "http://www.google.ca/img/1.ong",
            password = "12345"
        )
        val response = rest.postForEntity(url, request, CreateAccountResponse::class.java)

        assertEquals(200, response.statusCodeValue)

        val account = dao.findById(response.body.id).get()
        assertEquals(request.displayName, account.displayName)
        assertEquals(request.pictureUrl, account.pictureUrl)
        assertEquals(request.language, account.language)
        assertEquals(request.country, account.country)
        assertEquals(AccountStatus.ACTIVE, account.status)
        assertNotNull(account.created)
        assertNotNull(account.updated)
        assertNull(account.deleted)
        assertFalse(account.business)
        assertFalse(account.retail)
        assertEquals(TENANT_ID, account.tenantId)

        val phone = phoneDao.findById(account.phone?.id).get()
        assertEquals(request.phoneNumber, phone.number)
        assertEquals("CM", phone.country)
        assertNotNull(phone.created)

        val password = passwordDao.findByAccount(account).get()
        assertNotNull(password.value)
        assertNotNull(password.salt)

        val payload = argumentCaptor<AccountCreatedPayload>()
        verify(eventStream).publish(eq(EventURN.ACCOUNT_CREATED.urn), payload.capture())
        assertEquals(response.body.id, payload.firstValue.accountId)
        assertEquals(phone.number, payload.firstValue.phoneNumber)
    }

    @Test
    fun `create account with existing phone number`() {
        val request = CreateAccountRequest(
            phoneNumber = "+237221234100",
            language = "fr"
        )
        val response = rest.postForEntity(url, request, CreateAccountResponse::class.java)

        assertEquals(200, response.statusCodeValue)

        val account = dao.findById(response.body.id).get()
        assertNull(account.displayName)
        assertNull(account.pictureUrl)
        assertNotNull(account.created)
        assertNotNull(account.updated)
        assertNull(account.deleted)
        assertEquals(AccountStatus.ACTIVE, account.status)
        assertEquals(request.language, account.language)
        assertEquals(100, account.phone?.id)
        assertEquals(TENANT_ID, account.tenantId)
        assertFalse(account.business)
        assertFalse(account.retail)

        val payload = argumentCaptor<AccountCreatedPayload>()
        verify(eventStream).publish(eq(EventURN.ACCOUNT_CREATED.urn), payload.capture())
        assertEquals(response.body.id, payload.firstValue.accountId)
        assertEquals(request.phoneNumber, payload.firstValue.phoneNumber)
    }

    @Test
    fun `create account and add payment method`() {
        val request = CreateAccountRequest(
            phoneNumber = "+23774511555",
            language = "fr",
            country = "CM",
            displayName = "Ray Sponsible",
            pictureUrl = "http://www.google.ca/img/1.ong",
            addPaymentMethod = true
        )
        val response = rest.postForEntity(url, request, CreateAccountResponse::class.java)

        assertEquals(200, response.statusCodeValue)

        val account = dao.findById(response.body.id).get()
        assertEquals(request.displayName, account.displayName)
        assertEquals(request.pictureUrl, account.pictureUrl)
        assertEquals(request.language, account.language)
        assertEquals(request.country, account.country)
        assertEquals(AccountStatus.ACTIVE, account.status)
        assertNotNull(account.created)
        assertNotNull(account.updated)
        assertNull(account.deleted)
        assertEquals(TENANT_ID, account.tenantId)
        assertFalse(account.business)
        assertFalse(account.retail)

        val phone = phoneDao.findById(account.phone?.id).get()
        assertEquals(request.phoneNumber, phone.number)
        assertEquals("CM", phone.country)
        assertNotNull(phone.created)

        val password = passwordDao.findByAccount(account)
        assertTrue(password.isEmpty)

        val paymentMethods = paymentMethodDao.findByAccount(account)
        assertEquals(1, paymentMethods.size)
        assertEquals(account.displayName, paymentMethods[0].ownerName)
        assertNotNull(paymentMethods[0].token)
        assertEquals(phone, paymentMethods[0].phone)
        assertEquals(PaymentMethodProvider.ORANGE, paymentMethods[0].provider)
        assertEquals(PaymentMethodType.MOBILE, paymentMethods[0].type)

        val payload = argumentCaptor<AccountCreatedPayload>()
        verify(eventStream).publish(eq(EventURN.ACCOUNT_CREATED.urn), payload.capture())
        assertEquals(response.body.id, payload.firstValue.accountId)
        assertEquals(phone.number, payload.firstValue.phoneNumber)
    }

    @Test
    fun `malformed phone number`() {
        val request = CreateAccountRequest(
            phoneNumber = "2377451"
        )
        val ex = assertThrows<HttpStatusCodeException> {
            rest.postForEntity(url, request, CreateAccountResponse::class.java)
        }

        assertEquals(400, ex.rawStatusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals(ErrorURN.PHONE_NUMBER_MALFORMED.urn, response.error.code)

        verify(eventStream, never()).publish(any(), any())
    }

    @Test
    fun `phone number already assigned`() {
        val request = CreateAccountRequest(
            phoneNumber = "+237221234101"
        )
        val ex = assertThrows<HttpStatusCodeException> {
            rest.postForEntity(url, request, CreateAccountResponse::class.java)
        }

        assertEquals(409, ex.rawStatusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals(ErrorURN.PHONE_NUMBER_ALREADY_ASSIGNED.urn, response.error.code)

        verify(eventStream, never()).publish(any(), any())
    }
}
