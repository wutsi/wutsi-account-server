package com.wutsi.platform.account.delegate

import com.wutsi.platform.account.dto.Account
import com.wutsi.platform.account.dto.AccountSummary
import com.wutsi.platform.account.dto.Phone
import com.wutsi.platform.account.entity.AccountEntity

fun AccountEntity.toAccount() = Account(
    id = this.id ?: -1,
    displayName = this.displayName,
    pictureUrl = this.pictureUrl,
    created = this.created,
    updated = this.updated,
    status = this.status.shortName,
    language = this.language,
    superUser = this.superUser,
    phone = Phone(
        id = this.phone!!.id ?: -1,
        number = this.phone!!.number,
        country = this.phone!!.country,
        created = this.phone!!.created,
    )
)

fun AccountEntity.toAccountSummary() = AccountSummary(
    id = this.id ?: -1,
    displayName = this.displayName,
    pictureUrl = this.pictureUrl,
    created = this.created,
    updated = this.updated,
    status = this.status.shortName,
    language = this.language,
    superUser = this.superUser,
)
