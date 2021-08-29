package com.wutsi.platform.account.endpoint

import com.wutsi.platform.account.`delegate`.SearchAccountDelegate
import com.wutsi.platform.account.dto.SearchAccountResponse
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.`annotation`.CrossOrigin
import org.springframework.web.bind.`annotation`.GetMapping
import org.springframework.web.bind.`annotation`.RequestParam
import org.springframework.web.bind.`annotation`.RestController
import kotlin.Int
import kotlin.String

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
public class SearchAccountController(
    private val `delegate`: SearchAccountDelegate
) {
    @GetMapping("/v1/accounts")
    @PreAuthorize(value = "hasAuthority('user-read')")
    public fun invoke(
        @RequestParam(name = "phone-number", required = false) phoneNumber: String,
        @RequestParam(name = "limit", required = false, defaultValue = "20") limit: Int = 20,
        @RequestParam(name = "offset", required = false, defaultValue = "0") offset: Int = 0
    ): SearchAccountResponse = delegate.invoke(phoneNumber, limit, offset)
}
