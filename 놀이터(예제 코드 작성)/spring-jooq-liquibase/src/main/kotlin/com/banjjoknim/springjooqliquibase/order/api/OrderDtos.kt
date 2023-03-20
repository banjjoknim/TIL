package com.banjjoknim.springjooqliquibase.order.api

import com.jooq.entity.tables.pojos.Order

data class GetOrdersResponse(
    val orders: List<Order> = emptyList()
)

data class CreateOrderRequest(
    val productName: String,
    val productPrice: Int,
)

data class CreateOrderResponse(
    val orderId: Int,
    val affectedCount: Int,
)

data class UpdateOrderRequest(
    val orderId: Int,
    val productName: String,
    val productPrice: Int,
)

data class UpdateOrderResponse(
    val orderId: Int,
    val affectedCount: Int,
)

data class DeleteOrderResponse(
    val orderId: Int,
    val affectedCount: Int,
)
