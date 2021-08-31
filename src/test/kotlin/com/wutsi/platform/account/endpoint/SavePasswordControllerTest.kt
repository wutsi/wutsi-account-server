package com.wutsi.platform.account.endpoint

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.platform.account.dao.AccountRepository
import com.wutsi.platform.account.dao.PasswordRepository
import com.wutsi.platform.account.dto.SavePasswordRequest
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
import java.time.OffsetDateTime
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/SavePasswordController.sql"])
public class SavePasswordControllerTest : AbstractSecuredController() {
    @LocalServerPort
    public val port: Int = 0

    @Autowired
    private lateinit var dao: PasswordRepository

    @Autowired
    private lateinit var accountDao: AccountRepository

    private lateinit var rest: RestTemplate

    @BeforeEach
    override fun setUp() {
        super.setUp()

        rest = createResTemplate(scope = listOf("user-manage"), subjectId = 100)
    }

    @Test
    public fun `create password`() {
        val url = "http://localhost:$port/v1/accounts/100/password"
        val request = SavePasswordRequest(
            password = "This is a secret"
        )
        val response = rest.postForEntity(url, request, Any::class.java)
        assertEquals(200, response.statusCodeValue)

        val password = dao.findByAccount(accountDao.findById(100).get()).get()
        assertNotNull(password.value)
        assertNotNull(password.salt)
        assertNotNull(password.created)
        assertNotNull(password.updated)
    }

    @Test
    public fun `update password`() {
        rest = createResTemplate(scope = listOf("user-manage"), subjectId = 101)
        val url = "http://localhost:$port/v1/accounts/101/password"
        val request = SavePasswordRequest(
            password = "This is a secret"
        )
        val response = rest.postForEntity(url, request, Any::class.java)
        assertEquals(200, response.statusCodeValue)

        val password = dao.findByAccount(accountDao.findById(101).get()).get()
        assertNotNull(password.value)
        assertNotNull(password.salt)
        assertNotNull(password.created)
        assertEquals(OffsetDateTime.now().year, password.updated.year)
    }

    @Test
    public fun `cannot set another user password`() {
        rest = createResTemplate(scope = listOf("user-manage"), subjectId = 7777)
        val url = "http://localhost:$port/v1/accounts/101/password"
        val request = SavePasswordRequest(
            password = "This is a secret"
        )
        val ex = assertThrows<HttpStatusCodeException> {
            rest.postForEntity(url, request, Any::class.java)
        }

        assertEquals(403, ex.rawStatusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals("urn:error:wutsi:access-denied", response.error.code)
    }
}
