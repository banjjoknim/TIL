package com.banjjoknim.soliddesignpatternsample.designpattern.proxy.before

class WalletApplication

fun main() {
    val wallet = Wallet()
    val owner = Owner(wallet)
    owner.deposit(1000)
}
