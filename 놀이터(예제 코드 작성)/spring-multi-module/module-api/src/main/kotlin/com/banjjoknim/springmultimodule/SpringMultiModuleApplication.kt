package com.banjjoknim.springmultimodule

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SpringMultiModuleApplication

fun main(args: Array<String>) {
    runApplication<SpringMultiModuleApplication>(*args)
}
