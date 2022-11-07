package com.banjjoknim.soliddesignpatternsample.solid.srp.after

import com.banjjoknim.soliddesignpatternsample.solid.srp.common.Account
import com.banjjoknim.soliddesignpatternsample.solid.srp.common.AccountRepository
import org.springframework.stereotype.Service

@Service
class DepositAccountService(
    private val accountRepository: AccountRepository
) {
    fun depositAccount(request: DepositAccountRequest): Account {
        val account = accountRepository.findAccount(request.accountId)
        account.deposit(request.amount)
        return account
    }
}
