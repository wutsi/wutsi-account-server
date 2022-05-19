package com.wutsi.platform.account.service.metric

class CsvProductMetricMapper : AbstractCsvMapper<CsvProductMetric>() {
    override fun map(col: Array<String>): CsvProductMetric =
        CsvProductMetric(
            time = getLong("time", col),
            tenantId = getString("tenantid", col),
            merchantId = getString("merchantid", col),
            productId = getString("productid", col),
            value = getLong("value", col) ?: 0
        )
}
