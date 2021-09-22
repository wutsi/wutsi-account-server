package com.wutsi.platform.account.`delegate`

import com.wutsi.platform.account.dto.GetPaymentMethodResponse
import com.wutsi.platform.account.service.PaymentMethodService
import com.wutsi.platform.account.service.SecurityManager
import com.wutsi.platform.core.error.ParameterType.PARAMETER_TYPE_PATH
import org.springframework.stereotype.Service

@Service
public class GetPaymentMethodDelegate(
    private val paymentService: PaymentMethodService,
    private val securityManager: SecurityManager
) {
    fun invoke(id: Long, token: String): GetPaymentMethodResponse {
        val payment = paymentService.findByToken(id, token, PARAMETER_TYPE_PATH)
        return GetPaymentMethodResponse(
            paymentMethod = payment.toPaymentMethod(securityManager)
        )
    }
}
