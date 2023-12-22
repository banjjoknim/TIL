package com.banjjoknim.playground.jackson.common

import com.fasterxml.jackson.annotation.JacksonAnnotation

/**
 * [jackson-annotations](https://www.baeldung.com/jackson-annotations) 참고.
 *
 * @see com.fasterxml.jackson.annotation.JacksonAnnotation
 * @see com.fasterxml.jackson.annotation.JacksonAnnotationsInside
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD) // 현재 상황에서는 PROPERTY 로 적용할 경우 제대로 적용되지 않는다. 아마 어노테이션 자체가 자바 기반으로 사용되어 PROPERTY 를 인식하지 못하는 것 같다(자바에서는 PROPERTY 타입을 사용할 수 없음).
@JacksonAnnotation // NOTE: important; MUST be considered a 'Jackson' annotation to be seen(or recognized otherwise via AnnotationIntrospect.isHandled())
annotation class Secret(
    val substituteValue: String = ""
)
