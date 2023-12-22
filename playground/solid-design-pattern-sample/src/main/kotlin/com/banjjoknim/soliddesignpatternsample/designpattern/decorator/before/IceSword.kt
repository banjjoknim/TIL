package com.banjjoknim.soliddesignpatternsample.designpattern.decorator.before

import com.banjjoknim.soliddesignpatternsample.designpattern.decorator.common.Sword

class IceSword : Sword() {
    override fun skill(): String {
        return "얼음 ${DEFAULT.skill()}"
    }
}
