package com.banjjoknim.soliddesignpatternsample.designpattern.strategy.before

class PriceCalculatorBefore {
    fun calculate(price: Int, ageType: AgeTypeBefore): Int {
        return when (ageType) {
            AgeTypeBefore.KIDS -> price * 70 / 100
            AgeTypeBefore.ADULTS -> price * 85 / 100
        }
    }
}
