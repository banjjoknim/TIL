package com.banjjoknim.soliddesignpatternsample.designpattern.decorator.common

class Hero(
    private var sword: Sword = Sword.DEFAULT
) {
    fun changeSword(sword: Sword) {
        this.sword = sword
    }

    fun attack() {
        val swordSkill = sword.skill()
        println("용사가 [$swordSkill]을 발동합니다.")
    }
}
