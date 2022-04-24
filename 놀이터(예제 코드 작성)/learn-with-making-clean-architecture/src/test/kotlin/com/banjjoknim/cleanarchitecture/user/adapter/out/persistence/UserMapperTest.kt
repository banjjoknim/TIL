package com.banjjoknim.cleanarchitecture.user.adapter.out.persistence

import com.banjjoknim.cleanarchitecture.user.pojo.Nickname
import com.banjjoknim.cleanarchitecture.user.pojo.User
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(classes = [UserMapper::class])
class UserMapperTest {

    @Autowired
    private lateinit var userMapper: UserMapper

    @Test
    fun `User POJO 를 User Entity 로 변환한다`() {
        val user = User(1L, Nickname("banjjoknim"))

        val userEntity = userMapper.mapToDomainEntity(user)

        assertThat(userEntity.id).isEqualTo(1)
        assertThat(userEntity.nickname).isEqualTo(NicknameColumn("banjjoknim"))
    }

    @Test
    fun `User Entity 를 User POJO 로 변환한다`() {
        val userEntity = UserEntity(1L, NicknameColumn("banjjoknim"))

        val user = userMapper.mapToDomainModel(userEntity)

        assertThat(user.id).isEqualTo(1)
        assertThat(user.nickname).isEqualTo(Nickname("banjjoknim"))
    }
}
