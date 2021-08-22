package com.wutsi.platform.account.endpoint

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.platform.account.util.ErrorURN
import com.wutsi.platform.core.error.ErrorResponse
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.jdbc.Sql
import org.springframework.web.client.HttpStatusCodeException
import org.springframework.web.client.RestTemplate
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/CheckPasswordController.sql"])
public class CheckPasswordControllerTest {
    @LocalServerPort
    public val port: Int = 0

    private val rest = RestTemplate()

    @Test
    public fun `check password`() {
        val password = "xxx"
        val url = "http://localhost:$port/v1/accounts/100/password?password=$password"
        val response = rest.getForEntity(url, Any::class.java)

        assertEquals(200, response.statusCodeValue)
    }

    @Test
    public fun `password mismatch`() {
        val password = "123456"
        val url = "http://localhost:$port/v1/accounts/100/password?password=$password"
        val ex = assertThrows<HttpStatusCodeException> {
            rest.getForEntity(url, Any::class.java)
        }

        assertEquals(409, ex.rawStatusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals(ErrorURN.PASSWORD_MISMATCH.urn, response.error.code)
    }

    @Test
    public fun `no password`() {
        val password = "xxx"
        val url = "http://localhost:$port/v1/accounts/101/password?password=$password"
        val ex = assertThrows<HttpStatusCodeException> {
            rest.getForEntity(url, Any::class.java)
        }

        assertEquals(409, ex.rawStatusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals(ErrorURN.PASSWORD_MISMATCH.urn, response.error.code)
    }

    @Test
    public fun `invalid account`() {
        val password = "xxx"
        val url = "http://localhost:$port/v1/accounts/9999/password?password=$password"
        val ex = assertThrows<HttpStatusCodeException> {
            rest.getForEntity(url, Any::class.java)
        }

        assertEquals(404, ex.rawStatusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals(ErrorURN.ACCOUNT_NOT_FOUND.urn, response.error.code)
    }
}
