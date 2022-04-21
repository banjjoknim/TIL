package com.banjjoknim.cleanarchitecture.user.domain.entity

import javax.persistence.Column
import javax.persistence.Embeddable

@Embeddable
data class NicknameColumn(
    @Column(name = "nickname")
    val value: String
)
