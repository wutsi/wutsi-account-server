package com.wutsi.platform.account.`delegate`

import com.wutsi.platform.account.dto.SearchAccountRequest
import com.wutsi.platform.account.dto.SearchAccountResponse
import com.wutsi.platform.account.service.AccountService
import com.wutsi.platform.account.service.ImageKit
import com.wutsi.platform.core.logging.KVLogger
import org.springframework.stereotype.Service

@Service
class SearchAccountDelegate(
    private val service: AccountService,
    private val logger: KVLogger,
    private val imageKit: ImageKit,
) {
    fun invoke(request: SearchAccountRequest): SearchAccountResponse {
        logger.add("phone_number", request.phoneNumber)
        logger.add("ids", request.ids)
        logger.add("limit", request.limit)
        logger.add("offset", request.offset)

        val accounts = service.search(request)
        logger.add("count", accounts.size)

        return SearchAccountResponse(
            accounts = accounts.map { it.toAccountSummary(imageKit) }
        )
    }
}
