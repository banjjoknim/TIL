package com.banjjoknim.springmultimodule.user.application.register

import com.banjjoknim.springmultimodule.user.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserRegisterService(
    private val userRegisterPersistencePort: UserRegisterPersistencePort
) : UserRegisterUseCase {
    @Transactional
    override fun registerUser(requestData: UserRegisterRequestData): UserRegisterResponseData {
        val newUser = User(name = requestData.name)
        val user = userRegisterPersistencePort.registerUser(newUser)
        return UserRegisterResponseData(user)
    }
}
