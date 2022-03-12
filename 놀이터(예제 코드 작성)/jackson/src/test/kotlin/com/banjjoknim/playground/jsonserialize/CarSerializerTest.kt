package com.banjjoknim.playground.jsonserialize

import com.banjjoknim.playground.jackson.jsonserialize.CarNameSerializer
import com.banjjoknim.playground.jackson.jsonserialize.CarPriceSerializer
import com.banjjoknim.playground.jackson.jsonserialize.CarSerializer
import com.banjjoknim.playground.model.Car
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
class CarSerializerTest {

    private lateinit var mapper: ObjectMapper

    companion object {
        private val car = Car("banjjoknim", 10_000_000)
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
        assertThat(result).isEqualTo("""{"name":"banjjoknim","price":10000000}""")
    }

    @DisplayName("등록된 커스텀 직렬화기의 동작을 테스트한다")
    @Nested
    inner class CarSerializerTestCases {
        @Test
        fun `모든 필드를 직렬화한다`() {
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
        fun `이름만 직렬화한다`() {
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
        fun `가격만 직렬화한다`() {
            // given
            val module = SimpleModule()
            module.addSerializer(Car::class.java, CarPriceSerializer())
            mapper.registerModule(module)

            // when
            val result = mapper.writeValueAsString(car)

            // then
            assertThat(result).isEqualTo("""{"price":10000000}""")
        }
    }
}
