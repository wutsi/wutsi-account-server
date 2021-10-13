package com.wutsi.platform.account.endpoint

import com.wutsi.platform.account.`delegate`.GetAccountDelegate
import com.wutsi.platform.account.dto.GetAccountResponse
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.`annotation`.GetMapping
import org.springframework.web.bind.`annotation`.PathVariable
import org.springframework.web.bind.`annotation`.RestController
import javax.validation.constraints.NotNull
import kotlin.Long

@RestController
public class GetAccountController(
    private val `delegate`: GetAccountDelegate
) {
    @GetMapping("/v1/accounts/{id}")
    @PreAuthorize(value = "hasAuthority('user-read')")
    public fun invoke(@PathVariable(name = "id") @NotNull id: Long): GetAccountResponse =
        delegate.invoke(id)
}
