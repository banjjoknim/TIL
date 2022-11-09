package com.banjjoknim.soliddesignpatternsample.solid.lsp.after

import com.banjjoknim.soliddesignpatternsample.solid.lsp.common.VehicleType
import org.springframework.stereotype.Component

@Component
class VehicleServiceFactoryAfter(
    val vehicleServiceAfters: List<VehicleServiceAfter>
) {
    fun getVehicleServiceAfter(vehicleType: VehicleType): VehicleServiceAfter {
        return vehicleServiceAfters.find { it.vehicleType() == vehicleType }
            ?: throw NoSuchElementException("해당 이동수단에 대한 구현이 존재하지 않습니다.")
    }
}
