package com.wutsi.platform.account.endpoint

import com.wutsi.platform.account.`delegate`.DeleteAccountDelegate
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.`annotation`.CrossOrigin
import org.springframework.web.bind.`annotation`.DeleteMapping
import org.springframework.web.bind.`annotation`.PathVariable
import org.springframework.web.bind.`annotation`.RestController
import javax.validation.constraints.NotNull
import kotlin.Long

@RestController
@CrossOrigin(
    origins = ["*"],
    allowedHeaders = ["Content-Type", "Authorization", "Content-Length", "X-Requested-With"],
    methods = [
        org.springframework.web.bind.annotation.RequestMethod.GET,
        org.springframework.web.bind.annotation.RequestMethod.DELETE,
        org.springframework.web.bind.annotation.RequestMethod.OPTIONS,
        org.springframework.web.bind.annotation.RequestMethod.HEAD,
        org.springframework.web.bind.annotation.RequestMethod.POST,
        org.springframework.web.bind.annotation.RequestMethod.PUT
    ]
)
public class DeleteAccountController(
    private val `delegate`: DeleteAccountDelegate
) {
    @DeleteMapping("/v1/accounts/{id}")
    @PreAuthorize(value = "hasAuthority('user-manage')")
    public fun invoke(@PathVariable(name = "id") @NotNull id: Long) {
        delegate.invoke(id)
    }
}
