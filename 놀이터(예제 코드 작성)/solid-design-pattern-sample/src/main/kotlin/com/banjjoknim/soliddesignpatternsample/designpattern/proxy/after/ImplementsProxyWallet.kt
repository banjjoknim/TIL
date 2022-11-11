package com.banjjoknim.soliddesignpatternsample.designpattern.proxy.after

class ImplementsProxyWallet(
    private val realWallet: RealWallet
) : Wallet {
    override fun deposit(amount: Int) {
        println("지갑에 $amount 원이 입금되었습니다.")
        realWallet.deposit(amount)
    }

    override fun withdrawal(amount: Int) {
        println("지갑에서 $amount 원이 출금되었습니다.")
        realWallet.withdrawal(amount)
    }
}
