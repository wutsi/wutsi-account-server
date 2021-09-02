package com.wutsi.platform.account.`delegate`

import com.wutsi.platform.account.dto.SearchAccountResponse
import com.wutsi.platform.account.service.AccountService
import org.springframework.stereotype.Service

@Service
class SearchAccountDelegate(private val service: AccountService) {
    fun invoke(phoneNumber: String, limit: Int = 20, offset: Int = 0): SearchAccountResponse =
        SearchAccountResponse(
            accounts = service.search(
                phoneNumber = phoneNumber,
                limit = limit,
                offset = offset
            )
                .map { it.toAccountSummary() }
        )
}
