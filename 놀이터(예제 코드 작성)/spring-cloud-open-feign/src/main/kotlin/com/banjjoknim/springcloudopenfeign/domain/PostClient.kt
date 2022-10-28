package com.banjjoknim.springcloudopenfeign.domain

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

/**
 * Feign is a declarative web service client. It makes writing web service clients easier.
 * To use Feign create an interface and annotate it.
 * It has pluggable annotation support including Feign annotations and JAX-RS annotations.
 * Feign also supports pluggable encoders and decoders.
 * Spring Cloud adds support for Spring MVC annotations and for using the same HttpMessageConverters used by default in Spring Web.
 * Spring Cloud integrates Eureka, as well as Spring Cloud LoadBalancer to provide a load-balanced http client when using Feign.
 *
 * 쉽게 말하면 RestTemplate, RestClient 등의 `HTTP Client`를 어노테이션으로 선언하여 사용하는 것이라 생각하면 된다.
 * 인터페이스를 만들고 @FeignClient 를 선언하기만 하면 구현체는 런타임에 알아서 만들어진다.
 *
 * @see org.springframework.cloud.openfeign.FeignClient
 */
@FeignClient(value = "post", url = "https://jsonplaceholder.typicode.com/")
interface PostClient {

    @GetMapping("/posts")
    fun findPosts(): List<Post>

    @GetMapping("/posts/{postId}")
    fun findPost(@PathVariable postId: Long): Post
}
