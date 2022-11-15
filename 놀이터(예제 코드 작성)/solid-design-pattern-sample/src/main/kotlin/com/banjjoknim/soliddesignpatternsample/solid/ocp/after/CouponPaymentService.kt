package com.banjjoknim.soliddesignpatternsample.solid.ocp.after

import com.banjjoknim.soliddesignpatternsample.solid.ocp.common.Payment
import com.banjjoknim.soliddesignpatternsample.solid.ocp.common.PaymentRepository
import com.banjjoknim.soliddesignpatternsample.solid.ocp.common.PaymentType
import org.springframework.stereotype.Repository

@Repository
class CouponPaymentService(
    private val paymentRepository: PaymentRepository
): PaymentServiceAfter {
    override fun pay(request: PaymentRequestAfter): Payment {
        println("쿠폰 결제는 잔고 차감이 없습니다.")
        return paymentRepository.createPayment(request.amount, request.paymentType)
    }

    override fun getPaymentType(): PaymentType {
        return PaymentType.COUPON
    }
}
