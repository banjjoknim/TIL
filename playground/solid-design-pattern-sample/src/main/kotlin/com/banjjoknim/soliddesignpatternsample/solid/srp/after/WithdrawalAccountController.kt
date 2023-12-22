package com.banjjoknim.soliddesignpatternsample.solid.srp.after

import com.banjjoknim.soliddesignpatternsample.solid.srp.common.Account
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/accounts")
@RestController
class WithdrawalAccountController(
    private val withdrawalAccountService: WithdrawalAccountService
) {
    @PatchMapping("/withdrawal")
    fun withdrawal(@RequestBody request: WithdrawalAccountRequest): Account {
        return withdrawalAccountService.withdrawalAccount(request)
    }
}
