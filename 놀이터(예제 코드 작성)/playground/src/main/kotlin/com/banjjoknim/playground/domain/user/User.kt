package com.banjjoknim.playground.domain.user

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L,
    var username: String,
    var password: String,
    var email: String,
    var role: String // ROLE_USER, ROLE_MANAGER, ROLE_ADMIN ...
)
