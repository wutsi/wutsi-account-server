package com.wutsi.platform.account.`delegate`

import com.wutsi.platform.account.dao.PasswordRepository
import com.wutsi.platform.account.service.AccountService
import com.wutsi.platform.account.service.PasswordHasher
import com.wutsi.platform.account.util.ErrorURN.PASSWORD_MISMATCH
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.exception.ConflictException
import org.springframework.stereotype.Service

@Service
public class CheckPasswordDelegate(
    private val hasher: PasswordHasher,
    private val accountService: AccountService,
    private val dao: PasswordRepository
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

        val xpassword = hasher.hash(password, obj.salt)
        if (xpassword != obj.value)
            throw ConflictException(
                Error(
                    code = PASSWORD_MISMATCH.urn
                )
            )
    }
}
