package com.wutsi.platform.account.endpoint

import com.wutsi.platform.account.`delegate`.DeleteAccountDelegate
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.`annotation`.DeleteMapping
import org.springframework.web.bind.`annotation`.PathVariable
import org.springframework.web.bind.`annotation`.RestController
import kotlin.Long

@RestController
public class DeleteAccountController(
    private val `delegate`: DeleteAccountDelegate
) {
    @DeleteMapping("/v1/accounts/{id}")
    @PreAuthorize(value = "hasAuthority('user-manage')")
    public fun invoke(@PathVariable(name = "id") id: Long) {
        delegate.invoke(id)
    }
}
