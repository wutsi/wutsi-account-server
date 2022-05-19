package com.wutsi.platform.account.service.metric

import com.opencsv.CSVReader
import java.io.InputStream
import java.io.InputStreamReader

class MetricAggregator : Aggregator {
    override fun aggregate(input: InputStream): List<CsvAccountMetric> {
        val reader = CSVReader(InputStreamReader(input))
        reader.use {
            return aggregate(reader)
        }
    }

    private fun aggregate(csv: CSVReader): List<CsvAccountMetric> {
        val mapper = CsvProductMetricMapper()
        val result = mutableMapOf<String, Long>()
        var row = 0
        val iterator: Iterator<Array<String>> = csv.iterator()
        while (iterator.hasNext()) {
            val data = iterator.next()
            if (row == 0) {
                mapper.column(data)
            } else {
                // Load the data
                val item = mapper.map(data)
                if (item.merchantId.isNullOrEmpty())
                    continue

                // Map
                if (result.containsKey(item.merchantId)) {
                    result[item.merchantId!!] = result[item.merchantId!!]!! + item.value
                } else {
                    result[item.merchantId!!] = item.value
                }
            }
            row++
        }

        return result.map { CsvAccountMetric(it.key, it.value) }
    }
}
