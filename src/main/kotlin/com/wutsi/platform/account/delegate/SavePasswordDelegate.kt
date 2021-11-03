package com.wutsi.platform.account.`delegate`

import com.wutsi.platform.account.dto.SavePasswordRequest
import com.wutsi.platform.account.service.AccountService
import com.wutsi.platform.account.service.PasswordService
import com.wutsi.platform.account.service.SecurityManager
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
public class SavePasswordDelegate(
    private val passwordService: PasswordService,
    private val accountService: AccountService,
    private val securityManager: SecurityManager
) {
    @Transactional
    public fun invoke(id: Long, request: SavePasswordRequest) {
        val account = accountService.findById(id)
        securityManager.checkOwnership(account)
        passwordService.set(account, request)
    }
}
