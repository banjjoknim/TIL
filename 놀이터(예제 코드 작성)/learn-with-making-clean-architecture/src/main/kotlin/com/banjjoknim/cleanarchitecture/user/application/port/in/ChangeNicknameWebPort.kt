package com.banjjoknim.cleanarchitecture.user.application.port.`in`

interface ChangeNicknameWebPort {
    fun changeNickname(changeNicknameRequest: ChangeNicknameRequest): ChangeNicknameResponse
}
