package com.banjjoknim.playground.jackson.jsonserialize

import com.banjjoknim.playground.jackson.common.Car
import com.banjjoknim.playground.jackson.common.CarUsingJsonSerializeAnnotation
import com.banjjoknim.playground.jackson.common.CarUsingNoAnnotation
import com.banjjoknim.playground.jackson.common.CarUsingSecretAnnotation
import com.banjjoknim.playground.jackson.common.Owner
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
        fun `@Secret 어노테이션을 적용하여 직렬화한다`() {
            // given
            mapper.setAnnotationIntrospector(SecretAnnotationIntrospector())

            // when
            val actual = mapper.writeValueAsString(carUsingSecretAnnotation)

            // then
            assertThat(actual).isEqualTo("""{"name":"banjjoknim","secret":"****","price":10000000,"owner":{"name":"ban","age":30}}""")
        }
    }
}
