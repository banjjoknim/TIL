package com.banjjoknim.soliddesignpatternsample.solid.srp.common

import org.springframework.stereotype.Repository

@Repository
class AccountRepositoryStub : AccountRepository {
    override fun createAccount(holderName: String): Account {
        return Account(holderName = "banjjoknim", balance = 0)
    }

    override fun findAccount(accountId: Long): Account {
        return Account(id = 1L, balance = 10000)
    }
}
