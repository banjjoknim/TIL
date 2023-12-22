package com.banjjoknim.soliddesignpatternsample.solid.srp.after

import com.banjjoknim.soliddesignpatternsample.solid.srp.common.Account
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/accounts")
@RestController
class CreateAccountController(
    private val createAccountService: CreateAccountService
) {
    @PostMapping("")
    fun createAccount(@RequestBody request: CreateAccountRequest): Account {
        return createAccountService.createAccount(request)
    }
}
