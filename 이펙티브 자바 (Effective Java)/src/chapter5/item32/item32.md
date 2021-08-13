# 아이템 32. 제네릭과 가변인수를 함께 쓸 때는 신중하라

- 가변인수는 메서드에 넘기는 인수의 개수를 클라이언트가 조절할 수 있게 해주는데, 구현 방식에 허점이 있다.
- 가변인수 메서드를 호출하면 가변인수를 담기 위한 배열이 자동으로 하나 만들어진다. 그래서 내부로 감춰야 했을 이 배열을 클라이언트에 노출하는 문제가 생겼다.
- 그 결과, varargs 매개변수에 제네릭이나 매개변수화 타입이 포함되면 알기 어려운 컴파일 경고가 발생한다.

## 실체화 불가 타입

- 실체화 불가 타입은 런타임에는 컴파일타입보다 타입 관련 정보를 적게 담고 있다.
- 거의 모든 제네릭과 매개변수화 타입은 실체화되지 않는다.
- 메서드를 선언할 때 실체화 불가 타입으로 varargs 매개변수를 선언하면 컴파일러가 경고를 보낸다.
- 가변인수 메서드를 호출할 때도 varargs 매개변수가 실체화 불가 타입으로 추론되면, 그 호출에 대해서도 경고를 낸다.
- 매개변수화 타입의 변수가 타입이 다른 객체를 참조하면 힙 오염이 발생한다.

### 제네릭과 varargs를 혼용하면 타입 안전성이 깨진다.

```java
public class Item32 {
    static void dangerous(List<String>... stringLists) {
        List<Integer> intList = List.of(42);
        Object[] objects = stringLists;
        objects[0] = intList; // 힙 오염 발생
        String s = stringLists[0].get(0); // ClassCastException
    }
}
```

- 컴파일러는 자동으로 보이지 않는 형변환을 해준다. 따라서 이 메서드에서는 형변환하는 곳에 인수를 건네 호출하면 ClassCastException을 던진다.
- 이렇게 타입 안전성이 깨지니 **제네릭 varargs 배열 매개변수에 값을 저장하는 것은 안전하지 않다.**
- 제네릭 배열을 직접 작성하는 건 허용하지 않으면서 제네릭 varargs 매개변수를 받는 메서드를 선언할 수 있게 한 이유는 다음과 같다.
    - 제네릭이나 매개변수화 타입의 varargs 매개변수를 받는 메서드가 실무에서 매우 유용하기 때문에 언어 설계자가 이 모순을 수용하기로 한 것이다.
    - 자바 라이브러리도 이런 메서드를 여럿 제공하며, 이들은 타입 안전하다.
        - ex. `Arrays.asList(T... a)`, `Collections.addAll(Collection<? super T> c, T... elements)`
          , `EnumSet.of(E first, E... rest)`

## @SafeVarargs

- 자바 7 전에는 제네릭 가변인수 메서드의 작성자가 호출자 쪽에서 발생하는 경고에 대해서 해줄 수 있는 일이 없었다.
    - 사용자는 이 경고들을 그냥 두거나 호출하는 곳마다 @SuppressWarnings("unchecked") 애너테이션을 달아 경고를 숨겨야 했다.
- 자바 7에서는 @SafeVarargs 애너테이션이 추가되어 제네릭 가변인수 메서드 작성자가 클라이언트 측에서 발생하는 경고를 숨길 수 있게 되었다.
- **@SafeVarargs 애너테이션은 메서드 작성자가 그 메서드가 타입 안전함을 보장하는 장치다.** 컴파일러는 이 약속을 믿고 더 이상 해당 메서드가 안전하지 않을 수 있다는 경고를 하지 않는다.
- 메서드가 안전한 게 확실하지 않다면 절대 @SafeVarargs 애너테이션을 달아서는 안 된다.
    - 가변인수 메서드를 호출할 때 varargs 매개변수를 담는 제네릭 배열이 만들어진다.
    - 이때, 메서드가 이 배열에 아무것도 저장하지 않고(그 매개변수들을 덮어쓰지 않고) 그 배열의 참조가 밖으로 노출되지 않는다면(신뢰할 수 없는 코드가 배열에 접근할 수 없다면) 타입 안전하다.
    - 즉, varargs 매개변수 배열이 호출자로부터 그 메서드로 순수하게 인수들을 전달하는 일만 한다면 그 메서드는 안전하다.

### 자신의 제네릭 매개변수 배열의 참조를 노출한다. - 안전하지 않다.

