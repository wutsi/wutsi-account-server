package com.wutsi.platform.account.error

enum class ErrorURN(val urn: String) {
    ACCOUNT_NOT_FOUND("urn:wutsi:error:account:account-not-found"),
    ACCOUNT_DELETED("urn:wutsi:error:account:account-deleted"),
    ATTRIBUTE_INVALID("urn:wutsi:error:account:invalid-attribute"),
    PASSWORD_MISMATCH("urn:wutsi:error:account:password-mismatch"),
    PAYMENT_METHOD_NOT_FOUND("urn:wutsi:error:account:payment-method-not-found"),
    PAYMENT_METHOD_DELETED("urn:wutsi:error:account:payment-method-deleted"),
    PAYMENT_METHOD_INVALID_TYPE("urn:wutsi:error:account:invalid-payment-type"),
    PAYMENT_METHOD_OWNERSHIP("urn:wutsi:error:account:payment-method-ownership"),
    PICTURE_URL_MALFORMED("urn:wutsi:error:account:picture-url-malformed"),
    PHONE_NUMBER_MALFORMED("urn:wutsi:error:account:phone-number-malformed"),
    PHONE_NUMBER_MISSING("urn:wutsi:error:account:phone-number-missing"),
    PHONE_NUMBER_ALREADY_ASSIGNED("urn:wutsi:error:account:phone-number-already-assigned"),
    PHONE_NUMBER_NOT_FOUND("urn:wutsi:error:account:phone-number-not-found"),
}
