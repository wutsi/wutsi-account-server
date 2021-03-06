package com.wutsi.platform.account.event

enum class EventURN(val urn: String) {
    ACCOUNT_CREATED("urn:wutsi:error:account:account-created"),
    ACCOUNT_UPDATED("urn:wutsi:error:account:account-updated"),
    ACCOUNT_DELETED("urn:wutsi:error:account:account-deleted"),
}
