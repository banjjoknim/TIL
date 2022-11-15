package com.banjjoknim.soliddesignpatternsample.designpattern.adapter.common

class PowerStation {
    fun generateElectric(request: PowerStationElectricRequest): PowerStationElectric {
        return PowerStationElectric(request.current)
    }
}
