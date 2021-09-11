package com.wutsi.platform.account.entity

import com.wutsi.platform.payment.PaymentMethodProvider
import com.wutsi.platform.payment.PaymentMethodProvider.UNKNOWN
import com.wutsi.platform.payment.PaymentMethodType
import java.time.OffsetDateTime
import javax.persistence.Entity
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToOne
import javax.persistence.Table

@Entity
@Table(name = "T_PAYMENT_METHOD")
data class PaymentMethodEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    var token: String = "",

    @ManyToOne
    @JoinColumn(name = "account_fk")
    var account: AccountEntity = AccountEntity(),

    @OneToOne
    @JoinColumn(name = "phone_fk")
    var phone: PhoneEntity? = null,

    var ownerName: String = "",

    @Enumerated
    val type: PaymentMethodType = PaymentMethodType.UNKNOWN,

    @Enumerated
    var provider: PaymentMethodProvider = UNKNOWN,

    val created: OffsetDateTime = OffsetDateTime.now(),
    val updated: OffsetDateTime = OffsetDateTime.now(),
    var deleted: OffsetDateTime? = null,
    var isDeleted: Boolean = false
)
