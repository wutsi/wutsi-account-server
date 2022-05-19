package com.wutsi.platform.account.endpoint

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.platform.account.dao.PaymentMethodRepository
import com.wutsi.platform.account.dto.AddPaymentMethodResponse
import com.wutsi.platform.account.dto.UpdatePaymentMethodRequest
import com.wutsi.platform.account.error.ErrorURN
import com.wutsi.platform.core.error.ErrorResponse
import com.wutsi.platform.payment.PaymentMethodProvider
import com.wutsi.platform.payment.PaymentMethodType
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/UpdatePaymentMethodController.sql"])
public class UpdatePaymentMethodControllerTest : AbstractSecuredController() {
    @LocalServerPort
    public val port: Int = 0

    private lateinit var rest: RestTemplate

    @Autowired
    private lateinit var dao: PaymentMethodRepository

    @BeforeEach
    override fun setUp() {
        super.setUp()

        rest = createResTemplate(listOf("payment-method-manage"), subjectId = 100)
    }

    @Test
    public fun `update a payment method`() {
        val request = UpdatePaymentMethodRequest(
            ownerName = "Thomas NKono",
            provider = PaymentMethodProvider.MTN.name
        )
        val url = "http://localhost:$port/v1/accounts/100/payment-methods/0000-00000-100"
        rest.postForEntity(url, request, Any::class.java)

        val payment = dao.findByToken("0000-00000-100").get()
        assertEquals(request.ownerName, payment.ownerName)
        assertEquals(PaymentMethodProvider.MTN, payment.provider)
        assertEquals(PaymentMethodType.MOBILE, payment.type)
        assertEquals(100L, payment.phone?.id)
    }

    @Test
    public fun `cannot update payment method to another account`() {
        rest = createResTemplate(listOf("payment-method-manage"), subjectId = 7777)
        val request = UpdatePaymentMethodRequest(
            ownerName = "Thomas NKono",
            provider = PaymentMethodProvider.MTN.name
        )
        val url = "http://localhost:$port/v1/accounts/100/payment-methods/0000-00000-100"
        val ex = assertThrows<HttpStatusCodeException> {
            rest.postForEntity(url, request, AddPaymentMethodResponse::class.java)
        }

        assertEquals(403, ex.rawStatusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals(ErrorURN.ILLEGAL_ACCOUNT_ACCESS.urn, response.error.code)
    }

    @Test
    public fun `cannot update payment method without permission`() {
        rest = createResTemplate(listOf(), subjectId = 100)
        val request = UpdatePaymentMethodRequest(
            ownerName = "Thomas NKono",
            provider = PaymentMethodProvider.MTN.name
        )
        val url = "http://localhost:$port/v1/accounts/100/payment-methods/0000-00000-100"
        val ex = assertThrows<HttpStatusCodeException> {
            rest.postForEntity(url, request, AddPaymentMethodResponse::class.java)
        }

        assertEquals(403, ex.rawStatusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals("urn:error:wutsi:access-denied", response.error.code)
    }

    @Test
    public fun `cannot update payment method with invalid token`() {
        val request = UpdatePaymentMethodRequest(
            ownerName = "Thomas NKono",
            provider = PaymentMethodProvider.MTN.name
        )
        val url = "http://localhost:$port/v1/accounts/100/payment-methods/xxxx-xxx"
        val ex = assertThrows<HttpStatusCodeException> {
            rest.postForEntity(url, request, AddPaymentMethodResponse::class.java)
        }

        assertEquals(404, ex.rawStatusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals(ErrorURN.PAYMENT_METHOD_NOT_FOUND.urn, response.error.code)
    }

    @Test
    public fun `cannot update payment method with invalid account`() {
        val request = UpdatePaymentMethodRequest(
            ownerName = "Thomas NKono",
            provider = PaymentMethodProvider.MTN.name
        )
        val url = "http://localhost:$port/v1/accounts/888888/payment-methods/xxxx-xxx"
        val ex = assertThrows<HttpStatusCodeException> {
            rest.postForEntity(url, request, AddPaymentMethodResponse::class.java)
        }

        assertEquals(404, ex.rawStatusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals(ErrorURN.ACCOUNT_NOT_FOUND.urn, response.error.code)
    }
}
