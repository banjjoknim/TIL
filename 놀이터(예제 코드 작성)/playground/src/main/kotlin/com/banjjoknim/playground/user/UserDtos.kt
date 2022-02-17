package com.banjjoknim.playground.user

import javax.validation.constraints.NotBlank

data class CreateUserRequest(
    @field:NotBlank(message = "이름을 입력해주세요")
    val name: String,
    @field:NotBlank(message = "이메일을 입력해주세요")
    val email: String,
    @field:NotBlank(message = "휴대폰 번호를 입력해주세요")
    val phoneNumber: String
) {
    fun toUser(): User {
        return User(name = name, email = email, phoneNumber = phoneNumber)
    }
}
