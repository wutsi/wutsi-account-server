package com.wutsi.platform.account.service

import com.google.i18n.phonenumbers.NumberParseException
import com.wutsi.platform.account.dao.PaymentMethodRepository
import com.wutsi.platform.account.dto.AddPaymentMethodRequest
import com.wutsi.platform.account.dto.UpdatePaymentMethodRequest
import com.wutsi.platform.account.entity.AccountEntity
import com.wutsi.platform.account.entity.PaymentMethodEntity
import com.wutsi.platform.account.util.ErrorURN
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.Parameter
import com.wutsi.platform.core.error.ParameterType.PARAMETER_TYPE_PAYLOAD
import com.wutsi.platform.core.error.exception.BadRequestException
import com.wutsi.platform.payment.PaymentMethodProvider
import com.wutsi.platform.payment.PaymentMethodType
import org.springframework.stereotype.Service

@Service
class MobilePaymentService(
    private val dao: PaymentMethodRepository,
    private val paymentService: PaymentMethodService,
    private val phoneService: PhoneService
) : PaymentMethodTypeService {
    override fun validate(request: AddPaymentMethodRequest) {
        if (request.phoneNumber.isNullOrEmpty())
            throw BadRequestException(
                error = Error(
                    code = ErrorURN.PHONE_NUMBER_MISSING.urn,
                    parameter = Parameter(
                        name = "phoneNumber",
                        value = request.phoneNumber,
                        type = PARAMETER_TYPE_PAYLOAD
                    )
                )
            )
    }

    override fun save(account: AccountEntity, request: AddPaymentMethodRequest): PaymentMethodEntity {
        try {

            val phone = phoneService.findOrCreate(request.phoneNumber!!)
            return dao.save(
                PaymentMethodEntity(
                    ownerName = request.ownerName,
                    type = PaymentMethodType.values().find { it.shortName == request.type }!!,
                    provider = PaymentMethodProvider.values().find { it.shortName == request.provider }!!,
                    phone = phone,
                    token = paymentService.hash(phone),
                    account = account
                )
            )
        } catch (ex: NumberParseException) {
            throw BadRequestException(
                error = Error(
                    code = ErrorURN.PHONE_NUMBER_MALFORMED.urn,
                    parameter = Parameter(
                        name = "phoneNumber",
                        type = PARAMETER_TYPE_PAYLOAD,
                        value = request.phoneNumber
                    )
                ),
                ex
            )
        }
    }

    override fun validate(request: UpdatePaymentMethodRequest) {
    }

    override fun save(account: AccountEntity, payment: PaymentMethodEntity, request: UpdatePaymentMethodRequest) {
        payment.ownerName = request.ownerName
        payment.provider = PaymentMethodProvider.values().find { it.shortName == request.provider }!!
        dao.save(payment)
    }
}
