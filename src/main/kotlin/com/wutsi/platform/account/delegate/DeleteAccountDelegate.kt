package com.wutsi.platform.account.`delegate`

import com.wutsi.platform.account.dao.AccountRepository
import com.wutsi.platform.account.entity.AccountStatus.ACCOUNT_STATUS_DELETED
import com.wutsi.platform.account.service.SecurityManager
import com.wutsi.platform.account.util.ErrorURN.ACCOUNT_NOT_FOUND
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.Parameter
import com.wutsi.platform.core.error.ParameterType.PARAMETER_TYPE_PATH
import com.wutsi.platform.core.error.exception.NotFoundException
import org.springframework.stereotype.Service
import java.time.OffsetDateTime
import javax.transaction.Transactional

@Service
public class DeleteAccountDelegate(
    private val dao: AccountRepository,
    private val securityManager: SecurityManager
) {
    @Transactional
    public fun invoke(id: Long) {
        val account = dao.findById(id)
            .orElseThrow {
                NotFoundException(
                    error = Error(
                        code = ACCOUNT_NOT_FOUND.urn,
                        parameter = Parameter(
                            name = "id",
                            value = id,
                            type = PARAMETER_TYPE_PATH
                        )
                    )
                )
            }

        securityManager.checkOwnership(account)

        if (account.status == ACCOUNT_STATUS_DELETED)
            return

        account.status = ACCOUNT_STATUS_DELETED
        account.phone = null
        account.deleted = OffsetDateTime.now()
        dao.save(account)
    }
}
