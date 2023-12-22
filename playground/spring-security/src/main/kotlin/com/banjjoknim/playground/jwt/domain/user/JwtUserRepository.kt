package com.banjjoknim.playground.jwt.domain.user

import org.springframework.data.jpa.repository.JpaRepository

interface JwtUserRepository : JpaRepository<JwtUser, Long> {
    fun findByUsername(username: String): JwtUser?
}
