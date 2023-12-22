package com.banjjoknim.soliddesignpatternsample.designpattern.abstractfactory.after

import com.banjjoknim.soliddesignpatternsample.designpattern.abstractfactory.common.Banana
import com.banjjoknim.soliddesignpatternsample.designpattern.abstractfactory.common.BananaJuice

class BananaMagic : Magic {
    override fun createFruit() {
        val banana = Banana()
        println("과일 [${banana.name}]를 만들었습니다!")
    }

    override fun createJuice() {
        val bananaJuice = BananaJuice()
        println("과일주스 [${bananaJuice.name}]을 만들었습니다!")
    }
}
