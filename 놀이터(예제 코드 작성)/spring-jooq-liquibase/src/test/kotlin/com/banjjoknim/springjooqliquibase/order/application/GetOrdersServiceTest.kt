package com.banjjoknim.springjooqliquibase.order.application

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jooq.JooqTest
import org.springframework.context.annotation.Import

/**
 * @see org.springframework.boot.test.autoconfigure.jooq.JooqTest
 * @see com.banjjoknim.springjooqliquibase.TestJooqPersistentContextConfiguration
 */
//@Import(TestJooqPersistentContextConfiguration::class) // 테스트 설정 사용. 주석시 실제 설정 사용.
@DisplayName("주문 목록 조회 테스트")
@Import(value = [GetOrdersService::class])
@JooqTest
class GetOrdersServiceTest(
    @Autowired
    private val getOrdersService: GetOrdersService,
) {

    @DisplayName("주문 목록을 조회한다.")
    @Test
    fun `주문 목록을 조회한다`() {
        // given

        // when
        val response = getOrdersService.getOrders()

        // then
        assertThat(response.orders).hasSize(1)
    }
}
