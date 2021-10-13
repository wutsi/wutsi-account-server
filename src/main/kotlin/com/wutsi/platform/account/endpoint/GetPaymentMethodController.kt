package com.wutsi.platform.account.endpoint

import com.wutsi.platform.account.`delegate`.GetPaymentMethodDelegate
import com.wutsi.platform.account.dto.GetPaymentMethodResponse
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.`annotation`.GetMapping
import org.springframework.web.bind.`annotation`.PathVariable
import org.springframework.web.bind.`annotation`.RestController
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import kotlin.Long
import kotlin.String

@RestController
public class GetPaymentMethodController(
    private val `delegate`: GetPaymentMethodDelegate
) {
    @GetMapping("/v1/accounts/{id}/payment-methods/{token}")
    @PreAuthorize(value = "hasAuthority('payment-method-read')")
    public fun invoke(
        @PathVariable(name = "id") @NotNull id: Long,
        @PathVariable(name = "token")
        @NotBlank token: String
    ): GetPaymentMethodResponse = delegate.invoke(id, token)
}
