# 아이템 27. 비검사 경고를 제거하라

## 컴파일러 경고

- 컴파일러 경고는 다양하다.
- 비검사 형변환 경고, 비검사 메서드 호출 경고, 비검사 매개변수화 가변인수 타입 경고, 비검사 변환 경고 등이 있다.

### 할 수 있는 한 모든 비검사 경고를 제거하라.

- 비검사 경고를 모두 제거한다면 그 코드는 타입 안전성이 보장된다.
- 즉, 런타임에 ClassCastException이 발생할 일이 없고, 의도한대로 잘 동작하리라 확신할 수 있다.
- 경고를 제거할 수는 없지만 타입 안전하다고 확신할 수 있다면 @SuppressWarnings("unchecked") 애너테이션을 달아서 경고를 숨기도록 한다.
    - 단, 타입 안전함을 검증하지 않은 채 경고를 숨기면 이후에 더 큰 문제가 발생할 수 있으니 확실하게 검증해야 한다.

### @SuppressWarnings

- @SuppressWarnings 애너테이션은 개별 지역변수 선언부터 클래스 전체까지 어떤 선언에도 달 수 있다.
- 하지만 **@SuppressWarnings 애너테이션은 항상 가능한 한 좁은 범위에 적용하도록 해야한다.**
    - 보통은 변수선언, 아주 짧은 메서드, 혹은 생성자가 될 것이다.
    - 심각한 경고를 놓치는 일이 없도록 해야하며, 따라서 절대로 클래스 전체에 적용해서는 안 된다.

#### ArrayList의 toArray 메서드

```java
public class ArrayList<E> extends AbstractList<E> implements List<E>, RandomAccess, Cloneable, java.io.Serializable {
    // ...

    public <T> T[] toArray(T[] a) {
        if (a.length < size)
            // Make a new array of a's runtime type, but my contents:
            return (T[]) Arrays.copyOf(elementData, size, a.getClass());
        System.arraycopy(elementData, 0, a, 0, size);
        if (a.length > size)
            a[size] = null;
        return a;
    }

    // ...
}
```

- ArrayList를 컴파일하면 이 메서드에서 경고가 발생한다.
- 애너테이션은 선언에만 달 수 있기 때문에 return 문에는 @SuppressWarnings를 다는 게 불가능하다.
- 메서드 전체에 애너테이션을 달 수도 있지만, 범위가 필요 이상으로 넘어갈 수 있으니 최대한 자제해야 한다(하지만 현재 이 글을 작성하는 시점에는 toArray() 메서드에 애너테이션이 붙어있다).

#### 지역변수를 추가해 @SuppressWarnings의 범위를 좁힌다.

```java
public class ArrayList<E> extends AbstractList<E> implements List<E>, RandomAccess, Cloneable, java.io.Serializable {
    // ...

    public <T> T[] toArray(T[] a) {
        if (a.length < size) {
            // 생성한 배열과 매개변수로 받은 배열의 타입이 모두 T[]로 같으므로 올바른 형변환이다.
            @SuppressWarnings("unchecked") T[] result = (T[]) Arrays.copyOf(elementData, size, a.getClass());
            return result;
        }
        System.arraycopy(elementData, 0, a, 0, size);
        if (a.length > size)
            a[size] = null;
        return a;
    }

    // ...
}
```

- 이 코드는 깔끔하게 컴파일되고 비검사 경고를 숨기는 범위도 최소로 좁혔다.
- **@SuppressWarnings("unchecked") 애너테이션을 사용할 때면 그 경고를 무시해도 안전한 이유를 항상 주석으로 남겨야 한다.**
    - 이는 다른 사람이 그 코드를 이해하는 데 도움이 되며, 더 중요하게는, 다른 사람이 그 코드를 잘못 수정하여 타입 안전성을 잃는 상황을 줄여준다.

## 핵심 정리

- 비검사 경고는 중요하니 무시하면 안 된다.
- 모든 비검사 경고는 런타임에 ClassCastException을 일으킬 수 있는 잠재적 가능성을 뜻한다. 따라서 최대한 제거해야 한다.
- 경고를 없앨 방법을 찾지 못했다면, 그 코드가 타입 안전함을 증명하고 가능한 한 범위를 좁혀 @SuppressWarnings("unchecked") 애너테이션으로 경고를 숨기도록 한다.
- 경고를 숨긴다면 경고를 숨기기로 한 근거를 주석으로 남겨야 한다.

## 참고자료

- [@SuppressWarnings를 사용하여 경고 제외](https://www.ibm.com/docs/ko/adfz/developer-for-zos/9.5.1?topic=code-excluding-warnings)
- [JAVA Spring의 @SuppressWarnings 어노테이션](https://haranglog.tistory.com/3)