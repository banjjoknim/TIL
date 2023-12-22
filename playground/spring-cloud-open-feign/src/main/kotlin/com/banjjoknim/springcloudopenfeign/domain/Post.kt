package com.banjjoknim.springcloudopenfeign.domain

data class Post(
    val userId: Long,
    val id: Long,
    val title: String,
    val completed: Boolean
)
