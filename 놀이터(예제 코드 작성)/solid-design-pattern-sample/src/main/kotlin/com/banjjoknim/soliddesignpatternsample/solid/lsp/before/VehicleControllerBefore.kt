package com.banjjoknim.soliddesignpatternsample.solid.lsp.before

import com.banjjoknim.soliddesignpatternsample.solid.lsp.common.Vehicle
import com.banjjoknim.soliddesignpatternsample.solid.lsp.common.VehicleMoveRequest
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/vehicles")
@RestController
class VehicleControllerBefore(
    private val vehicleServiceFactoryBefore: VehicleServiceFactoryBefore
) {
    @PatchMapping("/none-lsp")
    fun move(@RequestBody request: VehicleMoveRequest): Vehicle {
        val vehicleServiceBefore = vehicleServiceFactoryBefore.getVehicleServiceBefore(request.vehicleType)
        return vehicleServiceBefore.move(request)
    }
}
