package com.banjjoknim.springcloudopenfeign.domain

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class PostController(
    private val postClient: PostClient
) {

    @GetMapping("/posts")
    fun posts(): List<Post> {
        return postClient.findPosts()
    }

    @GetMapping("/posts/{postId}")
    fun post(@PathVariable postId: Long): Post {
        return postClient.findPost(postId)
    }
}
