package com.wutsi.platform.account.`delegate`

import com.wutsi.platform.account.dao.BusinessHourRepository
import com.wutsi.platform.account.dto.ListBusinessHourResponse
import org.springframework.stereotype.Service

@Service
public class ListBusinessHoursDelegate(
    private val dao: BusinessHourRepository,
) {
    public fun invoke(id: Long) = ListBusinessHourResponse(
        businessHours = dao.findByAccountId(id).map { it.toBusinessHour() }
    )
}
