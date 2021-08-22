package com.wutsi.platform.account.entity

import java.time.OffsetDateTime
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.OneToOne
import javax.persistence.Table

@Entity
@Table(name = "T_PASSWORD")
data class PasswordEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @OneToOne
    @JoinColumn(name = "account_fk")
    var account: AccountEntity = AccountEntity(),

    var value: String = "",
    val salt: String = "",

    val created: OffsetDateTime = OffsetDateTime.now(),
    val updated: OffsetDateTime = OffsetDateTime.now()
)
