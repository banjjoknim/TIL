package com.banjjoknim.soliddesignpatternsample.solid.lsp.common

import javax.persistence.Entity
import javax.persistence.Id

@Entity
class Vehicle(
    var positionX: Int = 0,
    var positionY: Int = 0,
    @Id
    val id: Long = 0L
)
