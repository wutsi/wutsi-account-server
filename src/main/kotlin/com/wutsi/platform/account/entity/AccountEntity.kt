package com.wutsi.platform.account.entity

import com.wutsi.platform.account.entity.AccountStatus.ACCOUNT_STATUS_INVALID
import java.time.OffsetDateTime
import javax.persistence.Entity
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.OneToOne
import javax.persistence.Table

@Entity
@Table(name = "T_ACCOUNT")
data class AccountEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @OneToOne
    @JoinColumn(name = "phone_fk")
    var phone: PhoneEntity? = PhoneEntity(),

    var displayName: String? = null,
    var pictureUrl: String? = null,

    @Enumerated
    var status: AccountStatus = ACCOUNT_STATUS_INVALID,

    val created: OffsetDateTime = OffsetDateTime.now(),
    val updated: OffsetDateTime = OffsetDateTime.now(),
    var deleted: OffsetDateTime? = null
)
