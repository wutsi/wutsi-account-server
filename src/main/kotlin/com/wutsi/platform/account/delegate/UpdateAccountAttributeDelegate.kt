package com.wutsi.platform.account.`delegate`

import com.wutsi.platform.account.dto.UpdateAccountAttributeRequest
import com.wutsi.platform.account.error.ErrorURN
import com.wutsi.platform.account.service.AccountService
import com.wutsi.platform.account.service.SecurityManager
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.Parameter
import com.wutsi.platform.core.error.ParameterType.PARAMETER_TYPE_PATH
import com.wutsi.platform.core.error.ParameterType.PARAMETER_TYPE_PAYLOAD
import com.wutsi.platform.core.error.exception.BadRequestException
import com.wutsi.platform.core.logging.KVLogger
import org.springframework.stereotype.Service
import java.net.MalformedURLException
import java.net.URL
import java.util.Locale
import javax.transaction.Transactional

@Service
public class UpdateAccountAttributeDelegate(
    private val service: AccountService,
    private val securityManager: SecurityManager,
    private val logger: KVLogger,
) {
    companion object {
        const val DEFAULT_LANGUAGE = "en"
        const val DEFAULT_COUNTRY = "US"
    }

    @Transactional
    public fun invoke(
        id: Long,
        name: String,
        request: UpdateAccountAttributeRequest
    ) {
        logger.add("attribute", name)
        logger.add("value", request.value)

        val account = service.findById(id)
        securityManager.checkOwnership(account)

        when (name) {
            "display-name" -> account.displayName = toString(request.value)
            "picture-url" -> account.pictureUrl = toPictureUrl(request.value)?.toString()
            "language" -> account.language = toLanguage(request.value)
            "country" -> account.country = toCountry(request.value)
            else -> throw BadRequestException(
                error = Error(
                    code = ErrorURN.ATTRIBUTE_INVALID.urn,
                    parameter = Parameter(
                        name = "name",
                        value = name,
                        type = PARAMETER_TYPE_PATH
                    )
                )
            )
        }
    }

    private fun toString(value: String?): String? =
        if (value.isNullOrEmpty())
            null
        else
            value

    private fun toPictureUrl(value: String?): URL? {
        if (value.isNullOrEmpty())
            return null
        try {
            return URL(value)
        } catch (ex: MalformedURLException) {
            throw BadRequestException(
                error = Error(
                    code = ErrorURN.PICTURE_URL_MALFORMED.urn,
                    parameter = Parameter(
                        name = "pictureUrl",
                        value = value,
                        type = PARAMETER_TYPE_PAYLOAD
                    )
                ),
                ex
            )
        }
    }

    private fun toLanguage(value: String?): String {
        if (value.isNullOrEmpty())
            return DEFAULT_LANGUAGE

        val locale = Locale.getAvailableLocales().find { it.language.equals(value, true) }
        return locale?.language ?: DEFAULT_LANGUAGE
    }

    private fun toCountry(value: String?): String {
        if (value.isNullOrEmpty())
            return DEFAULT_COUNTRY

        val locale = Locale.getAvailableLocales().find { it.country.equals(value, true) }
        return locale?.country ?: DEFAULT_COUNTRY
    }
}
