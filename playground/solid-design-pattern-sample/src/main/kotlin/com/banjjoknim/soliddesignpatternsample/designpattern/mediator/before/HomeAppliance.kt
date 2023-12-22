package com.banjjoknim.soliddesignpatternsample.designpattern.mediator.before

interface HomeAppliance {
    val name: String

    fun turnOnBut(homeAppliance: HomeAppliance, messageSender: MessageSender)

    fun turnOff()
}
