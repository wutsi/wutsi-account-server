package com.wutsi.platform.account.endpoint

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.platform.account.dao.PaymentMethodRepository
import com.wutsi.platform.account.dao.PhoneRepository
import com.wutsi.platform.account.dto.AddPaymentMethodRequest
import com.wutsi.platform.account.dto.AddPaymentMethodResponse
import com.wutsi.platform.account.entity.PaymentMethodProvider
import com.wutsi.platform.account.entity.PaymentMethodType
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
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/AddPaymentMethodController.sql"])
public class AddPaymentMethodControllerTest : AbstractSecuredController() {
    @LocalServerPort
    public val port: Int = 0

    private lateinit var rest: RestTemplate

    @Autowired
    private lateinit var dao: PaymentMethodRepository

    @Autowired
    private lateinit var phoneDao: PhoneRepository

    @BeforeEach
    override fun setUp() {
        super.setUp()

        rest = createResTemplate(listOf("payment-method-manage"), subjectId = 100)
    }

    @Test
    public fun `add payment method with existing phone number`() {
        val request = AddPaymentMethodRequest(
            ownerName = "Roger Milla",
            type = PaymentMethodType.PAYMENT_METHOD_TYPE_MOBILE_PAYMENT.shortName,
            provider = PaymentMethodProvider.PAYMENT_METHOD_PROVIDER_ORANGE.shortName,
            phoneNumber = "+237221234100"
        )
        val url = "http://localhost:$port/v1/accounts/100/payment-methods"
        val response = rest.postForEntity(url, request, AddPaymentMethodResponse::class.java)

        assertEquals(200, response.statusCodeValue)

        val phone = phoneDao.findByNumber(request.phoneNumber!!).get()
        assertEquals(request.phoneNumber, phone.number)
        assertEquals("CM", phone.country)

        val payment = dao.findByToken(response.body.token).get()
        assertEquals(request.ownerName, payment.ownerName)
        assertEquals(PaymentMethodType.PAYMENT_METHOD_TYPE_MOBILE_PAYMENT, payment.type)
        assertEquals(PaymentMethodProvider.PAYMENT_METHOD_PROVIDER_ORANGE, payment.provider)
        assertEquals(phone.id, payment.phone?.id)
        assertFalse(payment.isDeleted)
        assertNull(payment.deleted)
        assertNotNull(payment.created)
        assertNotNull(payment.updated)
    }

    @Test
    public fun `add payment method with new phone number`() {
        val request = AddPaymentMethodRequest(
            ownerName = "Roger Milla",
            type = PaymentMethodType.PAYMENT_METHOD_TYPE_MOBILE_PAYMENT.shortName,
            provider = PaymentMethodProvider.PAYMENT_METHOD_PROVIDER_ORANGE.shortName,
            phoneNumber = "+237221234111"
        )
        val url = "http://localhost:$port/v1/accounts/100/payment-methods"
        val response = rest.postForEntity(url, request, AddPaymentMethodResponse::class.java)

        assertEquals(200, response.statusCodeValue)

        val phone = phoneDao.findByNumber(request.phoneNumber!!).get()
        assertEquals(request.phoneNumber, phone.number)
        assertEquals("CM", phone.country)

        val payment = dao.findByToken(response.body.token).get()
        assertEquals(request.ownerName, payment.ownerName)
        assertEquals(PaymentMethodType.PAYMENT_METHOD_TYPE_MOBILE_PAYMENT, payment.type)
        assertEquals(PaymentMethodProvider.PAYMENT_METHOD_PROVIDER_ORANGE, payment.provider)
        assertEquals(phone.id, payment.phone?.id)
        assertFalse(payment.isDeleted)
        assertNull(payment.deleted)
        assertNotNull(payment.created)
        assertNotNull(payment.updated)
    }

    @Test
    public fun `cannot add payment method to another account`() {
        rest = createResTemplate(listOf("payment-method-manage"), subjectId = 7777)
        val request = AddPaymentMethodRequest(
            ownerName = "Roger Milla",
            type = PaymentMethodType.PAYMENT_METHOD_TYPE_MOBILE_PAYMENT.shortName,
            provider = PaymentMethodProvider.PAYMENT_METHOD_PROVIDER_ORANGE.shortName,
            phoneNumber = "+237221234111"
        )
        val url = "http://localhost:$port/v1/accounts/100/payment-methods"
        val ex = assertThrows<HttpStatusCodeException> {
            rest.postForEntity(url, request, AddPaymentMethodResponse::class.java)
        }

        assertEquals(403, ex.rawStatusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals("urn:error:wutsi:access-denied", response.error.code)
    }

    @Test
    public fun `cannot add payment method without permission`() {
        rest = createResTemplate(listOf(), subjectId = 100)
        val request = AddPaymentMethodRequest(
            ownerName = "Roger Milla",
            type = PaymentMethodType.PAYMENT_METHOD_TYPE_MOBILE_PAYMENT.shortName,
            provider = PaymentMethodProvider.PAYMENT_METHOD_PROVIDER_ORANGE.shortName,
            phoneNumber = "+237221234111"
        )
        val url = "http://localhost:$port/v1/accounts/100/payment-methods"
        val ex = assertThrows<HttpStatusCodeException> {
            rest.postForEntity(url, request, AddPaymentMethodResponse::class.java)
        }

        assertEquals(403, ex.rawStatusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals("urn:error:wutsi:access-denied", response.error.code)
    }

    @Test
    public fun `cannot add payment method already owner by another account`() {
        val request = AddPaymentMethodRequest(
            ownerName = "Roger Milla",
            type = PaymentMethodType.PAYMENT_METHOD_TYPE_MOBILE_PAYMENT.shortName,
            provider = PaymentMethodProvider.PAYMENT_METHOD_PROVIDER_ORANGE.shortName,
            phoneNumber = "+237221234200"
        )
        val url = "http://localhost:$port/v1/accounts/100/payment-methods"
        val ex = assertThrows<HttpStatusCodeException> {
            rest.postForEntity(url, request, AddPaymentMethodResponse::class.java)
        }

        assertEquals(409, ex.rawStatusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals(ErrorURN.PAYMENT_METHOD_OWNERSHIP.urn, response.error.code)
    }
}
