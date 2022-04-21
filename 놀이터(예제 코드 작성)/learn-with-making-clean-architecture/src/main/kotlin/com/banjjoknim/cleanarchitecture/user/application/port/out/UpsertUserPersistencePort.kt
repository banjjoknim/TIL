package com.banjjoknim.cleanarchitecture.user.application.port.out

import com.banjjoknim.cleanarchitecture.user.domain.model.User

interface UpsertUserPersistencePort {
    fun upsert(user: User)
}
