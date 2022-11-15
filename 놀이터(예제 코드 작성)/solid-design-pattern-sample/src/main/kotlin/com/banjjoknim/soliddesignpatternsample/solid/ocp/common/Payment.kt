package com.banjjoknim.soliddesignpatternsample.solid.ocp.common

import javax.persistence.Entity
import javax.persistence.Id

@Entity
class Payment(
    val amount: Int,
    val type: PaymentType,
    @Id
    val id: Long = 0L
)
