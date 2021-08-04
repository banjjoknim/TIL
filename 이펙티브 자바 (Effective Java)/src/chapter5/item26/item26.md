# 아이템 26. 로 타입은 사용하지 말라

## 제네릭 클래스, 제네릭 인터페이스

- 클래스와 인터페이스 선언에 타입 매개변수(type parameter)가 쓰이면, 이를 **제네릭 클래스** 혹은 **제네릭 인터페이스**라 한다.
    - ex. List<E> 는 원소의 타입을 나타내는 매개변수 E를 받은 List이다.
- 제네릭 클래스와 제네릭 인터페이스를 통틀어 **제네릭 타입(generic type)**이라 한다.
- 각각의 제네릭 타입은 일련의 **매개변수화 타입(parameterized type)**을 정의한다.
    - 먼저 클래스(혹은 인터페이스) 이름이 나오고, 이어서 꺾쇠괄호 안에 실제 타입 매개변수들을 나열한다.
    - ex. List<String>은 원소의 타입이 String인 리스트를 뜻하는 매개변수화 타입이다. 여기서 String이 정규(formal) 타입 매개변수 E에 해당하는 실제(actual) 타입
      매개변수이다.
- 제네릭 타입을 하나 정의하면 그에 딸린 **로 타입(raw type)**도 함께 정의된다.
    - 로 타입이란 제네릭 타입에서 타입 매개변수를 전혀 사용하지 않을 때를 말한다.
    - ex. List<E>의 로 타입은 List이다.
    - 로 타입은 타입 선언에서 제네릭 타입 정보가 전부 지워진 것처럼 동작하는데, 이는 제네릭 도입 이전 코드와의 호환을 위한 것이다.

### 컬렉션의 로 타입 - 따라 하지 말 것!

```java
class RawCollection {
    private final Collection stamps = null; // 편의상 null을 할당했다.

    void sample() {
        stamps.add(new Coin()); // "unchecked call" 경고를 내뱉는다.
    }

    private static class Coin {
        // ...
    }
}
```

- 실수로 도장(Stamp) 대신 동전(Coin)을 넣어도 아무 오류 없이 컴파일되고 실행된다(컴파일러가 모호한 경고 메시지를 보여주긴 할 것이다).

### 반복자의 로 타입 - 따라 하지 말 것!

```java
class RawIterator {
    private final Collection stamps = null; // 편의상 null을 할당했다.

    void sample() {
        for (Iterator i = stamps.iterator(); i.hasNext(); ) {
            Stamp stamp = (Stamp) i.next(); // ClassCastException을 던진다.
            stamp.cancel();
        }
    }

    private static class Stamp {
        public void cancel() {
            // ...
        }
    }
}
```

- **오류는 가능한 한 발생 즉시, 이상적으로는 컴파일할 때 발견하는 것이 좋다.**
- 여기서는 오류가 발생하고 한참 뒤인 런타임에서야 알아챌 수 있다.

### 매개변수화된 컬렉션 타입 - 타입 안정성 확보!

```java
class ParameterizedCollectionSample {
    private final Collection<Stamp> stamps = null;
}
```

- 이렇게 선언하면 컴파일러는 stamps에는 Stamp 인스턴스만 넣어야 함을 컴파일러가 인지하게 된다(컴파일러 경고를 숨기지 않았어야 한다).
- 컴파일러는 컬렉션에서 원소를 꺼내는 모든 곳에 보이지 않는 형변환을 추가하여 절대 실패하지 않음을 보장한다.
- 로 타입을 쓰면 제네릭이 안겨주는 안정성과 표현력을 모두 잃게 된다. 따라서 절대로 사용하지 말아야 한다.
    - 로 타입은 단지 이전 버전과의 호환성 때문에 존재할 뿐이다.
    - 이를 위해 제네릭 구현에는 소거(erasure) 방식을 사용했다.

## 로 타입 vs 제네릭 타입

- List 같은 로 타입은 사용해서는 안 된다. 하지만 `List<Object>`처럼 임의 객체를 허용하는 매개변수화 타입은 괜찮다.
- List는 제네릭 타입에서 완전히 발을 뺀 것이고, `List<Object>`는 모든 타입을 허용한다는 의사를 컴파일러에 명확히 전달한 것이다.
- 매개변수로 List를 받는 메서드에 List<String>을 넘길 수는 있지만, `List<Object>`를 받는 메서드에는 넘길 수 없다.
    - 이는 제네릭의 하위 타입 규칙 때문이다.
    - `List<String>`은 로 타입인 List의 하위 타입이지만, `List<Object>`의 하위 타입은 아니다.
    - `List<Object>` 같은 매개변수화 타입을 사용할 때와 달리 List 같은 로 타입을 사용하면 타입 안정성을 잃게 된다.

### 런타임에 실패한다. - unsafeAdd 메서드가 로 타입(List)을 사용

```java
class FailWithRawType {
    public static void main(String[] args) {
        List<String> strings = new ArrayList<>();
        unsafeAdd(strings, Integer.valueOf(42));
        String s = strings.get(0); // 컴파일러가 자동으로 형변환 코드를 넣어준다.
    }

    private static void unsafeAdd(List list, Object o) {
        list.add(o);
    }
}
```

참고로, 아래는 컴파일러가 만들어준 코드다. 생성자와 strings.get(0)을 (String) 으로 형변환하는 코드가 추가되었다.

```java
class FailWithRawType {
    FailWithRawType() {
    }

    public static void main(String[] args) {
        List<String> strings = new ArrayList();
        unsafeAdd(strings, 42);
        String s = (String) strings.get(0);
    }

    private static void unsafeAdd(List list, Object o) {
        list.add(o);
    }
}
```

