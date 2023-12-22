package com.banjjoknim.springjooqliquibase.order.api

import com.banjjoknim.springjooqliquibase.order.application.GetOrdersService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/orders")
@RestController
class GetOrdersApi(
    private val getOrdersService: GetOrdersService
) {

    @GetMapping("")
    fun getOrders(): GetOrdersResponse {
        return getOrdersService.getOrders()
    }
}
