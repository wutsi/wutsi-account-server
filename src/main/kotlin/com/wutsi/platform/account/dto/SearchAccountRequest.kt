package com.wutsi.platform.account.dto

data class SearchAccountRequest(
    val ids: List<Long> = emptyList(),
    val phoneNumber: String? = null,
    val business: Boolean? = null,
    val hasStore: Boolean? = null,
    val limit: Int = 30,
    val offset: Int = 0,
    val sortBy: String? = null
)
