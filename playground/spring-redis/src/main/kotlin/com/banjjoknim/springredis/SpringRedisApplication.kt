package com.banjjoknim.springredis

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SpringRedisApplication

fun main(args: Array<String>) {
	runApplication<SpringRedisApplication>(*args)
}
