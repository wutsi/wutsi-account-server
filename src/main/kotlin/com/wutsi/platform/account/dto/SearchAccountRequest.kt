package com.wutsi.platform.account.dto

import kotlin.Int
import kotlin.String
import kotlin.collections.List

public data class SearchAccountRequest(
    public val ids: List<Long> = emptyList(),
    public val phoneNumber: String? = null,
    public val limit: Int = 30,
    public val offset: Int = 0
)
