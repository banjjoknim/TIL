package com.banjjoknim.springjooqliquibase

import com.jooq.entity.tables.references.ORDER
import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.jooq.tools.jdbc.MockConnection
import org.jooq.tools.jdbc.MockDataProvider
import org.jooq.tools.jdbc.MockResult
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import java.time.LocalDateTime

@TestConfiguration
class TestJooqPersistentContextConfiguration {

    @Bean
    fun dsl(): DSLContext {
        val mockDataProvider = mockDataProvider()
        val mockConnection = MockConnection(mockDataProvider)
        return DSL.using(mockConnection, SQLDialect.MYSQL)
    }

    fun mockDataProvider(): MockDataProvider {
        val mockDataProvider = MockDataProvider {
            val dsl = DSL.using(SQLDialect.MYSQL)
            val mockResults = arrayListOf<MockResult>()

            when {
                it.sql().uppercase().startsWith("SELECT") -> createMockOrders(dsl, mockResults)
            }

            mockResults.toTypedArray()
        }
        return mockDataProvider
    }

    private fun createMockOrders(dsl: DSLContext, mockResults: ArrayList<MockResult>) {
        val orderRecord = dsl.newRecord(
            ORDER.ORDER_ID, ORDER.PRODUCT_NAME, ORDER.PRODUCT_PRICE,
            ORDER.CREATED_AT, ORDER.UPDATED_AT, ORDER.DELETED_AT
        ).values(1, "테스트 연필", 1, LocalDateTime.now(), LocalDateTime.now(), null)
            .into(ORDER)

        val orderRecordResult = dsl.newResult(ORDER)
        orderRecordResult.add(orderRecord)
        mockResults.add(MockResult(1, orderRecordResult))
    }
}
