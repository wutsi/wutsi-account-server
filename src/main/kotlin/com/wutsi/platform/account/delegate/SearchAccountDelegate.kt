package com.wutsi.platform.account.`delegate`

import com.wutsi.platform.account.dto.SearchAccountRequest
import com.wutsi.platform.account.dto.SearchAccountResponse
import com.wutsi.platform.account.service.AccountService
import org.springframework.stereotype.Service

@Service
class SearchAccountDelegate(private val service: AccountService) {
    fun invoke(request: SearchAccountRequest): SearchAccountResponse =
        SearchAccountResponse(
            accounts = service.search(request).map { it.toAccountSummary() }
        )
}
