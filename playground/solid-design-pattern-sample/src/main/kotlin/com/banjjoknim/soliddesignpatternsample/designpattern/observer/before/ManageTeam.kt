package com.banjjoknim.soliddesignpatternsample.designpattern.observer.before

import com.banjjoknim.soliddesignpatternsample.designpattern.observer.common.OrderState

class ManageTeam {
    fun sendMessage(state: OrderState) {
        println("운영팀 : 주문이 ${state.description} 상태로 변경되었네요. 소비자에게 메시지를 전송합니다.")
    }
}
