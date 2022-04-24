package com.banjjoknim.cleanarchitecture.user.adapter.out.persistence

import javax.persistence.Embedded
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

/**
 * 패키지를 독립적 배포의 관점에서 바라봤을 때, UserEntity 는 Persistence 영역에 존재하는 것이 타당하다.
 *
 * 만약 그렇지 않다면 UserEntity 가 존재하지 않는 UserEntityRepository 가 배포될 것이기 때문이다.
 */
@Table(name = "User")
@Entity
class UserEntity(
    @Id
    val id: Long = 0L,
    @Embedded
    var nickname: NicknameColumn
)
