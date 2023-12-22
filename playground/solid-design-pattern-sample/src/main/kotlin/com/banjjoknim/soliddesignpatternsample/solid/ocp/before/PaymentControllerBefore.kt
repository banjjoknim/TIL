package com.banjjoknim.soliddesignpatternsample.solid.ocp.before

import com.banjjoknim.soliddesignpatternsample.solid.ocp.common.Payment
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/payments")
@RestController
class PaymentControllerBefore(
    private val paymentServiceBefore: PaymentServiceBefore
) {
    @PostMapping("/none-ocp")
    fun pay(@RequestBody request: PaymentRequestBefore): Payment {
        return paymentServiceBefore.pay(request)
    }
}
