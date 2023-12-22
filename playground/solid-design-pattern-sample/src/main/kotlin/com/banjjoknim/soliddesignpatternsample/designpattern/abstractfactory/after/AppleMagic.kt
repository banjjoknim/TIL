package com.banjjoknim.soliddesignpatternsample.designpattern.abstractfactory.after

import com.banjjoknim.soliddesignpatternsample.designpattern.abstractfactory.common.Apple
import com.banjjoknim.soliddesignpatternsample.designpattern.abstractfactory.common.AppleJuice

class AppleMagic : Magic {
    override fun createFruit() {
        val apple = Apple()
        println("과일 [${apple.name}]를 만들었습니다!")
    }

    override fun createJuice() {
        val appleJuice = AppleJuice()
        println("과일주스 [${appleJuice.name}]을 만들었습니다!")
    }
}
