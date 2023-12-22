package com.banjjoknim.soliddesignpatternsample.solid.srp.common

interface AccountRepository {
    fun createAccount(holderName: String): Account

    fun findAccount(accountId: Long): Account
}
