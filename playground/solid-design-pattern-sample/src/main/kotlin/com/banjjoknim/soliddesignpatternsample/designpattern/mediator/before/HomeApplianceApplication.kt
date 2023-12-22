package com.banjjoknim.soliddesignpatternsample.designpattern.mediator.before

class HomeApplianceApplication

fun main() {
    val airConditioner = AirConditioner("에어컨")
    val television = Television("텔레비전")
    val messageSender = MessageSender()

    airConditioner.turnOnBut(television, messageSender)
}
