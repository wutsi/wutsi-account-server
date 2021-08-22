package com.wutsi.platform.account.endpoint

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.platform.account.dao.AccountRepository
import com.wutsi.platform.account.dao.PhoneRepository
import com.wutsi.platform.account.dto.CreateAccountRequest
import com.wutsi.platform.account.dto.CreateAccountResponse
import com.wutsi.platform.account.entity.AccountStatus
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
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/CreateAccountController.sql"])
public class CreateAccountControllerTest {
    @LocalServerPort
    public val port: Int = 0

    @Autowired
    private lateinit var dao: AccountRepository

    @Autowired
    private lateinit var phoneDao: PhoneRepository

    private val rest = RestTemplate()
    private lateinit var url: String

    @BeforeEach
    fun setUp() {
        url = "http://localhost:$port/v1/accounts"
    }

    @Test
    fun `create account`() {
        val request = CreateAccountRequest(
            phoneNumber = "+23774511111"
        )
        val response = rest.postForEntity(url, request, CreateAccountResponse::class.java)

        assertEquals(200, response.statusCodeValue)

        val account = dao.findById(response.body.id).get()
        assertNull(account.displayName)
        assertNull(account.pictureUrl)
        assertNotNull(account.created)
        assertNotNull(account.updated)
        assertNull(account.deleted)
        assertEquals(AccountStatus.ACCOUNT_STATUS_ACTIVE, account.status)

        val phone = phoneDao.findById(account.phone?.id).get()
        assertEquals("+23774511111", phone.number)
        assertEquals("CM", phone.country)
        assertNotNull(phone.created)
    }

    @Test
    fun `create account with existing phone number`() {
        val request = CreateAccountRequest(
            phoneNumber = "+237221234100"
        )
        val response = rest.postForEntity(url, request, CreateAccountResponse::class.java)

        assertEquals(200, response.statusCodeValue)

        val account = dao.findById(response.body.id).get()
        assertNull(account.displayName)
        assertNull(account.pictureUrl)
        assertNotNull(account.created)
        assertNotNull(account.updated)
        assertNull(account.deleted)
        assertEquals(AccountStatus.ACCOUNT_STATUS_ACTIVE, account.status)
        assertEquals(100, account.phone?.id)
    }

    @Test
    fun `malformed phone number`() {
        val request = CreateAccountRequest(
            phoneNumber = "2377451"
        )
        val ex = assertThrows<HttpStatusCodeException> {
            rest.postForEntity(url, request, CreateAccountResponse::class.java)
        }

        assertEquals(400, ex.rawStatusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals(ErrorURN.PHONE_NUMBER_MALFORMED.urn, response.error.code)
    }

    @Test
    fun `phone number already assigned`() {
        val request = CreateAccountRequest(
            phoneNumber = "+237221234101"
        )
        val ex = assertThrows<HttpStatusCodeException> {
            rest.postForEntity(url, request, CreateAccountResponse::class.java)
        }

        assertEquals(409, ex.rawStatusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals(ErrorURN.PHONE_NUMBER_ALREADY_ASSIGNED.urn, response.error.code)
    }
}
