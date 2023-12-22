package com.banjjoknim.cleanarchitecture.user.adapter.out.persistence

import com.banjjoknim.cleanarchitecture.user.application.port.out.UpsertUserPersistencePort
import com.banjjoknim.cleanarchitecture.user.pojo.User
import org.springframework.stereotype.Component

@Component
class UpsertUserPersistenceAdapter(
    private val userMapper: UserMapper,
    private val userEntityRepository: UserEntityRepository
) : UpsertUserPersistencePort {
    override fun upsertUser(user: User) {
        val userEntity = userMapper.mapToDomainEntity(user)
        userEntityRepository.save(userEntity)
    }
}
