package com.banjjoknim.soliddesignpatternsample.designpattern.observer.common

enum class OrderState(
    val description: String
) {
    RECEIVED("접수됨"),
    DELIVERY_READY_COMPLETE("배송 준비")
}
