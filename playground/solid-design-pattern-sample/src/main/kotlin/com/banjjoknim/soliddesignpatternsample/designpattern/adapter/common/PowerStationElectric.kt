package com.banjjoknim.soliddesignpatternsample.designpattern.adapter.common

data class PowerStationElectric(
    val current: Int,
) {
    fun toHomeElectric(): HomeElectric {
        return HomeElectric(this.current * (11000 / 220))
    }
}
