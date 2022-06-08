package com.wutsi.platform.account.`delegate`

import com.wutsi.platform.account.dao.AccountRepository
import com.wutsi.platform.account.entity.AccountEntity
import com.wutsi.platform.account.event.AccountUpdatedPayload
import com.wutsi.platform.account.event.EventURN
import com.wutsi.platform.account.service.AccountService
import com.wutsi.platform.account.service.SecurityManager
import com.wutsi.platform.core.stream.EventStream
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DisableBusinessDelegate(
    private val dao: AccountRepository,
    private val service: AccountService,
    private val securityManager: SecurityManager,
    private val eventStream: EventStream
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(DisableBusinessDelegate::class.java)
    }

    @Transactional
    fun invoke(id: Long) {
        val account = service.findById(id)
        securityManager.checkOwnership(account)

        if (account.business) {
            account.business = false
            dao.save(account)

            publishEvent(account)
        }
    }

    private fun publishEvent(account: AccountEntity) {
        val event = EventURN.ACCOUNT_UPDATED
        try {
            eventStream.publish(
                event.urn,
                AccountUpdatedPayload(
                    accountId = account.id!!,
                    attribute = "business"
                )
            )
        } catch (ex: Exception) {
            LOGGER.error("Unable to push event $event", ex)
        }
    }
}
