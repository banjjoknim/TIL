package com.banjjoknim.playground.domain.user

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RequestMapping("/users")
@RestController
class UserApi(
    private val userService: UserService,
    private val userServiceBaseOnInheritanceEvent: UserServiceBaseOnInheritanceEvent,
    private val userServiceBaseOnAnnotationEvent: UserServiceBaseOnAnnotationEvent
) {
    /**
     * 의존성을 모두 갖고 서비스 내에서 서비스를 호출하는 방식
     */
    @PostMapping("")
    fun createUser(@RequestBody @Valid request: CreateUserRequest): ResponseEntity<Unit> {
        userService.createUser(request)
        return ResponseEntity.ok().build()
    }

    /**
     * 상속 기반의 이벤트 사용 방식
     */
    @PostMapping("/inheritance")
    fun createUserWithInheritanceEvent(@RequestBody @Valid request: CreateUserRequest): ResponseEntity<Unit> {
        userServiceBaseOnInheritanceEvent.createUser(request)
        return ResponseEntity.ok().build()
    }

    /**
     * 어노테이션 기반의 이벤트 사용 방식
     */
    @PostMapping("/annotation")
    fun createUserWithAnnotationEvent(@RequestBody @Valid request: CreateUserRequest): ResponseEntity<Unit> {
        userServiceBaseOnAnnotationEvent.createUser(request)
        return ResponseEntity.ok().build()
    }

    /**
     * 트랜잭션 이벤트 리스너 어노테이션을 이용한 이벤트 사용 방식
     */
    @PostMapping("/transactional")
    fun createUserWithTransactionalEventListener(@RequestBody @Valid request: CreateUserRequest): ResponseEntity<Unit> {
        userServiceBaseOnAnnotationEvent.createUserWithTransactionalEventListener(request)
        return ResponseEntity.ok().build()
    }

    /**
     * 비동기 이벤트 리스너를 이용한 이벤트 사용 방식
     */
    @PostMapping("/async")
    fun createUserWithAsyncEventListener(@RequestBody @Valid request: CreateUserRequest): ResponseEntity<Unit> {
        userServiceBaseOnAnnotationEvent.createUserWithAsyncEventListener(request)
        return ResponseEntity.ok().build()
    }

    @GetMapping("/{userId}")
    fun retrieveUser(@PathVariable userId: Long): ResponseEntity<RetrieveUserResponse> {
        val response = userService.retrieveUser(userId)
        return ResponseEntity.ok(response)
    }
}
