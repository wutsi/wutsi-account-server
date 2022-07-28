package com.wutsi.platform.account.dto

import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size
import kotlin.String

public data class AddPaymentMethodRequest(
    @get:NotBlank
    @get:Size(max = 100)
    public val ownerName: String = "",
    public val phoneNumber: String? = null,
    @get:NotBlank
    public val type: String = "",
    @get:NotBlank
    public val provider: String = "",
)
