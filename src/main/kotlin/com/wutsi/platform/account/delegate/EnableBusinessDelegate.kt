package com.wutsi.platform.account.`delegate`

import com.wutsi.platform.account.dao.AccountRepository
import com.wutsi.platform.account.dto.EnableBusinessRequest
import com.wutsi.platform.account.entity.AccountEntity
import com.wutsi.platform.account.event.AccountUpdatedPayload
import com.wutsi.platform.account.event.EventURN
import com.wutsi.platform.account.service.AccountService
import com.wutsi.platform.account.service.SecurityManager
import com.wutsi.platform.core.stream.EventStream
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class EnableBusinessDelegate(
    private val dao: AccountRepository,
    private val service: AccountService,
    private val securityManager: SecurityManager,
    private val eventStream: EventStream
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(EnableBusinessDelegate::class.java)
    }

    fun invoke(id: Long, request: EnableBusinessRequest) {
        val account = service.findById(id)
        securityManager.checkOwnership(account)

        account.business = true
        account.displayName = request.displayName
        account.whatsapp = request.whatsapp
        account.country = request.country
        account.cityId = request.cityId
        account.street = request.street
        account.biography = request.biography
        account.categoryId = request.categoryId
        dao.save(account)

        publishEvent(account)
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
