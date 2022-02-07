package com.wutsi.platform.account.endpoint

import com.wutsi.platform.account.dao.BusinessHourRepository
import com.wutsi.platform.account.dto.SaveBusinessHourRequest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.jdbc.Sql
import org.springframework.web.client.RestTemplate
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/SaveBusinessHourController.sql"])
public class SaveBusinessHourControllerTest : AbstractSecuredController() {
    @LocalServerPort
    public val port: Int = 0

    @Autowired
    private lateinit var dao: BusinessHourRepository

    private lateinit var rest: RestTemplate

    @Test
    fun create() {
        rest = createResTemplate(subjectId = 100)
        val request = SaveBusinessHourRequest(
            dayOfWeek = 2,
            opened = true,
            openTime = "10:30",
            closeTime = "8:30"
        )
        val response = rest.postForEntity(url(100), request, Any::class.java)

        assertEquals(200, response.statusCodeValue)

        val hours = dao.findByAccountId(100L)
        assertEquals(1, hours.size)
        assertEquals(request.dayOfWeek, hours[0].dayOfWeek)
        assertEquals(request.opened, hours[0].opened)
        assertEquals(request.openTime, hours[0].openTime)
        assertEquals(request.closeTime, hours[0].closeTime)
    }

    @Test
    fun update() {
        rest = createResTemplate(subjectId = 101)
        val request = SaveBusinessHourRequest(
            dayOfWeek = 0,
            opened = true,
            openTime = "10:30",
            closeTime = "8:30"
        )
        val response = rest.postForEntity(url(101), request, Any::class.java)

        assertEquals(200, response.statusCodeValue)

        val hours = dao.findByAccountId(101L)
        assertEquals(1, hours.size)
        assertEquals(request.dayOfWeek, hours[0].dayOfWeek)
        assertEquals(request.opened, hours[0].opened)
        assertEquals(request.openTime, hours[0].openTime)
        assertEquals(request.closeTime, hours[0].closeTime)
    }

    private fun url(accountId: Long): String =
        "http://localhost:$port/v1/accounts/$accountId/business-hours"
}
