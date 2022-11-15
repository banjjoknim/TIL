package com.banjjoknim.soliddesignpatternsample.designpattern.composite.after

class FranchiseeStore(
    val name: String
) : Store {
    override fun calculate() {
        println("프랜차이즈 가게 [$name]이(가) 정산을 진행합니다.")
    }
}
