package com.github.banjjoknim.springserversentevent.chatting.api

import com.github.banjjoknim.springserversentevent.chatting.application.ConnectService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class ConnectController(
    private val connectService: ConnectService
) {

    @PostMapping("/connect")
    fun connect(@RequestBody request: ConnectRequest) {
        connectService.connect(request)
    }
}
