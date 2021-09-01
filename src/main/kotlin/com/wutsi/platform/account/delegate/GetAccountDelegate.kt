package com.wutsi.platform.account.`delegate`

import com.wutsi.platform.account.dto.GetAccountResponse
import com.wutsi.platform.account.service.AccountService
import com.wutsi.platform.account.service.SecurityManager
import org.springframework.stereotype.Service

@Service
public class GetAccountDelegate(
    private val service: AccountService,
    private val securityManager: SecurityManager
) {
    public fun invoke(id: Long): GetAccountResponse {
        val account = service.findById(id)
        return GetAccountResponse(
            account = account.toAccount(securityManager)
        )
    }
}
