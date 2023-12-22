package com.banjjoknim.soliddesignpatternsample.designpattern.observer.before

import com.banjjoknim.soliddesignpatternsample.designpattern.observer.common.OrderState

class OrderApplication

fun main() {
    val order = Order(state = OrderState.RECEIVED, DeliveryTeam(), ManageTeam())
    order.changeState(OrderState.DELIVERY_READY_COMPLETE)
}
