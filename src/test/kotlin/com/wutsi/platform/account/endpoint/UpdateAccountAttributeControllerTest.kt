package com.wutsi.platform.account.endpoint

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.platform.account.dao.AccountRepository
import com.wutsi.platform.account.dto.UpdateAccountAttributeRequest
import com.wutsi.platform.account.error.ErrorURN
import com.wutsi.platform.account.event.AccountUpdatedPayload
import com.wutsi.platform.account.event.EventURN
import com.wutsi.platform.core.error.ErrorResponse
import com.wutsi.platform.core.stream.EventStream
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.jdbc.Sql
import org.springframework.web.client.HttpStatusCodeException
import org.springframework.web.client.RestTemplate
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/UpdateAccountAttributeController.sql"])
class UpdateAccountAttributeControllerTest : AbstractSecuredController() {
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

        rest = createResTemplate(
            subjectId = 100,
            scope = listOf("user-manage")
        )
    }

    @Test
    fun `cannot update another user profile`() {
        val url = "http://localhost:$port/v1/accounts/101/attributes/display-name"
        val request = UpdateAccountAttributeRequest(
            value = "Roger Milla"
        )
        val ex = assertThrows<HttpStatusCodeException> {
            rest.postForEntity(url, request, Any::class.java)
        }

        assertEquals(403, ex.rawStatusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals(ErrorURN.ILLEGAL_ACCOUNT_ACCESS.urn, response.error.code)

        verify(eventStream, never()).publish(any(), any())
    }

    @Test
    fun `set display-name`() {
        val url = "http://localhost:$port/v1/accounts/100/attributes/display-name"
        val request = UpdateAccountAttributeRequest(
            value = "Roger Milla"
        )
        val response = rest.postForEntity(url, request, Any::class.java)
        assertEquals(200, response.statusCodeValue)

        val account = dao.findById(100).get()
        assertEquals(request.value, account.displayName)

        val payload = argumentCaptor<AccountUpdatedPayload>()
        verify(eventStream).publish(eq(EventURN.ACCOUNT_UPDATED.urn), payload.capture())
        assertEquals(100L, payload.firstValue.accountId)
        assertEquals(TENANT_ID, payload.firstValue.tenantId)
        assertEquals("display-name", payload.firstValue.attribute)
    }

    @Test
    fun `reset display-name`() {
        val url = "http://localhost:$port/v1/accounts/100/attributes/display-name"
        val request = UpdateAccountAttributeRequest(
            value = null
        )
        val response = rest.postForEntity(url, request, Any::class.java)
        assertEquals(200, response.statusCodeValue)

        val account = dao.findById(100).get()
        assertNull(account.displayName)

        val payload = argumentCaptor<AccountUpdatedPayload>()
        verify(eventStream).publish(eq(EventURN.ACCOUNT_UPDATED.urn), payload.capture())
        assertEquals(100L, payload.firstValue.accountId)
        assertEquals(TENANT_ID, payload.firstValue.tenantId)
        assertEquals("display-name", payload.firstValue.attribute)
    }

    @Test
    fun `empty display-name`() {
        val url = "http://localhost:$port/v1/accounts/100/attributes/display-name"
        val request = UpdateAccountAttributeRequest(
            value = ""
        )
        val response = rest.postForEntity(url, request, Any::class.java)
        assertEquals(200, response.statusCodeValue)

        val account = dao.findById(100).get()
        assertNull(account.displayName)

        val payload = argumentCaptor<AccountUpdatedPayload>()
        verify(eventStream).publish(eq(EventURN.ACCOUNT_UPDATED.urn), payload.capture())
        assertEquals(100L, payload.firstValue.accountId)
        assertEquals(TENANT_ID, payload.firstValue.tenantId)
        assertEquals("display-name", payload.firstValue.attribute)
    }

    @Test
    fun `set picture-url`() {
        val url = "http://localhost:$port/v1/accounts/100/attributes/picture-url"
        val request = UpdateAccountAttributeRequest(
            value = "https://me.com/roger-milla/0.png"
        )
        val response = rest.postForEntity(url, request, Any::class.java)
        assertEquals(200, response.statusCodeValue)

        val account = dao.findById(100).get()
        assertEquals(request.value, account.pictureUrl)

        val payload = argumentCaptor<AccountUpdatedPayload>()
        verify(eventStream).publish(eq(EventURN.ACCOUNT_UPDATED.urn), payload.capture())
        assertEquals(100L, payload.firstValue.accountId)
        assertEquals(TENANT_ID, payload.firstValue.tenantId)
        assertEquals("picture-url", payload.firstValue.attribute)
    }

    @Test
    fun `reset picture-url`() {
        val url = "http://localhost:$port/v1/accounts/100/attributes/picture-url"
        val request = UpdateAccountAttributeRequest(
            value = null
        )
        val response = rest.postForEntity(url, request, Any::class.java)
        assertEquals(200, response.statusCodeValue)

        val account = dao.findById(100).get()
        assertNull(account.pictureUrl)

        val payload = argumentCaptor<AccountUpdatedPayload>()
        verify(eventStream).publish(eq(EventURN.ACCOUNT_UPDATED.urn), payload.capture())
        assertEquals(100L, payload.firstValue.accountId)
        assertEquals(TENANT_ID, payload.firstValue.tenantId)
        assertEquals("picture-url", payload.firstValue.attribute)
    }

    @Test
    fun `empty picture-url`() {
        val url = "http://localhost:$port/v1/accounts/100/attributes/picture-url"
        val request = UpdateAccountAttributeRequest(
            value = ""
        )
        val response = rest.postForEntity(url, request, Any::class.java)
        assertEquals(200, response.statusCodeValue)

        val account = dao.findById(100).get()
        assertNull(account.pictureUrl)

        val payload = argumentCaptor<AccountUpdatedPayload>()
        verify(eventStream).publish(eq(EventURN.ACCOUNT_UPDATED.urn), payload.capture())
        assertEquals(100L, payload.firstValue.accountId)
        assertEquals(TENANT_ID, payload.firstValue.tenantId)
        assertEquals("picture-url", payload.firstValue.attribute)
    }

    @Test
    fun `malformed picture-url`() {
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

        verify(eventStream, never()).publish(any(), any())
    }

    @Test
    fun `invalid-attribute`() {
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

        verify(eventStream, never()).publish(any(), any())
    }

    @Test
    fun `deleted-account`() {
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

        verify(eventStream, never()).publish(any(), any())
    }

    @Test
    fun `set language`() {
        val url = "http://localhost:$port/v1/accounts/100/attributes/language"
        val request = UpdateAccountAttributeRequest(
            value = "fr"
        )
        val response = rest.postForEntity(url, request, Any::class.java)
        assertEquals(200, response.statusCodeValue)

        val account = dao.findById(100).get()
        assertEquals(request.value, account.language)

        val payload = argumentCaptor<AccountUpdatedPayload>()
        verify(eventStream).publish(eq(EventURN.ACCOUNT_UPDATED.urn), payload.capture())
        assertEquals(100L, payload.firstValue.accountId)
        assertEquals(TENANT_ID, payload.firstValue.tenantId)
        assertEquals("language", payload.firstValue.attribute)
    }

    @Test
    fun `reset language`() {
        val url = "http://localhost:$port/v1/accounts/100/attributes/language"
        val request = UpdateAccountAttributeRequest(
            value = ""
        )
        val response = rest.postForEntity(url, request, Any::class.java)
        assertEquals(200, response.statusCodeValue)

        val account = dao.findById(100).get()
        assertEquals("en", account.language)

        val payload = argumentCaptor<AccountUpdatedPayload>()
        verify(eventStream).publish(eq(EventURN.ACCOUNT_UPDATED.urn), payload.capture())
        assertEquals(100L, payload.firstValue.accountId)
        assertEquals(TENANT_ID, payload.firstValue.tenantId)
        assertEquals("language", payload.firstValue.attribute)
    }

    @Test
    fun `reset invalid language`() {
        val url = "http://localhost:$port/v1/accounts/100/attributes/language"
        val request = UpdateAccountAttributeRequest(
            value = "??"
        )
        val response = rest.postForEntity(url, request, Any::class.java)
        assertEquals(200, response.statusCodeValue)

        val account = dao.findById(100).get()
        assertEquals("en", account.language)

        val payload = argumentCaptor<AccountUpdatedPayload>()
        verify(eventStream).publish(eq(EventURN.ACCOUNT_UPDATED.urn), payload.capture())
        assertEquals(100L, payload.firstValue.accountId)
        assertEquals(TENANT_ID, payload.firstValue.tenantId)
        assertEquals("language", payload.firstValue.attribute)
    }

    @Test
    fun `set country`() {
        val url = "http://localhost:$port/v1/accounts/100/attributes/country"
        val request = UpdateAccountAttributeRequest(
            value = "CM"
        )
        val response = rest.postForEntity(url, request, Any::class.java)
        assertEquals(200, response.statusCodeValue)

        val account = dao.findById(100).get()
        assertEquals(request.value, account.country)

        val payload = argumentCaptor<AccountUpdatedPayload>()
        verify(eventStream).publish(eq(EventURN.ACCOUNT_UPDATED.urn), payload.capture())
        assertEquals(100L, payload.firstValue.accountId)
        assertEquals(TENANT_ID, payload.firstValue.tenantId)
        assertEquals("country", payload.firstValue.attribute)
    }

    @Test
    fun `set transfer security`() {
        val url = "http://localhost:$port/v1/accounts/100/attributes/transfer-secured"
        val request = UpdateAccountAttributeRequest(
            value = "true"
        )
        val response = rest.postForEntity(url, request, Any::class.java)
        assertEquals(200, response.statusCodeValue)

        val account = dao.findById(100).get()
        assertTrue(account.isTransferSecured)

        val payload = argumentCaptor<AccountUpdatedPayload>()
        verify(eventStream).publish(eq(EventURN.ACCOUNT_UPDATED.urn), payload.capture())
        assertEquals(100L, payload.firstValue.accountId)
        assertEquals(TENANT_ID, payload.firstValue.tenantId)
        assertEquals("transfer-secured", payload.firstValue.attribute)
    }

    @Test
    fun `reset transfer security`() {
        val url = "http://localhost:$port/v1/accounts/100/attributes/transfer-secured"
        val request = UpdateAccountAttributeRequest(
            value = "false"
        )
        val response = rest.postForEntity(url, request, Any::class.java)
        assertEquals(200, response.statusCodeValue)

        val account = dao.findById(100).get()
        assertFalse(account.isTransferSecured)

        val payload = argumentCaptor<AccountUpdatedPayload>()
        verify(eventStream).publish(eq(EventURN.ACCOUNT_UPDATED.urn), payload.capture())
        assertEquals(100L, payload.firstValue.accountId)
        assertEquals(TENANT_ID, payload.firstValue.tenantId)
        assertEquals("transfer-secured", payload.firstValue.attribute)
    }

    @Test
    fun `null transfer secured`() {
        val url = "http://localhost:$port/v1/accounts/100/attributes/transfer-secured"
        val request = UpdateAccountAttributeRequest(
            value = null
        )
        val response = rest.postForEntity(url, request, Any::class.java)
        assertEquals(200, response.statusCodeValue)

        val account = dao.findById(100).get()
        assertFalse(account.isTransferSecured)

        val payload = argumentCaptor<AccountUpdatedPayload>()
        verify(eventStream).publish(eq(EventURN.ACCOUNT_UPDATED.urn), payload.capture())
        assertEquals(100L, payload.firstValue.accountId)
        assertEquals(TENANT_ID, payload.firstValue.tenantId)
        assertEquals("transfer-secured", payload.firstValue.attribute)
    }

    @Test
    fun `set business`() {
        val url = "http://localhost:$port/v1/accounts/100/attributes/business"
        val request = UpdateAccountAttributeRequest(
            value = "true"
        )
        val response = rest.postForEntity(url, request, Any::class.java)
        assertEquals(200, response.statusCodeValue)

        val account = dao.findById(100).get()
        assertTrue(account.business)

        val payload = argumentCaptor<AccountUpdatedPayload>()
        verify(eventStream).publish(eq(EventURN.ACCOUNT_UPDATED.urn), payload.capture())
        assertEquals(100L, payload.firstValue.accountId)
        assertEquals(TENANT_ID, payload.firstValue.tenantId)
        assertEquals("business", payload.firstValue.attribute)
    }

    @Test
    fun `set website`() {
        val url = "http://localhost:$port/v1/accounts/100/attributes/website"
        val request = UpdateAccountAttributeRequest(
            value = "https://www.google.ca"
        )
        val response = rest.postForEntity(url, request, Any::class.java)
        assertEquals(200, response.statusCodeValue)

        val account = dao.findById(100).get()
        assertEquals(request.value, account.website)

        val payload = argumentCaptor<AccountUpdatedPayload>()
        verify(eventStream).publish(eq(EventURN.ACCOUNT_UPDATED.urn), payload.capture())
        assertEquals(100L, payload.firstValue.accountId)
        assertEquals(TENANT_ID, payload.firstValue.tenantId)
        assertEquals("website", payload.firstValue.attribute)
    }

    @Test
    fun `set biography`() {
        val url = "http://localhost:$port/v1/accounts/100/attributes/biography"
        val request = UpdateAccountAttributeRequest(
            value = "This is a nice store"
        )
        val response = rest.postForEntity(url, request, Any::class.java)
        assertEquals(200, response.statusCodeValue)

        val account = dao.findById(100).get()
        assertEquals(request.value, account.biography)

        val payload = argumentCaptor<AccountUpdatedPayload>()
        verify(eventStream).publish(eq(EventURN.ACCOUNT_UPDATED.urn), payload.capture())
        assertEquals(100L, payload.firstValue.accountId)
        assertEquals(TENANT_ID, payload.firstValue.tenantId)
        assertEquals("biography", payload.firstValue.attribute)
    }

    @Test
    fun `set category-id`() {
        val url = "http://localhost:$port/v1/accounts/100/attributes/category-id"
        val request = UpdateAccountAttributeRequest(
            value = "555"
        )
        val response = rest.postForEntity(url, request, Any::class.java)
        assertEquals(200, response.statusCodeValue)

        val account = dao.findById(100).get()
        assertEquals(555, account.categoryId)

        val payload = argumentCaptor<AccountUpdatedPayload>()
        verify(eventStream).publish(eq(EventURN.ACCOUNT_UPDATED.urn), payload.capture())
        assertEquals(100L, payload.firstValue.accountId)
        assertEquals(TENANT_ID, payload.firstValue.tenantId)
        assertEquals("category-id", payload.firstValue.attribute)
    }

    @Test
    fun `set whatstapp`() {
        val url = "http://localhost:$port/v1/accounts/100/attributes/whatsapp"
        val request = UpdateAccountAttributeRequest(
            value = "+15147580101"
        )
        val response = rest.postForEntity(url, request, Any::class.java)
        assertEquals(200, response.statusCodeValue)

        val account = dao.findById(100).get()
        assertEquals("+15147580101", account.whatsapp)

        val payload = argumentCaptor<AccountUpdatedPayload>()
        verify(eventStream).publish(eq(EventURN.ACCOUNT_UPDATED.urn), payload.capture())
        assertEquals(100L, payload.firstValue.accountId)
        assertEquals(TENANT_ID, payload.firstValue.tenantId)
        assertEquals("whatsapp", payload.firstValue.attribute)
    }

    @Test
    fun `set street`() {
        val url = "http://localhost:$port/v1/accounts/100/attributes/street"
        val request = UpdateAccountAttributeRequest(
            value = "340 Pascal"
        )
        val response = rest.postForEntity(url, request, Any::class.java)
        assertEquals(200, response.statusCodeValue)

        val account = dao.findById(100).get()
        assertEquals("340 Pascal", account.street)

        val payload = argumentCaptor<AccountUpdatedPayload>()
        verify(eventStream).publish(eq(EventURN.ACCOUNT_UPDATED.urn), payload.capture())
        assertEquals(100L, payload.firstValue.accountId)
        assertEquals(TENANT_ID, payload.firstValue.tenantId)
        assertEquals("street", payload.firstValue.attribute)
    }

    @Test
    fun `set city-id`() {
        val url = "http://localhost:$port/v1/accounts/100/attributes/city-id"
        val request = UpdateAccountAttributeRequest(
            value = "340"
        )
        val response = rest.postForEntity(url, request, Any::class.java)
        assertEquals(200, response.statusCodeValue)

        val account = dao.findById(100).get()
        assertEquals(340, account.cityId)

        val payload = argumentCaptor<AccountUpdatedPayload>()
        verify(eventStream).publish(eq(EventURN.ACCOUNT_UPDATED.urn), payload.capture())
        assertEquals(100L, payload.firstValue.accountId)
        assertEquals(TENANT_ID, payload.firstValue.tenantId)
        assertEquals("city-id", payload.firstValue.attribute)
    }

    @Test
    fun `set timezone-id`() {
        val url = "http://localhost:$port/v1/accounts/100/attributes/timezone-id"
        val request = UpdateAccountAttributeRequest(
            value = "Africa/Douala"
        )
        val response = rest.postForEntity(url, request, Any::class.java)
        assertEquals(200, response.statusCodeValue)

        val account = dao.findById(100).get()
        assertEquals(request.value, account.timezoneId)

        val payload = argumentCaptor<AccountUpdatedPayload>()
        verify(eventStream).publish(eq(EventURN.ACCOUNT_UPDATED.urn), payload.capture())
        assertEquals(100L, payload.firstValue.accountId)
        assertEquals(TENANT_ID, payload.firstValue.tenantId)
        assertEquals("timezone-id", payload.firstValue.attribute)
    }

    @Test
    fun `set has-store`() {
        val url = "http://localhost:$port/v1/accounts/100/attributes/has-store"
        val request = UpdateAccountAttributeRequest(
            value = "true"
        )
        val response = rest.postForEntity(url, request, Any::class.java)
        assertEquals(200, response.statusCodeValue)

        val account = dao.findById(100).get()
        assertEquals(request.value.toBoolean(), account.hasStore)

        val payload = argumentCaptor<AccountUpdatedPayload>()
        verify(eventStream).publish(eq(EventURN.ACCOUNT_UPDATED.urn), payload.capture())
        assertEquals(100L, payload.firstValue.accountId)
        assertEquals(TENANT_ID, payload.firstValue.tenantId)
        assertEquals("has-store", payload.firstValue.attribute)
    }
}
