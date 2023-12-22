package com.banjjoknim.springcloudopenfeign

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@EnableFeignClients
@SpringBootApplication
class SpringCloudOpenFeignApplication

fun main(args: Array<String>) {
	runApplication<SpringCloudOpenFeignApplication>(*args)
}
