package com.banjjoknim.soliddesignpatternsample.solid.ocp.common

interface PaymentRepository {
    fun createPayment(amount: Int, paymentType: PaymentType): Payment
}
