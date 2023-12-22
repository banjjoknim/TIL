package com.banjjoknim.soliddesignpatternsample.designpattern.abstractfactory.before

import com.banjjoknim.soliddesignpatternsample.designpattern.abstractfactory.common.Apple
import com.banjjoknim.soliddesignpatternsample.designpattern.abstractfactory.common.AppleJuice
import com.banjjoknim.soliddesignpatternsample.designpattern.abstractfactory.common.Banana
import com.banjjoknim.soliddesignpatternsample.designpattern.abstractfactory.common.BananaJuice
import com.banjjoknim.soliddesignpatternsample.designpattern.abstractfactory.common.FruitType
import com.banjjoknim.soliddesignpatternsample.designpattern.abstractfactory.common.Orange
import com.banjjoknim.soliddesignpatternsample.designpattern.abstractfactory.common.OrangeJuice

class Magic {
    fun createFruit(type: FruitType) {
        val fruit = when (type) {
            FruitType.APPLE -> Apple()
            FruitType.BANANA -> Banana()
            FruitType.ORANGE -> Orange()
        }
        println("과일 [${fruit.name}]를 만들었습니다!")
    }

    fun createJuice(type: FruitType) {
        val juice = when (type) {
            FruitType.APPLE -> AppleJuice()
            FruitType.BANANA -> BananaJuice()
            FruitType.ORANGE -> OrangeJuice()
        }
        println("과일주스 [${juice.name}]을 만들었습니다!")
    }
}