```java
public class Item32 {
    static <T> T[] toArray(T... args) {
        return args;
    }
}
```

- 이 메서드가 반환하는 배열의 타입은 이 메서드에 인수를 넘기는 컴파일타임에 결정되는데, 그 시점에는 컴파일러에게 충분한 정보가 주어지지 않아 타입을 잘못 판단할 수 있다.
- 이는 자신의 varargs 매개변수 배열을 그대로 반환하면 힙 오염을 이 메서드를 호출한 쪽의 콜스택으로까지 전이하는 결과를 낳을 수 있다.

## 제네릭 varargs 매개변수 배열에 다른 메서드가 접근하도록 허용하면 안전하지 않다.

단, 예외가 두 가지 있다.

- 첫 번째, @SafeVarargs로 제대로 애노테이트된 또 다른 varargs 메서드에 넘기는 것은 안전하다.
- 두 번째, 이 배열 내용의 일부 함수를 호출만 하는(varargs를 받지 않는) 일반 메서드에 넘기는 것도 안전하다.

### 제네릭 varargs 매개변수를 안전하게 사용하는 메서드

```java
public class Item32 {
    @SafeVarargs
    static <T> List<T> flatten(List<? extends T>... lists) {
        List<T> result = new ArrayList<>();
        for (List<? extends T> list : lists) {
            result.addAll(list);
        }
        return result;
    }
}
```

- @SafeVarargs 애너테이션을 사용해야 할 때를 정하는 규칙은 간단하다.
- **제네릭이나 매개변수화 타입의 varargs 매개변수를 받는 모든 메서드에 @SafeVarargs를 달면 된다.**
- 즉, 안전하지 않은 varargs 메서드는 절대 작성해서는 안 된다는 뜻이다.

#### 다음 두 조건을 모두 만족하는 제네릭 varargs 메서드는 안전하다.

- varargs 매개변수 배열에 아무것도 저장하지 않는다.
- 그 배열(혹은 복제본)을 신뢰할 수 없는 코드에 노출하지 않는다.

> @SafeVarargs 애너테이션은 재정의할 수 없는 메서드에만 달아야 한다. 재정의한 메서드도 안전할지는 보장할 수 없기 때문이다.
> 자바 8에서 이 애너테이션은 오직 정적 메서드와 final 인스턴스 메서드에만 붙일 수 있고, 자바 9부터는 private 인스턴스 메서드에도 허용된다.

### 제네릭 varargs 매개변수를 List로 대체한 예 - 타입 안전하다.

```java
public class Item32 {
    static <T> List<T> flatten(List<List<? extends T>> lists) {
        List<T> result = new ArrayList<>();
        for (List<? extends T> list : lists) {
            result.addAll(list);
        }
        return result;
    }
}
```

- @SafeVarargs 애너테이션이 유일한 정답은 아니다.
- 실제는 배열인 varargs 매개변수를 위 메서드처럼 List 매개변수로 바꿀 수도 있다.

### List.of

```java
class Item32 {
    List<T> audience = flatten(List.of(friends, romans, countrymen));
}

```

- 정적 팩터리 메서드인 List.of를 활용하면 위와 같이 임의의 인수를 넘길 수 있다.
- List.of에는 @SafeVarargs 애너테이션이 달려 있기 때문에 이것이 가능하다.
- 이 방식의 장점은 컴파일러가 이 메서드의 타입 안전성을 검증할 수 있다는 것이다.
- 단점이라면 클라이언트 코드가 살짝 지저분해지고 속도가 조금 느려질 수 있다.
- 또한, 이 방식은 위에서 언급한 toArray처럼 varargs 메서드를 안전하게 작성하는 게 불가능한 상황에서도 쓸 수 있다.
    - toArray의 List 버전이 바로 List.of로, 자바 라이브러리 차원에서 제공하니 우리가 직접 작성할 필요도 없다.
- 이를 사용하면 결과 코드는 배열 없이 제네릭만 사용하게 되므로 타입 안전해진다.

## 핵심 정리

- 가변인수와 제네릭은 궁합이 좋지 않다. 가변인수 기능은 배열을 노출하여 추상화가 완벽하지 못하고, 배열과 제네릭의 타입 규칙이 서로 다르기 때문이다.
- 제네릭 varargs 매개변수는 타입 안전하지는 않지만, 허용된다.
- 메서드에 제네릭 (혹은 매개변수화된) varargs 매개변수를 사용하고자 한다면, 그 메서드가 타입 안전한지 확인한 다음 @SafeVarargs 애너테이션을 달아 사용하는 데 불편함이 없도록 한다.