package com.wutsi.platform.account.endpoint

import com.wutsi.platform.account.dto.SearchAccountResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.jdbc.Sql
import org.springframework.web.client.RestTemplate
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/SearchAccountController.sql"])
public class SearchAccountControllerTest : AbstractSecuredController() {
    @LocalServerPort
    public val port: Int = 0

    private lateinit var rest: RestTemplate

    @BeforeEach
    override fun setUp() {
        super.setUp()

        rest = createResTemplate(listOf("user-read"))
    }

    @Test
    public fun `search by phone`() {
        val url = "http://localhost:$port/v1/accounts?phone-number=+237221234100"
        val response = rest.getForEntity(url, SearchAccountResponse::class.java)

        assertEquals(200, response.statusCodeValue)

        val accounts = response.body.accounts
        assertEquals(1, accounts.size)

        val account = accounts[0]
        assertEquals(100, account.id)
        assertEquals("Ray Sponsible", account.displayName)
        assertEquals("https://me.com/12343/picture.png", account.pictureUrl)
        assertEquals("active", account.status)
        assertNotNull(account.created)
        assertNotNull(account.updated)
    }

    @Test
    public fun `search by phone without +`() {
        val url = "http://localhost:$port/v1/accounts?phone-number=237221234100"
        val response = rest.getForEntity(url, SearchAccountResponse::class.java)

        assertEquals(200, response.statusCodeValue)

        val accounts = response.body.accounts
        assertEquals(1, accounts.size)

        val account = accounts[0]
        assertEquals(100, account.id)
        assertEquals("Ray Sponsible", account.displayName)
        assertEquals("https://me.com/12343/picture.png", account.pictureUrl)
        assertEquals("active", account.status)
        assertNotNull(account.created)
        assertNotNull(account.updated)
    }
}
