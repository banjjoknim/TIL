package com.banjjoknim.springmultimodule.user.adapter.register

import com.banjjoknim.springmultimodule.user.application.register.UserRegisterRequestData
import javax.validation.constraints.NotBlank

data class UserRegisterRequest(
    @NotBlank
    val name: String = ""
) {
    fun toData(): UserRegisterRequestData {
        return UserRegisterRequestData(name)
    }
}
