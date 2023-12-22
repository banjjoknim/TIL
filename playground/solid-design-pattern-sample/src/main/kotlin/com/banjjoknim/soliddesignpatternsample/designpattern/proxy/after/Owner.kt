package com.banjjoknim.soliddesignpatternsample.designpattern.proxy.after

class Owner(
    private val wallet: Wallet
) {
    fun deposit(amount: Int) {
        wallet.deposit(amount)
    }

    fun withdrawal(amount: Int) {
        wallet.withdrawal(amount)
    }
}
