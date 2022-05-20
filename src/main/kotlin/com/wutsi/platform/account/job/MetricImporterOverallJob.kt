package com.wutsi.platform.account.job

import com.wutsi.analytics.tracking.entity.MetricType
import com.wutsi.platform.account.service.metric.ConversionImporterOverall
import com.wutsi.platform.account.service.metric.MetricImporterOverall
import com.wutsi.platform.account.service.metric.ScoreImporterOverall
import com.wutsi.platform.core.cron.AbstractCronJob
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class MetricImporterOverallJob(
    private val importer: MetricImporterOverall,
    private val conversion: ConversionImporterOverall,
    private val score: ScoreImporterOverall,
    @Value("\${wutsi.application.jobs.metric-importer-overall.enabled}") private val enabled: Boolean
) : AbstractCronJob() {
    override fun getJobName(): String = "metric-importer-overall"

    override fun getToken(): String? = null

    override fun doRun(): Long {
        val date = LocalDate.now()
        return importer.import(date, MetricType.CHAT) +
            importer.import(date, MetricType.SHARE) +
            importer.import(date, MetricType.VIEW) +
            importer.import(date, MetricType.ORDER) +
            importer.import(date, MetricType.SALE) +
            conversion.import(date, MetricType.ORDER) + // IMPORTANT: Must be after all metrics
            score.import(date, MetricType.VIEW) // IMPORTANT: Must be the last
    }

    @Scheduled(cron = "\${wutsi.application.jobs.metric-importer-overall.cron}")
    override fun run() {
        if (enabled)
            super.run()
    }
}
