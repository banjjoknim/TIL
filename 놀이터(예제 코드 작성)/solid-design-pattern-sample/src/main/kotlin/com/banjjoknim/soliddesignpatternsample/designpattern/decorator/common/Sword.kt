package com.banjjoknim.soliddesignpatternsample.designpattern.decorator.common

abstract class Sword {
    abstract fun skill(): String

    companion object {
        val DEFAULT = object : Sword() {
            override fun skill(): String {
                return "참격"
            }
        }
    }
}