- 이 프로그램을 이대로 실행하면 strings.get(0)의 결과를 형변환하려 할 때 ClassCastException을 던진다.

### 잘못된 예 - 모르는 타입의 원소도 받는 로 타입을 사용했다.

```java
class UseUnknownElementRawTypeSample {
    static int numElementsInCommon(Set s1, Set s2) {
        int result = 0;
        for (Object o1 : s1) {
            if (s2.contains(o1)) {
                result++;
            }
        }
        return result;
    }
}
```

- 이 메서드는 동작하지만 로 타입을 사용해 안전하지 않다.
- 따라서 비한정적 와일드카드 타입(unbounded wildcard type)을 대신 사용하는 게 좋다.
- 제네릭 타입을 쓰고 싶지만 실제 타입 매개변수가 무엇인지 신경 쓰고 싶지 않다면 물음표(?)를 사용하자.
    - ex. 제네릭 타입인 Set<E>의 비한정적 와일드카드 타입은 Set<?>이다. 이는 어떤 타입이라도 담을 수 있는 가장 범용적인 매개변수화 Set 타입이다.

#### 비한정적 와일드카드 타입을 사용하라. - 타입 안전하며 유연하다.

```java
class UseUnknownElementRawTypeSample {
    static int numElementsInCommon(Set<?> s1, Set<?> s2) {
        int result = 0;
        for (Object o1 : s1) {
            if (s2.contains(o1)) {
                result++;
            }
        }
        return result;
    }
}
```

- 로 타입 컬렉션에는 아무 원소나 넣을 수 있으니 타입 불변식을 훼손하기 쉽다.
- 반면, **Collection<?>에는 (null 외에는) 어떤 원소도 넣을 수 없다.** 다른 원소를 넣으려 하면 컴파일할 때 오류 메시지를 보게 된다.
    - 즉, 컬렉션의 불변식을 훼손하지 못하게 막아준다.
    - (null 외의) 어떤 원소도 Collection<?>에 넣지 못하게 했으며 컬렉션에서 꺼낼 수 있는 객체의 타입도 전혀 알 수 없게 해준다.

## 예외

로 타입을 쓰지 말라는 규칙에도 소소한 예외가 있다.

### 1. class 리터럴에는 로 타입을 써야 한다.

- 자바 명세는 class 리터럴에 매개변수화 타입을 사용하지 못하게 했다(배열과 기본 타입은 허용한다).
    - ex. List.class, String[].class, int.class는 허용 / List<String>.class, List<?>.class는 허용하지 않음.

### 2. instanceof 연산자는 비한정적 와일드카드 타입 이외의 매개변수화 타입에는 적용할 수 없다.

- 이는 런타임에는 제네릭의 정보가 지워지기 때문이다.
- 로 타입이든 비한정적 와일드카드 타입이든 instanceof는 완전히 똑같이 동작한다.
- 비한정적 와일드카드 타입의 꺾쇠괄호와 물음표는 아무런 역할 없이 코드만 지저분하게 하므로, 차라리 로 타입을 사용하는 편이 깔끔하다.

#### 로 타입을 써도 좋은 예 - instanceof 연산자

```java
if (o instanceof Set) {
    Set<?> s = (Set<?>) o;
    // ...
}
```

- o 타입이 Set임을 확인한 다음 와일드카드 타입인 Set<?>로 형변환해야 한다(로 타입인 Set이 아니다).
- 이는 검사 형변환(checked cast)이므로 컴파일러 경고가 뜨지 않는다.

## 용어 정리

|한글 용어|영문 용어|예|
|-|-|-|
|매개변수화 타입|parameterized type|`List<String>`|
|실제 타입 매개변수|actual type parameter|`String`|
|제네릭 타입|generic type|`List<E>`|
|정규 타입 매개변수|formal type parameter|`E`|
|비한정적 와일드카드 타입|unbounded wildcard typ|`List<?>`|
|로 타입|raw type|`List`|
|한정적 타입 매개변수|bounded type parameter|`<E extends Number>`|
|재귀적 타입 한정|recursive type bound|`<T extends Comparable<T>>`|
|한정적 와일드카드 타입|bounded wildcard type|`List<? extends Number>`|
|제네릭 메서드|generic method|`static <E> List<E> asList(E[] a)`|
|타입 토큰|type token|`String.class`|

## 핵심 정리

- 로 타입을 사용하면 런타임에 예외가 발생할 수 있으니 사용하면 안 된다.
- 로 타입은 제네릭이 도입되기 이전 코드와의 호환성을 위해 제공될 뿐이다.
- `Set<Object>`는 어떤 타입의 객체도 저장할 수 있는 매개변수화 타입이고, `Set<?>`는 모종의 타입 객체만 저장할 수 있는 와일드카드 타입이다.
    - 이들의 로 타입인 Set은 제네릭 타입 시스템에 속하지 않는다.
    - `Set<Object>`와 `Set<?>`은 안전하지만, 로 타입인 Set은 안전하지 않다.

## 참고자료

- [자바 제네릭(Generics) 기초](https://woowacourse.github.io/tecoble/post/2020-11-09-generics-basic/)
- [제네릭](https://velog.io/@guswns3371/%EC%A0%9C%EB%84%A4%EB%A6%AD)
- [자바의 제네릭 타입 소거, 리스트에 관하여 (Java Generics Type Erasure, List)](https://jyami.tistory.com/99)
- [[Java] Generic Type erasure란 무엇일까?](https://devlog-wjdrbs96.tistory.com/263)
