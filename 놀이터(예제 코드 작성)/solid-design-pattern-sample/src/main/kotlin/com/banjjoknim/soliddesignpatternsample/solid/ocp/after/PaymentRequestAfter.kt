package com.banjjoknim.soliddesignpatternsample.solid.ocp.after

import com.banjjoknim.soliddesignpatternsample.solid.ocp.common.PaymentType

data class PaymentRequestAfter(
    val amount: Int,
    val paymentType: PaymentType
)
