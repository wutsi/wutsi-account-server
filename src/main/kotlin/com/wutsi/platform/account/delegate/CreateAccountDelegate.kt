package com.wutsi.platform.account.`delegate`

import com.google.i18n.phonenumbers.NumberParseException
import com.wutsi.platform.account.dao.AccountRepository
import com.wutsi.platform.account.dto.AddPaymentMethodRequest
import com.wutsi.platform.account.dto.CreateAccountRequest
import com.wutsi.platform.account.dto.CreateAccountResponse
import com.wutsi.platform.account.dto.SavePasswordRequest
import com.wutsi.platform.account.entity.AccountEntity
import com.wutsi.platform.account.entity.AccountStatus.ACTIVE
import com.wutsi.platform.account.entity.PasswordEntity
import com.wutsi.platform.account.entity.PaymentMethodEntity
import com.wutsi.platform.account.entity.PhoneEntity
import com.wutsi.platform.account.error.ErrorURN
import com.wutsi.platform.account.event.AccountCreatedPayload
import com.wutsi.platform.account.event.EventURN
import com.wutsi.platform.account.service.MobilePaymentService
import com.wutsi.platform.account.service.PasswordService
import com.wutsi.platform.account.service.PhoneService
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.Parameter
import com.wutsi.platform.core.error.ParameterType.PARAMETER_TYPE_PAYLOAD
import com.wutsi.platform.core.error.exception.BadRequestException
import com.wutsi.platform.core.error.exception.ConflictException
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.stream.EventStream
import com.wutsi.platform.core.tracing.TracingContext
import com.wutsi.platform.payment.PaymentMethodProvider
import com.wutsi.platform.payment.PaymentMethodType
import com.wutsi.platform.tenant.WutsiTenantApi
import com.wutsi.platform.tenant.dto.MobileCarrier
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
public class CreateAccountDelegate(
    private val phoneService: PhoneService,
    private val passwordService: PasswordService,
    private val mobilePaymentService: MobilePaymentService,
    private val dao: AccountRepository,
    private val stream: EventStream,
    private val tracingContext: TracingContext,
    private val tenantApi: WutsiTenantApi,
    private val logger: KVLogger,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(CreateAccountRequest::class.java)
    }

    @Transactional
    public fun invoke(request: CreateAccountRequest): CreateAccountResponse {
        try {
            val phone = phoneService.findOrCreate(request.phoneNumber)
            if (dao.findByPhone(phone).isPresent) {
                throw ConflictException(
                    error = Error(
                        code = ErrorURN.PHONE_NUMBER_ALREADY_ASSIGNED.urn,
                        parameter = Parameter(
                            name = "phoneNumber",
                            type = PARAMETER_TYPE_PAYLOAD,
                            value = request.phoneNumber
                        )
                    )
                )
            }

            val account = createAccount(phone, request)
            logger.add("account_id", account.id)

            val password = setPassword(account, request)
            logger.add("password_id", password?.id)

            val paymentMethod = addPaymentMethod(account, phone, request)
            logger.add("payment_method_id", paymentMethod?.id)

            publishEvent(account, phone)
            return CreateAccountResponse(
                id = account.id ?: -1
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

    private fun createAccount(phone: PhoneEntity, request: CreateAccountRequest): AccountEntity =
        dao.save(
            AccountEntity(
                phone = phone,
                status = ACTIVE,
                language = request.language,
                country = request.country,
                displayName = request.displayName,
                pictureUrl = request.pictureUrl,
            )
        )

    private fun setPassword(account: AccountEntity, request: CreateAccountRequest): PasswordEntity? {
        if (!request.password.isNullOrEmpty()) {
            return passwordService.set(
                account,
                SavePasswordRequest(
                    password = request.password
                )
            )
        }
        return null
    }

    private fun addPaymentMethod(
        account: AccountEntity,
        phone: PhoneEntity,
        request: CreateAccountRequest
    ): PaymentMethodEntity? {
        if (!request.addPaymentMethod)
            return null

        val tenantId = tracingContext.tenantId()?.toLong()
            ?: return null

        val tenant = tenantApi.getTenant(tenantId).tenant
        val carrier = tenant.mobileCarriers.find { matches(it, phone) }
            ?: return null
        val provider = PaymentMethodProvider.values()
            .find { it.type == PaymentMethodType.MOBILE && carrier.name.equals(it.name, true) }
            ?: return null

        return mobilePaymentService.save(
            account,
            AddPaymentMethodRequest(
                ownerName = account.displayName ?: "",
                phone.number,
                type = provider.type.name,
                provider = provider.name
            )
        )
    }

    private fun matches(carrier: MobileCarrier, phone: PhoneEntity): Boolean =
        carrier.phonePrefixes
            .flatMap { it.prefixes }
            .find { phone.number.startsWith(it) } != null

    fun publishEvent(account: AccountEntity, phone: PhoneEntity) {
        try {
            stream.publish(
                EventURN.ACCOUNT_CREATED.urn,
                AccountCreatedPayload(
                    account.id!!,
                    tracingContext.tenantId()?.toLong(),
                    phone.number,
                )
            )
        } catch (ex: Exception) {
            LOGGER.error("Unable to push event ${EventURN.ACCOUNT_CREATED.urn}", ex)
        }
    }
}
