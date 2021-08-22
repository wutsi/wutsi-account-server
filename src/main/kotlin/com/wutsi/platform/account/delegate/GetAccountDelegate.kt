package com.wutsi.platform.account.`delegate`

import com.wutsi.platform.account.dto.Account
import com.wutsi.platform.account.dto.GetAccountResponse
import com.wutsi.platform.account.dto.Phone
import com.wutsi.platform.account.service.AccountService
import org.springframework.stereotype.Service

@Service
public class GetAccountDelegate(private val service: AccountService) {
    public fun invoke(id: Long): GetAccountResponse {
        val account = service.findById(id)
        return GetAccountResponse(
            account = Account(
                id = account.id ?: -1,
                displayName = account.displayName,
                pictureUrl = account.pictureUrl,
                created = account.created,
                updated = account.updated,
                phone = Phone(
                    id = account.phone!!.id ?: -1,
                    number = account.phone!!.number,
                    country = account.phone!!.country,
                    created = account.phone!!.created
                )
            )
        )
    }
}
