package com.banjjoknim.soliddesignpatternsample.designpattern.observer.after

import com.banjjoknim.soliddesignpatternsample.designpattern.observer.common.OrderState

class DeliveryTeam : Team {
    override val name: String
        get() = "배송팀"

    override fun onNotice(state: OrderState) {
        println("$name : 주문이 ${state.description} 상태로 변경되었네요. 배송을 시작합니다.")
    }
}
