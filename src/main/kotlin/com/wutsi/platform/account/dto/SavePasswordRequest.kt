package com.wutsi.platform.account.dto

import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size
import kotlin.String

public data class SavePasswordRequest(
    @get:NotBlank
    @get:Size(min = 6)
    public val password: String = "",
)
