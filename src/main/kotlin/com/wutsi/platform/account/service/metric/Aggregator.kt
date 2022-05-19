package com.wutsi.platform.account.service.metric

import com.opencsv.exceptions.CsvException
import java.io.IOException
import java.io.InputStream

interface Aggregator {
    @Throws(IOException::class, CsvException::class)
    fun aggregate(input: InputStream): List<CsvAccountMetric>
}
