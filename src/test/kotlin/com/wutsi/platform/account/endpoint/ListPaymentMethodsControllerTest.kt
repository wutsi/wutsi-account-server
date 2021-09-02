package com.wutsi.platform.account.endpoint

import com.wutsi.platform.account.dto.ListPaymentMethodResponse
import com.wutsi.platform.account.entity.PaymentMethodProvider.PAYMENT_METHOD_PROVIDER_MTN
import com.wutsi.platform.account.entity.PaymentMethodProvider.PAYMENT_METHOD_PROVIDER_ORANGE
import com.wutsi.platform.account.entity.PaymentMethodType.PAYMENT_METHOD_TYPE_MOBILE_PAYMENT
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
@Sql(value = ["/db/clean.sql", "/db/ListPaymentMethodController.sql"])
public class ListPaymentMethodsControllerTest : AbstractSecuredController() {
    @LocalServerPort
    public val port: Int = 0

    private lateinit var rest: RestTemplate

    @BeforeEach
    override fun setUp() {
        super.setUp()

        rest = createResTemplate(listOf("payment-method-read"), subjectId = 100)
    }

    @Test
    fun `return all payment-methods`() {
        val url = "http://localhost:$port/v1/accounts/100/payment-methods"
        val response = rest.getForEntity(url, ListPaymentMethodResponse::class.java)

        assertEquals(200, response.statusCodeValue)

        val paymentMethods = response.body.paymentMethods
        assertEquals(2, paymentMethods.size)

        assertEquals("0000-00000-100", paymentMethods[0].token)
        assertEquals("...4100", paymentMethods[0].maskedNumber)
        assertEquals("Ray Sponsible", paymentMethods[0].ownerName)
        assertEquals(PAYMENT_METHOD_TYPE_MOBILE_PAYMENT.shortName, paymentMethods[0].type)
        assertEquals(PAYMENT_METHOD_PROVIDER_MTN.shortName, paymentMethods[0].provider)
        assertNotNull(paymentMethods[0].created)
        assertNotNull(paymentMethods[0].updated)

        assertEquals("0000-00000-101", paymentMethods[1].token)
        assertEquals("...4101", paymentMethods[1].maskedNumber)
        assertEquals("Ray Sponsible", paymentMethods[1].ownerName)
        assertEquals(PAYMENT_METHOD_TYPE_MOBILE_PAYMENT.shortName, paymentMethods[1].type)
        assertEquals(PAYMENT_METHOD_PROVIDER_ORANGE.shortName, paymentMethods[1].provider)
        assertNotNull(paymentMethods[1].created)
        assertNotNull(paymentMethods[1].updated)
    }

    @Test
    fun `cannot get payment-methods of another user`() {
        rest = createResTemplate(listOf("payment-method-read"), subjectId = 777)

        val url = "http://localhost:$port/v1/accounts/100/payment-methods"
        val ex = assertThrows<HttpStatusCodeException> {
            rest.getForEntity(url, ListPaymentMethodResponse::class.java)
        }
        assertEquals(403, ex.rawStatusCode)
    }

    @Test
    fun `invalid account id`() {
        rest = createResTemplate(listOf("payment-method-read"), subjectId = 777)

        val url = "http://localhost:$port/v1/accounts/9999/payment-methods"
        val ex = assertThrows<HttpStatusCodeException> {
            rest.getForEntity(url, ListPaymentMethodResponse::class.java)
        }
        assertEquals(404, ex.rawStatusCode)
    }
}
