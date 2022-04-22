package com.banjjoknim.cleanarchitecture.user.pojo

class User(
    var id: Long = 0L,
    var nickname: Nickname
) {
    fun changeNickname(newNickname: String) {
        this.nickname = Nickname(newNickname)
    }
}
