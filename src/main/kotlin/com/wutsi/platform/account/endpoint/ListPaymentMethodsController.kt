package com.wutsi.platform.account.endpoint

import com.wutsi.platform.account.`delegate`.ListPaymentMethodsDelegate
import com.wutsi.platform.account.dto.ListPaymentMethodResponse
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.`annotation`.CrossOrigin
import org.springframework.web.bind.`annotation`.GetMapping
import org.springframework.web.bind.`annotation`.PathVariable
import org.springframework.web.bind.`annotation`.RestController
import javax.validation.constraints.NotNull
import kotlin.Long

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
public class ListPaymentMethodsController(
    private val `delegate`: ListPaymentMethodsDelegate
) {
    @GetMapping("/v1/accounts/{id}/payment-methods")
    @PreAuthorize(value = "hasAuthority('payment-method-read')")
    public fun invoke(@PathVariable(name = "id") @NotNull id: Long): ListPaymentMethodResponse =
        delegate.invoke(id)
}
