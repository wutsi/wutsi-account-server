package com.wutsi.platform.account.job

import com.wutsi.analytics.tracking.entity.MetricType
import com.wutsi.platform.account.service.metric.ConversionImporter
import com.wutsi.platform.account.service.metric.MetricImporterOverall
import com.wutsi.platform.account.service.metric.ScoreImporter
import com.wutsi.platform.core.cron.AbstractCronJob
import com.wutsi.platform.core.logging.KVLogger
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.Clock
import java.time.LocalDate

@Service
class MetricImporterJob(
    private val importer: MetricImporterOverall,
    private val conversion: ConversionImporter,
    private val score: ScoreImporter,
    private val clock: Clock,
    private val logger: KVLogger
) : AbstractCronJob() {
    override fun getJobName(): String = "metric-importer"

    override fun getToken(): String? = null

    override fun doRun(): Long {
        val date = LocalDate.now(clock).minusDays(1) // Yesterday
        logger.add("date", date)

        return importer.import(date, MetricType.CHAT) +
            importer.import(date, MetricType.SHARE) +
            importer.import(date, MetricType.VIEW) +
            importer.import(date, MetricType.ORDER) +
            conversion.import(date, MetricType.ORDER) + // IMPORTANT: Must be after all metrics
            score.import(date, MetricType.VIEW) // IMPORTANT: Must be the last
    }

    @Scheduled(cron = "\${wutsi.application.jobs.metric-importer.cron}")
    override fun run() {
        super.run()
    }
}
