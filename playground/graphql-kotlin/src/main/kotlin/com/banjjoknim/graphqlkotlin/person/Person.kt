package com.banjjoknim.graphqlkotlin.person

import java.time.LocalDateTime

data class Person(
    var name: String,
    var age: Long? = 0L,
    var birthDate: LocalDateTime = LocalDateTime.now()
)
