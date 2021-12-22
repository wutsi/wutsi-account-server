package com.wutsi.platform.account.endpoint

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.platform.account.dto.GetAccountResponse
import com.wutsi.platform.account.error.ErrorURN
import com.wutsi.platform.core.error.ErrorResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.jdbc.Sql
import org.springframework.web.client.HttpStatusCodeException
import org.springframework.web.client.RestTemplate
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/GetAccountController.sql"])
public class GetAccountControllerTest : AbstractSecuredController() {
    @LocalServerPort
    public val port: Int = 0

    private lateinit var rest: RestTemplate

    @BeforeEach
    override fun setUp() {
        super.setUp()

        rest = createResTemplate(subjectId = 100)
    }

    @Test
    public fun `get account`() {
        val url = "http://localhost:$port/v1/accounts/100"
        val response = rest.getForEntity(url, GetAccountResponse::class.java)

        assertEquals(200, response.statusCodeValue)

        val account = response.body.account
        assertEquals(100, account.id)
        assertEquals("Ray Sponsible", account.displayName)
        assertEquals("https://me.com/12343/picture.png", account.pictureUrl)
        assertEquals("ACTIVE", account.status)
        assertEquals("fr", account.language)
        assertNotNull(account.created)
        assertNotNull(account.updated)
        assertTrue(account.superUser)
        assertFalse(account.transferSecured)

        assertNotNull(account.phone)
        assertNotNull("+237221234100", account.phone?.number)
        assertNotNull("CM", account.phone?.country)
        assertNotNull(account.phone?.created)
    }

    @Test
    fun `phone is returned without user-phone permission`() {
        rest = createResTemplate(listOf("user-read", "user-phone"), subjectId = 101)
        val url = "http://localhost:$port/v1/accounts/100"
        val response = rest.getForEntity(url, GetAccountResponse::class.java)

        assertEquals(200, response.statusCodeValue)

        val account = response.body.account
        assertNotNull(account.phone)
        assertNotNull("+237221234100", account.phone?.number)
        assertNotNull("CM", account.phone?.country)
        assertNotNull(account.phone?.created)
    }

    @Test
    fun `phone is not returned without user-phone scope`() {
        rest = createResTemplate(listOf("user-read"), subjectId = 101)
        val url = "http://localhost:$port/v1/accounts/100"
        val response = rest.getForEntity(url, GetAccountResponse::class.java)

        assertEquals(200, response.statusCodeValue)

        val account = response.body.account
        assertNull(account.phone)
    }

    @Test
    public fun `deleted-account`() {
        val url = "http://localhost:$port/v1/accounts/199"

        val ex = assertThrows<HttpStatusCodeException> {
            rest.getForEntity(url, GetAccountResponse::class.java)
        }
        assertEquals(404, ex.rawStatusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals(ErrorURN.ACCOUNT_DELETED.urn, response.error.code)
    }

    @Test
    public fun `invalid id`() {
        val url = "http://localhost:$port/v1/accounts/9999"

        val ex = assertThrows<HttpStatusCodeException> {
            rest.getForEntity(url, GetAccountResponse::class.java)
        }
        assertEquals(404, ex.rawStatusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals(ErrorURN.ACCOUNT_NOT_FOUND.urn, response.error.code)
    }

    @Test
    public fun `invalid tenant`() {
        val url = "http://localhost:$port/v1/accounts/100"

        val ex = assertThrows<HttpStatusCodeException> {
            rest = createResTemplate(tenantId = 999999)
            rest.getForEntity(url, GetAccountResponse::class.java)
        }
        assertEquals(403, ex.rawStatusCode)
    }
}
