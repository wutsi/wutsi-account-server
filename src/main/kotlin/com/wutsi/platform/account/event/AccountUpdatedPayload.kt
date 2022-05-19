package com.wutsi.platform.account.event

data class AccountUpdatedPayload(
    val accountId: Long = -1,
    val attribute: String? = null,
)
