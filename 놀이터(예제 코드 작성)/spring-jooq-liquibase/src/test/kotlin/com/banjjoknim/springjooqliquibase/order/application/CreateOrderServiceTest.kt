package com.banjjoknim.springjooqliquibase.order.application

import com.banjjoknim.springjooqliquibase.order.api.CreateOrderRequest
import com.jooq.entity.tables.pojos.Order
import com.jooq.entity.tables.references.ORDER
import org.assertj.core.api.Assertions.assertThat
import org.jooq.DSLContext
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jooq.JooqTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.jdbc.Sql

/**
 * @see org.springframework.boot.test.autoconfigure.jooq.JooqTest
 * @see com.banjjoknim.springjooqliquibase.TestJooqPersistentContextConfiguration
 */
//@Import(TestJooqPersistentContextConfiguration::class) // 테스트 설정 사용. 주석시 실제 설정 사용.
@DisplayName("주문 정보 생성 테스트")
@Import(value = [CreateOrderService::class])
@JooqTest
class CreateOrderServiceTest(
    @Autowired
    private val dsl: DSLContext,
    @Autowired
    private val createOrderService: CreateOrderService,
) {

    @Sql(statements = ["INSERT INTO `ORDER` (`product_name`, `product_price`) values ('필통', 10000)"])
    @DisplayName("주문 정보를 생성한다.")
    @Test
    fun `주문 정보를 생성한다`() {
        // given
        val request = CreateOrderRequest(productName = "볼펜", productPrice = 2000)

        // when
        val response = createOrderService.createOrder(request)
        val orders = dsl.fetch(ORDER)
            .into(Order::class.java)

        // then
        assertThat(response.affectedCount).isEqualTo(1)
        assertThat(orders).hasSize(3)
    }
}
