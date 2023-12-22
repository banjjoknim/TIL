package com.banjjoknim.soliddesignpatternsample.solid.ocp.before

import com.banjjoknim.soliddesignpatternsample.solid.ocp.common.PaymentType

data class PaymentRequestBefore(
    val amount: Int,
    val type: PaymentType
)
