package com.banjjoknim.soliddesignpatternsample.solid.srp.common

import javax.persistence.Entity
import javax.persistence.Id

@Entity
class Account(
    val holderName: String = "",
    var balance: Int = 0,
    @Id
    val id: Long = 0,
) {
    fun deposit(amount: Int) {
        this.balance = this.balance + amount
    }

    fun withdrawal(amount: Int) {
        this.balance = this.balance - amount
    }
}
