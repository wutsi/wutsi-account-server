package com.wutsi.platform.account.service.metric

data class CsvProductMetric(
    var time: Long? = null,
    var tenantId: String? = null,
    var merchantId: String? = null,
    var productId: String? = null,
    var value: Long = 1
)
