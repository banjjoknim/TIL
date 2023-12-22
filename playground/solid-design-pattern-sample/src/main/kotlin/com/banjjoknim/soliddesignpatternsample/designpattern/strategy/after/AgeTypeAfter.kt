package com.banjjoknim.soliddesignpatternsample.designpattern.strategy.after

enum class AgeTypeAfter(val discountPolicy: DiscountPolicy) {
    KIDS(DiscountPolicy { price -> price * 70 / 100 }),
    ADULTS(DiscountPolicy { price -> price * 90 / 100 }),
}
