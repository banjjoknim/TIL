package com.banjjoknim.cleanarchitecture.user.adapter.out.persistence

import com.banjjoknim.cleanarchitecture.user.pojo.Nickname
import com.banjjoknim.cleanarchitecture.user.pojo.User
import org.springframework.stereotype.Component

@Component
class UserMapper {
    fun mapToDomainEntity(user: User): UserEntity {
        return UserEntity(user.id, NicknameColumn(user.nickname.value))
    }

    fun mapToDomainModel(userEntity: UserEntity): User {
        return User(userEntity.id, Nickname(userEntity.nickname.value))
    }
}
