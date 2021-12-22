package com.wutsi.platform.account.endpoint

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.platform.account.dao.PaymentMethodRepository
import com.wutsi.platform.account.error.ErrorURN
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
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/DeletePaymentMethodController.sql"])
class DeletePaymentMethodControllerTest : AbstractSecuredController() {
    @LocalServerPort
    public val port: Int = 0

    @Autowired
    private lateinit var dao: PaymentMethodRepository

    private lateinit var rest: RestTemplate

    @BeforeEach
    override fun setUp() {
        super.setUp()

        rest = createResTemplate(listOf("payment-method-manage"), subjectId = 100)
    }

    @Test
    fun `delete a payment method`() {
        val url = "http://localhost:$port/v1/accounts/100/payment-methods/0000-00000-100"
        rest.delete(url)

        val payment = dao.findById(100).get()
        assertTrue(payment.isDeleted)
        assertNotNull(payment.deleted)
        assertNotEquals("0000-00000-100", payment.token)
    }

    @Test
    fun `already deleted`() {
        rest = createResTemplate(listOf("payment-method-manage"), subjectId = 199)
        val url = "http://localhost:$port/v1/accounts/199/payment-methods/0000-00000-199"

        val ex = assertThrows<HttpStatusCodeException> {
            rest.delete(url)
        }
        assertEquals(404, ex.rawStatusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals(ErrorURN.PAYMENT_METHOD_DELETED.urn, response.error.code)
    }

    @Test
    fun `cannot delete payment-method of another account`() {
        rest = createResTemplate(listOf("payment-method-manage"), subjectId = 7777)
        val url = "http://localhost:$port/v1/accounts/101/payment-methods/0000-00000-101"

        val ex = assertThrows<HttpStatusCodeException> {
            rest.delete(url)
        }

        assertEquals(403, ex.rawStatusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals(ErrorURN.ILLEGAL_ACCOUNT_ACCESS.urn, response.error.code)
    }

    @Test
    fun `invalid scope`() {
        rest = createResTemplate(listOf("xxx"), subjectId = 101)
        val url = "http://localhost:$port/v1/accounts/101/payment-methods/0000-00000-101"

        val ex = assertThrows<HttpStatusCodeException> {
            rest.delete(url)
        }

        assertEquals(403, ex.rawStatusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals("urn:error:wutsi:access-denied", response.error.code)
    }
}
