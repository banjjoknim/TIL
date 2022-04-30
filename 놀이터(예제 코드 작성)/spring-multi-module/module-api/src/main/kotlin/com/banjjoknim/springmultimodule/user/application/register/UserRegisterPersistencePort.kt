package com.banjjoknim.springmultimodule.user.application.register

import com.banjjoknim.springmultimodule.user.User

interface UserRegisterPersistencePort {
    fun registerUser(user: User): User
}
