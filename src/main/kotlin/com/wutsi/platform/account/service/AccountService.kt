package com.wutsi.platform.account.service

import com.wutsi.platform.account.dao.AccountRepository
import com.wutsi.platform.account.dto.SearchAccountRequest
import com.wutsi.platform.account.entity.AccountEntity
import com.wutsi.platform.account.error.ErrorURN
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
    private val em: EntityManager,
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

    fun search(request: SearchAccountRequest): List<AccountEntity> {
        val query = em.createQuery(sql(request))
        parameters(request, query)
        return query
            .setFirstResult(request.offset)
            .setMaxResults(request.limit)
            .resultList as List<AccountEntity>
    }

    private fun sql(request: SearchAccountRequest): String {
        val select = select()
        val where = where(request)
        return if (where.isNullOrEmpty())
            select
        else
            "$select WHERE $where"
    }

    private fun select(): String =
        "SELECT a FROM AccountEntity a"

    private fun where(request: SearchAccountRequest): String {
        val criteria = mutableListOf("a.isDeleted=:is_deleted")
        if (!request.phoneNumber.isNullOrEmpty())
            criteria.add("a.phone.number=:phone_number")
        if (request.ids.isNotEmpty())
            criteria.add("a.id IN :ids")
        return criteria.joinToString(separator = " AND ")
    }

    private fun parameters(request: SearchAccountRequest, query: Query) {
        query.setParameter("is_deleted", false)
        if (!request.phoneNumber.isNullOrEmpty())
            query.setParameter("phone_number", normalizePhoneNumber(request.phoneNumber))
        if (request.ids.isNotEmpty())
            query.setParameter("ids", request.ids)
    }

    private fun normalizePhoneNumber(phoneNumber: String?): String? {
        phoneNumber ?: return null

        val value = phoneNumber.trim()
        return if (value.startsWith("+"))
            value
        else
            "+$value"
    }
}
