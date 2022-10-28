package com.banjjoknim.springcloudopenfeign.domain.member

import javax.persistence.Entity
import javax.persistence.Id

@Entity
class Member(
    @Id
    val id: Long = 0L,
    val name: String = "",
    var nickname: String = ""
)

