# 아이템 31. 한정적 와일드카드를 사용해 API 유연성을 높이라

## 매개변수화 타입은 불공변(invariant)이다.

- 서로 다른 타입 Type1과 Type2가 있을 때 `List<Type1>`은 `List<Type2>`의 하위 타입도 상위 타입도 아니다.
- 즉, `List<String>`은 `List<Object>`의 하위 타입이 아니다.

### 와일드카드 타입을 사용하지 않은 pushAll 메서드 - 결함이 있다!

```java
public class Stack {
    public void pushAll(Iterable<E> src) {
        for (E e : src) {
            push(e);
        }
    }
}
```

- 이 메서드는 Iterable src의 원소 타입이 스택의 원소 타입과 일치하면 잘 작동한다.
- 하지만 `Stack<Number>`로 선언한 후 pushAll(intVal)을 호출하면(여기서 intVal은 Integer 타입이다) 논리적으로는 잘 동작해야 할 것 같지만 그렇지 않다.
- 이는 매개변수화 타입이 불공변이기 때문이다.

### E 생산자(producer) 매개변수에 와일드카드 타입 적용

```java
public class Stack {
    public void pushAll(Iterable<? extends E> src) {
        for (E e : src) {
            push(e);
        }
    }
}
```

- 자바는 한정적 와일드카드 타입이라는 특별한 매개변수화 타입을 지원한다.
- pushAll의 입력 매개변수 타입은 'E의 Iterable'이 아니라 'E의 하위 타입의 Iterable'이어야 한다는 뜻을 가진다.
- 와일드 카드 타입 `Iterable<? extends E>`가 정확히 이런 뜻을 의미한다. 여기서 하위 타입은 자기 자신도 포함한다.
- 여기서 생산자(producer)라는 단어는 입력 매개변수로부터 이 컬렉션으로 원소를 옮겨 담는다는 뜻이다.

### 와일드카드 타입을 사용하지 않은 popAll 메서드 - 결함이 있다!

```java
public class Stack {
    public void popAll(Collection<E> dst) {
        while (!isEmpty()) {
            dst.add(pop());
        }
    }
}
```

- 주어진 컬렉션의 원소 타입이 스택의 원소 타입과 일치한다면 말끔히 컴파일되고 문제없이 동작한다. 하지만 완벽하진 않다. 타입이 다를 경우, `Collection<Object>`
  는 `Collection<Number>`의 하위 타입이 아니다. 라는 오류가 발생한다(앞의 예시와 비슷하다).
- 이를 해결하기 위해서는 popAll의 입력 매개변수의 타입이 'E의 Collection'이 아니라 'E의 상위 타입의 Collection'이어야 한다(모든 타입은 자기 자신의 상위 타입이다).

### E 소비자(consumer) 매개변수에 와일드카드 타입 적용

```java
public class Stack {
    public void popAll(Collection<? super E> dst) {
        while (!isEmpty()) {
            dst.add(pop());
        }
    }
}
```

- 이제 Stack과 클라이언트 코드 모두 깔끔하게 컴파일된다.
- **유연성을 극대화하려면 원소의 생산자나 소비자용 입력 매개변수에 와일드카드 타입을 사용하라**
- 한편, 입력 매개변수가 생산자와 소비자 역할을 동시에 한다면 와일드카드 타입을 써도 좋을 게 없다. 타입을 정확히 지정해야 하므로, 이럴 때는 와일드카드 타입을 쓰지 말아야 한다.

## 펙스(PECS) : producer-extends, consumer-super

- 매개변수화 타입 T가 생산자라면 `<? extends T>`를 사용하고, 소비자라면 `<? super T>`를 사용하도록 한다.
- 이는 와일드카드 타입을 사용하는 기본 원칙이다. 이는 `겟풋 원칙(Get and Put Principle)`으로 불리기도 한다.

### T 생산자 매개변수에 와일드카드 타입 적용

```java
public class Item31 {
    public Chooser(Collection<? extends T> choices);
}
```

- 한정적 와일드카드 타입을 사용하도록 수정하기 전과는 달리, `Chooser<Number>`의 생성자에 `List<Integer>`를 넘길 수 있다.
- 즉, 한정적 와일드카드 타입으로 선언한 수정 후 생성자에서는 문제가 사라진다.

```java
public class Item31 {
    public static <E> Set<E> union(Set<E> s1, Set<E> s2);
}
```

- s1과 s2 모두 생산자이니 PECS 공식에 따라 아래와 같이 선언해야 한다.

```java
public class Item31 {
    public static <E> Set<E> union(Set<? extends E> s1, Set<? extends E> s2);
}
```

- 반환 타입은 여전히 `Set<E>`인 것에 주의해야 한다.
- **반환 타입에는 한정적 와일드카드 타입을 사용하면 안 된다.** 유연성을 높이기는 커녕 클라이언트 코드에서도 와일드카드 타입을 써야 하기 때문이다.
- **클래스 사용자가 와일드카드 타입을 신경 써야 한다면 그 API에 무슨 문제가 있을 가능성이 크다.**

