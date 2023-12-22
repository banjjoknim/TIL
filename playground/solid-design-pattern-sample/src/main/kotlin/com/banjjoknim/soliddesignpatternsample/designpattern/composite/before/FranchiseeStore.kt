package com.banjjoknim.soliddesignpatternsample.designpattern.composite.before

class FranchiseeStore(
    val name: String
) {
    fun calculate() {
        println("프랜차이즈 가게 [$name]이(가) 정산을 진행합니다.")
    }
}
