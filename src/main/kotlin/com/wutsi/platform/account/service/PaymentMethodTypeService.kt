package com.wutsi.platform.account.service

import com.wutsi.platform.account.dto.AddPaymentMethodRequest
import com.wutsi.platform.account.dto.UpdatePaymentMethodRequest
import com.wutsi.platform.account.entity.AccountEntity
import com.wutsi.platform.account.entity.PaymentMethodEntity

interface PaymentMethodTypeService {
    fun validate(request: AddPaymentMethodRequest)
    fun save(account: AccountEntity, request: AddPaymentMethodRequest): PaymentMethodEntity

    fun validate(request: UpdatePaymentMethodRequest)
    fun save(account: AccountEntity, payment: PaymentMethodEntity, request: UpdatePaymentMethodRequest)
}
