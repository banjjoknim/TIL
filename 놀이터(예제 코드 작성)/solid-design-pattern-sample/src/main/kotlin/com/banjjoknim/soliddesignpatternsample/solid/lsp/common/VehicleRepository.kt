package com.banjjoknim.soliddesignpatternsample.solid.lsp.common

interface VehicleRepository {
    fun getVehicle(vehicleId: Long): Vehicle
}
