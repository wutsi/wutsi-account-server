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
@Sql(value = ["/db/clean.sql", "/db/ScoreImporter.sql"])
internal class ScoreImporterOverallTest {
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
    private lateinit var importer: ScoreImporterOverall

    @Autowired
    private lateinit var dao: AccountRepository

    private val date = LocalDate.of(2020, 4, 14)

    @BeforeEach
    fun setUp() {
        File(storageDirectory).deleteRecursively()
    }

    @Test
    fun run() {
        store()

        importer.import(date, MetricType.VIEW)

        assertScore(100, 0.12)
        assertScore(101, 0.21)
    }

    private fun assertScore(productId: Long, expected: Double) {
        val product = dao.findById(productId)

        assertEquals(expected, product.get().score)
    }

    private fun store() {
        val path = "aggregates/overall/view.csv"
        storage.store(
            path,
            ByteArrayInputStream(CSV.trimIndent().toByteArray()),
            "application/csv"
        )
    }
}
