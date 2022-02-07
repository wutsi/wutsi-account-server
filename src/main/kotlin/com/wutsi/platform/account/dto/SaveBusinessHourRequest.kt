package com.wutsi.platform.account.dto

import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.Size
import kotlin.Boolean
import kotlin.Int
import kotlin.String

public data class SaveBusinessHourRequest(
    @get:Min(0)
    @get:Max(6)
    public val dayOfWeek: Int = 0,
    public val opened: Boolean = false,
    @get:Size(max = 5)
    public val openTime: String? = null,
    @get:Size(max = 5)
    public val closeTime: String? = null
)
