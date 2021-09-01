package com.wutsi.platform.account.entity

enum class AccountStatus(val shortName: String) {
    ACCOUNT_STATUS_INVALID("invalid"),
    ACCOUNT_STATUS_ACTIVE("active"),
    ACCOUNT_STATUS_SUSPENDED("suspended")
}
