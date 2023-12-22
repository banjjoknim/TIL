package com.banjjoknim.soliddesignpatternsample.designpattern.mediator.after

class AirConditioner(override val name: String) : HomeAppliance {
    override fun turnOn() {
        println("${name}을 켭니다.")
    }

    override fun turnOff() {
        println("${name}을 끕니다.")
    }
}
