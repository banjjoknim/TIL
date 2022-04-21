package com.banjjoknim.cleanarchitecture.user.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class UserTest {

    @DisplayName("회원 이름 변경 테스트 케이스")
    @Nested
    inner class ChangeNicknameTestCases {
        @Test
        fun `회원 닉네임을 변경한다`() {
            // given
            val user = User(nickname = Nickname("banjjoknim"))

            // when
            user.changeNickname("colt")

            // then
            assertThat(user.nickname).isEqualTo(Nickname("colt"))
        }
    }
}
