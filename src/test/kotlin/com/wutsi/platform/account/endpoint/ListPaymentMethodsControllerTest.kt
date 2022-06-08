package com.wutsi.platform.account.endpoint

import com.wutsi.platform.account.dto.ListPaymentMethodResponse
import com.wutsi.platform.payment.PaymentMethodProvider.MTN
import com.wutsi.platform.payment.PaymentMethodProvider.ORANGE
import com.wutsi.platform.payment.PaymentMethodType.MOBILE
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.jdbc.Sql
import org.springframework.web.client.HttpStatusCodeException
import org.springframework.web.client.RestTemplate
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/ListPaymentMethodController.sql"])
class ListPaymentMethodsControllerTest : AbstractSecuredController() {
    @LocalServerPort
    val port: Int = 0

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

        val paymentMethods = response.body!!.paymentMethods
        assertEquals(2, paymentMethods.size)

        assertEquals("0000-00000-100", paymentMethods[0].token)
        assertEquals("+237 2 21...00", paymentMethods[0].maskedNumber)
        assertEquals("Ray Sponsible", paymentMethods[0].ownerName)
        assertEquals(MOBILE.name, paymentMethods[0].type)
        assertEquals(MTN.name, paymentMethods[0].provider)
        assertNotNull(paymentMethods[0].created)
        assertNotNull(paymentMethods[0].updated)
        assertNotNull(paymentMethods[0].phone)
        assertNotNull("+237221234100", paymentMethods[0].phone?.number)
        assertNotNull("CM", paymentMethods[0].phone?.country)
        assertNotNull(paymentMethods[0].phone?.created)

        assertEquals("0000-00000-101", paymentMethods[1].token)
        assertEquals("+237 2 21...01", paymentMethods[1].maskedNumber)
        assertEquals("Ray Sponsible", paymentMethods[1].ownerName)
        assertEquals(MOBILE.name, paymentMethods[1].type)
        assertEquals(ORANGE.name, paymentMethods[1].provider)
        assertNotNull(paymentMethods[1].created)
        assertNotNull(paymentMethods[1].updated)
        assertNotNull("+237221234101", paymentMethods[1].phone?.number)
        assertNotNull("CM", paymentMethods[1].phone?.country)
        assertNotNull(paymentMethods[1].phone?.created)
    }

    @Test
    fun `return all payment-methods do not return other users phone details`() {
        rest = createResTemplate(listOf("payment-method-read"), subjectId = 900)

        val url = "http://localhost:$port/v1/accounts/100/payment-methods"
        val response = rest.getForEntity(url, ListPaymentMethodResponse::class.java)

        assertEquals(200, response.statusCodeValue)

        val paymentMethods = response.body!!.paymentMethods
        assertEquals(2, paymentMethods.size)

        assertEquals("+237 2 21...00", paymentMethods[0].maskedNumber)
        assertNull(paymentMethods[0].phone)

        assertEquals("+237 2 21...01", paymentMethods[1].maskedNumber)
        assertNull(paymentMethods[1].phone)
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
