package com.banjjoknim.soliddesignpatternsample.designpattern.strategy.after

class PriceCalculatorApplicationAfter

fun main() {
    val priceCalculator = PriceCalculatorAfter()
    val price = 10000
    val ageType = AgeTypeAfter.KIDS
    val calculatedPrice = priceCalculator.calculate(price, ageType.discountPolicy)
    println(calculatedPrice)
}
