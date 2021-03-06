package com.wutsi.platform.account.endpoint

import com.wutsi.platform.account.`delegate`.DeletePaymentMethodDelegate
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.`annotation`.DeleteMapping
import org.springframework.web.bind.`annotation`.PathVariable
import org.springframework.web.bind.`annotation`.RestController
import kotlin.Long
import kotlin.String

@RestController
public class DeletePaymentMethodController(
    public val `delegate`: DeletePaymentMethodDelegate,
) {
    @DeleteMapping("/v1/accounts/{id}/payment-methods/{token}")
    @PreAuthorize(value = "hasAuthority('payment-method-manage')")
    public fun invoke(@PathVariable(name = "id") id: Long, @PathVariable(name = "token") token: String) {
        delegate.invoke(id, token)
    }
}
