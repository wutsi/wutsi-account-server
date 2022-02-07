package com.wutsi.platform.account.dto

import kotlin.collections.List

public data class ListBusinessHourResponse(
    public val businessHours: List<BusinessHour> = emptyList()
)
