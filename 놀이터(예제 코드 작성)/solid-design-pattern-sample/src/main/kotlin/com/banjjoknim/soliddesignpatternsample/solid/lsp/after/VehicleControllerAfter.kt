package com.banjjoknim.soliddesignpatternsample.solid.lsp.after

import com.banjjoknim.soliddesignpatternsample.solid.lsp.common.Vehicle
import com.banjjoknim.soliddesignpatternsample.solid.lsp.common.VehicleMoveRequest
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/vehicles")
@RestController
class VehicleControllerAfter(
    private val vehicleServiceFactoryAfter: VehicleServiceFactoryAfter
) {
    @PatchMapping("/with-lsp")
    fun move(@RequestBody request: VehicleMoveRequest): Vehicle {
        val vehicleServiceAfter = vehicleServiceFactoryAfter.getVehicleServiceAfter(request.vehicleType)
        return vehicleServiceAfter.move(request)
    }
}
