package com.banjjoknim.cleanarchitecture.user.application.port.out

import com.banjjoknim.cleanarchitecture.user.pojo.User

interface LoadUserPersistencePort {
    fun loadUser(userId: Long): User
}
