package com.wutsi.platform.account.endpoint

import com.wutsi.platform.account.dto.ListCategoryResponse
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ListCategoriesControllerTest : AbstractSecuredController() {
    @LocalServerPort
    public val port: Int = 0

    @Test
    public fun invoke() {
        val rest = createResTemplate(listOf("user-read"), subjectId = 100)
        val url = "http://localhost:$port/v1/categories"
        val response = rest.getForEntity(url, ListCategoryResponse::class.java)

        assertEquals(200, response.statusCodeValue)

        assertEquals(48, response.body!!.categories.size)
    }
}
