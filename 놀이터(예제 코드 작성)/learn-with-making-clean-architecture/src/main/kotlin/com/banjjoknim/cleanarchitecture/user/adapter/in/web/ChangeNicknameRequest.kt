package com.banjjoknim.cleanarchitecture.user.adapter.`in`.web

import com.banjjoknim.cleanarchitecture.user.application.port.`in`.ChangeNicknameRequestData
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

data class ChangeNicknameRequest(
    val userId: Long,
    @field:NotBlank
    @field:Size(max = NICKNAME_LENGTH_LIMIT)
    val newNickname: String
) {
    fun toData(): ChangeNicknameRequestData {
        return ChangeNicknameRequestData(userId, newNickname)
    }

    companion object {
        private const val NICKNAME_LENGTH_LIMIT = 10
    }
}
