package com.wutsi.platform.account.endpoint

import com.wutsi.platform.account.dto.ListBusinessHourResponse
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.jdbc.Sql
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/ListBusinessHourController.sql"])
public class ListBusinessHoursControllerTest : AbstractSecuredController() {
    @LocalServerPort
    public val port: Int = 0

    @Test
    fun list() {
        val rest = createResTemplate(subjectId = 100)
        val response = rest.getForEntity(url(100), ListBusinessHourResponse::class.java)

        assertEquals(200, response.statusCodeValue)

        val hours = response.body!!.businessHours
        assertEquals(2, hours.size)

        assertFalse(hours[0].opened)
        assertEquals(0, hours[0].dayOfWeek)
        assertNull(hours[0].openTime)
        assertNull(hours[0].closeTime)

        assertTrue(hours[1].opened)
        assertEquals(1, hours[1].dayOfWeek)
        assertEquals("8:30", hours[1].openTime)
        assertEquals("23:30", hours[1].closeTime)
    }

    private fun url(accountId: Long): String =
        "http://localhost:$port/v1/accounts/$accountId/business-hours"
}
