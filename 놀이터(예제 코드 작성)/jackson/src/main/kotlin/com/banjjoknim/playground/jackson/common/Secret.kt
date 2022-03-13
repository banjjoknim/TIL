package com.banjjoknim.playground.jackson.common

import com.fasterxml.jackson.annotation.JacksonAnnotation

/**
 * [jackson-annotations](https://www.baeldung.com/jackson-annotations) 참고.
 *
 * @see com.fasterxml.jackson.annotation.JacksonAnnotation
 * @see com.fasterxml.jackson.annotation.JacksonAnnotationsInside
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.PROPERTY)
@JacksonAnnotation // Jackson 에서 이 어노테이션을 인식할 수 있게 만들어준다.
annotation class Secret
