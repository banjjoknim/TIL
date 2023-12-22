package com.banjjoknim.cleanarchitecture.user.application.service

import com.banjjoknim.cleanarchitecture.user.application.port.`in`.ChangeNicknameRequestData
import com.banjjoknim.cleanarchitecture.user.application.port.`in`.ChangeNicknameResponseData
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
    override fun changeNickname(data: ChangeNicknameRequestData): ChangeNicknameResponseData {
        val user = loadUserPersistencePort.loadUser(data.userId)
        user.changeNickname(data.newNickname)
        upsertUserPersistencePort.upsertUser(user)
        return ChangeNicknameResponseData(user.id)
    }
}
