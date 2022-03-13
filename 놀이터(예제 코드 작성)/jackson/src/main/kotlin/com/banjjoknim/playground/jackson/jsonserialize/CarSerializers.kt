package com.banjjoknim.playground.jackson.jsonserialize

import com.banjjoknim.playground.jackson.common.Car
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer

/**
 * [jackson-object-mapper-tutorial](https://www.baeldung.com/jackson-object-mapper-tutorial) 참고.
 *
 * [how-to-(de)serialize-field-from-object-based-on-annotation-using-jackson](https://stackoverflow.com/questions/18659835/how-to-deserialize-field-from-object-based-on-annotation-using-jackson)
 *
 * Custom Serializer 를 만들기 위해서는 아래와 같이 StdSerializer<T> 를 상속해야 한다.
 *
 * 만약 어노테이션을 이용한 설정 또는 프로퍼티마다 다르게 작동하는 Serializer 를 만들고 싶다면 JsonSerializer 의 add-on interface 인 ContextualSerializer 를 구현하면 된다.
 *
 * JsonSerializer<T> 만 확장할 경우엔 애노테이션 정보를 얻을 수 없다. 추가적으로 ContextualSerializer 인터페이스를 구현해주면 createContextual() 메서드를 구현해줘야 하는데 두번째 인자로 넘어오는 BeanProperty 를 이용해 애노테이션 정보를 구할 수 있다.
 *
 * Custom Serializer 가 JsonSerializer<T> 와 ContextualSerialier 를 모두 구현할 경우 createContextual() 함수가 먼저 호출된다.
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
