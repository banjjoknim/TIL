package com.banjjoknim.cleanarchitecture.user.domain

data class Nickname(val value: String) {
    init {
        if (value.length > NICKNAME_LENGTH_LIMIT) {
            throw IllegalArgumentException("회원의 닉네임의 길이는 $NICKNAME_LENGTH_LIMIT 를 초과할 수 없습니다.")
        }
    }

    companion object {
        private const val NICKNAME_LENGTH_LIMIT = 10
    }
}
