package com.banjjoknim.soliddesignpatternsample.designpattern.mediator.after

class RemoteControl(
    private val homeAppliances: List<HomeAppliance>,
    private val messageSender: MessageSender
) {
    fun turnOn(homeAppliance: HomeAppliance) {
        homeAppliances.filterNot { it == homeAppliance }
            .forEach { it.turnOff() }
        homeAppliance.turnOn()
        messageSender.sendMessage(homeAppliance)
    }

    fun turnOff(homeAppliance: HomeAppliance) {
        homeAppliance.turnOff()
    }
}
