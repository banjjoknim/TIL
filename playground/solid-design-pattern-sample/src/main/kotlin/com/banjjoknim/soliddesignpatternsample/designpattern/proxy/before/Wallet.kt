package com.banjjoknim.soliddesignpatternsample.designpattern.proxy.before

class Wallet(
    private var balance: Int = 0
) {
    fun deposit(amount: Int) {
        println("지갑에 $amount 원이 입금되었습니다.")
        this.balance += amount
    }

    fun withdrawal(amount: Int) {
        println("지갑에서 $amount 원이 출금되었습니다.")
        this.balance -= amount
    }
}
