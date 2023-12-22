package com.banjjoknim.springjooqliquibase.order.api

import com.banjjoknim.springjooqliquibase.order.application.UpdateOrderService
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/orders")
@RestController
class UpdateOrderApi(
    private val updateOrderService: UpdateOrderService
) {

    @PatchMapping("")
    fun updateOrder(@RequestBody request: UpdateOrderRequest): UpdateOrderResponse {
        return updateOrderService.updateOrder(request)
    }
}
