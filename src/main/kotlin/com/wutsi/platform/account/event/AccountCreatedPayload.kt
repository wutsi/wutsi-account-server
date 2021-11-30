package com.wutsi.platform.account.event

data class AccountCreatedPayload(
    val id: Long = -1,
    val phoneNumber: String = "",
)
