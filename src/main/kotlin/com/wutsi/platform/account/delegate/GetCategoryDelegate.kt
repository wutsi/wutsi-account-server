package com.wutsi.platform.account.`delegate`

import com.wutsi.platform.account.dao.CategoryRepository
import com.wutsi.platform.account.dto.GetCategoryResponse
import com.wutsi.platform.account.error.ErrorURN
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.Parameter
import com.wutsi.platform.core.error.ParameterType
import com.wutsi.platform.core.error.exception.NotFoundException
import org.springframework.stereotype.Service

@Service
public class GetCategoryDelegate(private val dao: CategoryRepository) {
    public fun invoke(id: Long): GetCategoryResponse =
        GetCategoryResponse(
            category = dao.findById(id)
                .orElseThrow {
                    NotFoundException(
                        error = Error(
                            code = ErrorURN.CATEGORY_NOT_FOUND.urn,
                            parameter = Parameter(
                                name = "id",
                                value = id,
                                type = ParameterType.PARAMETER_TYPE_PATH
                            )
                        )
                    )
                }
                .toCategory()
        )
}
