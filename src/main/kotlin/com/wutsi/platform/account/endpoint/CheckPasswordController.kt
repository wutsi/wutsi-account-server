package com.wutsi.platform.account.endpoint

import com.wutsi.platform.account.`delegate`.CheckPasswordDelegate
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.`annotation`.GetMapping
import org.springframework.web.bind.`annotation`.PathVariable
import org.springframework.web.bind.`annotation`.RequestParam
import org.springframework.web.bind.`annotation`.RestController
import kotlin.Long
import kotlin.String

@RestController
public class CheckPasswordController(
    public val `delegate`: CheckPasswordDelegate,
) {
    @GetMapping("/v1/accounts/{id}/password")
    @PreAuthorize(value = "hasAuthority('user-read')")
    public fun invoke(
        @PathVariable(name = "id") id: Long,
        @RequestParam(
            name = "password",
            required = false
        ) password: String
    ) {
        delegate.invoke(id, password)
    }
}
