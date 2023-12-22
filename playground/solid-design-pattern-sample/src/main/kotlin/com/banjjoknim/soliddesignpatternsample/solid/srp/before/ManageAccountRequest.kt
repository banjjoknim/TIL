package com.banjjoknim.soliddesignpatternsample.solid.srp.before

data class ManageAccountRequest(
    val accountId: Long,
    val holderName: String,
    val amount: Int,
    val type: ManageAccountType
)
