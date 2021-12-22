package com.wutsi.platform.account.service

import com.wutsi.platform.account.entity.AccountEntity
import com.wutsi.platform.account.entity.PaymentMethodEntity
import com.wutsi.platform.account.error.ErrorURN
import com.wutsi.platform.core.error.Error
import com.wutsi.platform.core.error.exception.ForbiddenException
import com.wutsi.platform.core.security.WutsiPrincipal
import com.wutsi.platform.core.tracing.TracingContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

@Service
class SecurityManager(
    private val tracingContext: TracingContext
) {
    companion object {
        const val PERMISSION_USER_PHONE = "user-phone"
        const val PERMISSION_PAYMENT_DETAILS = "payment-method-details"
    }

    fun checkOwnership(account: AccountEntity): Boolean {
        if (!isOwner(account))
            throw ForbiddenException(
                error = Error(
                    code = ErrorURN.ILLEGAL_ACCOUNT_ACCESS.urn
                )
            )
        return true
    }

    fun checkTenant(account: AccountEntity): Boolean {
        if (account.tenantId != tracingContext.tenantId()?.toLong())
            throw ForbiddenException(
                error = Error(
                    code = ErrorURN.ILLEGAL_TENANT_ACCESS.urn
                )
            )
        return true
    }

    fun canAccessPhone(account: AccountEntity): Boolean =
        isOwner(account) || hasAuthority(PERMISSION_USER_PHONE)

    fun canAccessPaymentMethodDetails(payment: PaymentMethodEntity): Boolean =
        isOwner(payment.account) || hasAuthority(PERMISSION_PAYMENT_DETAILS)

    private fun isOwner(account: AccountEntity): Boolean {
        val authentication = SecurityContextHolder.getContext().authentication
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

    private fun principal(): WutsiPrincipal {
        val authentication = SecurityContextHolder.getContext().authentication
        val principal = authentication.principal
        return principal as WutsiPrincipal
    }
}
