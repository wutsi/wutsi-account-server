package com.wutsi.platform.account.dto

import javax.validation.constraints.NotBlank
import kotlin.String

public data class SavePasswordRequest(
    @get:NotBlank
    public val password: String = ""
)
