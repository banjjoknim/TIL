package com.banjjoknim.argocdsample

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloApi {

    @GetMapping("/hello")
    fun hello(): String {
        return "Argo!!"
    }
}