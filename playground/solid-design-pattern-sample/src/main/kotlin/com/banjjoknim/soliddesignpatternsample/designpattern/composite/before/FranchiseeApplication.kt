package com.banjjoknim.soliddesignpatternsample.designpattern.composite.before

class FranchiseeApplication

fun main() {
    val coffeeCorporation = FranchiseeCorporation("커피프린스")
    coffeeCorporation.addStore(FranchiseeStore("1호점"))
    coffeeCorporation.calculateAllStores()

    println()
    println("가맹점 정산을 완료했습니다.")
    println()

    val franchiseeCorporation = FranchiseeCorporation("모두의 프랜차이즈")
    franchiseeCorporation.addCorporation(coffeeCorporation)
    franchiseeCorporation.calculateAllCorporations()
}
