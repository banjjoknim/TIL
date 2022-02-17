package com.banjjoknim.playground.user

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RequestMapping("/users")
@RestController
class UserApi(
    private val userService: UserService
) {
    @PostMapping("")
    fun createUser(@RequestBody @Valid request: CreateUserRequest): ResponseEntity<Unit> {
        userService.createUser(request)
        return ResponseEntity.ok().build()
    }
}
