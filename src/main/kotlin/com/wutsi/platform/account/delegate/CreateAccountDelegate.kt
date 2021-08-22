package com.wutsi.platform.account.`delegate`

import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat.E164
import com.wutsi.platform.account.dao.AccountRepository
import com.wutsi.platform.account.dao.PhoneRepository
import com.wutsi.platform.account.dto.CreateAccountRequest
import com.wutsi.platform.account.dto.CreateAccountResponse
import com.wutsi.platform.account.entity.AccountEntity
import com.wutsi.platform.account.entity.AccountStatus.ACCOUNT_STATUS_ACTIVE
import com.wutsi.platform.account.entity.PhoneEntity
import com.wutsi.platform.account.util.ErrorURN
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.Parameter
import com.wutsi.platform.core.error.ParameterType.PARAMETER_TYPE_PAYLOAD
import com.wutsi.platform.core.error.exception.BadRequestException
import com.wutsi.platform.core.error.exception.ConflictException
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
public class CreateAccountDelegate(
    private val phoneDao: PhoneRepository,
    private val dao: AccountRepository
) {
    @Transactional
    public fun invoke(request: CreateAccountRequest): CreateAccountResponse {
        try {
            val phone = createPhone(request)
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

            val account = dao.save(
                AccountEntity(
                    phone = createPhone(request),
                    status = ACCOUNT_STATUS_ACTIVE
                )
            )
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

    private fun createPhone(request: CreateAccountRequest): PhoneEntity {
        val util = PhoneNumberUtil.getInstance()
        val phoneNumber = util.parse(request.phoneNumber, "")
        val number = util.format(phoneNumber, E164)

        return phoneDao.findByNumber(number).orElseGet {
            phoneDao.save(
                PhoneEntity(
                    number = number,
                    country = util.getRegionCodeForCountryCode(phoneNumber.countryCode)
                )
            )
        }
    }
}
