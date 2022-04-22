package com.banjjoknim.cleanarchitecture.user.application.port.out

import com.banjjoknim.cleanarchitecture.user.pojo.User

interface UpsertUserPersistencePort {
    fun upsert(user: User)
}
