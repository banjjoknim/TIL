package com.banjjoknim.cleanarchitecture.user.adapter.`in`.web

import com.banjjoknim.cleanarchitecture.user.application.port.`in`.ChangeNicknameUseCase
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RequestMapping("/users")
@RestController
class ChangeNicknameWebAdapter(
    private val changeNicknameWebPort: ChangeNicknameUseCase
) {
    @PostMapping("")
    fun changeNickname(@RequestBody @Valid changeNicknameRequest: ChangeNicknameRequest): ChangeNicknameResponse {
        val requestData = changeNicknameRequest.toData()
        val responseData = changeNicknameWebPort.changeNickname(requestData)
        return ChangeNicknameResponse(responseData.userId)
    }
}
