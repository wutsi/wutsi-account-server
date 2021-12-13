package com.wutsi.platform.account.`delegate`

import com.wutsi.platform.account.dto.GetAccountResponse
import com.wutsi.platform.account.service.AccountService
import com.wutsi.platform.account.service.ImageKit
import com.wutsi.platform.account.service.SecurityManager
import org.springframework.stereotype.Service

@Service
public class GetAccountDelegate(
    private val service: AccountService,
    private val securityManager: SecurityManager,
    private val imageKit: ImageKit,
) {
    fun invoke(id: Long): GetAccountResponse =
        GetAccountResponse(
            account = service.findById(id).toAccount(securityManager, imageKit)
        )
}
