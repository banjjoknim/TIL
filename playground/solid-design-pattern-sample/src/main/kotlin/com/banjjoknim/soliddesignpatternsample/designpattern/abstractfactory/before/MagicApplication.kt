package com.banjjoknim.soliddesignpatternsample.designpattern.abstractfactory.before

import com.banjjoknim.soliddesignpatternsample.designpattern.abstractfactory.common.FruitType

class MagicApplication

fun main() {
    val magic = Magic()
    val fruitType = FruitType.APPLE
    magic.createFruit(fruitType)
    magic.createJuice(fruitType)
}
