package com.wutsi.platform.account.endpoint

import com.wutsi.platform.account.`delegate`.SaveBusinessHourDelegate
import com.wutsi.platform.account.dto.SaveBusinessHourRequest
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.`annotation`.PathVariable
import org.springframework.web.bind.`annotation`.PostMapping
import org.springframework.web.bind.`annotation`.RequestBody
import org.springframework.web.bind.`annotation`.RestController
import javax.validation.Valid
import kotlin.Long

@RestController
public class SaveBusinessHourController(
    private val `delegate`: SaveBusinessHourDelegate
) {
    @PostMapping("/v1/accounts/{id}/business-hours")
    @PreAuthorize(value = "hasAuthority('user-manage')")
    public fun invoke(
        @PathVariable(name = "id") id: Long,
        @Valid @RequestBody
        request: SaveBusinessHourRequest
    ) {
        delegate.invoke(id, request)
    }
}
