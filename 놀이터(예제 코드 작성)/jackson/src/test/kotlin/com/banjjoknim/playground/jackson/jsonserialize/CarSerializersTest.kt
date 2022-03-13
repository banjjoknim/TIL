package com.banjjoknim.playground.jackson.jsonserialize

import com.banjjoknim.playground.jackson.common.Car
import com.banjjoknim.playground.jackson.common.CarUsingJsonSerializeAnnotation
import com.banjjoknim.playground.jackson.common.CarUsingNoAnnotation
import com.banjjoknim.playground.jackson.common.CarUsingSecretAnnotation
import com.banjjoknim.playground.jackson.common.Owner
import com.fasterxml.jackson.databind.AnnotationIntrospector
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

/**
 * @see com.fasterxml.jackson.databind.ObjectMapper
 * @see com.fasterxml.jackson.databind.module.SimpleModule
 * @see com.fasterxml.jackson.databind.ser.DefaultSerializerProvider
 * @see com.fasterxml.jackson.databind.ser.BeanSerializer -> 객체를 직렬화할때 사용되는 Serializer
 * @see com.fasterxml.jackson.core.json.WriterBasedJsonGenerator
 * @see com.fasterxml.jackson.databind.ser.std.BeanSerializerBase
 * @see com.fasterxml.jackson.databind.ser.BeanPropertyWriter
 */
class CarSerializersTest {

    private lateinit var mapper: ObjectMapper

    companion object {
        private val owner = Owner("ban", 30)
        private val car = Car("banjjoknim", 10_000_000, owner)
        private val carUsingNoAnnotation = CarUsingNoAnnotation()
        private val carUsingJsonSerializeAnnotation = CarUsingJsonSerializeAnnotation()
        private val carUsingSecretAnnotation = CarUsingSecretAnnotation()
    }

    @BeforeEach
    fun setup() {
        mapper = ObjectMapper().registerKotlinModule()
    }

    @Test
    fun `기본 ObjectMapper의 동작을 테스트한다`() {
        // given

        // when
        val result = mapper.writeValueAsString(car)

        // then
        assertThat(result).isEqualTo("""{"name":"banjjoknim","price":10000000,"owner":{"name":"ban","age":30}}""")
    }

