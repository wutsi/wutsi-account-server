package com.wutsi.platform.account.service

import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat.E164
import com.wutsi.platform.account.dao.PhoneRepository
import com.wutsi.platform.account.entity.PhoneEntity
import org.springframework.stereotype.Service

@Service
class PhoneService(
    private val dao: PhoneRepository
) {
    fun findOrCreate(phoneNumber: String): PhoneEntity {
        val util = PhoneNumberUtil.getInstance()
        val phoneNumber = util.parse(phoneNumber, "")
        val number = util.format(phoneNumber, E164)

        return dao.findByNumber(number).orElseGet {
            dao.save(
                PhoneEntity(
                    number = number,
                    country = util.getRegionCodeForCountryCode(phoneNumber.countryCode)
                )
            )
        }
    }
}
