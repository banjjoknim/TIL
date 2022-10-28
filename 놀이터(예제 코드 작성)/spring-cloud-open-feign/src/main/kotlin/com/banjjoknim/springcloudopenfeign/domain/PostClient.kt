package com.banjjoknim.springcloudopenfeign.domain

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@FeignClient(value = "post", url = "https://jsonplaceholder.typicode.com/")
interface PostClient {

    @GetMapping("/posts")
    fun findPosts(): List<Post>

    @GetMapping("/posts/{postId}")
    fun findPost(@PathVariable postId: Long): Post
}
