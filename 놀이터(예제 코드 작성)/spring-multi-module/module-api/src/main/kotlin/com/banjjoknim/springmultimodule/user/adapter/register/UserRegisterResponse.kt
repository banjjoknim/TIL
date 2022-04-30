package com.banjjoknim.springmultimodule.user.adapter.register

import com.banjjoknim.springmultimodule.user.application.register.UserRegisterResponseData

data class UserRegisterResponse(
    val userId: Long
) {
    constructor(responseData: UserRegisterResponseData) : this(
        userId = responseData.userId
    )
}
