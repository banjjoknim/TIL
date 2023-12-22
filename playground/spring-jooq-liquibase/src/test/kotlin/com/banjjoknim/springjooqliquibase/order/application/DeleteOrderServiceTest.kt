package com.banjjoknim.springjooqliquibase.order.application

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
@DisplayName("주문 정보 삭제 테스트")
@Import(value = [DeleteOrderService::class])
@JooqTest
class DeleteOrderServiceTest(
    @Autowired
    private val dsl: DSLContext,
    @Autowired
    private val deleteOrderService: DeleteOrderService,
) {

    @Sql(statements = ["INSERT INTO `ORDER` (`product_name`, `product_price`) values ('필통', 10000)"])
    @DisplayName("주문 정보를 삭제한다")
    @Test
    fun `주문 정보를 삭제한다`() {
        // given
        val orders = dsl.fetch(ORDER)
        val latestOrder = orders.last()

        // when
        val response = deleteOrderService.deleteOrder(latestOrder.orderId ?: -1)

        // then
        assertThat(response.affectedCount).isEqualTo(1)
    }
}
