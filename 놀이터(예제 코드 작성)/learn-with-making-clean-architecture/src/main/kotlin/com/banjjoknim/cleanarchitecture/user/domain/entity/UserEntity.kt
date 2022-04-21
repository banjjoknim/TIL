package com.banjjoknim.cleanarchitecture.user.domain.entity

import com.banjjoknim.cleanarchitecture.user.domain.model.Nickname
import com.banjjoknim.cleanarchitecture.user.domain.model.User
import javax.persistence.Embedded
import javax.persistence.Entity
import javax.persistence.Id

@Entity
class UserEntity(
    @Id
    val id: Long = 0L,
    @Embedded
    var nickname: NicknameColumn
) {
    fun toDomainModel(): User {
        return User(id, Nickname(nickname.value))
    }
}
