package com.wutsi.platform.account.dto

import javax.validation.constraints.NotBlank
import kotlin.String

public data class CreateAccountRequest(
    @get:NotBlank
    public val phoneNumber: String = ""
)
