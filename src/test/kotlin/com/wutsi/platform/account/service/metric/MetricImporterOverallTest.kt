package com.wutsi.platform.account.service.metric

import com.wutsi.analytics.tracking.entity.MetricType
import com.wutsi.platform.account.dao.AccountRepository
import com.wutsi.platform.core.storage.StorageService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.jdbc.Sql
import java.io.ByteArrayInputStream
import java.io.File
import java.time.LocalDate

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/MetricImporterOverall.sql"])
internal class MetricImporterOverallTest {
    companion object {
        const val CSV: String = """
            "time","tenantid","merchantid","productid","value"
            "1586837219285","1","100","100","31"
            "1586837219485","1","101","102","11"
        """
    }

    @Value("\${wutsi.platform.storage.local.directory:\${user.home}/wutsi/storage}")
    protected lateinit var storageDirectory: String

    @Autowired
    private lateinit var storage: StorageService

    @Autowired
    private lateinit var importer: MetricImporterOverall

    @Autowired
    private lateinit var dao: AccountRepository

    @BeforeEach
    fun setUp() {
        File(storageDirectory).deleteRecursively()
    }

    @Test
    fun view() {
        store(MetricType.VIEW)

        importer.import(LocalDate.now(), MetricType.VIEW)

        assertTotalViews(100, 1000 + 31)
        assertTotalViews(101, 100 + 11)
    }

    @Test
    fun share() {
        store(MetricType.SHARE)

        importer.import(LocalDate.now(), MetricType.SHARE)

        assertTotalShares(100, 100 + 31)
        assertTotalShares(101, 10 + 11)
    }

    @Test
    fun chat() {
        store(MetricType.CHAT)

        importer.import(LocalDate.now(), MetricType.CHAT)

        assertTotalChats(100, 10 + 31)
        assertTotalChats(101, 1 + 11)
    }

    @Test
    fun order() {
        store(MetricType.ORDER)

        importer.import(LocalDate.now(), MetricType.ORDER)

        assertTotalOrders(100, 31)
        assertTotalOrders(101, 11)
    }

    private fun assertTotalViews(productId: Long, expected: Long) {
        val product = dao.findById(productId)
        assertEquals(expected, product.get().totalViews)
    }

    private fun assertTotalChats(productId: Long, expected: Long) {
        val product = dao.findById(productId)
        assertEquals(expected, product.get().totalChats)
    }

    private fun assertTotalOrders(productId: Long, expected: Long) {
        val product = dao.findById(productId)
        assertEquals(expected, product.get().totalOrders)
    }

    private fun assertTotalShares(productId: Long, expected: Long) {
        val product = dao.findById(productId)
        assertEquals(expected, product.get().totalShares)
    }

    private fun store(type: MetricType) {
        val path = "aggregates/overall/" + type.name.lowercase() + ".csv"
        storage.store(path, ByteArrayInputStream(CSV.trimIndent().toByteArray()), "application/csv")
    }
}
