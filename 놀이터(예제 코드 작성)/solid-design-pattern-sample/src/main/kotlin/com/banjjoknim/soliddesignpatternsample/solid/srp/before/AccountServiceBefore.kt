package com.banjjoknim.soliddesignpatternsample.solid.srp.before

import com.banjjoknim.soliddesignpatternsample.solid.srp.common.Account
import com.banjjoknim.soliddesignpatternsample.solid.srp.common.AccountRepository
import org.springframework.stereotype.Service

@Service
class AccountServiceBefore(
    private val accountRepository: AccountRepository
) {
    fun manageAccount(request: ManageAccountRequest): Account {
        return when (request.type) {
            ManageAccountType.CREATE -> {
                accountRepository.createAccount(request.holderName)
            }
            ManageAccountType.DEPOSIT -> {
                val account = accountRepository.findAccount(request.accountId)
                account.deposit(request.amount)
                account
            }
            ManageAccountType.WITHDRAWAL -> {
                val account = accountRepository.findAccount(request.accountId)
                account.withdrawal(request.amount)
                account
            }
        }
    }
}
