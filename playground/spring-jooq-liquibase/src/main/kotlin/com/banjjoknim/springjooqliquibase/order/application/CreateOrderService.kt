package com.banjjoknim.springjooqliquibase.order.application

import com.banjjoknim.springjooqliquibase.order.api.CreateOrderRequest
import com.banjjoknim.springjooqliquibase.order.api.CreateOrderResponse
import com.jooq.entity.tables.references.ORDER
import org.jooq.DSLContext
import org.springframework.stereotype.Service

@Service
class CreateOrderService(
    private val dsl: DSLContext,
) {

    fun createOrder(request: CreateOrderRequest): CreateOrderResponse {
        return dsl.transactionResult { config ->
            val orderRecord = config.dsl().newRecord(ORDER, object {
                val productName = request.productName
                val productPrice = request.productPrice
            })
            val affectedCount = orderRecord.store()
            CreateOrderResponse(orderId = orderRecord.orderId ?: -1, affectedCount = affectedCount)
        }
    }
}
