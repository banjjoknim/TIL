package com.banjjoknim.soliddesignpatternsample.solid.lsp.after

import com.banjjoknim.soliddesignpatternsample.solid.lsp.common.Vehicle
import com.banjjoknim.soliddesignpatternsample.solid.lsp.common.VehicleMoveRequest
import com.banjjoknim.soliddesignpatternsample.solid.lsp.common.VehicleType

abstract class VehicleServiceAfter {
    abstract fun move(request: VehicleMoveRequest): Vehicle

    abstract fun vehicleType(): VehicleType
}
