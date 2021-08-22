package com.wutsi.platform.account.entity

import java.time.OffsetDateTime
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "T_PHONE")
data class PhoneEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    val number: String = "",
    val country: String = "",
    val created: OffsetDateTime = OffsetDateTime.now()
)
