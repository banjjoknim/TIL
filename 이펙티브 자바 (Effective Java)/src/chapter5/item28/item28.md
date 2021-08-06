# 아이템 28. 배열보다는 리스트를 사용하라

배열과 제네릭에는 중요한 차이가 두 가지 있다. 공변(variant)과 실체화(reify)이다.

## 공변과 불공변

- 배열은 공변(covariant)이다.
    - Sub가 Super의 하위 타입이라면 배열 Sub[]는 배열 Super[]의 하위 타입이 된다.
    - 공변, 즉 함께 변한다는 뜻이다.
- 반면, 제네릭은 불공변(invariant)이다.
    - 즉, 서로 다른 타입 Type1과 Type2가 있을 때, `List<Type1>`은 `List<Type2>`의 하위 타입도 아니고 상위 타입도 아니다.

### 런타임에 실패한다.

```java
class Temp {
    public static void main(String[] args) {
        Object[] objectArray = new Long[1];
        objectArray[0] = "타입이 달라 넣을 수 없다." // ArrayStoreException을 던진다.
    }
}
```

다음 코드는 문법에 맞지 않는다.

```java
class Temp {
    public static void main(String[] args) {
        List<Object> ol = new ArrayList<Long>();
        ol.add("타입이 달라 넣을 수 없다.");
    }
}
```

- 어느 쪽이든 Long용 저장소에 String을 넣을 수는 없다.
- 다만 배열에서는 그 실수를 런타임에야 알게 되지만, 리스트를 사용하면 컴파일할 때 바로 알 수 있다.

### 실체화(reify)

- 배열은 실체화된다.
    - 배열은 런타임에도 자신이 담기로 한 원소의 타입을 인지하고 확인한다.
    - 그래서 Long 배열에 String을 넣으려 하면 ArrayStoreException이 발생하는 것이다.
- 반면, 제네릭은 타입 정보가 런타임에는 소거(erasure)된다.
    - 이는 원소 타입을 컴파일에만 검사하며 런타임에는 알 수조차 없다는 뜻이다.

이와 같은 차이로 인해 배열과 제네릭은 잘 어우러지지 못한다.

- 배열은 제네릭 타입, 매개변수화 타입, 타입 매개변수로 사용할 수 없다.
    - `new List<E>[]`, `new List<String[]>`, `new E[]`와 같이 사용하려고 하면 컴파일할 때 제네릭 배열 생성 오류를 일으킨다.

## 제네릭 배열을 만들지 못하는 이유

- 제네릭 배열을 만들 수 있게 되면 타입 안전하지 않다.
- 만약 이를 허용한다면 컴파일러가 자동 생성한 형변환 코드에서 런타임에 ClassCastException이 발생할 수 있다.

### 제네릭 배열 생성을 허용하지 않는 이유 - 컴파일되지 않는다.

```java
import com.sun.tools.javac.util.List;

class Temp {
    public static void main(String[] args) {
        List<String>[] stringLists = new List<String>[1]; // (1)
        List<Integer> intList = List.of(42); // (2)
        Object[] objects = stringLists; // (3)
        objects[0] = intList; // (4)
        String s = stringLists.get(0); // (5) 컴파일 에러가 난다!
    }
}
```

컴파일 에러가 나는 과정은 다음과 같다.

- (1)이 허용된다고 가정한다.
- (2)는 원소가 하나인 `List<Integer>`를 생성한다.
- (3)은 (1)에서 생성한 `List<String>`의 배열을 Object 배열에 할당한다. 이때, 배열은 공변이므로 아무런 문제가 없다.
- (4)는 (2)에서 생성한 `List<Integer>`의 인스턴스를 Object 배열의 첫 원소로 저장한다. 이때, 제네릭은 소거 방식으로 구현되어서 이 역시 성공한다.
    - 즉, 런타임에는 `List<Integer>` 인스턴스의 타입은 단순히 `List`가 되고, `List<Integer>[]` 인스턴스의 타입은 `List[]`가 된다.
    - 따라서 (4)에서도 ArrayStoreException을 일으키지 않는다.
- `List<String>` 인스턴스만 담겠다고 선언한 stringLists 배열에는 현재 `List<Integer>` 인스턴스가 저장되어 있다.
    - 컴파일러는 꺼낸 원소를 자동으로 String으로 형변환하는데, 이때 원소의 타입이 Integer와 String으로 다르다. 따라서 런타임에 ClassCastException이 발생한다.