### 자바 7까지는 명시적 타입 인수를 사용해야 한다.

```java
public class Item31 {
    void add(int value) {
        // ...
    }

    add(10);

    class Set<T> {
        // ...
    }

    Set<Integer> = // ...;
}
```

- 컴파일러가 올바른 타입을 추론하지 못할 때면 언제든 명시적 타입 인수(explicit type argument)를 사용해서 타입을 알려주면 된다.
- 목표 타이핑(target typing)은 자바 8부터 지원하기 시작했다.
- 위 코드에서 value는 매개변수이고 10은 인수다.
    - 매개변수(parameter)와 인수(argument)는 다르다. 매개변수는 메서드 선언에 정의한 변수이고, 인수는 메서드 호출 시 넘기는 '실젯값'이다.
- T는 타입 매개변수가 되고, Integer는 타입 인수가 된다.

## Comparable은 언제나 소비자이다.

```java
public class Item31 {
    public static <E extends Comparable<E>> E max(List<E> list);

    public static <E extends Comparable<? super E>> E max(List<? extends E> list); // 와일드카드를 이용해서 다듬은 메서드
}
```

- `Comparable<E>`는 E 인스턴스를 소비한 뒤, 선후 관계를 뜻하는 정수를 생산한다.
- Comparable은 언제나 소비자이므로, 일반적으로 **`Comparable<E>`보다는 `Comparable<? super E>`를 사용하는 편이 낫다.**
- Comparator도 마찬가지다. 일반적으로 **Comparator<E>보다는 Comparator<? super E>를 사용하는 편이 낫다.**

**Comparable(혹은 Comparator)을 직접 구현하지 않고, 직접 구현한 다른 타입을 확장한 타입을 지원하기 위해서는 와일드카드가 필요하다.**

- 수정전 max() 메서드는 `List<ScheduledFuture<?>> scheduledFutures = ...;`를 처리할 수 없다.
- ScheduledFuture는 Delayed의 하위 인터페이스이고, Delayed는 Comparable<Delayed>를 확장했다.
- 즉, ScheduledFuture의 인스턴스는 다른 ShceduledFuture 인스턴스뿐 아니라 Delayed 인스턴스와도 비교할 수 있기 때문에 수정 전
  max가 `List<ScheduledFuture<?>>`를 거부한다. 아래의 상속 관계와 함께 생각해보자.

```java
public interface Comparable<E>;

public interface Delayed extends Comparable<Delayed>;

public interface ScheduledFuture<V> extends Delayedm Future<V>;
```

## 메서드를 정의할 때는 타입 매개변수와 와일드카드 중 어떤 것을 사용해도 괜찮을 때가 많다.

### swap 메서드의 두 가지 선언

```java
public class Item31 {
    public static <E> void swap(List<E> list, int i, int j);

    public static void swap(List<?> list, int i, int j);
}
```

- 타입 매개변수와 와일드카드는 서로 공통되는 부분이 있기 때문에 두 방식 모두 괜찮다.
- public API라면 간단한 두 번째가 낫다. 신경 써야 할 타입 매개변수도 없다. 따라서, 기본 규칙은 이렇게 말할 수 있다.
- **메서드 선언에 타입 매개변수가 한 번만 나오면 와일드 카드로 대체하라.**
- 비한정적 타입 매개변수라면 비한정적 와일드카드로 바꾸고, 한정적 타입 매개변수라면 한정적 와일드카드로 바꾸면 된다.
- 단, `List<?>`라는 타입의 리스트에는 null 외에는 어떤 값도 넣을 수 없다. 이 경우, 도우미 메서드를 따로 작성하여 활용하는 방법으로 사용할 수 있다. 이때, 실제 타입을 알아내려면 이 도우미
  메서드는 제네릭이어야 한다.

```java
public class Item31 {
    public static <E> void swap(List<E> list, int i, int j) {
        swapHelper(list, i, j);
    }

    private static <E> void swapHelper(List<E> list, int i, int j) {
        list.set(i, list.set(j, list.get(i)));
    }
}
```

- swapHelper 메서드는 리스트가 `List<E>`임을 알고 있다.
- 즉, 이 리스트에서 꺼낸 값의 타입은 항상 E이고, E 타입의 값이라면 이 리스트에 넣어도 안전함을 알고 있다.

## 핵심 정리

- 조금 복잡하더라도 와일드카드 타입을 적용하면 API가 훨씬 유연해진다. 따라서, 널리 쓰일 라이브러리를 작성한다면 반드시 와일드카드 타입을 적절히 사용해줘야 한다.
- PECS 공식을 기억하자. 생산자(producer)는 extends를, 소비자(consumer)는 super를 사용한다.
- Comparable과 Comparator는 모두 소비자이다.