package com.wutsi.platform.account.`delegate`

import com.wutsi.platform.account.dao.PaymentMethodRepository
import com.wutsi.platform.account.dto.ListPaymentMethodResponse
import com.wutsi.platform.account.service.AccountService
import com.wutsi.platform.account.service.SecurityManager
import com.wutsi.platform.core.error.ParameterType.PARAMETER_TYPE_PATH
import org.springframework.stereotype.Service

@Service
public class ListPaymentMethodsDelegate(
    private val dao: PaymentMethodRepository,
    private val accountService: AccountService,
    private val securityManager: SecurityManager
) {
    fun invoke(id: Long): ListPaymentMethodResponse {
        val account = accountService.findById(id, PARAMETER_TYPE_PATH)
        securityManager.checkOwnership(account)

        val paymentMethods = dao.findByAccount(account)
        return ListPaymentMethodResponse(
            paymentMethods = paymentMethods.filter { !it.isDeleted }
                .map { it.toPaymentMethodSummary() }
        )
    }
}
