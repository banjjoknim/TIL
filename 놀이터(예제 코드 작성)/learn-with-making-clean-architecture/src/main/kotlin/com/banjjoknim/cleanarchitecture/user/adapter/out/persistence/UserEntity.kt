package com.banjjoknim.cleanarchitecture.user.adapter.out.persistence

import com.banjjoknim.cleanarchitecture.user.domain.Nickname
import com.banjjoknim.cleanarchitecture.user.domain.User
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
