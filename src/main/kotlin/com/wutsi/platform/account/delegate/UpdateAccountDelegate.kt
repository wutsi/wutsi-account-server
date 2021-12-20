package com.wutsi.platform.account.`delegate`

import com.wutsi.platform.account.dao.AccountRepository
import com.wutsi.platform.account.dto.UpdateAccountRequest
import com.wutsi.platform.account.dto.UpdateAccountResponse
import com.wutsi.platform.account.event.AccountUpdatedPayload
import com.wutsi.platform.account.event.EventURN
import com.wutsi.platform.account.service.AccountService
import com.wutsi.platform.core.stream.EventStream
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
public class UpdateAccountDelegate(
    private val service: AccountService,
    private val dao: AccountRepository,
    private val stream: EventStream,
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(UpdateAccountDelegate::class.java)
    }

    public fun invoke(id: Long, request: UpdateAccountRequest): UpdateAccountResponse {
        // Save
        val account = service.findById(id)
        account.displayName = request.displayName
        account.country = request.country
        account.language = request.language
        dao.save(account)

        // Send notification
        publishEvent(id)

        return UpdateAccountResponse(id = id)
    }

    private fun publishEvent(id: Long) {
        try {
            stream.publish(
                EventURN.ACCOUNT_UPDATED.urn,
                AccountUpdatedPayload(id)
            )
        } catch (ex: Exception) {
            LOGGER.error("Unable to push event ${EventURN.ACCOUNT_UPDATED.urn}", ex)
        }
    }
}
