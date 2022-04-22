package com.banjjoknim.cleanarchitecture.user.adapter.out.persistence

import org.springframework.data.jpa.repository.JpaRepository

interface UserEntityRepository: JpaRepository<UserEntity, Long> {
}
