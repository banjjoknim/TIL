package com.banjjoknim.soliddesignpatternsample.designpattern.adapter.before

import com.banjjoknim.soliddesignpatternsample.designpattern.adapter.common.Home
import com.banjjoknim.soliddesignpatternsample.designpattern.adapter.common.PowerStation
import com.banjjoknim.soliddesignpatternsample.designpattern.adapter.common.PowerStationElectricRequest

class PowerStationApplication

fun main() {
    val powerStation = PowerStation()

    val electricRequest = PowerStationElectricRequest(1000)
    val powerStationElectric = powerStation.generateElectric(electricRequest)

    val homeElectric = powerStationElectric.toHomeElectric()

    val home = Home()
    home.takeElectric(homeElectric) // 가정은 발전소의 전기를 사용하는 클라이언트 역할을 한다.
}
