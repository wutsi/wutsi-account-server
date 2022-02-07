package com.wutsi.platform.account.`delegate`

import com.wutsi.platform.account.dao.BusinessHourRepository
import com.wutsi.platform.account.dto.SaveBusinessHourRequest
import com.wutsi.platform.account.entity.BusinessHourEntity
import com.wutsi.platform.account.service.AccountService
import com.wutsi.platform.account.service.SecurityManager
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
public class SaveBusinessHourDelegate(
    private val dao: BusinessHourRepository,
    private val securityManager: SecurityManager,
    private val accountService: AccountService
) {
    @Transactional
    fun invoke(id: Long, request: SaveBusinessHourRequest) {
        val account = accountService.findById(id)
        securityManager.checkOwnership(account)

        val hour = dao.findByAccountAndDayOfWeek(account, request.dayOfWeek)
            .orElseGet {
                BusinessHourEntity(
                    account = account,
                    dayOfWeek = request.dayOfWeek
                )
            }

        hour.opened = request.opened
        hour.closeTime = request.closeTime
        hour.openTime = request.openTime
        dao.save(hour)
    }
}
