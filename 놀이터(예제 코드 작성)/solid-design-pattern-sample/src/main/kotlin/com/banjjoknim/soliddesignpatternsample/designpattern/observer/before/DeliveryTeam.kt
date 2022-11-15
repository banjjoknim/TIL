package com.banjjoknim.soliddesignpatternsample.designpattern.observer.before

import com.banjjoknim.soliddesignpatternsample.designpattern.observer.common.OrderState

class DeliveryTeam {
    fun doDelivery(state: OrderState) {
        println("배송팀 : 주문이 ${state.description} 상태로 변경되었네요. 배송을 시작합니다.")
    }
}
