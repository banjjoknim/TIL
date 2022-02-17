package com.banjjoknim.playground.domain.user

import com.banjjoknim.playground.domain.event.AdminInheritanceEvent
import com.banjjoknim.playground.domain.event.CouponInheritanceEvent
import com.banjjoknim.playground.domain.event.SenderInheritanceEvent
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.BDDMockito.any
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.then
import org.mockito.BDDMockito.times
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.context.ApplicationEvent
import org.springframework.context.ApplicationEventPublisher

@ExtendWith(MockitoExtension::class)
class UserServiceBaseOnInheritanceEventTest {

    @Mock
    private lateinit var userRepository: UserRepository

    @Mock
    private lateinit var eventPublisher: ApplicationEventPublisher

    /**
     * 발행된 이벤트를 캡쳐해서 데이터를 저장할 수 있다.
     */
    @Captor
    private lateinit var eventPublisherCaptor: ArgumentCaptor<ApplicationEvent>

    private lateinit var userServiceBaseOnInheritanceEvent: UserServiceBaseOnInheritanceEvent

    @BeforeEach
    fun setup() {
        userServiceBaseOnInheritanceEvent = UserServiceBaseOnInheritanceEvent(userRepository, eventPublisher)
    }

    /**
     * 아래와 같이 테스트는 작성할 수 있지만, 통합테스트를 돌려야 실제로 이벤트가 발행되고 이벤트 리스너가 이벤트를 처리하는 것을 확인할 수 있다는 한계점이 있다.
     */
    @Test
    fun `회원 생성시 이벤트의 총 발행 횟수와 각각의 이벤트의 타입을 검사한다`() {
        given(userRepository.save(any()))
            .willReturn(User(name = "banjjoknim", email = "banjjoknim@github.com", phoneNumber = "010-1234-5678"))
        val request =
            CreateUserRequest(name = "banjjoknim", email = "banjjoknim@github.com", phoneNumber = "010-1234-5678")

        userServiceBaseOnInheritanceEvent.createUser(request)

        then(eventPublisher).should(times(3)).publishEvent(eventPublisherCaptor.capture())
        // 캡쳐한 이벤트는 순차적으로 저장되므로 아래와 같이 검증할 수도 있다.
        val events = eventPublisherCaptor.allValues
        assertThat(events[0]).isInstanceOf(AdminInheritanceEvent::class.java)
        assertThat(events[1]).isInstanceOf(CouponInheritanceEvent::class.java)
        assertThat(events[2]).isInstanceOf(SenderInheritanceEvent::class.java)
    }
}
