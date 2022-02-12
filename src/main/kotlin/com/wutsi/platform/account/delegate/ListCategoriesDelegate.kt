package com.wutsi.platform.account.`delegate`

import com.wutsi.platform.account.dao.CategoryRepository
import com.wutsi.platform.account.dto.ListCategoryResponse
import org.springframework.stereotype.Service

@Service
public class ListCategoriesDelegate(private val dao: CategoryRepository) {
    public fun invoke(): ListCategoryResponse =
        ListCategoryResponse(
            categories = dao.findAll().map { it.toCategorySummary() }
        )
}
