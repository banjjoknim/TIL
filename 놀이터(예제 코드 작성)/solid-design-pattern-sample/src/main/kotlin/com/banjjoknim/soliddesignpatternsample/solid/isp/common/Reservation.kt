package com.banjjoknim.soliddesignpatternsample.solid.isp.common

import javax.persistence.Entity
import javax.persistence.Id

@Entity
class Reservation(
    val seat: String,
    @Id
    val id: Long = 0L
)
