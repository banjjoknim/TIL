package com.banjjoknim.soliddesignpatternsample.designpattern.mediator.after

class HomeApplianceApplication

fun main() {
    val airConditioner = AirConditioner("에어컨")
    val television = Television("텔레비전")
    val messageSender = MessageSender()
    val remoteControl = RemoteControl(listOf(airConditioner, television), messageSender)

    remoteControl.turnOn(airConditioner)
}
