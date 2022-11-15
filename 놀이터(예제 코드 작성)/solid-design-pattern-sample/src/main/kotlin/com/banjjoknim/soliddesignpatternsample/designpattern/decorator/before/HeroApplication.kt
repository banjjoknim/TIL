package com.banjjoknim.soliddesignpatternsample.designpattern.decorator.before

import com.banjjoknim.soliddesignpatternsample.designpattern.decorator.common.Hero

class HeroApplication

fun main() {
    val hero = Hero()
    hero.attack()

    val fireSword = FireSword()
    hero.changeSword(fireSword)
    hero.attack()

    val iceSword = IceSword()
    hero.changeSword(iceSword)
    hero.attack()

    val fireIceSword = FireIceSword()
    hero.changeSword(fireIceSword)
    hero.attack()
}
