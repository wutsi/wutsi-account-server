package com.wutsi.platform.account.`delegate`

import com.wutsi.platform.account.dao.PasswordRepository
import com.wutsi.platform.account.dto.SavePasswordRequest
import com.wutsi.platform.account.entity.PasswordEntity
import com.wutsi.platform.account.service.AccountService
import com.wutsi.platform.account.service.PasswordHasher
import com.wutsi.platform.account.service.SecurityManager
import org.springframework.stereotype.Service
import java.util.UUID
import javax.transaction.Transactional

@Service
public class SavePasswordDelegate(
    private val dao: PasswordRepository,
    private val hasher: PasswordHasher,
    private val accountService: AccountService,
    private val securityManager: SecurityManager
) {
    @Transactional
    public fun invoke(id: Long, request: SavePasswordRequest) {
        val account = accountService.findById(id)
        securityManager.checkOwnership(account)

        val opt = dao.findByAccount(account)
        if (opt.isPresent) {
            val obj = opt.get()
            obj.value = hasher.hash(request.password, obj.salt)
            dao.save(obj)
        } else {
            val salt = UUID.randomUUID().toString()
            dao.save(
                PasswordEntity(
                    account = account,
                    salt = salt,
                    value = hasher.hash(request.password, salt)
                )
            )
        }
    }
}
