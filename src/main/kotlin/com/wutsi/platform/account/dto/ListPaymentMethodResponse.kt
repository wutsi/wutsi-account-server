package com.wutsi.platform.account.dto

import kotlin.collections.List

public data class ListPaymentMethodResponse(
    public val paymentMethods: List<PaymentMethodSummary> = emptyList()
)
