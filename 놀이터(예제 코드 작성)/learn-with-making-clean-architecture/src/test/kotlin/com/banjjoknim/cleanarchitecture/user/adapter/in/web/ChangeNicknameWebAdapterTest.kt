package com.banjjoknim.cleanarchitecture.user.adapter.`in`.web

import com.banjjoknim.cleanarchitecture.user.application.port.`in`.ChangeNicknameResponseData
import com.banjjoknim.cleanarchitecture.user.application.port.`in`.ChangeNicknameUseCase
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.filter.CharacterEncodingFilter

@WebMvcTest(ChangeNicknameWebAdapter::class)
class ChangeNicknameWebAdapterTest {

    @Autowired
    private lateinit var mockmvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockkBean
    private lateinit var changeNicknameUseCase: ChangeNicknameUseCase

    @BeforeEach
    fun setUp(webApplicationContext: WebApplicationContext) {
        mockmvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .addFilter<DefaultMockMvcBuilder>(CharacterEncodingFilter("UTF-8"))
            .alwaysDo<DefaultMockMvcBuilder>(MockMvcResultHandlers.print())
            .build()
    }

    @Test
    fun `닉네임을 변경한다`() {
        every { changeNicknameUseCase.changeNickname(any()) } returns ChangeNicknameResponseData(1L)
        val request = ChangeNicknameRequest(1L, "banjjoknim")

        mockmvc.post("/users") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            content { json("""{"userId":1}""") }
            status { isOk() }
        }
    }

    @Test
    fun `잘못된 닉네임 변경 요청에 BadRequest 응답을 반환한다`() {
        val request = ChangeNicknameRequest(1L, "banjjoknim!!")

        mockmvc.post("/users") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isBadRequest() }
        }
    }
}
