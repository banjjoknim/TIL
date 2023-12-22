package com.banjjoknim.soliddesignpatternsample.designpattern.proxy.after

open class RealWallet(
    private var balance: Int = 0
) : Wallet {
    override fun deposit(amount: Int) {
        this.balance += amount
    }

    override fun withdrawal(amount: Int) {
        this.balance -= amount
    }
}