- 이런 일을 방지하려면 제네릭 배열이 생성되지 않도록 해야 한다.

## 실체화 불가 타입

- `E`, `List<E>`, `List<String>` 같은 타입을 실체화 불가 타입(non-reifiable type)이라 한다.
    - 이들은 실체화되지 않아서 런타임에는 컴파일타임보다 타입 정보를 적게 가지는 타입이다.
    - 소거 메커니즘 때문에 매개변수화 타입 가운데 실체화될 수 있는 타입은 `List<?>`와 `Map<?,?>` 같은 비한정적 와일드카드 타입뿐이다.
        - 배열을 비한정적 와일드카드 타입으로 만들 수는 있지만, 유용하게 쓰일 일은 거의 없다.

- 제네릭 컬렉션에서는 자신의 원소 타입을 담은 배열을 반환하는 게 보통은 불가능하다.
- 제네릭 타입과 가변인수 메서드(varargs method)를 함께 쓰면 해석하기 어려운 경고 메시지를 받게 된다.
    - 가변인수 메서드를 호출할 때마다 가변인수 매개변수를 담을 배열이 하나 만들어지는데, 이때 그 배열의 원소가 실체화 불가 타입이라면 경고가 발생하는 것이다.
- 배열로 형변환할 때제네릭 배열 생성 오류나 비검사 형변환 경고가 뜨는 경우 대부분은 배열인 `E[]` 대신 컬렉션인 `List<E>`를 사용하면 해결된다.
- 코드가 조금 복잡해지고 성능이 살짝 나빠질 수도 있지만, 대신 타입 안전성과 상호운용성은 좋아진다.

## 예제 - Chooser 클래스

### Chooser - 제네릭을 시급히 적용해야 한다.

```java
public class Chooser {
    private final Object[] choiceArray;

    public Chooser(Collection choices) {
        this.choiceArray = choices.toArray();
    }

    public Object choose() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return choiceArray[random.nextInt(choiceArray.length)];
    }
}
```

- 위 클래스를 사용하려면 choose 메서드에를 호출할 때마다 반환된 Object를 원하는 타입으로 형변환해야 한다.
- 만약 타입이 다른 원소가 들어 있다면 런타임에 형변환 오류가 날 것이다.

### Chooser를 제네릭으로 만들기 위한 첫 시도 - 컴파일되지 않는다.

```java
public class Chooser {
    private final T[] choiceArray;

    public Chooser(Collection<T> choices) {
        this.choiceArray = choices.toArray();
    }

    // ...
}
```

- 위 클래스를 컴파일하면 오류 메시지가 출력될 것이다.
- 이를 해결하려면 Obejct[] 배열을 T 배열로 형변환하면 된다.

```java
public class Chooser {
    private final T[] choiceArray;

    public Chooser(Collection<T> choices) {
        this.choiceArray = (T[]) choices.toArray();
    }

    // ...
}
```

- 하지만 이렇게 해도 경고는 여전히 출력된다.
- T가 무슨 타입인지 알 수 없으니 컴파일러는 이 형변환이 런타임에도 안전한지 보장할 수 없다.
- 프로그램 자체는 동작하지만, 단지 컴파일러가 안전을 보장하지 못할 뿐이다.
- 만약 코드를 작성하는 사람이 안전하다고 확신한다면 주석을 남기고 애너테이션을 달아 경고를 숨겨도 된다. 그래도 역시 가능한 한 경고의 원인을 제거하는 것이 좋다.

### 리스트 기반 Chooser - 타입 안전성 확보!

```java
public class Chooser<T> {
    private final List<T> choiceList;

    public Chooser(Collection<T> choices) {
        choiceList = new ArrayList<>(choices);
    }

    public T choose() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return choiceList.get(random.nextInt(choiceList.size()));
    }
}
```

- 이로써 런타임에 ClassCastException을 만날 일이 사라졌다.

## 핵심 정리

- 배열은 공변이고 실체화되는 반면, 제네릭은 불공변이고 타입 정보가 소거된다.
- 그 결과 배열은 런타임에는 타입 안전하지만 컴파일타임에는 그렇지 않다. 그리고 제네릭은 배열과 반대다.
- 따라서, 배열과 제네릭을 섞어쓰는 것은 쉽지 않다. 만약 둘을 섞어 쓰다가 컴파일 오류나 경고를 만나면, 가장 먼저 배열을 리스트로 대체하는 방법을 적용해보도록 한다.