package com.wutsi.platform.account.`delegate`

import com.wutsi.platform.account.dto.AddPaymentMethodRequest
import com.wutsi.platform.account.dto.AddPaymentMethodResponse
import com.wutsi.platform.account.service.AccountService
import com.wutsi.platform.account.service.MobilePaymentService
import com.wutsi.platform.account.service.PaymentMethodTypeService
import com.wutsi.platform.account.service.SecurityManager
import com.wutsi.platform.account.util.ErrorURN
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.Parameter
import com.wutsi.platform.core.error.ParameterType.PARAMETER_TYPE_PATH
import com.wutsi.platform.core.error.ParameterType.PARAMETER_TYPE_PAYLOAD
import com.wutsi.platform.core.error.exception.BadRequestException
import com.wutsi.platform.core.error.exception.ConflictException
import com.wutsi.platform.payment.PaymentMethodType
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service

@Service
public class AddPaymentMethodDelegate(
    private val accountService: AccountService,
    private val mobile: MobilePaymentService,
    private val securityManager: SecurityManager
) {
    fun invoke(id: Long, request: AddPaymentMethodRequest): AddPaymentMethodResponse {
        val account = accountService.findById(id, PARAMETER_TYPE_PATH)
        securityManager.checkOwnership(account)

        val service: PaymentMethodTypeService = when (request.type.toUpperCase()) {
            PaymentMethodType.MOBILE.name -> mobile
            else -> throw BadRequestException(
                error = Error(
                    code = ErrorURN.PAYMENT_METHOD_INVALID_TYPE.urn,
                    parameter = Parameter(
                        name = "type",
                        value = request.type,
                        type = PARAMETER_TYPE_PAYLOAD
                    )
                )
            )
        }

        try {
            service.validate(request)
            val payment = service.save(account, request)
            return AddPaymentMethodResponse(
                token = payment.token
            )
        } catch (ex: DataIntegrityViolationException) {
            throw ConflictException(
                error = Error(
                    code = ErrorURN.PAYMENT_METHOD_OWNERSHIP.urn
                ),
                ex
            )
        }
    }
}
