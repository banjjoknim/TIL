package com.banjjoknim.soliddesignpatternsample.solid.lsp.after

import com.banjjoknim.soliddesignpatternsample.solid.lsp.common.Vehicle
import com.banjjoknim.soliddesignpatternsample.solid.lsp.common.VehicleMoveRequest
import com.banjjoknim.soliddesignpatternsample.solid.lsp.common.VehicleRepository
import com.banjjoknim.soliddesignpatternsample.solid.lsp.common.VehicleType
import org.springframework.stereotype.Service

@Service
class PlaneVehicleServiceAfter(
    private val vehicleRepository: VehicleRepository
): VehicleServiceAfter() {
    override fun move(request: VehicleMoveRequest): Vehicle {
        println("비행기가 이동합니다.")
        val vehicle = vehicleRepository.getVehicle(request.vehicleId)
        vehicle.positionX = request.positionX
        vehicle.positionY = request.positionY
        return vehicle
    }

    override fun vehicleType(): VehicleType {
        return VehicleType.PLANE
    }
}
