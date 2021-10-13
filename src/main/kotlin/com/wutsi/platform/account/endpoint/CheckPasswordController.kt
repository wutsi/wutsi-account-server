package com.wutsi.platform.account.endpoint

import com.wutsi.platform.account.`delegate`.CheckPasswordDelegate
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.`annotation`.GetMapping
import org.springframework.web.bind.`annotation`.PathVariable
import org.springframework.web.bind.`annotation`.RequestParam
import org.springframework.web.bind.`annotation`.RestController
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import kotlin.Long
import kotlin.String

@RestController
public class CheckPasswordController(
    private val `delegate`: CheckPasswordDelegate
) {
    @GetMapping("/v1/accounts/{id}/password")
    @PreAuthorize(value = "hasAuthority('user-read')")
    public fun invoke(
        @PathVariable(name = "id") @NotNull id: Long,
        @RequestParam(
            name = "password",
            required = true
        ) @NotBlank password: String
    ) {
        delegate.invoke(id, password)
    }
}
