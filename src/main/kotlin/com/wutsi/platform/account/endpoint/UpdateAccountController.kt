package com.wutsi.platform.account.endpoint

import com.wutsi.platform.account.`delegate`.UpdateAccountDelegate
import com.wutsi.platform.account.dto.UpdateAccountRequest
import com.wutsi.platform.account.dto.UpdateAccountResponse
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.`annotation`.PathVariable
import org.springframework.web.bind.`annotation`.PostMapping
import org.springframework.web.bind.`annotation`.RequestBody
import org.springframework.web.bind.`annotation`.RestController
import javax.validation.Valid
import kotlin.Long

@RestController
public class UpdateAccountController(
    private val `delegate`: UpdateAccountDelegate
) {
    @PostMapping("/v1/accounts/{id}")
    @PreAuthorize(value = "hasAuthority('user-manage')")
    public fun invoke(
        @PathVariable(name = "id") id: Long,
        @Valid @RequestBody
        request: UpdateAccountRequest
    ): UpdateAccountResponse = delegate.invoke(id, request)
}
