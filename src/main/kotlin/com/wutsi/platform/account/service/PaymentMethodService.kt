package com.wutsi.platform.account.service

import com.wutsi.platform.account.dao.PaymentMethodRepository
import com.wutsi.platform.account.entity.PaymentMethodEntity
import com.wutsi.platform.account.entity.PhoneEntity
import com.wutsi.platform.account.error.ErrorURN
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.ParameterType
import com.wutsi.platform.core.error.ParameterType.PARAMETER_TYPE_PATH
import com.wutsi.platform.core.error.exception.ForbiddenException
import com.wutsi.platform.core.error.exception.NotFoundException
import org.apache.commons.codec.digest.DigestUtils
import org.springframework.stereotype.Service
import java.time.OffsetDateTime
import java.util.UUID

@Service
public class PaymentMethodService(
    private val dao: PaymentMethodRepository
) {
    fun findByToken(
        accountId: Long,
        token: String,
        parameterType: ParameterType = PARAMETER_TYPE_PATH
    ): PaymentMethodEntity {
        val payment = dao.findByToken(token)
            .orElseThrow {
                NotFoundException(
                    error = Error(
                        code = ErrorURN.PAYMENT_METHOD_NOT_FOUND.urn
                    )
                )
            }

        if (payment.account.id != accountId) {
            throw ForbiddenException(
                error = Error(
                    code = ErrorURN.ILLEGAL_PAYMENT_METHOD_ACCESS.urn
                )
            )
        }

        if (payment.isDeleted)
            throw NotFoundException(
                error = Error(
                    code = ErrorURN.PAYMENT_METHOD_DELETED.urn
                )
            )

        return payment
    }

    fun delete(payment: PaymentMethodEntity) {
        payment.deleted = OffsetDateTime.now()
        payment.isDeleted = true
        payment.token = UUID.randomUUID().toString()
        dao.save(payment)
    }

    fun hash(phone: PhoneEntity): String =
        DigestUtils.md5Hex(phone.number)
}
