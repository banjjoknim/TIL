package com.banjjoknim.soliddesignpatternsample.solid.lsp.common

import org.springframework.stereotype.Repository

@Repository
class VehicleRepositoryStub : VehicleRepository {
    override fun getVehicle(vehicleId: Long): Vehicle {
        return Vehicle(id = vehicleId)
    }
}
