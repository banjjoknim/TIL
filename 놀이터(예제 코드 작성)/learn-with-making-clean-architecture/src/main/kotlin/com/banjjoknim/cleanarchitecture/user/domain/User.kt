package com.banjjoknim.cleanarchitecture.user.domain

class User(
    var id: Long = 0L,
    var nickname: String
) {
    fun changeName(newNickname: String) {
        if (newNickname.length > NICKNAME_LENGTH_LIMIT) {
            throw IllegalArgumentException("회원의 닉네임의 길이는 $NICKNAME_LENGTH_LIMIT 를 초과할 수 없습니다.")
        }
        this.nickname = newNickname
    }

    companion object {
        private const val NICKNAME_LENGTH_LIMIT = 10
    }
}
