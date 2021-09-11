package com.wutsi.platform.account.`delegate`

import com.wutsi.platform.account.dto.UpdatePaymentMethodRequest
import com.wutsi.platform.account.service.AccountService
import com.wutsi.platform.account.service.MobilePaymentService
import com.wutsi.platform.account.service.PaymentMethodService
import com.wutsi.platform.account.service.PaymentMethodTypeService
import com.wutsi.platform.account.service.SecurityManager
import com.wutsi.platform.core.error.ParameterType.PARAMETER_TYPE_PATH
import com.wutsi.platform.payment.PaymentMethodType
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
public class UpdatePaymentMethodDelegate(
    private val accountService: AccountService,
    private val paymentService: PaymentMethodService,
    private val mobile: MobilePaymentService,
    private val securityManager: SecurityManager
) {
    @Transactional
    public fun invoke(
        id: Long,
        token: String,
        request: UpdatePaymentMethodRequest
    ) {
        val account = accountService.findById(id, PARAMETER_TYPE_PATH)
        securityManager.checkOwnership(account)

        val payment = paymentService.findByToken(token, PARAMETER_TYPE_PATH)

        val service: PaymentMethodTypeService = when (payment.type) {
            PaymentMethodType.MOBILE_PAYMENT -> mobile
            else -> throw IllegalStateException("Unsupported payment type: ${payment.type}")
        }

        service.validate(request)
        service.save(account, payment, request)
    }
}
