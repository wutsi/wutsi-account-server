package com.wutsi.platform.account.`delegate`

import com.wutsi.platform.account.dto.SearchAccountResponse
import com.wutsi.platform.account.entity.AccountEntity
import org.springframework.stereotype.Service
import javax.persistence.EntityManager
import javax.persistence.Query

@Service
public class SearchAccountDelegate(
    private val em: EntityManager
) {
    public fun invoke(
        phoneNumber: String,
        limit: Int = 20,
        offset: Int = 0
    ): SearchAccountResponse {
        val query = em.createQuery(sql(phoneNumber))
        parameters(phoneNumber, query)
        val accounts = query
            .setFirstResult(offset)
            .setMaxResults(limit)
            .resultList as List<AccountEntity>

        return SearchAccountResponse(
            accounts = accounts.map { it.toAccountSummary() }
        )
    }

    private fun sql(phoneNumber: String): String {
        val select = select()
        val where = where(phoneNumber)

        return if (where.isNullOrEmpty())
            select
        else
            "$select WHERE $where"
    }

    private fun select(): String =
        "SELECT a FROM AccountEntity a"

    private fun where(phoneNumber: String): String {
        val criteria = mutableListOf("a.isDeleted=:is_deleted")
        if (!phoneNumber.isNullOrEmpty()) {
            criteria.add("a.phone.number=:phoneNumber")
        }
        return criteria.joinToString(separator = " AND ")
    }

    private fun parameters(phoneNumber: String, query: Query) {
        query.setParameter("is_deleted", false)
        if (!phoneNumber.isNullOrEmpty()) {
            val xphoneNumber = phoneNumber.trim()
            query.setParameter(
                "phoneNumber",
                if (xphoneNumber.startsWith("+")) xphoneNumber else "+$xphoneNumber"
            )
        }
    }
}
