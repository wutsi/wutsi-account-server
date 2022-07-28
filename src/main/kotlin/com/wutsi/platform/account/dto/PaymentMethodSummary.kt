package com.wutsi.platform.account.dto

import org.springframework.format.`annotation`.DateTimeFormat
import java.time.OffsetDateTime
import kotlin.String

public data class PaymentMethodSummary(
    public val token: String = "",
    public val type: String = "",
    public val provider: String = "",
    public val ownerName: String = "",
    public val maskedNumber: String = "",
    public val phone: Phone = Phone(),
    @get:DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    public val created: OffsetDateTime = OffsetDateTime.now(),
    @get:DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    public val updated: OffsetDateTime = OffsetDateTime.now(),
)
