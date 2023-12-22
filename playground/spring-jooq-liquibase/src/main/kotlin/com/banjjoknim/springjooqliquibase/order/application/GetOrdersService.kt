package com.banjjoknim.springjooqliquibase.order.application

import com.banjjoknim.springjooqliquibase.order.api.GetOrdersResponse
import com.jooq.entity.tables.pojos.Order
import com.jooq.entity.tables.references.ORDER
import org.jooq.DSLContext
import org.springframework.stereotype.Service

@Service
class GetOrdersService(
    private val dsl: DSLContext,
) {

    fun getOrders(): GetOrdersResponse {
        val orders = dsl.select(ORDER.fields().toList())
            .from(ORDER)
            .fetchInto(Order::class.java)
        return GetOrdersResponse(orders = orders)
    }
}
