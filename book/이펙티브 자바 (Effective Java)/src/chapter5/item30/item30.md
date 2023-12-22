# 아이템 30. 이왕이면 제네릭 메서드로 만들라

- 클래스와 마찬가지로, 메서드도 제네릭으로 만들 수 있다. 매개변수화 타입을 받는 정적 유틸리티 메서드는 보통 제네릭이다.
- ex. Collections의 '알고리즘' 메서드(binarySearch, sort 등 ...)

## 로 타입 사용 - 수용 불가!

```java
public class Item30 {
    public static Set union(Set s1, Set s2) {
        Set result = new HashSet<>();
        result.addAll(s2);
        return result;
    }
}
```

- 컴파일은 되지만 경고가 발생할 것이다.
- 경고를 없애려면 이 메서드를 타입 안전하게 만들어야 한다.
- 메서드 선언에서의 세 집합(입력 2개, 반환 1개)의 원소 타입을 타입 매개변수로 명시하고, 메서드 안에서도 이 타입 매개변수만 사용하게 수정하면 된다.
- **타입 매개변수들을 선언하는 타입 매개변수 목록은 메서드의 제한자와 반환 타입 사이에 온다.**
- 타입 매개변수의 명명 규칙은 제네릭 메서드나 제네릭 타입이나 똑같다.

## 제네릭 메서드

```java
public class Item30 {
    public static <E> Set<E> union(Set<E> s1, Set<E> s2) {
        Set<E> result = new HashSet<>(s1);
        result.addAll(s2);
        return result;
    }
}
```

- union 메서드는 집합 3개의 타입이 모두 같아야 한다. 한정적 와일드카드 타입을 사용하면 이를 더 유연하게 개선할 수 있다.
- 제네릭은 런타임에 타입 정보가 소거되므로 하나의 객체를 어떤 타입으로든 매개변수화할 수 있다.
    - 하지만 이렇게 하려면 요청한 타입 매개변수에 맞게 매번 그 객체의 타입을 바꿔주는 정적 팩터리를 만들어야 한다(이를 **제네릭 싱글턴 패턴**이라고 한다.).
    - 제네릭 싱글턴 팩터리는 Collections.reverseOrder 같은 함수 객체나 Collections.emptySet 같은 컬렉션용으로 사용한다.
- 항등함수(identity function)를 담은 클래스를 만들고 싶다면 자바 라이브러리의 Function.identity를 사용하면 된다.
    - 항등함수 객체는 상태가 없다. 따라서 요청할 때마다 새로 생성하는 것은 낭비다.
    - 제네릭은 소거 방식을 사용하기 때문에 제네릭 싱글턴 하나면 항등함수를 만들기에 충분하다.

## 제네릭 싱글턴 팩터리 패턴

```java
public class Item30 {
    private static UnaryOperator<Object> IDENTITY_FN = (t) -> t;

    @SuppressWarnings("unchecked")
    public static <T> UnaryOperator<T> identityFunction() {
        return (UnaryOperator<T>) IDENTITY_FN;
    }
}
```

- IDENTITY_FN을 `UnaryOperator<T>`로 형변환하면 비검사 형변환 경고가 발생한다. T가 어떤 타입이든 `UnaryOperator<Object>`는 `UnaryOperator<T>`가 아니기
  때문이다.
- 하지만 항등함수란 입력 값을 수정 없이 그대로 반환하는 특별한 함수이므로, T가 어떤 타입이든 `UnaryOperator<T>`를 사용해도 타입 안전하다.

### 제네릭 싱글턴을 사용하는 예

```java
public class Item30 {

    // ...

    public static void main(String[] args) {
        String[] strings = {"삼베", "대마", "나일론"};
        UnaryOperator<String> sameString = identityFunction();
        for (String s : strings) {
            System.out.println(sameString.apply(s));
        }

        Number[] numbers = {1, 2.0, 3L};
        UnaryOperator<Number> sameNumber = identityFunction();
        for (Number n : numbers) {
            System.out.println(sameNumber.apply(n));
        }
    }
}
```

- 위 코드는 형변환을 하지 않아도 컴파일 오류나 경고가 발생하지 않는다.

## 재귀적 타입 한정

- 자기 자신이 들어간 표현식을 사용하여 타입 매개변수의 허용 범위를 한정할 수 있다. 이는 재귀적 타입 한정(recursive type bound)이라고 하는 개념이다.
- 재귀적 타입 한정은 주로 타입의 자연적 순서를 정하는 Comparable 인터페이스와 함께 쓰인다.

```java
public interface Comparable<T> {
    int compareTo(T o);
}
```

- 여기서는 타입 매개변수 T는 Comparable<T>를 구현한 타입이 비교할 수 있는 원소의 타입을 정의한다.
- 실제로 거의 모든 타입은 자신과 같은 타입의 원소와만 비교할 수 있다.
    - ex) String은 `Comparable<String>`을 구현하고 Integer는 `Comparable<Integer>`를 구현하는 형식이다.

### 재귀적 타입 한정을 이용해 상호 비교할 수 있다.

```java
public class RecursiveTypeBoundEx {
    public static <E extends Comparable<E>> E max(Collection<E> c);
}
```

- 타입 한정인 `<E extends Comparable<E>>`는 "모든 타입 E는 자신과 비교할 수 있다"라고 읽을 수 있다. 즉, 상호 비교 가능하다는 뜻이다.

### 컬렉션에서 최댓값 반환한다. - 재귀적 타입 한정 사용

```java
public class RecursiveTypeBoundEx {
    public static <E extends Comparable<E>> E max(Collection<E> collection) {
        if (collection.isEmpty()) {
            throw new IllegalArgumentException("컬렉션이 비어 있습니다.");
        }

        E result = null;
        for (E e : collection) {
            if (result == null || e.compareTo(result) > 0) {
                result = Objects.requireNonNull(e);
            }
        }
        return result;
    }
}
```

- 위 메서드에서는 빈 컬렉션인 경우 IllegalArgumentException을 던진다. 따라서 `Optional<E>`를 반환하도록 하는 것이 더 낫다.

## 핵심 정리

- 제네릭 타입과 마찬가지로, 클라이언트에서 입력 매개변수와 반환값을 명시적으로 형변환해야 하는 메서드보다 제네릭 메서드가 더 안전하고 사용하기도 쉽다.
- 타입과 마찬가지로, 메서드도 형변환 없이 사용할 수 있는 편이 좋으며, 많은 경우 그렇게 하려면 제네릭 메서드가 되어야 한다.
- 타입과 마찬가지로, 형변환을 해줘야 하는 기존 메서드는 제네릭하게 만들도록 한다.
- 제네릭하게 만들면 기존 클라이언트는 그대로 둔 채 새로운 사용자에게 편의를 제공해줄 수 있다.

## 참고자료

- [[Java] Java의 Generics](https://medium.com/@joongwon/java-java%EC%9D%98-generics-604b562530b3)
- [[Effective Java] item30. 이왕이면 제네릭 메서드로 만들라](https://jyami.tistory.com/94)
- [java singleton pattern (싱글톤 패턴)](https://blog.seotory.com/post/2016/03/java-singleton-pattern)
- [싱글턴 패턴(Singleton Pattern)](https://webdevtechblog.com/%EC%8B%B1%EA%B8%80%ED%84%B4-%ED%8C%A8%ED%84%B4-singleton-pattern-db75ed29c36)