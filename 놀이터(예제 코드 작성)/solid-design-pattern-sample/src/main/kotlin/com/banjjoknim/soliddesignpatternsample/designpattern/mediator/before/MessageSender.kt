package com.banjjoknim.soliddesignpatternsample.designpattern.mediator.before

class MessageSender {
    fun sendMessage(homeAppliance: HomeAppliance) {
        println("메시지 : ${homeAppliance.name}을 켰습니다.")
    }
}
