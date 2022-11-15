package com.banjjoknim.soliddesignpatternsample.solid.srp.after

import com.banjjoknim.soliddesignpatternsample.solid.srp.common.Account
import com.banjjoknim.soliddesignpatternsample.solid.srp.common.AccountRepository
import org.springframework.stereotype.Service

@Service
class CreateAccountService(
    private val accountRepository: AccountRepository
) {
    fun createAccount(request: CreateAccountRequest): Account {
        return accountRepository.createAccount(request.holderName)
    }
}
