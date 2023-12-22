package com.banjjoknim.soliddesignpatternsample.designpattern.decorator.before

import com.banjjoknim.soliddesignpatternsample.designpattern.decorator.common.Sword

class FireIceSword : Sword() {
    override fun skill(): String {
        return "화염 얼음 ${DEFAULT.skill()}"
    }
}
