package com.banjjoknim.soliddesignpatternsample.solid.ocp.common

import org.springframework.stereotype.Repository

@Repository
class PaymentRepositoryStub : PaymentRepository {
    override fun createPayment(amount: Int, paymentType: PaymentType): Payment {
        return Payment(amount = amount, type = paymentType)
    }
}
