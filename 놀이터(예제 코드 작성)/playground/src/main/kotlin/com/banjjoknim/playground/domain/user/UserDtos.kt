package com.banjjoknim.playground.domain.user

import javax.validation.constraints.NotBlank

data class CreateUserRequest(
    @field:NotBlank(message = "이름을 입력해주세요")
    val name: String = "",
    @field:NotBlank(message = "이메일을 입력해주세요")
    val email: String = "",
    @field:NotBlank(message = "휴대폰 번호를 입력해주세요")
    val phoneNumber: String = ""
) {
    fun toUser(): User {
        return User(name = name, email = email, phoneNumber = phoneNumber)
    }
}

data class RetrieveUserResponse(
    val name: String,
    val email: String,
    val phoneNumber: String
) {
    constructor(user: User) : this(user.name, user.email, user.phoneNumber)
}
