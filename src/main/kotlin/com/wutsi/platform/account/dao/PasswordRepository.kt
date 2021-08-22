package com.wutsi.platform.account.dao

import com.wutsi.platform.account.entity.AccountEntity
import com.wutsi.platform.account.entity.PasswordEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface PasswordRepository : CrudRepository<PasswordEntity, Long> {
    fun findByAccount(account: AccountEntity): Optional<PasswordEntity>
}
