package com.wutsi.platform.account.service.metric

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream

internal class MetricAggregatorTest {
    companion object {
        const val CSV: String = """
            "time","tenantid","merchantid","productid","value"
            "1586837219285","1","555","100","31"
            "1586837219485","1","666","101","11"
            "1586837219485","1","666","102","5"
            "1586837219485","1","666","103","4"
            "1586837219485","1",,"103","40"
        """
    }

    private val aggregator = MetricAggregator()

    @Test
    fun aggregate() {
        val input = ByteArrayInputStream(CSV.trimIndent().toByteArray())
        val result = aggregator.aggregate(input).sortedBy { it.accountId }

        assertEquals(2, result.size)

        assertEquals("555", result[0].accountId)
        assertEquals(31L, result[0].value)

        assertEquals("666", result[1].accountId)
        assertEquals(20L, result[1].value)
    }
}
