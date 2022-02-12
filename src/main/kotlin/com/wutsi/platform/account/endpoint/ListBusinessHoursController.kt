package com.wutsi.platform.account.endpoint

import com.wutsi.platform.account.`delegate`.ListBusinessHoursDelegate
import com.wutsi.platform.account.dto.ListBusinessHourResponse
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.`annotation`.GetMapping
import org.springframework.web.bind.`annotation`.PathVariable
import org.springframework.web.bind.`annotation`.RestController
import kotlin.Long

@RestController
public class ListBusinessHoursController(
    private val `delegate`: ListBusinessHoursDelegate
) {
    @GetMapping("/v1/accounts/{id}/business-hours")
    @PreAuthorize(value = "hasAuthority('user-read')")
    public fun invoke(@PathVariable(name = "id") id: Long): ListBusinessHourResponse =
        delegate.invoke(id)
}
