package com.banjjoknim.cleanarchitecture.user.application.port.`in`

import com.banjjoknim.cleanarchitecture.user.adapter.`in`.web.ChangeNicknameRequest
import com.banjjoknim.cleanarchitecture.user.adapter.`in`.web.ChangeNicknameResponse

interface ChangeNicknameUseCase {
    fun changeNickname(changeNicknameRequest: ChangeNicknameRequest): ChangeNicknameResponse
}
