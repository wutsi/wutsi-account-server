package com.wutsi.platform.account.endpoint

import com.wutsi.platform.account.dao.AccountRepository
import com.wutsi.platform.account.dto.UpdateAccountRequest
import com.wutsi.platform.account.dto.UpdateAccountResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.jdbc.Sql
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/UpdateAccountController.sql"])
public class UpdateAccountControllerTest : AbstractSecuredController() {
    @LocalServerPort
    public val port: Int = 0

    @Autowired
    private lateinit var dao: AccountRepository

    private lateinit var rest: RestTemplate

    @BeforeEach
    override fun setUp() {
        super.setUp()

        rest = createResTemplate(listOf("user-manage"))
    }

    @Test
    public fun `update account`() {
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
        val account = dao.findById(response.body?.id).get()

        assertEquals(request.displayName, account.displayName)
        assertEquals(request.language, account.language)
        assertEquals(request.country, account.country)
    }

    @Test
    public fun `account not found`() {
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
    }
}
