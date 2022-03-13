package com.banjjoknim.playground.jackson.common

import com.banjjoknim.playground.jackson.jsonserialize.UsingJsonSerializeAnnotationCarSerializer
import com.fasterxml.jackson.databind.annotation.JsonSerialize

data class Car(
    val name: String,
    val price: Int = 10000000,
    val owner: Owner = Owner()
)

data class CarUsingNoAnnotation(
    val name: String = "banjjoknim",
    val secret: String = "secret",
    val price: Int = 10000000,
    val owner: Owner = Owner()
)

data class CarUsingJsonSerializeAnnotation(
    val name: String = "banjjoknim",
    @JsonSerialize(using = UsingJsonSerializeAnnotationCarSerializer::class)
    val secret: String = "secret",
    val price: Int = 10000000,
    val owner: Owner = Owner()
)

data class CarUsingSecretAnnotation(
    val name: String = "banjjoknim",
    @field:Secret("****")
    val secret: String = "secret",
    val price: Int = 10000000,
    val owner: Owner = Owner()
)
