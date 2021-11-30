package com.wutsi.platform.account.`delegate`

import com.wutsi.platform.account.dao.PasswordRepository
import com.wutsi.platform.account.error.ErrorURN.PASSWORD_MISMATCH
import com.wutsi.platform.account.service.AccountService
import com.wutsi.platform.account.service.PasswordService
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.exception.ConflictException
import org.springframework.stereotype.Service

@Service
public class CheckPasswordDelegate(
    private val service: PasswordService,
    private val accountService: AccountService,
    private val dao: PasswordRepository,
) {
    public fun invoke(id: Long, password: String) {
        val account = accountService.findById(id)

        val obj = dao.findByAccount(account)
            .orElseThrow {
                ConflictException(
                    Error(
                        code = PASSWORD_MISMATCH.urn
                    )
                )
            }

        val xpassword = service.hash(password, obj.salt)
        if (xpassword != obj.value)
            throw ConflictException(
                Error(
                    code = PASSWORD_MISMATCH.urn
                )
            )
    }
}
