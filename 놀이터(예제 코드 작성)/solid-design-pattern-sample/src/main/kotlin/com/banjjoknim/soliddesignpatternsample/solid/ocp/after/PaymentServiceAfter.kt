package com.banjjoknim.soliddesignpatternsample.solid.ocp.after

import com.banjjoknim.soliddesignpatternsample.solid.ocp.common.Payment
import com.banjjoknim.soliddesignpatternsample.solid.ocp.common.PaymentType

interface PaymentServiceAfter {
    fun pay(request: PaymentRequestAfter): Payment

    fun getPaymentType(): PaymentType
}
