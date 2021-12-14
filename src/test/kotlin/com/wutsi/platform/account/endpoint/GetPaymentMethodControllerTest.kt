package com.wutsi.platform.account.endpoint

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.platform.account.dto.GetAccountResponse
import com.wutsi.platform.account.dto.GetPaymentMethodResponse
import com.wutsi.platform.account.error.ErrorURN
import com.wutsi.platform.core.error.ErrorResponse
import com.wutsi.platform.payment.PaymentMethodProvider
import com.wutsi.platform.payment.PaymentMethodType
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.jdbc.Sql
import org.springframework.web.client.HttpStatusCodeException
import org.springframework.web.client.RestTemplate
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/GetPaymentMethodController.sql"])
public class GetPaymentMethodControllerTest : AbstractSecuredController() {
    @LocalServerPort
    public val port: Int = 0

    private lateinit var rest: RestTemplate

    @BeforeEach
    override fun setUp() {
        super.setUp()

        rest = createResTemplate(listOf("payment-method-read"), subjectId = 100)
    }

    @Test
    fun `get payment-method`() {
        val url = "http://localhost:$port/v1/accounts/100/payment-methods/0000-00000-100"
        val response = rest.getForEntity(url, GetPaymentMethodResponse::class.java)

        assertEquals(200, response.statusCodeValue)

        val paymentMethod = response.body.paymentMethod
        assertEquals("0000-00000-100", paymentMethod.token)
        assertEquals("+237 2 21...00", paymentMethod.maskedNumber)
        assertEquals("Ray Sponsible", paymentMethod.ownerName)
        assertEquals(PaymentMethodType.MOBILE.name, paymentMethod.type)
        assertEquals(PaymentMethodProvider.MTN.name, paymentMethod.provider)
        assertNotNull(paymentMethod.created)
        assertNotNull(paymentMethod.updated)

        assertNotNull(paymentMethod.phone)
        assertNotNull("+237221234100", paymentMethod.phone?.number)
        assertNotNull("CM", paymentMethod.phone?.country)
        assertNotNull(paymentMethod.phone?.created)
    }

    @Test
    fun `do not return phone information without payment-method-details permission`() {
        rest = createResTemplate(listOf("payment-method-read"), subjectId = 100)

        val url = "http://localhost:$port/v1/accounts/100/payment-methods/0000-00000-100"
        val response = rest.getForEntity(url, GetPaymentMethodResponse::class.java)

        assertEquals(200, response.statusCodeValue)

        val paymentMethod = response.body.paymentMethod
        assertNotNull(paymentMethod.phone)
    }

    @Test
    fun `return phone information with payment-method-details permission`() {
        rest = createResTemplate(listOf("payment-method-read", "payment-method-details"), subjectId = 100)

        val url = "http://localhost:$port/v1/accounts/100/payment-methods/0000-00000-100"
        val response = rest.getForEntity(url, GetPaymentMethodResponse::class.java)

        assertEquals(200, response.statusCodeValue)

        val paymentMethod = response.body.paymentMethod
        assertNotNull(paymentMethod.phone)
        assertNotNull("+237221234100", paymentMethod.phone?.number)
        assertNotNull("CM", paymentMethod.phone?.country)
        assertNotNull(paymentMethod.phone?.created)
    }

    @Test
    public fun `deleted payment-method`() {
        rest = createResTemplate(listOf("payment-method-read"), subjectId = 199)
        val url = "http://localhost:$port/v1/accounts/199/payment-methods/0000-00000-199"

        val ex = assertThrows<HttpStatusCodeException> {
            val response = rest.getForEntity(url, GetAccountResponse::class.java)
        }
        assertEquals(404, ex.rawStatusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals(ErrorURN.PAYMENT_METHOD_DELETED.urn, response.error.code)
    }

    @Test
    public fun `invalid payment-method token`() {
        val url = "http://localhost:$port/v1/accounts/100/payment-methods/999999"

        val ex = assertThrows<HttpStatusCodeException> {
            rest.getForEntity(url, GetAccountResponse::class.java)
        }
        assertEquals(404, ex.rawStatusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals(ErrorURN.PAYMENT_METHOD_NOT_FOUND.urn, response.error.code)
    }

    @Test
    public fun `invalid permission`() {
        rest = createResTemplate(listOf(), subjectId = 100)
        val url = "http://localhost:$port/v1/accounts/100/payment-methods/0000-00000-100"

        val ex = assertThrows<HttpStatusCodeException> {
            rest.getForEntity(url, GetAccountResponse::class.java)
        }
        assertEquals(403, ex.rawStatusCode)
    }
}
