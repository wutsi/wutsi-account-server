package com.wutsi.platform.account.endpoint

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.platform.account.dao.AccountRepository
import com.wutsi.platform.account.entity.AccountStatus
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/DeleteAccountController.sql"])
public class DeleteAccountControllerTest : AbstractSecuredController() {
    @LocalServerPort
    public val port: Int = 0

    @Autowired
    private lateinit var dao: AccountRepository

    private lateinit var rest: RestTemplate

    @BeforeEach
    override fun setUp() {
        super.setUp()

        rest = createResTemplate(listOf("user-manage"), subjectId = 100)
    }

    @Test
    public fun invoke() {
        val url = "http://localhost:$port/v1/accounts/100"
        rest.delete(url)

        val account = dao.findById(100).get()
        assertEquals(AccountStatus.ACCOUNT_STATUS_DELETED, account.status)
        assertNotNull(account.deleted)
        assertNull(account.phone)
    }

    @Test
    public fun `already deleted`() {
        rest = createResTemplate(listOf("user-manage"), subjectId = 199)
        val url = "http://localhost:$port/v1/accounts/199"
        rest.delete(url)

        val account = dao.findById(199).get()
        assertEquals(AccountStatus.ACCOUNT_STATUS_DELETED, account.status)
        assertEquals(2011, account.deleted?.year)
    }

    @Test
    public fun `cannot delete another user account`() {
        rest = createResTemplate(listOf("user-manage"), subjectId = 7777)
        val url = "http://localhost:$port/v1/accounts/101"

        val ex = assertThrows<HttpStatusCodeException> {
            rest.delete(url)
        }

        assertEquals(403, ex.rawStatusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals("urn:error:wutsi:access-denied", response.error.code)
    }
}
