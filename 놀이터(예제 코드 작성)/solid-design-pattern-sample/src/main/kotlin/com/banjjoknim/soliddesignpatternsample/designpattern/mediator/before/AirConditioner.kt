package com.banjjoknim.soliddesignpatternsample.designpattern.mediator.before

class AirConditioner(override val name: String) : HomeAppliance {
    override fun turnOnBut(homeAppliance: HomeAppliance, messageSender: MessageSender) {
        homeAppliance.turnOff()
        println("${name}을 켭니다.")
        messageSender.sendMessage(this)
    }

    override fun turnOff() {
        println("${name}을 끕니다.")
    }
}
