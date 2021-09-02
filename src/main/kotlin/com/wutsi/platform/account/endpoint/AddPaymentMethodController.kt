package com.wutsi.platform.account.endpoint

import com.wutsi.platform.account.`delegate`.AddPaymentMethodDelegate
import com.wutsi.platform.account.dto.AddPaymentMethodRequest
import com.wutsi.platform.account.dto.AddPaymentMethodResponse
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.`annotation`.CrossOrigin
import org.springframework.web.bind.`annotation`.PathVariable
import org.springframework.web.bind.`annotation`.PostMapping
import org.springframework.web.bind.`annotation`.RequestBody
import org.springframework.web.bind.`annotation`.RestController
import javax.validation.Valid
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
public class AddPaymentMethodController(
    private val `delegate`: AddPaymentMethodDelegate
) {
    @PostMapping("/v1/accounts/{id}/payment-methods")
    @PreAuthorize(value = "hasAuthority('payment-method-manage')")
    public fun invoke(
        @PathVariable(name = "id") @NotNull id: Long,
        @Valid @RequestBody
        request: AddPaymentMethodRequest
    ): AddPaymentMethodResponse = delegate.invoke(id, request)
}
