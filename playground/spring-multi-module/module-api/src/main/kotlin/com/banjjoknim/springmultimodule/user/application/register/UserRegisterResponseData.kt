package com.banjjoknim.springmultimodule.user.application.register

import com.banjjoknim.springmultimodule.user.User

data class UserRegisterResponseData(val userId: Long) {
    constructor(user: User) : this(
        userId = user.id
    )
}
