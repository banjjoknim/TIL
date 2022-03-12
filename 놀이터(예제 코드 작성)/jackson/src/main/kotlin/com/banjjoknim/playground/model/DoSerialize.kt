package com.banjjoknim.playground.model

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.PROPERTY)
annotation class DoSerialize(val doSerialize: Boolean = false)
