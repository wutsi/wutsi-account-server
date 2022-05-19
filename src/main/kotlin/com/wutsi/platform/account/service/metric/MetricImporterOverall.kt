package com.wutsi.platform.account.service.metric

import com.wutsi.analytics.tracking.entity.MetricType
import com.wutsi.platform.core.storage.StorageService
import org.springframework.stereotype.Service
import java.net.URL
import java.sql.PreparedStatement
import java.time.LocalDate
import javax.sql.DataSource

@Service
class MetricImporterOverall(
    ds: DataSource,
    storage: StorageService,
) : AbstractMetricImporter(ds, storage) {
    override fun sql(type: MetricType): String {
        val column = "total_${type.name.lowercase()}s"
        return """
            UPDATE T_ACCOUNT
                SET $column=?
                WHERE id=?
        """
    }

    override fun map(item: CsvAccountMetric, stmt: PreparedStatement) {
        stmt.setLong(1, item.value)
        stmt.setLong(2, item.accountId.toLong())
    }

    override fun toURL(date: LocalDate, type: MetricType): URL =
        storage.toURL(
            "aggregates/overall/" + type.name.lowercase() + ".csv"
        )
}
