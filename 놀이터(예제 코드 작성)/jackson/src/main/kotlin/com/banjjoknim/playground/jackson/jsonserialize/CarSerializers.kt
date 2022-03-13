package com.banjjoknim.playground.jackson.jsonserialize

import com.banjjoknim.playground.jackson.common.Car
import com.banjjoknim.playground.jackson.common.Secret
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.introspect.Annotated
import com.fasterxml.jackson.databind.introspect.AnnotatedMember
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector
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
 * ContexutalSerializer 를 사용하는 방법은 [TestContextualSerialization](https://github.com/FasterXML/jackson-databind/blob/master/src/test/java/com/fasterxml/jackson/databind/contextual/TestContextualSerialization.java) 참고하도록 한다.
 *
 * Custom Serializer 가 JsonSerializer<T> 와 ContextualSerialier 를 모두 구현할 경우 createContextual() 함수가 먼저 호출된다.
 *
 * @see com.fasterxml.jackson.databind.ser.std.StdSerializer
 * @see com.fasterxml.jackson.databind.ser.std.StringSerializer
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

class UsingJsonSerializeAnnotationCarSerializer : StdSerializer<String>(String::class.java) {
    override fun serialize(value: String, gen: JsonGenerator, provider: SerializerProvider) {
        gen.writeString("****")
    }
}

/**
 * AnnotationIntrospector 를 상속한 JacksonAnnotationIntrospector 은 Jackson 라이브러리가 직렬화/역직렬화시 `JacksonAnnotation` 정보를 어떻게 처리할지에 대한 정보가 정의되어 있는 클래스다.
 *
 * 따라서 어노테이션별로 어떻게 처리할지 재정의하고 싶다면 이 녀석을 override 해준뒤 ObjectMapper 에 등록해주면 된다.
 *
 * 등록할 때는 `ObjectMapper#setAnnotationIntrospector()` 를 사용한다.
 *
 * [FasterXML - AnnotationIntrospector](https://github.com/FasterXML/jackson-docs/wiki/AnnotationIntrospector)
 *
 * @see com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector#
 * @see com.fasterxml.jackson.databind.ObjectMapper
 *
 */
class SecretAnnotationIntrospector : JacksonAnnotationIntrospector() {
    /**
     *
     * `@JsonIgnore` 를 적용했을 때 무시할지 여부를 판단하는 함수이다.
     *
     * 따라서 직렬화 / 역직렬화시 무시하고 싶은 프로퍼티가 있다면 이 함수를 override 하면 된다.
     */
    override fun hasIgnoreMarker(m: AnnotatedMember): Boolean {
        return super.hasIgnoreMarker(m)
    }

    /**
     * `@JsonSerailize` 가 붙은 어노테이션의 처리를 재정의할 때 override 하는 함수이다.
     *
     * 자세한 내용은 JacksonAnnotationIntrospector#findSerializer() 의 구현을 살펴보도록 한다.
     *
     * 특정 프로퍼티에 대해 어떤 Serializer 를 사용할 것인지 결정하는 함수이다.
     *
     * 따라서 특정 조건에 따라 직렬화 처리에 사용할 Serializer 를 정의하고 싶다면 이 함수를 override 하면 된다.
     */
    override fun findSerializer(a: Annotated): Any? {
        val annotation = a.getAnnotation(Secret::class.java)
        if (annotation != null) {
            return SecretAnnotationSerializer(annotation.substituteValue)
        }
        return super.findSerializer(a) // 기존 JacksonAnnotationIntrospector 의 것을 사용한다.
    }
}

class SecretAnnotationSerializer(private val substituteValue: String) : StdSerializer<String>(String::class.java) {
    override fun serialize(value: String, gen: JsonGenerator, provider: SerializerProvider) {
        gen.writeString(substituteValue)
    }
}
