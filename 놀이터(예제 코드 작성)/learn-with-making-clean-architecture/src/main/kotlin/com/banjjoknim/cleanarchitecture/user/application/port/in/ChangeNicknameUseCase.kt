package com.banjjoknim.cleanarchitecture.user.application.port.`in`

interface ChangeNicknameUseCase {
    fun changeNickname(data: ChangeNicknameRequestData): ChangeNicknameResponseData
}
