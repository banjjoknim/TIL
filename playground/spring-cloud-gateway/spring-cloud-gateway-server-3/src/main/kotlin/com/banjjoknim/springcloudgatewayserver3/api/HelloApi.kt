package com.banjjoknim.springcloudgatewayserver3.api

import org.springframework.http.HttpHeaders
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloApi {

    @GetMapping("/hello")
    fun hello(
        @RequestHeader(name = HttpHeaders.AUTHORIZATION) authorization: String?,
        @RequestHeader(name = "Secret") secret: String?,
    ): String {
        return "hello reactive server 3! authorization: $authorization, secret: $secret"
    }
}