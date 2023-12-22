package com.banjjoknim.playground.jwt.domain.user

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class JwtUser(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L,
    var username: String = "",
    var password: String = "",
    var roles: String = "" // USER, ADMIN
) {
    fun getRoles(): List<String> {
        if (roles.isNotEmpty()) {
            return roles.split(",")
        }
        return emptyList()
    }
}
