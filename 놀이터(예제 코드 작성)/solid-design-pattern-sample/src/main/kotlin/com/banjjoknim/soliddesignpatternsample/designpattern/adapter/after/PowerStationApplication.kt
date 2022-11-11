package com.banjjoknim.soliddesignpatternsample.designpattern.adapter.after

import com.banjjoknim.soliddesignpatternsample.designpattern.adapter.common.Home
import com.banjjoknim.soliddesignpatternsample.designpattern.adapter.common.PowerStation

class PowerStationApplication

fun main() {
    val powerStation = PowerStation()
    val homePowerStationElectricAdapter = HomePowerStationElectricAdapter(powerStation)

    val homeElectric = homePowerStationElectricAdapter.generateElectric(1000)

    val home = Home()
    home.takeElectric(homeElectric) // 가정은 발전소의 전기를 사용하는 클라이언트 역할을 한다.
}
