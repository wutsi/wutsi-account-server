package com.wutsi.platform.account.endpoint

import com.wutsi.platform.account.dao.AccountRepository
import com.wutsi.platform.account.entity.AccountStatus
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.jdbc.Sql
import org.springframework.web.client.RestTemplate
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(value = ["/db/clean.sql", "/db/DeleteAccountController.sql"])
public class DeleteAccountControllerTest {
    @LocalServerPort
    public val port: Int = 0

    @Autowired
    private lateinit var dao: AccountRepository

    private val rest = RestTemplate()

    @BeforeEach
    fun setUp() {
    }

    @Test
    public fun invoke() {
        val url = "http://localhost:$port/v1/accounts/100"
        rest.delete(url)

        val account = dao.findById(100).get()
        assertEquals(AccountStatus.ACCOUNT_STATUS_DELETED, account.status)
        assertNotNull(account.deleted)
        assertNull(account.phone)
    }

    @Test
    public fun `already deleted`() {
        val url = "http://localhost:$port/v1/accounts/199"
        rest.delete(url)

        val account = dao.findById(199).get()
        assertEquals(AccountStatus.ACCOUNT_STATUS_DELETED, account.status)
        assertEquals(2011, account.deleted?.year)
    }
}
