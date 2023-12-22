package com.banjjoknim.playground.jwt.domain.user

import com.banjjoknim.playground.jwt.config.security.PrincipalDetails
import org.springframework.security.core.Authentication
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.GetMapping
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

    // user 권한만 접근 가능
    @GetMapping("/api/v1/user")
    fun user(@AuthenticationPrincipal authentication: Authentication): String {
        val principalDetails = authentication.principal as PrincipalDetails
        println("Authentication: ${principalDetails.username}")
        return "user"
    }

    // manager, admin 권한만 접근 가능
    @GetMapping("/api/v1/manager")
    fun manager(): String {
        return "manager"
    }

    // admin 권한만 접근 가능
    @GetMapping("/api/v1/admin")
    fun admin(): String {
        return "admin"
    }
}
