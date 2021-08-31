package com.wutsi.platform.account.endpoint

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.platform.account.dao.AccountRepository
import com.wutsi.platform.account.dto.UpdateAccountAttributeRequest
import com.wutsi.platform.account.util.ErrorURN
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
import kotlin.test.assertNull

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/UpdateAccountAttributeController.sql"])
public class UpdateAccountAttributeControllerTest : AbstractSecuredController() {
    @LocalServerPort
    public val port: Int = 0

    @Autowired
    private lateinit var dao: AccountRepository

    private lateinit var rest: RestTemplate

    @BeforeEach
    override fun setUp() {
        super.setUp()

        rest = createResTemplate(
            subjectId = 100,
            scope = listOf("user-manage")
        )
    }

    @Test
    public fun `cannot update another user profile`() {
        val url = "http://localhost:$port/v1/accounts/101/attributes/display-name"
        val request = UpdateAccountAttributeRequest(
            value = "Roger Milla"
        )
        val ex = assertThrows<HttpStatusCodeException> {
            rest.postForEntity(url, request, Any::class.java)
        }

        assertEquals(403, ex.rawStatusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals("urn:error:wutsi:access-denied", response.error.code)
    }

    @Test
    public fun `set display-name`() {
        val url = "http://localhost:$port/v1/accounts/100/attributes/display-name"
        val request = UpdateAccountAttributeRequest(
            value = "Roger Milla"
        )
        val response = rest.postForEntity(url, request, Any::class.java)
        assertEquals(200, response.statusCodeValue)

        val account = dao.findById(100).get()
        assertEquals(request.value, account.displayName)
    }

    @Test
    public fun `reset display-name`() {
        val url = "http://localhost:$port/v1/accounts/100/attributes/display-name"
        val request = UpdateAccountAttributeRequest(
            value = null
        )
        val response = rest.postForEntity(url, request, Any::class.java)
        assertEquals(200, response.statusCodeValue)

        val account = dao.findById(100).get()
        assertNull(account.displayName)
    }

    @Test
    public fun `empty display-name`() {
        val url = "http://localhost:$port/v1/accounts/100/attributes/display-name"
        val request = UpdateAccountAttributeRequest(
            value = ""
        )
        val response = rest.postForEntity(url, request, Any::class.java)
        assertEquals(200, response.statusCodeValue)

        val account = dao.findById(100).get()
        assertNull(account.displayName)
    }

    @Test
    public fun `set picture-url`() {
        val url = "http://localhost:$port/v1/accounts/100/attributes/picture-url"
        val request = UpdateAccountAttributeRequest(
            value = "https://me.com/roger-milla/0.png"
        )
        val response = rest.postForEntity(url, request, Any::class.java)
        assertEquals(200, response.statusCodeValue)

        val account = dao.findById(100).get()
        assertEquals(request.value, account.pictureUrl)
    }

    @Test
    public fun `reset picture-url`() {
        val url = "http://localhost:$port/v1/accounts/100/attributes/picture-url"
        val request = UpdateAccountAttributeRequest(
            value = null
        )
        val response = rest.postForEntity(url, request, Any::class.java)
        assertEquals(200, response.statusCodeValue)

        val account = dao.findById(100).get()
        assertNull(account.pictureUrl)
    }

    @Test
    public fun `empty picture-url`() {
        val url = "http://localhost:$port/v1/accounts/100/attributes/picture-url"
        val request = UpdateAccountAttributeRequest(
            value = ""
        )
        val response = rest.postForEntity(url, request, Any::class.java)
        assertEquals(200, response.statusCodeValue)

        val account = dao.findById(100).get()
        assertNull(account.pictureUrl)
    }

    @Test
    public fun `malformed picture-url`() {
        val url = "http://localhost:$port/v1/accounts/100/attributes/picture-url"
        val request = UpdateAccountAttributeRequest(
            value = "invalid-url"
        )
        val ex = assertThrows<HttpStatusCodeException> {
            rest.postForEntity(url, request, Any::class.java)
        }
        assertEquals(400, ex.rawStatusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals(ErrorURN.PICTURE_URL_MALFORMED.urn, response.error.code)
    }

    @Test
    public fun `invalid-attribute`() {
        val url = "http://localhost:$port/v1/accounts/100/attributes/__invalid_attribute__"
        val request = UpdateAccountAttributeRequest(
            value = "value"
        )
        val ex = assertThrows<HttpStatusCodeException> {
            rest.postForEntity(url, request, Any::class.java)
        }
        assertEquals(400, ex.rawStatusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals(ErrorURN.ATTRIBUTE_INVALID.urn, response.error.code)
    }

    @Test
    public fun `deleted-account`() {
        val url = "http://localhost:$port/v1/accounts/199/attributes/display-name"
        val request = UpdateAccountAttributeRequest(
            value = "Omam Biyick"
        )
        val ex = assertThrows<HttpStatusCodeException> {
            rest.postForEntity(url, request, Any::class.java)
        }
        assertEquals(404, ex.rawStatusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals(ErrorURN.ACCOUNT_DELETED.urn, response.error.code)
    }

    @Test
    public fun `set language`() {
        val url = "http://localhost:$port/v1/accounts/100/attributes/language"
        val request = UpdateAccountAttributeRequest(
            value = "fr"
        )
        val response = rest.postForEntity(url, request, Any::class.java)
        assertEquals(200, response.statusCodeValue)

        val account = dao.findById(100).get()
        assertEquals(request.value, account.language)
    }

    @Test
    public fun `reset language`() {
        val url = "http://localhost:$port/v1/accounts/100/attributes/language"
        val request = UpdateAccountAttributeRequest(
            value = ""
        )
        val response = rest.postForEntity(url, request, Any::class.java)
        assertEquals(200, response.statusCodeValue)

        val account = dao.findById(100).get()
        assertEquals("en", account.language)
    }

    @Test
    public fun `reset invalid language`() {
        val url = "http://localhost:$port/v1/accounts/100/attributes/language"
        val request = UpdateAccountAttributeRequest(
            value = "??"
        )
        val response = rest.postForEntity(url, request, Any::class.java)
        assertEquals(200, response.statusCodeValue)

        val account = dao.findById(100).get()
        assertEquals("en", account.language)
    }
}
