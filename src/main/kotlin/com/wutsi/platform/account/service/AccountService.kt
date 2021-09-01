package com.wutsi.platform.account.service

import com.wutsi.platform.account.dao.AccountRepository
import com.wutsi.platform.account.entity.AccountEntity
import com.wutsi.platform.account.util.ErrorURN
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.Parameter
import com.wutsi.platform.core.error.ParameterType
import com.wutsi.platform.core.error.ParameterType.PARAMETER_TYPE_PATH
import com.wutsi.platform.core.error.exception.NotFoundException
import org.springframework.stereotype.Service

@Service
public class AccountService(
    private val dao: AccountRepository
) {
    fun findById(id: Long, parameterType: ParameterType = PARAMETER_TYPE_PATH): AccountEntity {
        val account = dao.findById(id)
            .orElseThrow {
                NotFoundException(
                    error = Error(
                        code = ErrorURN.ACCOUNT_NOT_FOUND.urn,
                        parameter = Parameter(
                            name = "id",
                            value = id,
                            type = parameterType
                        )
                    )
                )
            }

        if (account.isDeleted)
            throw NotFoundException(
                error = Error(
                    code = ErrorURN.ACCOUNT_DELETED.urn,
                    parameter = Parameter(
                        name = "id",
                        value = id,
                        type = parameterType
                    )
                )
            )

        return account
    }
}
