package com.wutsi.platform.account.endpoint

import com.wutsi.platform.account.`delegate`.CheckPasswordDelegate
import org.springframework.web.bind.`annotation`.CrossOrigin
import org.springframework.web.bind.`annotation`.GetMapping
import org.springframework.web.bind.`annotation`.PathVariable
import org.springframework.web.bind.`annotation`.RequestParam
import org.springframework.web.bind.`annotation`.RestController
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
public class CheckPasswordController(
    private val `delegate`: CheckPasswordDelegate
) {
    @GetMapping("/v1/accounts/{id}/password")
    public fun invoke(
        @PathVariable(name = "id") @NotNull id: Long,
        @RequestParam(
            name = "password",
            required = true
        ) @NotBlank password: String
    ) {
        delegate.invoke(id, password)
    }
}
