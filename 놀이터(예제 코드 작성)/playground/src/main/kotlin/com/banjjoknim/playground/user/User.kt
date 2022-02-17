package com.banjjoknim.playground.user

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = 0L,

    @Column(name = "name")
    val name: String = "",

    @Column(name = "email")
    val email: String = "",

    @Column(name = "phoneNumber")
    val phoneNumber: String = ""
) {
}
