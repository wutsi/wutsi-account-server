package com.wutsi.platform.account.service

import com.wutsi.platform.account.dao.PasswordRepository
import com.wutsi.platform.account.dto.SavePasswordRequest
import com.wutsi.platform.account.entity.AccountEntity
import com.wutsi.platform.account.entity.PasswordEntity
import org.apache.commons.codec.digest.DigestUtils
import org.springframework.stereotype.Service
import java.util.UUID

@Service
public class PasswordService(private val dao: PasswordRepository) {
    fun set(account: AccountEntity, request: SavePasswordRequest) {
        val opt = dao.findByAccount(account)
        if (opt.isPresent) {
            val obj = opt.get()
            obj.value = hash(request.password, obj.salt)
            dao.save(obj)
        } else {
            val salt = UUID.randomUUID().toString()
            dao.save(
                PasswordEntity(
                    account = account,
                    salt = salt,
                    value = hash(request.password, salt)
                )
            )
        }
    }

    fun hash(password: String, salt: String): String =
        DigestUtils.md5Hex("$password.$salt")
}
