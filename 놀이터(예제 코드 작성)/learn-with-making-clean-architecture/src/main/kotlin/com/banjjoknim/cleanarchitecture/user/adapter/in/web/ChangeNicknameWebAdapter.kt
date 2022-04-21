package com.banjjoknim.cleanarchitecture.user.adapter.`in`.web

import com.banjjoknim.cleanarchitecture.user.application.port.`in`.ChangeNicknameCommand
import com.banjjoknim.cleanarchitecture.user.application.port.`in`.ChangeNicknamePort
import com.banjjoknim.cleanarchitecture.user.application.port.`in`.ChangeNicknameResult
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/users")
@RestController
class ChangeNicknameWebAdapter(
    private val changeNicknamePort: ChangeNicknamePort
) {
    @PostMapping("")
    fun changeNickname(@RequestBody changeNicknameCommand: ChangeNicknameCommand): ChangeNicknameResult {
        return changeNicknamePort.changeNickname(changeNicknameCommand)
    }
}
