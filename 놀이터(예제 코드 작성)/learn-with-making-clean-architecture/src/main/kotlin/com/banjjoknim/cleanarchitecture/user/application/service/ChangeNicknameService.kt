package com.banjjoknim.cleanarchitecture.user.application.service

import com.banjjoknim.cleanarchitecture.user.adapter.`in`.web.ChangeNicknameRequest
import com.banjjoknim.cleanarchitecture.user.adapter.`in`.web.ChangeNicknameResponse
import com.banjjoknim.cleanarchitecture.user.application.port.`in`.ChangeNicknameUseCase
import com.banjjoknim.cleanarchitecture.user.application.port.out.LoadUserPersistencePort
import com.banjjoknim.cleanarchitecture.user.application.port.out.UpsertUserPersistencePort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class ChangeNicknameService(
    private val loadUserPersistencePort: LoadUserPersistencePort,
    private val upsertUserPersistencePort: UpsertUserPersistencePort
) : ChangeNicknameUseCase {
    override fun changeNickname(changeNicknameRequest: ChangeNicknameRequest): ChangeNicknameResponse {
        val user = loadUserPersistencePort.loadUser(changeNicknameRequest.userId)
        user.changeNickname(changeNicknameRequest.newNickname)
        upsertUserPersistencePort.upsertUser(user)
        return ChangeNicknameResponse(user.id)
    }
}
