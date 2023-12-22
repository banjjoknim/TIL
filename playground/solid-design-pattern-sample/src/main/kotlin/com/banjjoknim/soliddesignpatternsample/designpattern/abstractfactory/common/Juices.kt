package com.banjjoknim.soliddesignpatternsample.designpattern.abstractfactory.common

interface Juice {
    val name: String
}

data class AppleJuice(override val name: String = "사과주스") : Juice

data class BananaJuice(override val name: String = "바나나주스") : Juice

data class OrangeJuice(override val name: String = "오렌지주스") : Juice
