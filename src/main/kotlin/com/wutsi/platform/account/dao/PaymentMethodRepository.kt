package com.wutsi.platform.account.dao

import com.wutsi.platform.account.entity.AccountEntity
import com.wutsi.platform.account.entity.PaymentMethodEntity
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface PaymentMethodRepository : CrudRepository<PaymentMethodEntity, Long> {
    fun findByToken(token: String): Optional<PaymentMethodEntity>
    fun findByAccount(account: AccountEntity): List<PaymentMethodEntity>
}
