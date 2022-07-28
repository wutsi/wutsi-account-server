package com.wutsi.platform.account.endpoint

import com.wutsi.platform.account.`delegate`.SearchAccountDelegate
import com.wutsi.platform.account.dto.SearchAccountRequest
import com.wutsi.platform.account.dto.SearchAccountResponse
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.`annotation`.PostMapping
import org.springframework.web.bind.`annotation`.RequestBody
import org.springframework.web.bind.`annotation`.RestController
import javax.validation.Valid

@RestController
public class SearchAccountController(
    public val `delegate`: SearchAccountDelegate,
) {
    @PostMapping("/v1/accounts/search")
    @PreAuthorize(value = "hasAuthority('user-read')")
    public fun invoke(@Valid @RequestBody request: SearchAccountRequest): SearchAccountResponse =
        delegate.invoke(request)
}
