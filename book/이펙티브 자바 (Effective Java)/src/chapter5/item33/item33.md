# 아이템 33. 타입 안전 이종 컨테이너를 고려하라

- 제네릭은 `Set<E>`, `Map<K,V>` 등의 컬렉션과 `ThreadLocal<T>`, `AtomicReference<T>` 등의 단일원소 컨테이너에도 흔히 쓰인다.
- 이런 모든 쓰임에서 매개변수화되는 대상은 원소가 아닌 컨테이너 자신이다. 따라서 하나의 컨테이너에서 매개변수화할 수 있는 타입의 수가 제한된다.

## 타입 안전 이종 컨테이너 패턴(type safe heterogeneous container pattern)

- 컨테이너 대신 키를 매개변수화한 다음, 컨테이너에 값을 넣거나 뺄 때 매개변수화한 키를 함께 제공한다.
- 제네릭 타입 시스템이 값의 타입과 키가 같음을 보장해줄 것이다. 이러한 설계 방식을 타입 안전 이종 컨테이너 패턴이라고 한다.

### class 리터럴

- class 리터럴의 타입은 Class가 아닌 `Class<T>`다.
    - ex. `String.class -> Class<String>`, `Integer.class -> Class<Integer>`
    - 컴파일타임 타입 정보와 런타임 타입 정보를 알아내기 위해 메서드들이 주고받는 class 리터럴을 타입 토큰(type token)이라 한다.

### 타입 안전 이종 컨테이너 패턴 - API

```java
public class Favorites {
    public <T> void putFavorite(Class<T> type, T instance) {
        // ...
    }

    public <T> T getInstance(Class<T> type) {
        return null; // 편의상 null을 리턴하도록 작성했다.
    }
}
```

- 위 Favorites 클래스는 키가 매개변수화 되었다.
- 클라이언트는 즐겨찾기를 저장하거나 얻어올 때 Class 객체를 알려주면 된다.

### 타입 안전 이종 컨테이너 패턴 - 클라이언트

```java
public class Favorites {
    public static void main(String[] args) {
        Favorites favorites = new Favorites();

        favorites.putFavorite(String.class, "Java");
        favorites.putFavorite(Integer.class, 0xcafebabe);
        favorites.putFavorite(Class.class, Favorites.class);

        String favoriteString = favorites.getInstance(String.class);
        int favoriteInteger = favorites.getInstance(Integer.class);
        Class<?> favoriteClass = favorites.getInstance(Class.class);

        System.out.printf("%s %x %s%n", favoriteString, favoriteInteger, favoriteClass.getName());
    }
}
```

- Favorites 인스턴스는 타입 안전하다.
- 또한 모든 키의 타입이 제각각이라, 일반적인 맵과 달리 여러 가지 타입의 원소를 담을 수 있다.

### 타입 안전 이종 컨테이너 패턴 - 구현

```java
public class Favorites {
    private Map<Class<?>, Object> favorites = new HashMap<>();

    public <T> void putFavorite(Class<T> type, T instance) {
        favorites.put(Objects.requireNonNull(type), instance);
    }

    public <T> T getInstance(Class<T> type) {
        return type.cast(favorites.get(type));
    }
}
```

- 비한정적 와일드카드 타입이라 이 맵 안에 아무것도 넣을 수 없는 것이 아니다. 와일드카드가 중첩(nested)되어 있기 때문에 가능하다.
- 맵이 아니라 키가 와일드카드 타입인 것이다. 이는 모든 키가 서로 다른 매개변수화 타입일 수 있다는 뜻으로, 따라서 다양한 타입을 지원할 수 있다.
- favorites 맵의 값 타입은 단순히 Object다. 이 맵은 키와 값 사이의 타입 관계를 보증하지 않는다는 뜻이다.
    - 즉, 모든 값이 키로 명시한 타입임을 보증하지 않는다.
- 이 객체의 타입은 Object다. 이를 T로 바꿔 반환해야 한다. 따라서 getFavorite 구현은 Class의 cast 메서드를 사용해 이 객체 참조를 Class 객체가 가리키는 타입으로 동적 형변환한다.

#### cast 메서드

