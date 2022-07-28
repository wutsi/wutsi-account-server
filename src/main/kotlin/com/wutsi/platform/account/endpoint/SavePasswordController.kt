package com.wutsi.platform.account.endpoint

import com.wutsi.platform.account.`delegate`.SavePasswordDelegate
import com.wutsi.platform.account.dto.SavePasswordRequest
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.`annotation`.PathVariable
import org.springframework.web.bind.`annotation`.PostMapping
import org.springframework.web.bind.`annotation`.RequestBody
import org.springframework.web.bind.`annotation`.RestController
import javax.validation.Valid
import kotlin.Long

@RestController
public class SavePasswordController(
    public val `delegate`: SavePasswordDelegate,
) {
    @PostMapping("/v1/accounts/{id}/password")
    @PreAuthorize(value = "hasAuthority('user-manage')")
    public fun invoke(
        @PathVariable(name = "id") id: Long,
        @Valid @RequestBody
        request: SavePasswordRequest
    ) {
        delegate.invoke(id, request)
    }
}
