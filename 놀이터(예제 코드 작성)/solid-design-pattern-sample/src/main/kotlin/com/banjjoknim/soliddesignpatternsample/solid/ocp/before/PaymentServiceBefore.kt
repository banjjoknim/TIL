package com.banjjoknim.soliddesignpatternsample.solid.ocp.before

import com.banjjoknim.soliddesignpatternsample.solid.ocp.common.Payment
import com.banjjoknim.soliddesignpatternsample.solid.ocp.common.PaymentRepository
import com.banjjoknim.soliddesignpatternsample.solid.ocp.common.PaymentType
import org.springframework.stereotype.Service

@Service
class PaymentServiceBefore(
    private val paymentRepository: PaymentRepository
) {
    fun pay(request: PaymentRequestBefore): Payment {
        when (request.type) {
            PaymentType.CASH -> println("현금 결제로 ${request.amount}원을 차감합니다.")
            PaymentType.CARD -> println("카드 결제로 ${request.amount}원을 차감합니다.")
            PaymentType.COUPON -> println("쿠폰 결제는 잔고 차감이 없습니다.")
            // NewPaymentType -> ...
        }
        return paymentRepository.createPayment(request.amount, request.type)
    }
}
