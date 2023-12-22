package com.banjjoknim.springjooqliquibase.order.application

import com.banjjoknim.springjooqliquibase.order.api.DeleteOrderResponse
import com.jooq.entity.tables.references.ORDER
import org.jooq.DSLContext
import org.springframework.stereotype.Service

@Service
class DeleteOrderService(
    private val dsl: DSLContext,
) {

    fun deleteOrder(orderId: Int): DeleteOrderResponse {
        return dsl.transactionResult { config ->
            val orderRecord = config.dsl().fetchOne(ORDER, ORDER.ORDER_ID.eq(orderId))
                ?: throw NoSuchElementException("not found order. orderId: $orderId")
            val affectedCount = orderRecord.delete()
            DeleteOrderResponse(orderId = orderId, affectedCount = affectedCount)
        }
    }
}
