package com.banjjoknim.cleanarchitecture.user.application.service

import com.banjjoknim.cleanarchitecture.user.application.port.`in`.ChangeNicknameCommand
import com.banjjoknim.cleanarchitecture.user.application.port.`in`.ChangeNicknamePort
import com.banjjoknim.cleanarchitecture.user.application.port.`in`.ChangeNicknameResult
import com.banjjoknim.cleanarchitecture.user.application.port.out.LoadUserPersistencePort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class ChangeNicknameService(
    private val loadUserAdapter: LoadUserPersistencePort
) : ChangeNicknamePort {
    override fun changeNickname(changeNicknameCommand: ChangeNicknameCommand): ChangeNicknameResult {
        val user = loadUserAdapter.loadUser(changeNicknameCommand.userId)
        user.changeNickname(changeNicknameCommand.newNickname)
        return ChangeNicknameResult(user.id)
    }
}
