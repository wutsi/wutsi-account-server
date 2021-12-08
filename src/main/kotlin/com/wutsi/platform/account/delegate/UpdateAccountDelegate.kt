package com.wutsi.platform.account.`delegate`

import com.wutsi.platform.account.dao.AccountRepository
import com.wutsi.platform.account.dto.UpdateAccountRequest
import com.wutsi.platform.account.dto.UpdateAccountResponse
import com.wutsi.platform.account.service.AccountService
import org.springframework.stereotype.Service

@Service
public class UpdateAccountDelegate(
    private val service: AccountService,
    private val dao: AccountRepository
) {
    public fun invoke(id: Long, request: UpdateAccountRequest): UpdateAccountResponse {
        val account = service.findById(id)
        account.displayName = request.displayName
        account.country = request.country
        account.language = request.language
        dao.save(account)

        return UpdateAccountResponse(id = id)
    }
}
