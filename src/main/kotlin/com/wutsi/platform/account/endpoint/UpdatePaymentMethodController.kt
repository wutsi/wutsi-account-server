package com.wutsi.platform.account.endpoint

import com.wutsi.platform.account.`delegate`.UpdatePaymentMethodDelegate
import com.wutsi.platform.account.dto.UpdatePaymentMethodRequest
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.`annotation`.PathVariable
import org.springframework.web.bind.`annotation`.PostMapping
import org.springframework.web.bind.`annotation`.RequestBody
import org.springframework.web.bind.`annotation`.RestController
import javax.validation.Valid
import kotlin.Long
import kotlin.String

@RestController
public class UpdatePaymentMethodController(
    private val `delegate`: UpdatePaymentMethodDelegate
) {
    @PostMapping("/v1/accounts/{id}/payment-methods/{token}")
    @PreAuthorize(value = "hasAuthority('payment-method-manage')")
    public fun invoke(
        @PathVariable(name = "id") id: Long,
        @PathVariable(name = "token") token: String,
        @Valid @RequestBody request: UpdatePaymentMethodRequest
    ) {
        delegate.invoke(id, token, request)
    }
}
