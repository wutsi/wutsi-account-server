package com.wutsi.platform.account.endpoint

import com.wutsi.platform.account.`delegate`.ListPaymentMethodsDelegate
import com.wutsi.platform.account.dto.ListPaymentMethodResponse
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.`annotation`.GetMapping
import org.springframework.web.bind.`annotation`.PathVariable
import org.springframework.web.bind.`annotation`.RestController
import kotlin.Long

@RestController
public class ListPaymentMethodsController(
    private val `delegate`: ListPaymentMethodsDelegate
) {
    @GetMapping("/v1/accounts/{id}/payment-methods")
    @PreAuthorize(value = "hasAuthority('payment-method-read')")
    public fun invoke(@PathVariable(name = "id") id: Long): ListPaymentMethodResponse =
        delegate.invoke(id)
}
