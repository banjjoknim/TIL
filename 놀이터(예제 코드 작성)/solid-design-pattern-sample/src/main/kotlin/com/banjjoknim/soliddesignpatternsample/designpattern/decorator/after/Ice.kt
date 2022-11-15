package com.banjjoknim.soliddesignpatternsample.designpattern.decorator.after

import com.banjjoknim.soliddesignpatternsample.designpattern.decorator.common.Sword

class Ice(private val sword: Sword) : SwordDecorator() {
    override fun skill(): String {
        return "얼음 ${sword.skill()}"
    }
}
