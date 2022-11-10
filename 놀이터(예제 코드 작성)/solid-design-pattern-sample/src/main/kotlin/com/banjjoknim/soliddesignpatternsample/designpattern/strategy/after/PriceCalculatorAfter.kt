package com.banjjoknim.soliddesignpatternsample.designpattern.strategy.after

class PriceCalculatorAfter {
    fun calculate(price: Int, discountPolicy: DiscountPolicy): Int {
        return discountPolicy.calculate(price)
    }
}

fun interface DiscountPolicy {
    fun calculate(price: Int): Int
}
