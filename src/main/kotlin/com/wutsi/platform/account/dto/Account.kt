package com.wutsi.platform.account.dto

import org.springframework.format.annotation.DateTimeFormat
import java.time.OffsetDateTime

data class Account(
    val id: Long = 0,
    val email: String? = null,
    val phone: Phone? = null,
    val pictureUrl: String? = null,
    val status: String = "",
    val displayName: String? = null,
    val language: String = "",
    val country: String = "",
    @get:DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ") val created: OffsetDateTime = OffsetDateTime.now(),
    @get:DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssZ") val updated: OffsetDateTime = OffsetDateTime.now(),
    val superUser: Boolean = false,
    val transferSecured: Boolean = true,
    val business: Boolean = false,
    val retail: Boolean = false,
    val biography: String? = null,
    val website: String? = null,
    val whatsapp: String? = null,
    val street: String? = null,
    val cityId: Long? = null,
    val timezoneId: String? = null,
    val category: Category? = null,
    val hasStore: Boolean = false,
    val facebookId: String? = null,
    val instagramId: String? = null,
    val twitterId: String? = null,
)
