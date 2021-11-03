package com.wutsi.platform.account.endpoint

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.platform.account.dao.AccountRepository
import com.wutsi.platform.account.dao.PasswordRepository
import com.wutsi.platform.account.dao.PhoneRepository
import com.wutsi.platform.account.dto.CreateAccountRequest
import com.wutsi.platform.account.dto.CreateAccountResponse
import com.wutsi.platform.account.entity.AccountStatus
import com.wutsi.platform.account.util.ErrorURN
import com.wutsi.platform.core.error.ErrorResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.jdbc.Sql
import org.springframework.web.client.HttpStatusCodeException
import org.springframework.web.client.RestTemplate
import kotlin.test.assertEquals
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
            pictureUrl = "http://www.google.ca/img/1.ong"
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

        val phone = phoneDao.findById(account.phone?.id).get()
        assertEquals(request.phoneNumber, phone.number)
        assertEquals("CM", phone.country)
        assertNotNull(phone.created)

        val password = passwordDao.findByAccount(account)
        assertTrue(password.isEmpty)
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

        val phone = phoneDao.findById(account.phone?.id).get()
        assertEquals(request.phoneNumber, phone.number)
        assertEquals("CM", phone.country)
        assertNotNull(phone.created)

        val password = passwordDao.findByAccount(account).get()
        assertNotNull(password.value)
        assertNotNull(password.salt)
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
    }
}
