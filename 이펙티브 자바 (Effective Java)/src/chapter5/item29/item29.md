# 아이템 29. 이왕이면 제네릭 타입으로 만들라

## Object 기반 스택 - 제네릭이 절실한 강력 후보!

```java
public class StackBasedObject {
    private Object[] elements;
    private int size = 0;
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    public StackBasedObject() {
        elements = new Object[DEFAULT_INITIAL_CAPACITY];
    }

    public void push(Object e) {
        ensureCapacity();
        elements[size++] = e;
    }

    public Object pop() {
        if (size == 0) {
            throw new EmptyStackException();
        }
        Object result = elements[--size];
        elements[size] = null; // 다 쓴 참조 해제
        return result;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    private void ensureCapacity() {
        if (elements.length == size) {
            elements = Arrays.copyOf(elements, 2 * size + 1);
        }
    }
}
```

위 클래스는 제네릭 타입이어야 마땅하다.

### 제네릭 스택으로 가는 첫 단계 - 컴파일되지 않는다.

```java
public class StackGeneric<E> {
    private E[] elements;
    private int size = 0;
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    public StackGeneric() {
        elements = new Object[DEFAULT_INITIAL_CAPACITY]; // 컴파일 에러 발생
    }

    public void push(E e) {
        ensureCapacity();
        elements[size++] = e;
    }

    public E pop() {
        if (size == 0) {
            throw new EmptyStackException();
        }
        E result = elements[--size];
        elements[size] = null; // 다 쓴 참조 해제
        return result;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    private void ensureCapacity() {
        if (elements.length == size) {
            elements = Arrays.copyOf(elements, 2 * size + 1);
        }
    }
}
```

- 일반 클래스를 제네릭 클래스로 바꿀 때, 첫 단계는 클래스 선언에 타입 매개변수를 추가하는 것이다.
- 여기서는 스택이 담을 원소 하나만 추가해주면 된다. 이때 타입 이름으로는 보통 E를 사용한다.

### 실체화 불가 타입으로는 배열을 만들 수 없다.

위 예제에서, E와 같은 실체화 불가 타입으로는 배열을 만들 수 없다. 이에 대한 해결책은 두 가지다.

- 첫 번째. 제네릭 배열 생성을 금지하는 제약을 우회하는 방법으로, Object 배열을 생성한 다음 제네릭 배열로 형변환한다. 이렇게 하면 일반적으로 타입 안전하지 않지만 컴파일러는 오류 대신 경고를 내보낸다.
- 두 번째. elements 필드의 타입을 E[]에서 Object[]로 바꾼다.

### 배열을 사용한 코드를 제네릭으로 만드는 방법 1

```java
public class StackGeneric<E> {
    private E[] elements;
    private int size = 0;
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    // 배열 elements는 push(E)로 넘어온 E 인스턴스만 담는다.
    // 따라서 타입 안전성을 보장하지만, 이 배열의 런타임 타입은 E[]가 아닌 Object[]다.

    @SuppressWarnings("unchecked")
    public StackGeneric() {
        elements = (E[]) new Object[DEFAULT_INITIAL_CAPACITY];
    }

    // ...
}
```

- 컴파일러는 이 프로그램이 타입 안전한지 증명할 방법이 없다. 따라서 직접 타입 안전성을 해치지 않음을 확인하고 명시해야 한다.
- 비검사 형변환이 안전함을 증명했다면 범위를 최소로 좁혀 @SuppressWarnings 애너테이션으로 해당 경고를 숨기도록 한다.

### 배열을 사용한 코드를 제네릭으로 만드는 방법 2

```java
public class StackGeneric<E> {
    private Object[] elements; // Obejct[] 타입이다!
    private int size = 0;
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    public StackGeneric() {
        elements = new Object[DEFAULT_INITIAL_CAPACITY];
    }

    public E pop() {
        if (size == 0) {
            throw new EmptyStackException();
        }

        @SuppressWarnings("unchecked") // push에서 E 타입만 허용하므로 이 형변환은 안전하다.
        E result = (E) elements[--size];

        elements[size] = null; // 다 쓴 참조 해제
        return result;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    private void ensureCapacity() {
        if (elements.length == size) {
            elements = Arrays.copyOf(elements, 2 * size + 1);
        }
    }
}
```

- E는 실체화 불가 타입이므로 컴파일러는 런타임에 이뤄지는 형변환이 안전한지 증명할 방법이 없다. 따라서 직접 증명하고 경고를 숨길 수 있다.

### 두 방식의 차이점

- 첫 번째 방법은 가독성이 더 좋다. 배열의 타입을 E[]로 선언하여 오직 E 타입 인스턴스만 받음을 확실히 어필한다. 그리고 코드도 더 짧다.
- 첫 번째 방식에서는 형변환을 배열 생성 시 단 한 번만 해주면 된다.
    - 하지만 E가 Object가 아닌 한 배열의 런타임 타입이 컴파일타임 타입과 달라 힙 오염(heap pollution)을 일으키니 주의해야 한다.
- 두 번째 방식에서는 배열에서 원소를 읽을 때마다 해줘야 한다.

## 대부분의 제네릭 타입은 타입 매개변수에 제약을 두지 않는다.

- `Stack<Object>`, `Stack<int[]>`, `Stack<List<String>>` 등등.. 어떤 참조 타입으로도 Stack을 만들 수 있다.
- 단, 기본 타입은 사용할 수 없다. 하지만 이는 박싱된 기본 타입을 사용해 우회할 수 있다.
- 타입 매개변수에 제약을 두는 제네릭 타입도 있다.
    - ex. java.util.concurrent.DelayQueue

### java.util.concurrent.DelayQueue

```java
class DelayQueue<E extends Delayed> implements BlockingQueue<E> {
    // ...
}
```

- DelayQueue 자신과 DelayQueue를 사용하는 클라이언트는 DelayQueue의 원소에서 형변환 없이 곧바로 Delayed 클래스의 메서드를 호출할 수 있다.
- 타입 매개변수 목록인 `<E extend Delayed>`는 `java.util.concurrent.Delayed`의 하위 타입만 받는다는 뜻이다.
- 이러한 타입 매개변수 E를 한정적 타입 매개변수(bounded type parameter)라 한다.
- 추가로, 모든 타입은 자기 자신의 하위 타입이므로 `DelayQueue<Delayed>`로도 사용할 수 있다.

## 핵심 정리

- 클라이언트에서 직접 형변환해야 하는 타입보다 제네릭 타입이 더 안전하고 쓰기 편하다.
- 새로운 타입을 설계할 때는 형변환 없이도 사용할 수 있도록 한다. 이를 위해서는 제네릭 타입으로 만들어야 할 경우가 많다.
- 기존 타입 중 제네릭이었어야 하는 것이 있다면 제네릭 타입으로 변경하도록 한다.
- 제네릭 타입을 사용하면 기존 클라이언트에는 아무 영향을 주지 않으면서, 새로운 사용자에게 타입 안전성이라는 편의를 제공해줄 수 있다.

## 참고자료

- [[Java] 힙 펄루션 (Heap pollution)](https://velog.io/@adduci/Java-%ED%9E%99-%ED%8E%84%EB%A3%A8%EC%85%98-Heap-pollution)