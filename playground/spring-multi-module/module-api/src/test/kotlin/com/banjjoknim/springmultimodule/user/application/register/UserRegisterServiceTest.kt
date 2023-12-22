package com.banjjoknim.springmultimodule.user.application.register

import com.banjjoknim.springmultimodule.user.User
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class UserRegisterServiceTest {

    private val userRegisterPersistencePort = mockk<UserRegisterPersistencePort>()
    private val userRegisterService = UserRegisterService(userRegisterPersistencePort)

    @Test
    fun `회원을 등록한다`() {
        every { userRegisterPersistencePort.registerUser(any()) } returns User("banjjoknim", 1)
        val requestData = UserRegisterRequestData("banjjoknim")

        val responseData = userRegisterService.registerUser(requestData)

        assertThat(responseData).isEqualTo(UserRegisterResponseData(1))
    }
}
