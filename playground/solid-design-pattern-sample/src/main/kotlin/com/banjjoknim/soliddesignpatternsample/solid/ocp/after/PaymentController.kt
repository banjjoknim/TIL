package com.banjjoknim.soliddesignpatternsample.solid.ocp.after

import com.banjjoknim.soliddesignpatternsample.solid.ocp.common.Payment
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/payments")
@RestController
class PaymentController(
    private val paymentServiceFactory: PaymentServiceFactory
) {
    @PostMapping("with-ocp")
    fun pay(request: PaymentRequestAfter): Payment {
        val paymentService = paymentServiceFactory.getPaymentService(request.paymentType)
        return paymentService.pay(request)
    }
}
