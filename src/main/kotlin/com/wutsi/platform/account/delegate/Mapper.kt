package com.wutsi.platform.account.delegate

import com.wutsi.platform.account.dto.Account
import com.wutsi.platform.account.dto.AccountSummary
import com.wutsi.platform.account.dto.PaymentMethod
import com.wutsi.platform.account.dto.PaymentMethodSummary
import com.wutsi.platform.account.dto.Phone
import com.wutsi.platform.account.entity.AccountEntity
import com.wutsi.platform.account.entity.PaymentMethodEntity
import com.wutsi.platform.account.entity.PhoneEntity
import com.wutsi.platform.account.service.SecurityManager

fun AccountEntity.toAccount(securityManager: SecurityManager) = Account(
    id = this.id ?: -1,
    displayName = this.displayName,
    pictureUrl = this.pictureUrl,
    created = this.created,
    updated = this.updated,
    status = this.status.shortName,
    language = this.language,
    superUser = this.isSuperUser,
    phone = if (securityManager.canAccessPhone(this))
        this.phone?.toPhone()
    else
        null
)

fun PhoneEntity.toPhone() = Phone(
    id = this.id ?: -1,
    number = this.number,
    country = this.country,
    created = this.created,
)

fun AccountEntity.toAccountSummary() = AccountSummary(
    id = this.id ?: -1,
    displayName = this.displayName,
    pictureUrl = this.pictureUrl,
    created = this.created,
    updated = this.updated,
    status = this.status.shortName,
    language = this.language,
    superUser = this.isSuperUser
)

fun PaymentMethodEntity.toPaymentMethod(securityManager: SecurityManager) = PaymentMethod(
    token = this.token,
    created = this.created,
    updated = this.updated,
    type = this.type.shortName,
    provider = this.provider.shortName,
    maskedNumber = toMaskedNumber(),
    ownerName = this.ownerName,
    phone = if (securityManager.canAccessPaymentMethodDetails(this))
        this.phone?.toPhone()
    else
        null,
)

fun PaymentMethodEntity.toPaymentMethodSummary() = PaymentMethodSummary(
    token = this.token,
    created = this.created,
    updated = this.updated,
    type = this.type.shortName,
    provider = this.provider.shortName,
    maskedNumber = toMaskedNumber(),
    ownerName = this.ownerName
)

fun PaymentMethodEntity.toMaskedNumber(): String =
    if (this.phone != null)
        "..." + this.phone!!.number.takeLast(4)
    else
        ""
