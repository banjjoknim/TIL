package com.banjjoknim.springmultimodule.user.adapter.register

import com.banjjoknim.springmultimodule.user.User
import com.banjjoknim.springmultimodule.user.UserRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.data.repository.findByIdOrNull

@DataJpaTest
@Import(value = [UserRegisterPersistenceAdapter::class])
class UserRegisterPersistenceAdapterTest {

    @Autowired
    private lateinit var userRegisterPersistenceAdapter: UserRegisterPersistenceAdapter

    @Autowired
    private lateinit var userRepository: UserRepository

    @Test
    fun `회원을 등록한다`() {
        userRegisterPersistenceAdapter.registerUser(User("banjjoknim"))

        val user = userRepository.findByIdOrNull(1)

        assertThat(user).isNotNull
        assertThat(user?.name).isEqualTo("banjjoknim")
    }
}
