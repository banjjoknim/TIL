package com.banjjoknim.springjooqliquibase.order.application

import com.jooq.entity.tables.pojos.Order
import com.jooq.entity.tables.references.ORDER
import org.assertj.core.api.Assertions.assertThat
import org.jooq.DSLContext
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jooq.JooqTest
import org.springframework.test.context.jdbc.Sql

/**
 * @see org.springframework.boot.test.autoconfigure.jooq.JooqTest
 * @see com.banjjoknim.springjooqliquibase.TestJooqPersistentContextConfiguration
 */
//@Import(TestJooqPersistentContextConfiguration::class) // 테스트 설정 사용. 주석시 실제 설정 사용.
@JooqTest
class CreateOrderServiceTest {

    @Autowired
    private lateinit var dsl: DSLContext

    @DisplayName("주문 목록을 조회한다.")
    @Test
    fun `주문 목록을 조회한다`() {
        // given

        // when
        val orders = dsl.fetch(ORDER)
            .into(Order::class.java)

        // then
        assertThat(orders).hasSize(1)
    }

    @Sql(statements = ["INSERT INTO `ORDER` (`product_name`, `product_price`) values ('필통', 10000)"])
    @DisplayName("주문 정보를 생성한다.")
    @Test
    fun `주문 정보를 생성한다`() {
        // given
        val newOrder = dsl.newRecord(ORDER, object {
            val productName = "볼펜"
            val productPrice = 2000
        })

        // when
        val affectedRows = newOrder.store()
        val orders = dsl.fetch(ORDER)
            .into(Order::class.java)

        // then
        assertThat(affectedRows).isEqualTo(1)
        assertThat(orders).hasSize(3)
    }

    @DisplayName("주문 정보를 수정한다")
    @Test
    fun `주문 정보를 수정한다`() {
        // given
        val orderId = 1
        val beforeOrder = dsl.fetchOne(ORDER, ORDER.ORDER_ID.eq(orderId))

        // when
        beforeOrder?.productName = "4B연필"
        val affectedRows = beforeOrder?.store()

        // then
        val afterOrder = dsl.fetchOne(ORDER, ORDER.ORDER_ID.eq(orderId))
        assertThat(affectedRows).isEqualTo(1)
        assertThat(afterOrder?.productName).isEqualTo("4B연필")
        assertThat(afterOrder).isNotEqualTo(beforeOrder)
    }

    @Sql(statements = ["INSERT INTO `ORDER` (`product_name`, `product_price`) values ('필통', 10000)"])
    @DisplayName("주문 정보를 삭제한다")
    @Test
    fun `주문 정보를 삭제한다`() {
        // given
        val orders = dsl.fetch(ORDER)
        val latestOrder = orders.last()

        // when
        val affectedRows = latestOrder?.delete()

        // then
        assertThat(affectedRows).isEqualTo(1)
    }
}
