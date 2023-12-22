package com.banjjoknim.springjooqliquibase.order.application

import com.banjjoknim.springjooqliquibase.order.api.UpdateOrderRequest
import com.banjjoknim.springjooqliquibase.order.api.UpdateOrderResponse
import com.jooq.entity.tables.references.ORDER
import org.jooq.DSLContext
import org.springframework.stereotype.Service

@Service
class UpdateOrderService(
    private val dsl: DSLContext,
) {

    fun updateOrder(request: UpdateOrderRequest): UpdateOrderResponse {
        return dsl.transactionResult { config ->
            val orderRecord = config.dsl().fetchOne(ORDER, ORDER.ORDER_ID.eq(request.orderId))
                ?: throw NoSuchElementException("not found order. orderId: ${request.orderId}")
            orderRecord.productName = request.productName
            orderRecord.productPrice = request.productPrice
            val affectedCount = orderRecord.store()
            UpdateOrderResponse(orderRecord.orderId ?: -1, affectedCount)
        }
    }
}
