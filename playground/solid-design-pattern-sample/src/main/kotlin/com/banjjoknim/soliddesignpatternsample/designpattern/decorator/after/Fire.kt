package com.banjjoknim.soliddesignpatternsample.designpattern.decorator.after

import com.banjjoknim.soliddesignpatternsample.designpattern.decorator.common.Sword

class Fire(private val sword: Sword) : SwordDecorator() {
    override fun skill(): String {
        return "불꽃 ${sword.skill()}"
    }
}
