package com.wutsi.platform.account.endpoint

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.platform.account.error.ErrorURN
import com.wutsi.platform.core.error.ErrorResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.jdbc.Sql
import org.springframework.web.client.HttpStatusCodeException
import org.springframework.web.client.RestTemplate
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/CheckPasswordController.sql"])
class CheckPasswordControllerTest : AbstractSecuredController() {
    @LocalServerPort
    val port: Int = 0

    private lateinit var rest: RestTemplate

    @BeforeEach
    override fun setUp() {
        super.setUp()

        rest = createResTemplate(listOf("user-read"), subjectId = 100)
    }

    @Test
    fun `check password`() {
        val password = "xxx"
        val url = "http://localhost:$port/v1/accounts/100/password?password=$password"

        val response = rest.getForEntity(url, Any::class.java)

        assertEquals(200, response.statusCodeValue)
    }

    @Test
    fun `password mismatch`() {
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
    fun `no password`() {
        val password = "xxx"
        val url = "http://localhost:$port/v1/accounts/101/password?password=$password"

        rest = createResTemplate(listOf("user-read"), subjectId = 101)
        val ex = assertThrows<HttpStatusCodeException> {
            rest.getForEntity(url, Any::class.java)
        }

        assertEquals(409, ex.rawStatusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals(ErrorURN.PASSWORD_MISMATCH.urn, response.error.code)
    }

    @Test
    fun `invalid account`() {
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
