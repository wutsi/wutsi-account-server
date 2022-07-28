package com.wutsi.platform.account.dto

import kotlin.Boolean
import kotlin.Int
import kotlin.String

public data class BusinessHour(
    public val dayOfWeek: Int = 0,
    public val opened: Boolean = false,
    public val openTime: String? = null,
    public val closeTime: String? = null,
)
