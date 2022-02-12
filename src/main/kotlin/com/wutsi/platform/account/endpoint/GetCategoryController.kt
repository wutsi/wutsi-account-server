package com.wutsi.platform.account.endpoint

import com.wutsi.platform.account.`delegate`.GetCategoryDelegate
import com.wutsi.platform.account.dto.GetCategoryResponse
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.`annotation`.GetMapping
import org.springframework.web.bind.`annotation`.PathVariable
import org.springframework.web.bind.`annotation`.RestController
import kotlin.Long

@RestController
public class GetCategoryController(
    private val `delegate`: GetCategoryDelegate
) {
    @GetMapping("/v1/categories/{id}")
    @PreAuthorize(value = "hasAuthority('user-read')")
    public fun invoke(@PathVariable(name = "id") id: Long): GetCategoryResponse = delegate.invoke(id)
}
