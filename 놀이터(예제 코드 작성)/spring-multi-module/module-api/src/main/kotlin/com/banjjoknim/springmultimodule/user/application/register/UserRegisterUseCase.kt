package com.banjjoknim.springmultimodule.user.application.register

interface UserRegisterUseCase {
    fun registerUser(requestData: UserRegisterRequestData): UserRegisterResponseData
}
