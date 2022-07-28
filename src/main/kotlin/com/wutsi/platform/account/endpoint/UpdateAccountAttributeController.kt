package com.wutsi.platform.account.endpoint

import com.wutsi.platform.account.`delegate`.UpdateAccountAttributeDelegate
import com.wutsi.platform.account.dto.UpdateAccountAttributeRequest
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.`annotation`.PathVariable
import org.springframework.web.bind.`annotation`.PostMapping
import org.springframework.web.bind.`annotation`.RequestBody
import org.springframework.web.bind.`annotation`.RestController
import javax.validation.Valid
import kotlin.Long
import kotlin.String

@RestController
public class UpdateAccountAttributeController(
    public val `delegate`: UpdateAccountAttributeDelegate,
) {
    @PostMapping("/v1/accounts/{id}/attributes/{name}")
    @PreAuthorize(value = "hasAuthority('user-manage')")
    public fun invoke(
        @PathVariable(name = "id") id: Long,
        @PathVariable(name = "name") name: String,
        @Valid @RequestBody request: UpdateAccountAttributeRequest,
    ) {
        delegate.invoke(id, name, request)
    }
}
