package com.banjjoknim.playground.jwt.domain.user

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class JwtUserController(
    private val jwtUserRepository: JwtUserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    @PostMapping("/join")
    fun join(@RequestBody jwtUser: JwtUser): String {
        jwtUser.password = passwordEncoder.encode(jwtUser.password)
        jwtUser.roles = "ROLE_USER"
        jwtUserRepository.save(jwtUser)
        return "회원가입완료"
    }
}
