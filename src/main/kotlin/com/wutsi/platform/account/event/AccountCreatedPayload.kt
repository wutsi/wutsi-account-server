package com.wutsi.platform.account.event

data class AccountCreatedPayload(
    val accountId: Long = -1,
    val tenantId: Long? = null,
    val phoneNumber: String = "",
)
