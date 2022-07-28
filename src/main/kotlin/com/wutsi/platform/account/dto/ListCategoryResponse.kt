package com.wutsi.platform.account.dto

import kotlin.collections.List

public data class ListCategoryResponse(
    public val categories: List<Category> = emptyList(),
)
