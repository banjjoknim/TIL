package com.banjjoknim.soliddesignpatternsample.designpattern.abstractfactory.after

import com.banjjoknim.soliddesignpatternsample.designpattern.abstractfactory.common.FruitType

class MagicApplication

fun main() {
    val fruitType = FruitType.APPLE
    val magic = Magic.getMagic(fruitType)
    magic.createFruit()
    magic.createJuice()
}
