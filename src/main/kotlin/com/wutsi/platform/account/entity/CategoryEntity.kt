package com.wutsi.platform.account.entity

import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "T_CATEGORY")
data class CategoryEntity(
    @Id
    val id: Long? = null,
    val title: String = "",
    val titleFrench: String = ""
)
