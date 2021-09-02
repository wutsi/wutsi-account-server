package com.wutsi.platform.account.service

import com.wutsi.platform.account.dao.AccountRepository
import com.wutsi.platform.account.entity.AccountEntity
import com.wutsi.platform.account.util.ErrorURN
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.Parameter
import com.wutsi.platform.core.error.ParameterType
import com.wutsi.platform.core.error.ParameterType.PARAMETER_TYPE_PATH
import com.wutsi.platform.core.error.exception.NotFoundException
import org.springframework.stereotype.Service
import javax.persistence.EntityManager
import javax.persistence.Query

@Service
class AccountService(
    private val dao: AccountRepository,
    private val em: EntityManager
) {
    fun findById(id: Long, parameterType: ParameterType = PARAMETER_TYPE_PATH): AccountEntity {
        val account = dao.findById(id)
            .orElseThrow {
                NotFoundException(
                    error = Error(
                        code = ErrorURN.ACCOUNT_NOT_FOUND.urn,
                        parameter = Parameter(
                            name = "id",
                            value = id,
                            type = parameterType
                        )
                    )
                )
            }

        if (account.isDeleted)
            throw NotFoundException(
                error = Error(
                    code = ErrorURN.ACCOUNT_DELETED.urn,
                    parameter = Parameter(
                        name = "id",
                        value = id,
                        type = parameterType
                    )
                )
            )

        return account
    }

    fun search(
        phoneId: Long? = null,
        phoneNumber: String,
        limit: Int = 20,
        offset: Int = 0
    ): List<AccountEntity> {
        val query = em.createQuery(sql(phoneId, phoneNumber))
        parameters(phoneId, phoneNumber, query)
        return query
            .setFirstResult(offset)
            .setMaxResults(limit)
            .resultList as List<AccountEntity>
    }

    private fun sql(phoneId: Long?, phoneNumber: String): String {
        val select = select()
        val where = where(phoneId, phoneNumber)

        return if (where.isNullOrEmpty())
            select
        else
            "$select WHERE $where"
    }

    private fun select(): String =
        "SELECT a FROM AccountEntity a"

    private fun where(phoneId: Long?, phoneNumber: String): String {
        val criteria = mutableListOf("a.isDeleted=:is_deleted")
        if (!phoneNumber.isNullOrEmpty())
            criteria.add("a.phone.number=:phone_number")
        if (phoneId != null)
            criteria.add("a.phone.id=:phone_id")
        return criteria.joinToString(separator = " AND ")
    }

    private fun parameters(phoneId: Long?, phoneNumber: String, query: Query) {
        query.setParameter("is_deleted", false)
        if (phoneId != null)
            query.setParameter("phone_id", phoneId)
        if (!phoneNumber.isNullOrEmpty()) {
            val xphoneNumber = phoneNumber.trim()
            query.setParameter(
                "phone_number",
                if (xphoneNumber.startsWith("+")) xphoneNumber else "+$xphoneNumber"
            )
        }
    }
}
