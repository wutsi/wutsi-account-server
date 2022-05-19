package com.wutsi.platform.account.service.metric

import com.wutsi.analytics.tracking.entity.MetricType
import com.wutsi.platform.core.logging.DefaultKVLogger
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.storage.StorageService
import org.slf4j.LoggerFactory
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.net.URL
import java.sql.PreparedStatement
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.sql.DataSource

abstract class AbstractMetricImporter(
    protected val ds: DataSource,
    protected val storage: StorageService,
) : Importer {
    protected abstract fun sql(type: MetricType): String
    protected abstract fun map(item: CsvAccountMetric, stmt: PreparedStatement)

    override fun import(date: LocalDate, type: MetricType): Long {
        val cnn = ds.connection
        val logger = DefaultKVLogger()
        logger.add("importer", javaClass.simpleName)
        logger.add("date", date)
        logger.add("type", type)

        try {
            val stmt = cnn.prepareStatement(sql(type))
            try {
                val imported = import(date, type, stmt, logger)

                logger.add("count", imported)
                logger.add("success", true)
                return imported
            } finally {
                stmt.close()
            }
        } catch (ex: Exception) {
            logger.add("success", false)
            logger.setException(ex)
            return 0
        } finally {
            logger.log()
            cnn.close()
        }
    }

    private fun import(date: LocalDate, type: MetricType, stmt: PreparedStatement, logger: KVLogger): Long {
        val metrics = load(date, type, logger)
        return import(metrics, stmt)
    }

    private fun load(date: LocalDate, type: MetricType, logger: KVLogger): List<CsvAccountMetric> {
        // Get the data
        val url = toURL(date, type)
        val output = ByteArrayOutputStream()
        logger.add("url", url)
        storage.get(url, output)

        // Aggregate the data
        val metrics = MetricAggregator().aggregate(ByteArrayInputStream(output.toByteArray()))
        logger.add("count", metrics.size)
        return metrics
    }

    private fun import(metrics: List<CsvAccountMetric>, stmt: PreparedStatement): Long {
        var imported = 0L
        metrics.forEach {
            try {
                map(it, stmt)
                stmt.executeUpdate()
                imported++
            } catch (ex: Exception) {
                LoggerFactory.getLogger(javaClass).warn("Unable to import $it", ex)
            }
        }
        return imported
    }

    protected open fun toURL(date: LocalDate, type: MetricType): URL =
        storage.toURL(
            "aggregates/daily/" +
                date.format(DateTimeFormatter.ofPattern("yyyy/MM/dd/")) +
                type.name.lowercase() + ".csv"
        )
}
