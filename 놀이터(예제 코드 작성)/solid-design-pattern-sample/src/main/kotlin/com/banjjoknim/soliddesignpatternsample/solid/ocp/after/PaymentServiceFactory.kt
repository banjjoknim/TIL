package com.banjjoknim.soliddesignpatternsample.solid.ocp.after

import com.banjjoknim.soliddesignpatternsample.solid.ocp.common.PaymentType
import org.springframework.stereotype.Component

@Component
class PaymentServiceFactory(val paymentServices: List<PaymentServiceAfter>) {
    fun getPaymentService(paymentType: PaymentType): PaymentServiceAfter {
        return this.paymentServices.find { it.getPaymentType() == paymentType }
            ?: throw NoSuchElementException("해당 결제방식에 대한 구현이 존재하지 않습니다.")
    }
}

//@Component
//class PaymentServiceFactory(
//    private val cashPaymentService: CashPaymentService,
//    private val cardPaymentService: CardPaymentService,
//    private val couponPaymentService: CouponPaymentService
//) {
//    fun findPaymentService(paymentType: PaymentType): PaymentServiceAfter {
//        return when (paymentType) {
//            PaymentType.CASH -> cashPaymentService
//            PaymentType.CARD -> cardPaymentService
//            PaymentType.COUPON -> couponPaymentService
//        }
//    }
//}
