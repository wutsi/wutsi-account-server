package com.wutsi.platform.account.util

import com.wutsi.platform.core.util.URN

enum class ErrorURN(val urn: String) {
    PHONE_NUMBER_MALFORMED(URN.of("error", "account", "phone-number-malformed").value),
    PHONE_NUMBER_ALREADY_ASSIGNED(URN.of("error", "account", "phone-number-already-assigned").value),
    ACCOUNT_NOT_FOUND(URN.of("error", "account", "account-not-found").value),
    ACCOUNT_DELETED(URN.of("error", "account", "account-deleted").value),
    PICTURE_URL_MALFORMED(URN.of("error", "account", "picture-url-malformed").value),
    ATTRIBUTE_INVALID(URN.of("error", "account", "attribute-invalid").value),
    PASSWORD_MISMATCH(URN.of("error", "account", "password-mismatch").value)
}
