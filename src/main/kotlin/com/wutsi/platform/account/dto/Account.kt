package com.wutsi.platform.account.dto

import org.springframework.format.annotation.DateTimeFormat
import java.time.OffsetDateTime

public data class Account(
    public val id: Long = 0,
    public val phone: Phone? = null,
    public val pictureUrl: String? = null,
    public val status: String = "",
    public val displayName: String? = null,
    public val language: String = "",
    public val country: String = "",
    @get:DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    public val created: OffsetDateTime = OffsetDateTime.now(),
    @get:DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ")
    public val updated: OffsetDateTime = OffsetDateTime.now(),
    public val superUser: Boolean = false,
    public val transferSecured: Boolean = true,
    public val business: Boolean = false,
    public val retail: Boolean = false,
    public val biography: String? = null,
    public val website: String? = null,
    public val categoryId: Long? = null,
    public val whatsapp: Boolean = false
)
