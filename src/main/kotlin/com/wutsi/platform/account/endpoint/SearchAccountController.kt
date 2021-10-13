package com.wutsi.platform.account.endpoint

import com.wutsi.platform.account.`delegate`.SearchAccountDelegate
import com.wutsi.platform.account.dto.SearchAccountResponse
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.`annotation`.GetMapping
import org.springframework.web.bind.`annotation`.RequestParam
import org.springframework.web.bind.`annotation`.RestController
import kotlin.Int
import kotlin.String

@RestController
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