- 형변환 연산자의 동적 버전이다.
- 단순히 주어진 인수가 Class 객체가 알려주는 타입의 인스턴스인지를 검사한 다음, 맞다면 그 인수를 그대로 반환하고, 아니면 ClassCastException을 던진다.
- cast 메서드의 시그니처는 아래와 같다. Class 객체의 타입 매개변수와 cast 메서드의 반환 타입이 같다.

```java
public class Class<T> {
    T cast(Object obj);
}
```

### 타입 안전 이종 컨테이너 - 제약

#### 동적 형변환으로 런타임 타입 안전성 확보

```java
public class Favorites {
    public <T> void putFavorite(Class<T> type, T instance) {
        favorites.put(Objects.requireNonNull(type), type.cast(instance));
    }
}
```

- 첫 번째 제약 : Class 객체를 제네릭이 아닌 로 타입으로 넘지면 타입 안전성이 쉽게 깨질 수 있으므로 주의해야 한다.
    - 따라서 타입 불변식을 어기지 않는다고 보장하려면 컨테이너에 값을 넣기 전에 미리 검사하면 된다.
    - 방법은 위와 같이 동적 형변환을 사용하면 된다.

- 두 번째 제약 : 실체화불가 타입에는 사용할 수 없다.
    - String, String[]은 저장할 수 있어도 `List<String>`은 저장할 수 없다.
    - `List<String>`을 저장하려는 코드는 컴파일되지 않는다. `List<String>`용 Class 객체를 얻을 수 없기 때문이다.
    - `List<String>.class`라고 쓰면 문법 오류가 난다. `List<String>.class`와 `List<Integer>.class`를 이용해서 똑같은 타입의 객체 참조를 반환한다면 문제가 생길
      것이다.

### 애너테이션 API & 한정적 타입 토큰

```java
public interface AnnotatedElement {
    // ...

    <T extends Annotation> T getAnnotation(Class<T> annotationClass);

    // ...
}
```

- 만약 타입을 제한하고 싶다면 한정적 타입 토큰을 활용하면 된다.
- 위의 애너테이션 API는 한정적 타입 토큰을 적극적으로 사용하는데, 이 메서드는 대상 요소에 달려 있는 애너테이션을 런타임에 읽어오는 기능을 한다.
- 이 메서드는 리플렉션의 대상이 되는 타입들, 즉 클래스(`java.lang.Class<T>`), 메서드(`java.lang.reflect.Method`), 필드(`java.lang.reflect.Fied`) 같이
  프로그램 요소를 표현하는 타입들에서 구현된다.
- annotationType 인수는 애터테이션 타입을 뜻하는 한정적 타입 토큰이다.

### asSubClass를 사용해 한정적 타입 토큰을 안전하게 형변환한다.

- Class 클래스는 형변환을 안전하게, 동적으로 수행해주는 인스턴스 메서드를 제공한다.
- asSubClass 메서드로, 호출된 인스턴스 자신의 Class 객체를 인수가 명시한 클래스로 형변환한다.
    - 형변환된다는 것은 이 클래스가 인수로 명시한 클래스의 하위 클래스라는 뜻이다.
    - 형변환에 성공하면 인수로 받은 클래스 객체를 반환하고, 실패하면 ClassCastException을 던진다.
- 아래 메서드는 컴파일 시점에는 타입을 알 수 없는 애너테이션을 asSubClass 메서드를 사용해 런타임에 읽어내는 예다.

```java
public class Item33 {
    static Annotation getAnnotation(AnnotatedElement element, String annotationTypeName) {
        Class<?> annotationType = null; // 비한정적 타입 토큰
        try {
            annotationType = Class.forName(annotationTypeName);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
        return element.getAnnotation(annotationType.asSubclass(Annotation.class));
    }
}
```

## 핵심 정리

- 컬렉션 API로 대표되는 일반적인 제네릭 형태에서는 한 컨테이너가 다룰 수 있는 타입 매개변수의 수가 고정되어 있다.
- 컨테이너 자체가 아닌 키를 타입 매개변수로 바꾸면 이런 제약이 없는 타입 안전 이종 컨테이너를 만들 수 있다.
- 타입 안전 이종 컨테이너는 Class를 키로 쓰며, 직접 구현한 키 타입도 쓸 수 있다. 이런 식으로 쓰이는 Class 객체를 타입 토큰이라 한다.
  `- ex. 데이터베이스의 행(컨테이너)를 표현한 DatabaseRow 타입` -> `Column<T>`