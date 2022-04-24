package com.banjjoknim.cleanarchitecture.user.adapter.out.persistence

import com.banjjoknim.cleanarchitecture.user.pojo.Nickname
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.jdbc.Sql

@DataJpaTest
@Import(value = [UserMapper::class, LoadUserPersistenceAdapter::class])
class LoadUserPersistenceAdapterTest {

    @Autowired
    private lateinit var loadUserPersistenceAdapter: LoadUserPersistenceAdapter

    @Sql(statements = ["INSERT INTO USER VALUES (1, 'old')"])
    @Test
    fun `회원이 존재하지 않을 경우 예외가 발생한다`() {
        assertThrows<NoSuchElementException>("존재하지 않는 회원입니다. userId: 100") { loadUserPersistenceAdapter.loadUser(100L) }
    }

    @Sql(statements = ["INSERT INTO USER VALUES (1, 'old')"])
    @Test
    fun `회원이 존재할 경우 회원을 조회할 수 있다`() {
        val user = loadUserPersistenceAdapter.loadUser(1L)

        assertThat(user.id).isEqualTo(1)
        assertThat(user.nickname).isEqualTo(Nickname("old"))
    }
}
