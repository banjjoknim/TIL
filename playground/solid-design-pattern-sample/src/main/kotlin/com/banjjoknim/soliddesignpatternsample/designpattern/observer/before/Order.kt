package com.banjjoknim.soliddesignpatternsample.designpattern.observer.before

import com.banjjoknim.soliddesignpatternsample.designpattern.observer.common.OrderState

class Order(
    private var state: OrderState = OrderState.RECEIVED,
    private val deliveryTeam: DeliveryTeam,
    private val manageTeam: ManageTeam
) {
    fun changeState(state: OrderState) {
        this.state = state
        deliveryTeam.doDelivery(state)
        manageTeam.sendMessage(state)
    }
}
