package com.wutsi.platform.account.endpoint

import com.wutsi.platform.account.`delegate`.CreateAccountDelegate
import com.wutsi.platform.account.dto.CreateAccountRequest
import com.wutsi.platform.account.dto.CreateAccountResponse
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.`annotation`.PostMapping
import org.springframework.web.bind.`annotation`.RequestBody
import org.springframework.web.bind.`annotation`.RestController
import javax.validation.Valid

@RestController
public class CreateAccountController(
    private val `delegate`: CreateAccountDelegate
) {
    @PostMapping("/v1/accounts")
    @PreAuthorize(value = "hasAuthority('user-manage')")
    public fun invoke(@Valid @RequestBody request: CreateAccountRequest): CreateAccountResponse =
        delegate.invoke(request)
}
