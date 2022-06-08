package com.wutsi.platform.account.endpoint

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.platform.account.dao.AccountRepository
import com.wutsi.platform.account.dto.UpdateAccountRequest
import com.wutsi.platform.account.dto.UpdateAccountResponse
import com.wutsi.platform.account.event.AccountUpdatedPayload
import com.wutsi.platform.account.event.EventURN
import com.wutsi.platform.core.stream.EventStream
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.jdbc.Sql
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import kotlin.test.assertEquals
import kotlin.test.assertNull

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/UpdateAccountController.sql"])
class UpdateAccountControllerTest : AbstractSecuredController() {
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

        rest = createResTemplate(listOf("user-manage"))
    }

    @Test
    fun `update account`() {
        // GIVEN
        val url = "http://localhost:$port/v1/accounts/100"
        val request = UpdateAccountRequest(
            displayName = "Roger Milla",
            language = "fr",
            country = "GA"
        )
        val response = rest.postForEntity(url, request, UpdateAccountResponse::class.java)

        // WHEN
        assertEquals(200, response.statusCodeValue)

        // THEN
        val account = dao.findById(response.body!!.id).get()

        assertEquals(request.displayName, account.displayName)
        assertEquals(request.language, account.language)
        assertEquals(request.country, account.country)

        val payload = argumentCaptor<AccountUpdatedPayload>()
        verify(eventStream).publish(eq(EventURN.ACCOUNT_UPDATED.urn), payload.capture())
        assertEquals(response.body!!.id, payload.firstValue.accountId)
        assertNull(payload.firstValue.attribute)
    }

    @Test
    fun `account not found`() {
        // GIVEN
        val url = "http://localhost:$port/v1/accounts/9999"
        val request = UpdateAccountRequest(
            displayName = "Roger Milla",
            language = "fr",
            country = "GA"
        )
        val ex = assertThrows<HttpClientErrorException> {
            rest.postForEntity(url, request, UpdateAccountResponse::class.java)
        }

        // WHEN
        assertEquals(404, ex.rawStatusCode)

        verify(eventStream, never()).publish(any(), any())
    }
}
