package com.banjjoknim.soliddesignpatternsample.solid.srp.before

import com.banjjoknim.soliddesignpatternsample.solid.srp.common.Account
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/accounts")
@RestController
class AccountControllerBefore(
    private val accountServiceBefore: AccountServiceBefore
) {
    @PatchMapping("/manage-account-before")
    fun manageAccount(@RequestBody request: ManageAccountRequest): Account {
        return accountServiceBefore.manageAccount(request)
    }
}
