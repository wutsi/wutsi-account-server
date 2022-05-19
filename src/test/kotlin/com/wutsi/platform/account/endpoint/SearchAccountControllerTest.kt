package com.wutsi.platform.account.endpoint

import com.wutsi.platform.account.dto.SearchAccountRequest
import com.wutsi.platform.account.dto.SearchAccountResponse
import com.wutsi.platform.account.entity.AccountSort
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.jdbc.Sql
import org.springframework.web.client.RestTemplate
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/SearchAccountController.sql"])
class SearchAccountControllerTest : AbstractSecuredController() {
    @LocalServerPort
    val port: Int = 0

    private lateinit var rest: RestTemplate

    private lateinit var url: String

    @BeforeEach
    override fun setUp() {
        super.setUp()

        rest = createResTemplate(listOf("user-read"))
        url = "http://localhost:$port/v1/accounts/search"
    }

    @Test
    fun `search by phone`() {
        val request = SearchAccountRequest(
            phoneNumber = "+237221234100"
        )
        val response = rest.postForEntity(url, request, SearchAccountResponse::class.java)

        assertEquals(200, response.statusCodeValue)

        val accounts = response.body!!.accounts
        assertEquals(1, accounts.size)

        val account = accounts[0]
        assertEquals(100, account.id)
        assertEquals("Ray Sponsible", account.displayName)
        assertEquals("https://me.com/12343/picture.png", account.pictureUrl)
        assertEquals("ACTIVE", account.status)
        assertEquals("fr", account.language)
        assertNotNull(account.created)
        assertNotNull(account.updated)
        assertTrue(account.superUser)
    }

    @Test
    fun `search by phone to normalize`() {
        val request = SearchAccountRequest(
            phoneNumber = " 237221234100"
        )
        val response = rest.postForEntity(url, request, SearchAccountResponse::class.java)

        assertEquals(200, response.statusCodeValue)

        val accounts = response.body!!.accounts
        assertEquals(1, accounts.size)

        val account = accounts[0]
        assertEquals(100, account.id)
        assertEquals("Ray Sponsible", account.displayName)
        assertEquals("https://me.com/12343/picture.png", account.pictureUrl)
        assertEquals("ACTIVE", account.status)
        assertEquals("fr", account.language)
        assertNotNull(account.created)
        assertNotNull(account.updated)
        assertTrue(account.superUser)
    }

    @Test
    fun `search by ID`() {
        val request = SearchAccountRequest(
            ids = listOf(100L, 101L)
        )
        val response = rest.postForEntity(url, request, SearchAccountResponse::class.java)

        assertEquals(200, response.statusCodeValue)

        val accounts = response.body!!.accounts
        assertEquals(2, accounts.size)
        assertEquals(100, accounts[0].id)
        assertEquals(101, accounts[1].id)
    }

    @Test
    fun `search business accounts - sort by recommended`() {
        val request = SearchAccountRequest(
            business = true,
            sortBy = AccountSort.RECOMMENDED.name
        )
        val response = rest.postForEntity(url, request, SearchAccountResponse::class.java)

        assertEquals(200, response.statusCodeValue)

        val accounts = response.body!!.accounts
        assertEquals(2, accounts.size)
        assertEquals(102, accounts[0].id)
        assertEquals(100, accounts[1].id)
    }
}
