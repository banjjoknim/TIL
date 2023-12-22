package com.banjjoknim.soliddesignpatternsample.designpattern.proxy.after

class ExtendsProxyWallet(balance: Int) : RealWallet(balance) {
    override fun deposit(amount: Int) {
        println("지갑에 $amount 원이 입금되었습니다.")
        super.deposit(amount)
    }

    override fun withdrawal(amount: Int) {
        println("지갑에서 $amount 원이 출금되었습니다.")
        super.withdrawal(amount)
    }
}
