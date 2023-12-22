package com.github.banjjoknim.springserversentevent.chatting.api

import com.github.banjjoknim.springserversentevent.chatting.application.SendMessageService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class SendMessageController(
    private val sendMessageService: SendMessageService
) {

    @PostMapping("/send-message")
    fun sendMessage(@RequestBody request: SendMessageRequest) {
        sendMessageService.sendMessage(request)
    }
}
