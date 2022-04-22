package com.banjjoknim.cleanarchitecture.user.pojo

import com.banjjoknim.cleanarchitecture.user.adapter.out.persistence.NicknameColumn
import com.banjjoknim.cleanarchitecture.user.adapter.out.persistence.UserEntity

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
