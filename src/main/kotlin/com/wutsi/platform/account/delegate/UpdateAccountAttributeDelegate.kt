package com.wutsi.platform.account.`delegate`

import com.wutsi.platform.account.dto.UpdateAccountAttributeRequest
import com.wutsi.platform.account.service.AccountService
import com.wutsi.platform.account.util.ErrorURN
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.Parameter
import com.wutsi.platform.core.error.ParameterType.PARAMETER_TYPE_PATH
import com.wutsi.platform.core.error.ParameterType.PARAMETER_TYPE_PAYLOAD
import com.wutsi.platform.core.error.exception.BadRequestException
import org.springframework.stereotype.Service
import java.net.MalformedURLException
import java.net.URL
import javax.transaction.Transactional

@Service
public class UpdateAccountAttributeDelegate(private val service: AccountService) {
    @Transactional
    public fun invoke(
        id: Long,
        name: String,
        request: UpdateAccountAttributeRequest
    ) {
        val account = service.findById(id)
        when (name) {
            "display-name" -> account.displayName = toString(request.value)
            "picture-url" -> account.pictureUrl = toPictureUrl(request.value)?.toString()
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
}
