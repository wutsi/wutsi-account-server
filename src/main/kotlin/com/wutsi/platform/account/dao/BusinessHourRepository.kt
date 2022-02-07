package com.wutsi.platform.account.dao

import com.wutsi.platform.account.entity.AccountEntity
import com.wutsi.platform.account.entity.BusinessHourEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface BusinessHourRepository : CrudRepository<BusinessHourEntity, Long> {
    fun findByAccountAndDayOfWeek(account: AccountEntity, dayOfWeek: Int): Optional<BusinessHourEntity>

    @Query("SELECT b FROM BusinessHourEntity b WHERE b.account.id = ?1 ORDER BY b.dayOfWeek")
    fun findByAccountId(accountId: Long): List<BusinessHourEntity>
}
