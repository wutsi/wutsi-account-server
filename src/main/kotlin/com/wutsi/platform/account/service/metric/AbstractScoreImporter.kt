package com.wutsi.platform.account.service.metric

import com.wutsi.analytics.tracking.entity.MetricType
import com.wutsi.platform.core.storage.StorageService
import java.sql.PreparedStatement
import javax.sql.DataSource

abstract class AbstractScoreImporter(
    ds: DataSource,
    storage: StorageService,
) : AbstractMetricImporter(ds, storage) {
    override fun sql(type: MetricType): String =
        """
            UPDATE T_ACCOUNT
                SET score=conversion +
                    CASE total_views
                        WHEN 0 THEN 0
                        ELSE CAST (total_shares+total_chats as DECIMAL)/total_views
                    END
                WHERE id=?
        """

    override fun map(item: CsvAccountMetric, stmt: PreparedStatement) {
        stmt.setLong(1, item.accountId.toLong())
    }
}
