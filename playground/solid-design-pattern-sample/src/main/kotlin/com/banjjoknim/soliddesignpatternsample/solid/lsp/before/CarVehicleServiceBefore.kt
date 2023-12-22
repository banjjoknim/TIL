package com.banjjoknim.soliddesignpatternsample.solid.lsp.before

import com.banjjoknim.soliddesignpatternsample.solid.lsp.common.Vehicle
import com.banjjoknim.soliddesignpatternsample.solid.lsp.common.VehicleMoveRequest
import com.banjjoknim.soliddesignpatternsample.solid.lsp.common.VehicleRepository
import com.banjjoknim.soliddesignpatternsample.solid.lsp.common.VehicleType
import org.springframework.stereotype.Service

@Service
class CarVehicleServiceBefore(
    private val vehicleRepository: VehicleRepository
) : VehicleServiceBefore(vehicleRepository) {
    override fun move(request: VehicleMoveRequest): Vehicle {
        val vehicle = vehicleRepository.getVehicle(request.vehicleId)
        vehicle.positionX += request.positionX // 하위 타입에서의 잘못된 오버라이딩!
        vehicle.positionY += request.positionY // 하위 타입에서의 잘못된 오버라이딩!
        return vehicle
    }

    override fun vehicleType(): VehicleType {
        return VehicleType.CAR
    }
}
