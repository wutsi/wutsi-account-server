package com.wutsi.platform.account.endpoint

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.verify
import com.wutsi.platform.account.dao.AccountRepository
import com.wutsi.platform.account.dto.EnableBusinessRequest
import com.wutsi.platform.account.event.AccountUpdatedPayload
import com.wutsi.platform.account.event.EventURN
import com.wutsi.platform.core.stream.EventStream
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.jdbc.Sql
import org.springframework.web.client.RestTemplate
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/EnableBusinessController.sql"])
class EnableBusinessControllerTest : AbstractSecuredController() {
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
    fun enable() {
        // WHEN
        val url = "http://localhost:$port/v1/accounts/100/business"
        val request = EnableBusinessRequest(
            displayName = "Yo Inc",
            categoryId = 101L,
            cityId = 1000,
            biography = "This is the biography",
            whatsapp = "+5147580000",
            street = "3030 linton",
            country = "CM"
        )
        rest.postForEntity(url, request, Any::class.java)

        // THEN
        val account = dao.findById(100).get()
        assertTrue(account.business)
        assertEquals(request.displayName, account.displayName)
        assertEquals(request.categoryId, account.categoryId)
        assertEquals(request.cityId, account.cityId)
        assertEquals(request.biography, account.biography)
        assertEquals(request.whatsapp, account.whatsapp)
        assertEquals(request.country, account.country)
        assertEquals(request.street, account.street)

        val payload = argumentCaptor<AccountUpdatedPayload>()
        verify(eventStream).publish(eq(EventURN.ACCOUNT_UPDATED.urn), payload.capture())
        assertEquals(100L, payload.firstValue.accountId)
        assertEquals("business", payload.firstValue.attribute)
    }
}
