package com.banjjoknim.soliddesignpatternsample.solid.srp.after

import com.banjjoknim.soliddesignpatternsample.solid.srp.common.Account
import com.banjjoknim.soliddesignpatternsample.solid.srp.common.AccountRepository
import org.springframework.stereotype.Service

@Service
class WithdrawalAccountService(
    private val accountRepository: AccountRepository
) {
    fun withdrawalAccount(request: WithdrawalAccountRequest): Account {
        val account = accountRepository.findAccount(request.accountId)
        account.withdrawal(request.amount)
        return account
    }
}
