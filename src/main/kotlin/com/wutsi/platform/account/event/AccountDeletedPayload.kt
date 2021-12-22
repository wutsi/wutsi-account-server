package com.wutsi.platform.account.event

data class AccountDeletedPayload(
    val accountId: Long = -1,
    val tenantId: Long = -1,
)
