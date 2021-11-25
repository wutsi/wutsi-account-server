package com.wutsi.platform.account.dto

import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size
import kotlin.String

public data class CreateAccountRequest(
    @get:NotBlank
    public val phoneNumber: String = "",
    @get:Size(max = 2)
    public val language: String = "en",
    @get:Size(max = 2)
    public val country: String = "US",
    public val displayName: String? = null,
    public val pictureUrl: String? = null,
    public val password: String? = null
)
