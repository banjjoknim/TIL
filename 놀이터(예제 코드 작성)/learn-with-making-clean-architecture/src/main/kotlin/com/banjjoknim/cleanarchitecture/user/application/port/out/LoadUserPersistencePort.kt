package com.banjjoknim.cleanarchitecture.user.application.port.out

import com.banjjoknim.cleanarchitecture.user.domain.User

interface LoadUserPersistencePort {
    fun loadUser(userId: Long): User
}
