package com.banjjoknim.cleanarchitecture.user.application.port.`in`

interface ChangeNicknamePort {
    fun changeNickname(changeNicknameCommand: ChangeNicknameCommand): ChangeNicknameResult
}
