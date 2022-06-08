package com.wutsi.platform.account.endpoint

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.platform.account.LanguageClientHttpRequestInterceptor
import com.wutsi.platform.account.dto.GetCategoryResponse
import com.wutsi.platform.account.error.ErrorURN
import com.wutsi.platform.core.error.ErrorResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.web.client.HttpStatusCodeException
import org.springframework.web.client.RestTemplate
import kotlin.test.assertEquals

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GetCategoryControllerTest : AbstractSecuredController() {
    @LocalServerPort
    val port: Int = 0

    private lateinit var rest: RestTemplate

    @BeforeEach
    override fun setUp() {
        super.setUp()

        rest = createResTemplate(subjectId = 100)
    }

    @Test
    fun get() {
        val url = "http://localhost:$port/v1/categories/1000"
        val response = rest.getForEntity(url, GetCategoryResponse::class.java)

        assertEquals(200, response.statusCodeValue)

        val category = response.body!!.category
        assertEquals(1000, category.id)
        assertEquals("Advertising/Marketing", category.title)
    }

    @Test
    fun french() {
        val url = "http://localhost:$port/v1/categories/1000"
        rest.interceptors.add(LanguageClientHttpRequestInterceptor("fr"))
        val response = rest.getForEntity(url, GetCategoryResponse::class.java)

        assertEquals(200, response.statusCodeValue)

        val category = response.body!!.category
        assertEquals(1000, category.id)
        assertEquals("Marketing/Publicité", category.title)
    }

    @Test
    fun `invalid id`() {
        val url = "http://localhost:$port/v1/categories/999999"

        val ex = assertThrows<HttpStatusCodeException> {
            rest.getForEntity(url, GetCategoryResponse::class.java)
        }
        assertEquals(404, ex.rawStatusCode)

        val response = ObjectMapper().readValue(ex.responseBodyAsString, ErrorResponse::class.java)
        assertEquals(ErrorURN.CATEGORY_NOT_FOUND.urn, response.error.code)
    }
}
