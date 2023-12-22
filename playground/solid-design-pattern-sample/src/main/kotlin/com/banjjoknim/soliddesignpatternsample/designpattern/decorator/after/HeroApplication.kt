package com.banjjoknim.soliddesignpatternsample.designpattern.decorator.after

import com.banjjoknim.soliddesignpatternsample.designpattern.decorator.common.Hero
import com.banjjoknim.soliddesignpatternsample.designpattern.decorator.common.Sword

class HeroApplication

fun main() {
    val hero = Hero()
    hero.attack()

    val fireSword = Fire(Sword.DEFAULT)
    hero.changeSword(fireSword)
    hero.attack()

    val iceSword = Ice(Sword.DEFAULT)
    hero.changeSword(iceSword)
    hero.attack()

    val fireIceSword = Fire(Ice(Sword.DEFAULT))
    hero.changeSword(fireIceSword)
    hero.attack()
}
