package com.banjjoknim.cleanarchitecture.user.adapter.`in`.web

import com.banjjoknim.cleanarchitecture.user.application.port.`in`.ChangeNicknameRequestData

data class ChangeNicknameRequest(
    val userId: Long,
    val newNickname: String
) {
    fun toData(): ChangeNicknameRequestData {
        return ChangeNicknameRequestData(userId, newNickname)
    }
}
