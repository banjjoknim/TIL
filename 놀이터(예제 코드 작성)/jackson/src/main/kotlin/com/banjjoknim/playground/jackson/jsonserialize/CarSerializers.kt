package com.banjjoknim.playground.jackson.jsonserialize

import com.banjjoknim.playground.model.Car
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer

/**
 * [jackson-object-mapper-tutorial](https://www.baeldung.com/jackson-object-mapper-tutorial) 참고.
 *
 * Custom Serializer 를 만들기 위해서는 아래와 같이 StdSerializer<T> 를 상속해야 한다.
 *
 * 만약 어노테이션을 이용한 설정 또는 프로퍼티마다 다르게 작동하는 Serializer 를 만들고 싶다면 JsonSerializer 의 add-on interface 인 ContextualSerializer 를 구현하면 된다.
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

class CarNameOwnerSerializer : StdSerializer<Car>(Car::class.java) {
    override fun serialize(value: Car, gen: JsonGenerator, provider: SerializerProvider) {
        gen.writeStartObject()
        gen.writeStringField("name", value.name)
        gen.writeObjectField("owner", value.owner)
        gen.writeEndObject()
    }
}

class CarNameOwnerNameSerializer : StdSerializer<Car>(Car::class.java) {
    override fun serialize(value: Car, gen: JsonGenerator, provider: SerializerProvider) {
        gen.writeStartObject()
        gen.writeStringField("name", value.name)
        gen.writeObjectFieldStart("owner")
        gen.writeStringField("name", value.owner.name)
        gen.writeEndObject()
    }
}
