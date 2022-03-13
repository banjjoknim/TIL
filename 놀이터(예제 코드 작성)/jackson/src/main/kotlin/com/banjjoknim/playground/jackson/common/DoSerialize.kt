package com.banjjoknim.playground.jackson.common

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.PROPERTY)
annotation class DoSerialize(val doSerialize: Boolean = false)
