package com.banjjoknim.soliddesignpatternsample.solid.lsp.common

data class VehicleMoveRequest(
    val vehicleId: Long,
    val positionX: Int,
    val positionY: Int,
    val vehicleType: VehicleType
)
