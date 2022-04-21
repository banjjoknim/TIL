package com.banjjoknim.cleanarchitecture.user.adapter.`in`.web

import com.banjjoknim.cleanarchitecture.user.application.port.`in`.ChangeNicknameRequest
import com.banjjoknim.cleanarchitecture.user.application.port.`in`.ChangeNicknameResponse
import com.banjjoknim.cleanarchitecture.user.application.port.`in`.ChangeNicknameUseCase
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/users")
@RestController
class ChangeNicknameWebAdapter(
    private val changeNicknameWebPort: ChangeNicknameUseCase
) {
    @PostMapping("")
    fun changeNickname(@RequestBody changeNicknameRequest: ChangeNicknameRequest): ChangeNicknameResponse {
        return changeNicknameWebPort.changeNickname(changeNicknameRequest)
    }
}
