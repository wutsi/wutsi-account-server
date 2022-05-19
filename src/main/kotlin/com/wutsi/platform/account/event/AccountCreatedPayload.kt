package com.wutsi.platform.account.event

data class AccountCreatedPayload(
    val accountId: Long = -1,
    val phoneNumber: String = "",
)
