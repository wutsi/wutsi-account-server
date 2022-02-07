package com.wutsi.platform.account.entity

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table

@Entity
@Table(name = "T_BUSINESS_HOUR")
data class BusinessHourEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "account_fk")
    val account: AccountEntity = AccountEntity(),

    val dayOfWeek: Int = 0,
    var opened: Boolean = false,
    var openTime: String? = null,
    var closeTime: String? = null
)
