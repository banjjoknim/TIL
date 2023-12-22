package com.banjjoknim.soliddesignpatternsample.designpattern.decorator.before

import com.banjjoknim.soliddesignpatternsample.designpattern.decorator.common.Sword

class FireSword : Sword() {
    override fun skill(): String {
        return "화염 ${DEFAULT.skill()}"
    }
}
