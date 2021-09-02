package com.wutsi.platform.account.util

import com.wutsi.platform.core.util.URN

enum class ErrorURN(val urn: String) {
    ACCOUNT_NOT_FOUND(URN.of("error", "account", "account-not-found").value),
    ACCOUNT_DELETED(URN.of("error", "account", "account-deleted").value),
    ATTRIBUTE_INVALID(URN.of("error", "account", "invalid-attribute").value),
    PASSWORD_MISMATCH(URN.of("error", "account", "password-mismatch").value),
    PAYMENT_METHOD_NOT_FOUND(URN.of("error", "account", "payment-method-not-found").value),
    PAYMENT_METHOD_DELETED(URN.of("error", "account", "payment-method-deleted").value),
    PAYMENT_METHOD_INVALID_TYPE(URN.of("error", "account", "invalid-payment-type").value),
    PAYMENT_METHOD_OWNERSHIP(URN.of("error", "account", "payment-method-ownership").value),
    PICTURE_URL_MALFORMED(URN.of("error", "account", "picture-url-malformed").value),
    PHONE_NUMBER_MALFORMED(URN.of("error", "account", "phone-number-malformed").value),
    PHONE_NUMBER_MISSING(URN.of("error", "account", "phone-number-missing").value),
    PHONE_NUMBER_ALREADY_ASSIGNED(URN.of("error", "account", "phone-number-already-assigned").value),
    PHONE_NUMBER_NOT_FOUND(URN.of("error", "account", "phone-number-not-found").value),
}
