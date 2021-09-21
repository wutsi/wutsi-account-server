package com.wutsi.platform.account.`delegate`

import com.wutsi.platform.account.service.AccountService
import com.wutsi.platform.account.service.PaymentMethodService
import com.wutsi.platform.account.service.SecurityManager
import com.wutsi.platform.core.error.ParameterType.PARAMETER_TYPE_PATH
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
public class DeletePaymentMethodDelegate(
    private val accountService: AccountService,
    private val paymentService: PaymentMethodService,
    private val securityManager: SecurityManager
) {
    @Transactional
    public fun invoke(id: Long, token: String) {
        val account = accountService.findById(id, PARAMETER_TYPE_PATH)
        securityManager.checkOwnership(account)

        val payment = paymentService.findByToken(id, token, PARAMETER_TYPE_PATH)
        paymentService.delete(payment)
    }
}
