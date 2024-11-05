package com.banjjoknim.argocdsample

import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloApi {

    @Value("\${APPLICATION_VERSION}")
    private lateinit var applicationVersion: String

    @Value("\${SECRET_NAME}")
    private lateinit var secretName: String

    @GetMapping("/hello")
    fun hello(): String {
        return "hello Argo! my name is... [$secretName]. applicationVersion: [$applicationVersion]"
    }
}