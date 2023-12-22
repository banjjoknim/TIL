package com.banjjoknim.springjooqliquibase.order.api

import com.banjjoknim.springjooqliquibase.order.application.CreateOrderService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/orders")
@RestController
class CreateOrderApi(
    private val createOrderService: CreateOrderService
) {

    @PostMapping("")
    fun createOrder(@RequestBody request: CreateOrderRequest): CreateOrderResponse {
        return createOrderService.createOrder(request)
    }
}
