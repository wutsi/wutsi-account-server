package com.wutsi.platform.account.`delegate`

import com.wutsi.platform.account.dao.CategoryRepository
import com.wutsi.platform.account.dto.GetAccountResponse
import com.wutsi.platform.account.service.AccountService
import com.wutsi.platform.account.service.ImageKit
import com.wutsi.platform.account.service.SecurityManager
import org.springframework.stereotype.Service

@Service
public class GetAccountDelegate(
    private val service: AccountService,
    private val securityManager: SecurityManager,
    private val imageKit: ImageKit,
    private val categoryDao: CategoryRepository
) {
    fun invoke(id: Long): GetAccountResponse {
        val account = service.findById(id)
        val category = account.categoryId?.let { categoryDao.findById(it).orElse(null) }
        securityManager.checkTenant(account)

        return GetAccountResponse(
            account = account.toAccount(securityManager, imageKit, category)
        )
    }
}
