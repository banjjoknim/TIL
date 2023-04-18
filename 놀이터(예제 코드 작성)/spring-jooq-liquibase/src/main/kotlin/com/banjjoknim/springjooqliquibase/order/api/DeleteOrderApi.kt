package com.banjjoknim.springjooqliquibase.order.api

import com.banjjoknim.springjooqliquibase.order.application.DeleteOrderService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/orders")
@RestController
class DeleteOrderApi(
    private val deleteOrderService: DeleteOrderService
) {

    @DeleteMapping("{orderId}")
    fun deleteOrder(@PathVariable orderId: Int): DeleteOrderResponse {
        return deleteOrderService.deleteOrder(orderId)
    }
}
