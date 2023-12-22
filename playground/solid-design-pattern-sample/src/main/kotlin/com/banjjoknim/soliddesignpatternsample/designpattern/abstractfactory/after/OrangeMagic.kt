package com.banjjoknim.soliddesignpatternsample.designpattern.abstractfactory.after

import com.banjjoknim.soliddesignpatternsample.designpattern.abstractfactory.common.Orange
import com.banjjoknim.soliddesignpatternsample.designpattern.abstractfactory.common.OrangeJuice

class OrangeMagic : Magic {
    override fun createFruit() {
        val orange = Orange()
        println("과일 [${orange.name}]를 만들었습니다!")
    }

    override fun createJuice() {
        val orangeJuice = OrangeJuice()
        println("과일주스 [${orangeJuice.name}]을 만들었습니다!")
    }
}
