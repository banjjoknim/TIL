package com.banjjoknim.cleanarchitecture.user.application.service

import com.banjjoknim.cleanarchitecture.user.application.port.`in`.ChangeNicknameRequest
import com.banjjoknim.cleanarchitecture.user.application.port.`in`.ChangeNicknameResponse
import com.banjjoknim.cleanarchitecture.user.application.port.`in`.ChangeNicknameUseCase
import com.banjjoknim.cleanarchitecture.user.application.port.out.LoadUserPersistencePort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class ChangeNicknameService(
    private val loadUserPersistencePort: LoadUserPersistencePort,
) : ChangeNicknameUseCase {
    override fun changeNickname(changeNicknameRequest: ChangeNicknameRequest): ChangeNicknameResponse {
        val user = loadUserPersistencePort.loadUser(changeNicknameRequest.userId)
        user.changeNickname(changeNicknameRequest.newNickname)
        return ChangeNicknameResponse(user.id)
    }
}
