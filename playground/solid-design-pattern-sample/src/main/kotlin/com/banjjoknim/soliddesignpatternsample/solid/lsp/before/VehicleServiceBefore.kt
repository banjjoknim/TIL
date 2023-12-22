package com.banjjoknim.soliddesignpatternsample.solid.lsp.before

import com.banjjoknim.soliddesignpatternsample.solid.lsp.common.Vehicle
import com.banjjoknim.soliddesignpatternsample.solid.lsp.common.VehicleMoveRequest
import com.banjjoknim.soliddesignpatternsample.solid.lsp.common.VehicleRepository
import com.banjjoknim.soliddesignpatternsample.solid.lsp.common.VehicleType

abstract class VehicleServiceBefore(
    private val vehicleRepository: VehicleRepository
) {
    open fun move(request: VehicleMoveRequest): Vehicle {
        val vehicle = vehicleRepository.getVehicle(request.vehicleId)
        vehicle.positionX = request.positionX
        vehicle.positionY = request.positionY
        return vehicle
    }

    abstract fun vehicleType(): VehicleType
}
