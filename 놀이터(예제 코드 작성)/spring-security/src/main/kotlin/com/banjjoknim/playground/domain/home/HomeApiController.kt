package com.banjjoknim.playground.domain.home

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HomeApiController {

    @GetMapping("/home")
    fun home(): String {
        return "<h1>home</h1>"
    }
}
