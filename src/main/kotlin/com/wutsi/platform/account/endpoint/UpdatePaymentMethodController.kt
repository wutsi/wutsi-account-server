package com.wutsi.platform.account.endpoint

import com.wutsi.platform.account.`delegate`.UpdatePaymentMethodDelegate
import com.wutsi.platform.account.dto.UpdatePaymentMethodRequest
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.`annotation`.CrossOrigin
import org.springframework.web.bind.`annotation`.PathVariable
import org.springframework.web.bind.`annotation`.PostMapping
import org.springframework.web.bind.`annotation`.RequestBody
import org.springframework.web.bind.`annotation`.RestController
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import kotlin.Long
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
public class UpdatePaymentMethodController(
    private val `delegate`: UpdatePaymentMethodDelegate
) {
    @PostMapping("/v1/accounts/{id}/payment-methods/{token}")
    @PreAuthorize(value = "hasAuthority('payment-method-manage')")
    public fun invoke(
        @PathVariable(name = "id") @NotNull id: Long,
        @PathVariable(name = "token") @NotBlank token: String,
        @Valid @RequestBody request: UpdatePaymentMethodRequest
    ) {
        delegate.invoke(id, token, request)
    }
}
