package com.wutsi.platform.account.service

import org.apache.commons.codec.digest.DigestUtils
import org.springframework.stereotype.Service

@Service
public class PasswordHasher {
    fun hash(password: String, salt: String): String =
        DigestUtils.md5Hex("$password.$salt")
}
