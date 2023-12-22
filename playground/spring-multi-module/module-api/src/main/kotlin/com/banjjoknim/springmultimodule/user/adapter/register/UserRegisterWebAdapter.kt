package com.banjjoknim.springmultimodule.user.adapter.register

import com.banjjoknim.springmultimodule.user.application.register.UserRegisterUseCase
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RequestMapping("/users")
@RestController
class UserRegisterWebAdapter(
    private val userRegisterUseCase: UserRegisterUseCase
) {
    @PostMapping("")
    fun registerUser(@RequestBody @Valid userRegisterRequest: UserRegisterRequest): ResponseEntity<UserRegisterResponse> {
        val requestData = userRegisterRequest.toData()
        val responseData = userRegisterUseCase.registerUser(requestData)
        return ResponseEntity.ok(UserRegisterResponse(responseData))
    }
}
