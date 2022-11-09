package com.banjjoknim.soliddesignpatternsample.solid.lsp.before

import com.banjjoknim.soliddesignpatternsample.solid.lsp.common.VehicleType
import org.springframework.stereotype.Component

@Component
class VehicleServiceFactoryBefore(
    val vehicleServiceBefores: List<VehicleServiceBefore>
) {
    fun getVehicleServiceBefore(vehicleType: VehicleType): VehicleServiceBefore {
        return vehicleServiceBefores.find { it.vehicleType() == vehicleType }
            ?: throw NoSuchElementException("해당 이동수단에 대한 구현이 존재하지 않습니다.")
    }
}
