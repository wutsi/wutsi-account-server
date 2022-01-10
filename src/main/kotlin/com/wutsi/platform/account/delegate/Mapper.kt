package com.wutsi.platform.account.delegate

import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL
import com.wutsi.platform.account.dto.Account
import com.wutsi.platform.account.dto.AccountSummary
import com.wutsi.platform.account.dto.PaymentMethod
import com.wutsi.platform.account.dto.PaymentMethodSummary
import com.wutsi.platform.account.dto.Phone
import com.wutsi.platform.account.entity.AccountEntity
import com.wutsi.platform.account.entity.PaymentMethodEntity
import com.wutsi.platform.account.entity.PhoneEntity
import com.wutsi.platform.account.service.ImageKit
import com.wutsi.platform.account.service.SecurityManager

fun AccountEntity.toAccount(securityManager: SecurityManager, imageKit: ImageKit) = Account(
    id = this.id ?: -1,
    displayName = this.displayName,
    pictureUrl = this.pictureUrl?.let { imageKit.transform(it, 256, 256, true) },
    created = this.created,
    updated = this.updated,
    status = this.status.name,
    language = this.language,
    country = this.country,
    superUser = this.isSuperUser,
    transferSecured = this.isTransferSecured,
    business = this.business,
    retail = this.retail,
    categoryId = this.categoryId,
    biography = this.biography,
    website = this.website,
    whatsapp = this.whatsapp,
    phone = if (securityManager.canAccessPhone(this))
        this.phone?.toPhone()
    else
        null,
)

fun PhoneEntity.toPhone() = Phone(
    id = this.id ?: -1,
    number = this.number,
    country = this.country,
    created = this.created,
)

fun AccountEntity.toAccountSummary(imageKit: ImageKit) = AccountSummary(
    id = this.id ?: -1,
    displayName = this.displayName,
    pictureUrl = this.pictureUrl?.let { imageKit.transform(it, 256, 256, true) },
    country = this.country,
    created = this.created,
    updated = this.updated,
    status = this.status.name,
    language = this.language,
    superUser = this.isSuperUser,
    business = this.business,
    retail = this.retail,
)

fun PaymentMethodEntity.toPaymentMethod(securityManager: SecurityManager) = PaymentMethod(
    token = this.token,
    created = this.created,
    updated = this.updated,
    type = this.type.name,
    provider = this.provider.name,
    maskedNumber = toMaskedNumber(),
    ownerName = this.ownerName,
    phone = if (securityManager.canAccessPaymentMethodDetails(this))
        this.phone?.toPhone()
    else
        null
)

fun PaymentMethodEntity.toPaymentMethodSummary(securityManager: SecurityManager) = PaymentMethodSummary(
    token = this.token,
    created = this.created,
    updated = this.updated,
    type = this.type.name,
    provider = this.provider.name,
    maskedNumber = toMaskedNumber(),
    ownerName = this.ownerName,
    phone = if (securityManager.canAccessPaymentMethodDetails(this))
        this.phone?.toPhone()
    else
        null
)

fun PaymentMethodEntity.toMaskedNumber(): String {
    if (this.phone == null)
        return ""

    try {
        val util = PhoneNumberUtil.getInstance()
        val phoneNumber = util.parse(this.phone!!.number, "")
        val number = util.format(phoneNumber, INTERNATIONAL)
        val i = number.indexOf(' ')
        return number.substring(0, i + 5) +
            "..." +
            number.substring(i)
                .filter { !it.isWhitespace() }
                .takeLast(2)
    } catch (ex: Exception) {
        return "..." + this.phone!!.number.takeLast(4)
    }
}
