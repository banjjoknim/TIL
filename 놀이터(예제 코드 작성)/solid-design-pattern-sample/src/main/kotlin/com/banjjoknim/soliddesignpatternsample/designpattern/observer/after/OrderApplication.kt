package com.banjjoknim.soliddesignpatternsample.designpattern.observer.after

import com.banjjoknim.soliddesignpatternsample.designpattern.observer.common.OrderState

class OrderApplication

fun main() {
    val order = Order()
    order.addTeam(DeliveryTeam())
    order.addTeam(ManageTeam())
    order.changeState(OrderState.DELIVERY_READY_COMPLETE)
}
