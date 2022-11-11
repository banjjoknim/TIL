package com.banjjoknim.soliddesignpatternsample.designpattern.proxy.after

class ProxyWalletApplication

fun main() {
    val wallet = ImplementsProxyWallet(RealWallet())
    val owner = Owner(wallet)
    owner.deposit(1000)
}
