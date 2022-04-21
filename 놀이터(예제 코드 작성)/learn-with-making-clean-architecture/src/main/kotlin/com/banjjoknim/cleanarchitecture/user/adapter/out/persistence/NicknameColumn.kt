package com.banjjoknim.cleanarchitecture.user.adapter.out.persistence

import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
data class NicknameColumn(
    @Column(name = "nickname")
    val value: String
)
