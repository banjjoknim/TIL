package com.banjjoknim.soliddesignpatternsample.designpattern.abstractfactory.common

interface Fruit {
    val name: String
}

data class Apple(override val name: String = "사과") : Fruit

data class Banana(override val name: String = "바나나") : Fruit

data class Orange(override val name: String = "오렌지") : Fruit
