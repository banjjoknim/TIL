package com.banjjoknim.cleanarchitecture.user.domain

import com.banjjoknim.cleanarchitecture.user.domain.model.Nickname
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

class NicknameTest {

    @DisplayName("닉네임 생성 테스트")
    @Nested
    inner class ChangeNicknameTestCases {
        @Test
        fun `10글자 이내이면 닉네임을 생성할 수 있다`() {
            assertDoesNotThrow { Nickname("banjjoknim") }
        }

        @Test
        fun `10글자가 넘으면 닉네임을 생성할 경우 예외가 발생한다`() {
            assertThrows<IllegalArgumentException> { Nickname("i'm banjjoknim") }
        }
    }
}
