package com.banjjoknim.cleanarchitecture.user.application.service

import com.banjjoknim.cleanarchitecture.user.application.port.`in`.ChangeNicknameRequestData
import com.banjjoknim.cleanarchitecture.user.application.port.out.LoadUserPersistencePort
import com.banjjoknim.cleanarchitecture.user.application.port.out.UpsertUserPersistencePort
import com.banjjoknim.cleanarchitecture.user.pojo.Nickname
import com.banjjoknim.cleanarchitecture.user.pojo.User
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ChangeNicknameServiceTest {

    private val loadUserPersistencePort = mockk<LoadUserPersistencePort>()
    private val upsertUserPersistencePort = mockk<UpsertUserPersistencePort>()
    private val changeNicknameService = ChangeNicknameService(loadUserPersistencePort, upsertUserPersistencePort)

    @DisplayName("닉네임 변경 테스트 케이스")
    @Nested
    inner class ChangeNicknameTestCases {

        private lateinit var testUser: User
        private val testChangeNicknameRequestData = ChangeNicknameRequestData(1L, "new")

        @BeforeEach
        fun setUp() {
            testUser = User(1L, Nickname("old"))
        }

        @Test
        fun `닉네임을 변경한다`() {
            every { loadUserPersistencePort.loadUser(any()) } returns testUser
            every { upsertUserPersistencePort.upsertUser(any()) } just Runs

            changeNicknameService.changeNickname(testChangeNicknameRequestData)

            assertThat(testUser.nickname).isEqualTo(Nickname("new"))
        }
    }
}
