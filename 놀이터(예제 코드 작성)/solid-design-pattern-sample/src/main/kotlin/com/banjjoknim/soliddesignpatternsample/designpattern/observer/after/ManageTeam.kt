package com.banjjoknim.soliddesignpatternsample.designpattern.observer.after

import com.banjjoknim.soliddesignpatternsample.designpattern.observer.common.OrderState

class ManageTeam : Team {
    override val name: String
        get() = "운영팀"

    override fun onNotice(state: OrderState) {
        println("$name : 주문이 ${state.description} 상태로 변경되었네요. 소비자에게 메시지를 전송합니다.")
    }
}
