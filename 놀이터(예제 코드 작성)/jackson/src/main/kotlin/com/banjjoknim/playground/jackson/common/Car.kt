package com.banjjoknim.playground.jackson.common

data class Car(
    val name: String,
    val price: Int = 0,
    val owner: Owner
)
