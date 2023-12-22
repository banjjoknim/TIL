package com.banjjoknim.soliddesignpatternsample.designpattern.observer.after

import com.banjjoknim.soliddesignpatternsample.designpattern.observer.common.OrderState

interface Team {
    val name: String

    fun onNotice(state: OrderState)
}
