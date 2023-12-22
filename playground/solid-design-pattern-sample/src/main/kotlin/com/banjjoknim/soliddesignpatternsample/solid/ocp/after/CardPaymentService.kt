package com.banjjoknim.soliddesignpatternsample.solid.ocp.after

import com.banjjoknim.soliddesignpatternsample.solid.ocp.common.Payment
import com.banjjoknim.soliddesignpatternsample.solid.ocp.common.PaymentRepository
import com.banjjoknim.soliddesignpatternsample.solid.ocp.common.PaymentType
import org.springframework.stereotype.Repository

@Repository
class CardPaymentService(
    private val paymentRepository: PaymentRepository
): PaymentServiceAfter {
    override fun pay(request: PaymentRequestAfter): Payment {
        println("카드 결제로 ${request.amount}원을 차감합니다.")
        return paymentRepository.createPayment(request.amount, request.paymentType)
    }

    override fun getPaymentType(): PaymentType {
        return PaymentType.CARD
    }
}
