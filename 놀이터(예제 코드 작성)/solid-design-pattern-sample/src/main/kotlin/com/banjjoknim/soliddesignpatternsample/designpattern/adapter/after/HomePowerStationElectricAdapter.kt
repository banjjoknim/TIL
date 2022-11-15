package com.banjjoknim.soliddesignpatternsample.designpattern.adapter.after

import com.banjjoknim.soliddesignpatternsample.designpattern.adapter.common.HomeElectric
import com.banjjoknim.soliddesignpatternsample.designpattern.adapter.common.PowerStation
import com.banjjoknim.soliddesignpatternsample.designpattern.adapter.common.PowerStationElectricRequest

class HomePowerStationElectricAdapter(
    private val powerStation: PowerStation
): ElectricService {
    override fun generateElectric(current: Int): HomeElectric {
        val powerStationElectricRequest = PowerStationElectricRequest(current)
        val powerStationElectric = powerStation.generateElectric(powerStationElectricRequest)
        return powerStationElectric.toHomeElectric()
    }
}
