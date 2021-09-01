package com.wutsi.platform.account.`delegate`

import com.wutsi.platform.account.dao.AccountRepository
import com.wutsi.platform.account.service.AccountService
import com.wutsi.platform.account.service.SecurityManager
import org.springframework.stereotype.Service
import java.time.OffsetDateTime
import javax.transaction.Transactional

@Service
public class DeleteAccountDelegate(
    private val dao: AccountRepository,
    private val service: AccountService,
    private val securityManager: SecurityManager
) {
    @Transactional
    public fun invoke(id: Long) {
        val account = service.findById(id)
        securityManager.checkOwnership(account)

        account.phone = null
        account.isDeleted = true
        account.deleted = OffsetDateTime.now()
        dao.save(account)
    }
}
