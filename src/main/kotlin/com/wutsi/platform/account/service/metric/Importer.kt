package com.wutsi.platform.account.service.metric

import com.wutsi.analytics.tracking.entity.MetricType
import java.time.LocalDate

interface Importer {
    fun import(date: LocalDate, type: MetricType): Long
}
