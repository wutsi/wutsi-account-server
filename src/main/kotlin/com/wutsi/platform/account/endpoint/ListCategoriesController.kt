package com.wutsi.platform.account.endpoint

import com.wutsi.platform.account.`delegate`.ListCategoriesDelegate
import com.wutsi.platform.account.dto.ListCategoryResponse
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.`annotation`.GetMapping
import org.springframework.web.bind.`annotation`.RestController

@RestController
public class ListCategoriesController(
    private val `delegate`: ListCategoriesDelegate
) {
    @GetMapping("/v1/categories")
    @PreAuthorize(value = "hasAuthority('user-read')")
    public fun invoke(): ListCategoryResponse = delegate.invoke()
}