    @DisplayName("등록된 커스텀 직렬화기의 동작을 테스트한다")
    @Nested
    inner class CarSerializerTestCases {
        @Test
        fun `자동차의 모든 필드만 직렬화한다`() {
            // given
            val module = SimpleModule()
            module.addSerializer(Car::class.java, CarSerializer())
            mapper.registerModule(module)

            // when
            val result = mapper.writeValueAsString(car)

            // then
            assertThat(result).isEqualTo("""{"name":"banjjoknim","price":10000000}""")
        }

        @Test
        fun `자동차의 이름만 직렬화한다`() {
            // given
            val module = SimpleModule()
            module.addSerializer(Car::class.java, CarNameSerializer())
            mapper.registerModule(module)

            // when
            val result = mapper.writeValueAsString(car)

            // then
            assertThat(result).isEqualTo("""{"name":"banjjoknim"}""")
        }

        @Test
        fun `자동차의 가격만 직렬화한다`() {
            // given
            val module = SimpleModule()
            module.addSerializer(Car::class.java, CarPriceSerializer())
            mapper.registerModule(module)

            // when
            val result = mapper.writeValueAsString(car)

            // then
            assertThat(result).isEqualTo("""{"price":10000000}""")
        }

        @Test
        fun `자동차의 이름과 오너의 모든 필드를 직렬화한다`() {
            // given
            val module = SimpleModule()
            module.addSerializer(Car::class.java, CarNameOwnerSerializer())
            mapper.registerModule(module)

            // when
            val result = mapper.writeValueAsString(car)

            // then
            assertThat(result).isEqualTo("""{"name":"banjjoknim","owner":{"name":"ban","age":30}}""")
        }

        @Test
        fun `자동차의 이름과 오너의 이름만 직렬화한다`() {
            // given
            val module = SimpleModule()
            module.addSerializer(Car::class.java, CarNameOwnerNameSerializer())
            mapper.registerModule(module)

            // when
            val result = mapper.writeValueAsString(car)

            // then
            assertThat(result).isEqualTo("""{"name":"banjjoknim","owner":{"name":"ban"}}""")
        }

        @Test
        fun `아무 어노테이션도 적용하지 않고 직렬화한다`() {
            // given

            // when
            val actual = mapper.writeValueAsString(carUsingNoAnnotation)

            // then
            assertThat(actual).isEqualTo("""{"name":"banjjoknim","secret":"secret","price":10000000,"owner":{"name":"ban","age":30}}""")
        }

        @Test
        fun `@JsonSerialize 어노테이션을 적용하여 직렬화한다`() {
            // given

            // when
            val actual = mapper.writeValueAsString(carUsingJsonSerializeAnnotation)

            // then
            assertThat(actual).isEqualTo("""{"name":"banjjoknim","secret":"****","price":10000000,"owner":{"name":"ban","age":30}}""")
        }

        @Test
        fun `@Secret 어노테이션, AnnotationIntrospector 을 적용하여 직렬화한다`() {
            // given
            mapper.setAnnotationIntrospector(SecretAnnotationIntrospector())

            // when
            val actual = mapper.writeValueAsString(carUsingSecretAnnotation)

            // then
            assertThat(actual).isEqualTo("""{"name":"banjjoknim","secret":"****","price":10000000,"owner":{"name":"ban","age":30}}""")
        }

        @Test
        fun `@Secret 어노테이션, BeanSerializerModifier 를 적용하여 직렬화한다`() {
            // given
            val module = object : SimpleModule() {
                override fun setupModule(context: SetupContext) {
                    super.setupModule(context)
                    context.addBeanSerializerModifier(SecretBeanSerializerModifier())
                }
            }
            mapper.registerModule(module)

            // when
            val actual = mapper.writeValueAsString(carUsingSecretAnnotation)

            // then
            assertThat(actual).isEqualTo("""{"name":"banjjoknim","price":10000000,"owner":{"name":"ban","age":30}}""")
        }

        /**
         *
         * Kotlin + Spring Boot 를 사용한다면 `com.fasterxml.jackson.module:jackson-module-kotlin` 의존성을 사용할 것이다.
         *
         * 이를 사용하면 기본 생성자 없이도 `@RequestBody` 에서 json 을 객체로 역직렬화 할 수 있다.
         *
         * `com.fasterxml.jackson.module:jackson-module-kotlin` 에서 이러한 역할을 해주는 것이 KotlinAnnotationIntrospector 이다.
         *
         * 하지만 새로운 AnnotationIntrospector 를 등록하면 KotlinAnnotationIntrospector 가 무시되어 기본생성자 없이는 `@RequestBody` 객체를 만들지 못하게 된다.
         *
         * 따라서 아래와 같이 기존의 AnnotationIntrospector 도 등록해주어야 한다.
         *
         * 이는 AnnotationIntrospector.Pair 도우미 클래스를 사용해서 할 수 있다.
         *
         * 이때, 순서대로 기본 Introspector, 보조 Introspector 로 등록된다.
         *
         * [AnnotationIntrospector](https://github.com/FasterXML/jackson-docs/wiki/AnnotationIntrospector)
         *
         * @see com.fasterxml.jackson.databind.ObjectMapper
         * @see com.fasterxml.jackson.databind.introspect.AnnotationIntrospectorPair
         * @see com.fasterxml.jackson.databind.AnnotationIntrospector
         * @see com.fasterxml.jackson.module.kotlin.KotlinAnnotationIntrospector
         */
        @Test
        fun `Kotlin + Spring Boot를 사용하면 기본적으로 3가지의 AnnotationIntrospector 가 ObjectMapper 에 존재한다`() {
            // given
            val originalAnnotationIntrospector = mapper.serializationConfig.annotationIntrospector

            // when
            val allIntrospectorNames = mapper.serializationConfig.annotationIntrospector.allIntrospectors()
                .map { annotationIntrospector -> annotationIntrospector::class.simpleName }

            // then
            assertThat(originalAnnotationIntrospector.allIntrospectors()).hasSize(3)
            assertThat(allIntrospectorNames[0]).isEqualTo("KotlinAnnotationIntrospector")
            assertThat(allIntrospectorNames[1]).isEqualTo("JacksonAnnotationIntrospector")
            assertThat(allIntrospectorNames[2]).isEqualTo("KotlinNamesAnnotationIntrospector")
        }

        @Test
        fun `Kotlin + Spring Boot 를 사용할 시 ObjectMapper 에 새로운 AnnotationIntrospector 를 추가하면 KotlinAnnotationIntrospector 가 무시된다`() {
            // given
            val originalAnnotationIntrospector = mapper.serializationConfig.annotationIntrospector

            // when
            mapper.setAnnotationIntrospector(SecretAnnotationIntrospector())
            val allIntrospectorNames = mapper.serializationConfig.annotationIntrospector.allIntrospectors()
                .map { annotationIntrospector -> annotationIntrospector::class.simpleName }

            // then
            assertThat(originalAnnotationIntrospector.allIntrospectors()).hasSize(3)
            assertThat(allIntrospectorNames).hasSize(1)
            assertThat(allIntrospectorNames[0]).isEqualTo("SecretAnnotationIntrospector")
        }

        @Test
        fun `Kotlin + Spring Boot 를 사용할 시 ObjectMapper 에 새로운 AnnotationIntrospector 를 추가할 때 Pair 로 추가하면 KotlinAnnotationIntrospector 가 무시되지 않는다`() {
            // given
            val originalAnnotationIntrospector = mapper.serializationConfig.annotationIntrospector

            // when
            mapper.setAnnotationIntrospector(
                AnnotationIntrospector.pair(SecretAnnotationIntrospector(), originalAnnotationIntrospector) // 내부 구현은 아래와 같다.
//                AnnotationIntrospectorPair(SecretAnnotationIntrospector(), originalAnnotationIntrospector)
            )
            val allIntrospectorNames = mapper.serializationConfig.annotationIntrospector.allIntrospectors()
                .map { annotationIntrospector -> annotationIntrospector::class.simpleName }

            // then
            assertThat(allIntrospectorNames).hasSize(4)
            assertThat(allIntrospectorNames[0]).isEqualTo("SecretAnnotationIntrospector")
            assertThat(allIntrospectorNames[1]).isEqualTo("KotlinAnnotationIntrospector")
            assertThat(allIntrospectorNames[2]).isEqualTo("JacksonAnnotationIntrospector")
            assertThat(allIntrospectorNames[3]).isEqualTo("KotlinNamesAnnotationIntrospector")
        }
    }
}
