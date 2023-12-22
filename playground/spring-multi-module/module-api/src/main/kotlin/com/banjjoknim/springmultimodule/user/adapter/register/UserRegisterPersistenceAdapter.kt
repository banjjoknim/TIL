package com.banjjoknim.springmultimodule.user.adapter.register

import com.banjjoknim.springmultimodule.user.User
import com.banjjoknim.springmultimodule.user.UserRepository
import com.banjjoknim.springmultimodule.user.application.register.UserRegisterPersistencePort
import org.springframework.stereotype.Repository

@Repository
class UserRegisterPersistenceAdapter(
    private val userRepository: UserRepository
) : UserRegisterPersistencePort {
    override fun registerUser(user: User): User {
        return userRepository.save(user)
    }
}
