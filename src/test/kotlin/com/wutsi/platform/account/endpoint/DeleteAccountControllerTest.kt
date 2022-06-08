package com.wutsi.platform.account.endpoint

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.platform.account.dao.AccountRepository
import com.wutsi.platform.account.error.ErrorURN
import com.wutsi.platform.account.event.AccountDeletedPayload
import com.wutsi.platform.account.event.EventURN
import com.wutsi.platform.core.error.ErrorResponse
import com.wutsi.platform.core.stream.EventStream
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.jdbc.Sql
import org.springframework.web.client.HttpStatusCodeException
import org.springframework.web.client.RestTemplate
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/DeleteAccountController.sql"])
class DeleteAccountControllerTest : AbstractSecuredController() {
    @LocalServerPort
    val port: Int = 0

    @Autowired
    private lateinit var dao: AccountRepository

    private lateinit var rest: RestTemplate

    @MockBean
    private lateinit var eventStream: EventStream

    @BeforeEach
    override fun setUp() {
        super.setUp()

        rest = createResTemplate(listOf("user-manage"), subjectId = 100)
    }

    @Test
    fun invoke() {
        val url = "http://localhost:$port/v1/accounts/100"
        rest.delete(url)

        val account = dao.findById(100).get()
        assertTrue(account.isDeleted)
        assertNotNull(account.deleted)
        assertNull(account.phone)

        val payload = argumentCaptor<AccountDeletedPayload>()
        verify(eventStream).publish(eq(EventURN.ACCOUNT_DELETED.urn), payload.capture())
        assertEquals(100L, payload.firstValue.accountId)
    }

    @Test
    fun `already deleted`() {
        rest = createResTemplate(listOf("user-manage"), subjectId = 199)
        val url = "http://localhost:$port/v1/accounts/199"

        val ex = assertThrows<HttpStatusCodeException> {
            rest.delete(url)
        }
        assertEquals(404, ex.rawStatusCode)

        verify(eventStream, never()).publish(any(), any())
    }

    @Test
    fun `cannot delete another user account`() {
        rest = createResTemplate(listOf("user-manage"), subjectId = 7777)
        val url = "http://localhost:$port/v1/accounts/101"

        val ex = assertThrows<HttpStatusCodeException> {
            rest.delete(url)
        }

        assertEquals(403, ex.rawStatusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals(ErrorURN.ILLEGAL_ACCOUNT_ACCESS.urn, response.error.code)

        verify(eventStream, never()).publish(any(), any())
    }

    @Test
    fun `invalid tenant`() {
        val url = "http://localhost:$port/v1/accounts/200"

        val ex = assertThrows<HttpStatusCodeException> {
            rest = createResTemplate(tenantId = 999999)
            rest.delete(url)
        }

        assertEquals(403, ex.rawStatusCode)
    }
}
