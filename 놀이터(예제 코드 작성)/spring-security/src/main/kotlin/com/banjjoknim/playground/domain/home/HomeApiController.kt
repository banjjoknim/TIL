package com.banjjoknim.playground.domain.home

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

/**
 * `@CrossOrigin` 어노테이션을 사용하더라도 Security 인증이 필요한 요청은 전부 거부된다.
 *
 * `@CrossOrigin` 어노테이션은 인증이 필요하지 않은 요청만 허용해준다.
 *
 * @see org.springframework.web.bind.annotation.CrossOrigin
 */
//@CrossOrigin("*")
@RestController
class HomeApiController {

    @GetMapping("/home")
    fun home(): String {
        return "<h1>home</h1>"
    }

    @PostMapping("/token")
    fun token(): String {
        return "<h1>token</h1>"
    }
}
