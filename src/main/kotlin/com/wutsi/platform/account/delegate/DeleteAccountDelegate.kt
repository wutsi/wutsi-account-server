package com.wutsi.platform.account.`delegate`

import com.wutsi.platform.account.dao.AccountRepository
import com.wutsi.platform.account.entity.AccountEntity
import com.wutsi.platform.account.event.AccountDeletedPayload
import com.wutsi.platform.account.event.EventURN
import com.wutsi.platform.account.service.AccountService
import com.wutsi.platform.account.service.SecurityManager
import com.wutsi.platform.core.stream.EventStream
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.OffsetDateTime
import javax.transaction.Transactional

@Service
public class DeleteAccountDelegate(
    private val dao: AccountRepository,
    private val service: AccountService,
    private val securityManager: SecurityManager,
    private val stream: EventStream
) {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(DeleteAccountDelegate::class.java)
    }

    @Transactional
    public fun invoke(id: Long) {
        val account = service.findById(id)
        securityManager.checkOwnership(account)

        account.phone = null
        account.isDeleted = true
        account.deleted = OffsetDateTime.now()
        dao.save(account)

        publishEvent(account)
    }

    private fun publishEvent(account: AccountEntity) {
        try {
            stream.publish(
                EventURN.ACCOUNT_DELETED.urn,
                AccountDeletedPayload(
                    accountId = account.id!!,
                )
            )
        } catch (ex: Exception) {
            LOGGER.error("Unable to push event ${EventURN.ACCOUNT_DELETED.urn}", ex)
        }
    }
}
