package com.wutsi.platform.account.dto

import org.springframework.format.annotation.DateTimeFormat
import java.time.OffsetDateTime

data class PaymentMethod(
    val token: String = "",
    val type: String = "",
    val provider: String = "",
    val ownerName: String = "",
    val maskedNumber: String = "",
    val phone: Phone? = null,
    @get:DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ") val created: OffsetDateTime = OffsetDateTime.now(),
    @get:DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ") val updated: OffsetDateTime = OffsetDateTime.now(),
)
