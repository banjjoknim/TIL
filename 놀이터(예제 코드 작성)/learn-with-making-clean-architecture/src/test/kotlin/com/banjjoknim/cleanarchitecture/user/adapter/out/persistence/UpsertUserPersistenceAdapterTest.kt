package com.banjjoknim.cleanarchitecture.user.adapter.out.persistence

import com.banjjoknim.cleanarchitecture.user.pojo.Nickname
import com.banjjoknim.cleanarchitecture.user.pojo.User
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.context.jdbc.Sql

@DataJpaTest
@Import(value = [UserMapper::class, UpsertUserPersistenceAdapter::class])
class UpsertUserPersistenceAdapterTest {

    @Autowired
    private lateinit var upsertUserPersistenceAdapter: UpsertUserPersistenceAdapter

    @Autowired
    private lateinit var userEntityRepository: UserEntityRepository

    @Sql(statements = ["INSERT INTO USER VALUES (1, 'old')"])
    @Test
    fun `회원 상태를 저장하거나 수정한다`() {
        val updatedUser = User(1L, Nickname("new"))

        upsertUserPersistenceAdapter.upsertUser(updatedUser)

        val userEntity = userEntityRepository.findByIdOrNull(1L)
        assertThat(userEntity?.nickname).isEqualTo(NicknameColumn("new"))
    }
}
