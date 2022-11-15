package com.banjjoknim.soliddesignpatternsample.designpattern.strategy.before

class PriceCalculatorApplicationBefore

fun main() {
    val priceCalculator = PriceCalculatorBefore()
    val price = 10000
    val ageTypeBefore = AgeTypeBefore.ADULTS
    val calculatedPrice = priceCalculator.calculate(price, ageTypeBefore)
    println(calculatedPrice)
}
