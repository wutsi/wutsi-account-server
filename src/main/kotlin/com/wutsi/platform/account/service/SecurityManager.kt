package com.wutsi.platform.account.service

import com.wutsi.platform.account.entity.AccountEntity
import com.wutsi.platform.core.security.WutsiPrincipal
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class SecurityManager {
    companion object {
        const val SCOPE_PHONE = "user-phone"
    }

    fun checkOwnership(account: AccountEntity) {
        if (!isOwner(account))
            throw AccessDeniedException("Current user not owner of hte Account")
    }

    fun canAccessPhone(account: AccountEntity): Boolean =
        isOwner(account) || hasAuthority(SCOPE_PHONE)

    private fun isOwner(account: AccountEntity): Boolean {
        val authentication = SecurityContextHolder.getContext().authentication
            ?: return false
        val principal = authentication.principal
        return principal is WutsiPrincipal &&
            (account.id.toString() == principal.id || principal.admin)
    }

    private fun hasAuthority(authority: String): Boolean {
        val authentication = SecurityContextHolder.getContext().authentication
        val found = authentication?.authorities?.find { it.authority.equals(authority) }
        if (found != null)
            return true

        val principal = authentication.principal
        return principal is WutsiPrincipal && principal.admin
    }
}
