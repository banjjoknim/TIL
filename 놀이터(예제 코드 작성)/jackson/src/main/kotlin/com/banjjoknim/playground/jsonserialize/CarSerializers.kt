package com.banjjoknim.playground.jsonserialize

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer

/**
 * [jackson-object-mapper-tutorial](https://www.baeldung.com/jackson-object-mapper-tutorial) 참고.
 *
 * Custom Serializer 를 만들기 위해서는 아래와 같이 StdSerializer<T> 를 상속해야 한다.
 *
 * @see com.fasterxml.jackson.databind.ser.std.StdSerializer
 * @see com.fasterxml.jackson.databind.ser.ContextualSerializer
 */
class CarSerializer : StdSerializer<Car>(Car::class.java) {
    override fun serialize(value: Car, gen: JsonGenerator, provider: SerializerProvider) {
        gen.writeStartObject()
        gen.writeStringField("name", value.name)
        gen.writeNumberField("price", value.price)
        gen.writeEndObject()
    }
}

class CarNameSerializer : StdSerializer<Car>(Car::class.java) {
    override fun serialize(value: Car, gen: JsonGenerator, provider: SerializerProvider) {
        gen.writeStartObject()
        gen.writeStringField("name", value.name)
        gen.writeEndObject()
    }
}

class CarPriceSerializer : StdSerializer<Car>(Car::class.java) {
    override fun serialize(value: Car, gen: JsonGenerator, provider: SerializerProvider) {
        gen.writeStartObject()
        gen.writeNumberField("price", value.price)
        gen.writeEndObject()
    }
}
