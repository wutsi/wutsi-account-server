package com.wutsi.platform.account.endpoint

import com.wutsi.platform.account.`delegate`.CreateAccountDelegate
import com.wutsi.platform.account.dto.CreateAccountRequest
import com.wutsi.platform.account.dto.CreateAccountResponse
import org.springframework.web.bind.`annotation`.CrossOrigin
import org.springframework.web.bind.`annotation`.PostMapping
import org.springframework.web.bind.`annotation`.RequestBody
import org.springframework.web.bind.`annotation`.RestController
import javax.validation.Valid

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
public class CreateAccountController(
    private val `delegate`: CreateAccountDelegate
) {
    @PostMapping("/v1/accounts")
    public fun invoke(@Valid @RequestBody request: CreateAccountRequest): CreateAccountResponse =
        delegate.invoke(request)
}
