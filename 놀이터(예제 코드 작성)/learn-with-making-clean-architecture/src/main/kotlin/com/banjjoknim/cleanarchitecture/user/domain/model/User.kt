package com.banjjoknim.cleanarchitecture.user.domain.model

import com.banjjoknim.cleanarchitecture.user.domain.entity.NicknameColumn
import com.banjjoknim.cleanarchitecture.user.domain.entity.UserEntity

class User(
    var id: Long = 0L,
    var nickname: Nickname
) {
    fun changeNickname(newNickname: String) {
        this.nickname = Nickname(newNickname)
    }

    fun toDomainEntity(): UserEntity {
        return UserEntity(id, NicknameColumn(nickname.value))
    }
}
