package com.wutsi.platform.account.entity

import com.wutsi.platform.account.entity.AccountStatus.UNKNOWN
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

    val tenantId: Long = -1,

    @OneToOne
    @JoinColumn(name = "phone_fk")
    var phone: PhoneEntity? = null,

    var displayName: String? = null,
    var pictureUrl: String? = null,

    @Enumerated
    var status: AccountStatus = UNKNOWN,

    val created: OffsetDateTime = OffsetDateTime.now(),
    val updated: OffsetDateTime = OffsetDateTime.now(),
    var deleted: OffsetDateTime? = null,
    var language: String = "",
    var country: String = "",
    val isSuperUser: Boolean = false,
    var isDeleted: Boolean = false,
    var isTransferSecured: Boolean = true,
    var business: Boolean = false,
    var retail: Boolean = false,
    var website: String? = null,
    var biography: String? = null,
    var categoryId: Long? = null,
    var whatsapp: String? = null,
    var street: String? = null,
    var cityId: Long? = null,
    var timezoneId: String? = null,
    var hasStore: Boolean = false,
    var email: String? = null,
    var facebookId: String? = null,
    var instagramId: String? = null,
    var twitterId: String? = null,

    val totalViews: Long = 0,
    val totalShares: Long = 0,
    val totalChats: Long = 0,
    val totalOrders: Long = 0,
    val conversion: Double = 0.0,
    val score: Double = 0.0,
    val totalSales: Long = 0,
)
