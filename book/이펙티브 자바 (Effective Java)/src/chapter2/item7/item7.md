# 아이템 7. 다 쓴 객체 참조를 해제하라

## 메모리 누수가 일어나는 위치는 어디인가?

```java
public class Stack {
    private Object[] elements;
    private int size = 0;
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    public Stack() {
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
        return elements[--size];
    }

    private void ensureCapacity() {
        if (elements.length == size) {
            elements = Arrays.copyOf(elements, 2 * size + 1);
        }
    }
}
```

- 이 스택을 사용하는 프로그램을 오래 실행하다 보면 점차 가비지 컬렉션 활동과 메모리 사용량이 늘어나 결국 성능이 저하될 것이다.
- 상대적으로 드문 경우긴 하지만 심할 때는 디스크 페이징이나 OutOfMemoryError를 일으켜 프로그램이 예기치 않게 종료될 수도 있다.

- 이 코드에서는 프로그램 스택이 커졌다가 줄어들었을 때 스택에서 꺼내진 객체들을 가비지 컬렉터가 회수하지 않는다.
- 이 스택이 그 객체들의 다 쓴 참조(obsolete reference)를 여전히 가지고 있기 때문이다.
- 여기서 다 쓴 참조란 문자 그대로 앞으로 다시 쓰지 않을 참조를 뜻한다.
- 앞의 코드에서는 elements 배열의 '활성 영역'밖의 참조들이 모두 여기에 해당한다. 활성 영역은 인덱스가 size보다 작은 원소들로 구성된다.

## 가비지 컬렉션에서의 메모리 누수

- 객체 참조 하나를 살려두면 가비지 컬렉터는 그 객체뿐 아니라 그 객체가 참조하는 모든 객체(그리고 또 그 객체들이 참조하는 모든 객체)를 회수해가지 못한다.
- 그래서 단 몇 개의 객체가 매우 많은 객체를 회수되지 못하게 할 수 있고 잠재적으로 성능에 악영향을 줄 수 있다.

### 해법

- 해당 참조를 다 썼을 때 null 처리(참조 해제)하면 된다.

### 제대로 구현한 pop 메서드

```java
public class Stack {

    // ...

    public Object pop() {
        if (size == 0) {
            throw new EmptyStackException();
        }
        Object result = elements[--size];
        elements[size] = null; // 다 쓴 참조 해제
        return result;
    }
}
```

### null로 객체 참조를 해제

- 다 쓴 참조를 null 처리하면 다른 이점도 따라온다.
- 만약 null 처리한 참조를 실수로 사용하려 하면 프로그램은 즉시 NullPointerException을 던지며 종료된다. 프로그램 오류는 가능한 한 조기에 발견하는 게 좋다.
- 하지만, **객체 참조를 null 처리하는 일은 예외적인 경우여야 한다.**
- 모든 객체를 다 쓰자마자 일일이 null 처리하는 것은 프로그램을 필요 이상으로 지저분하게 만들 뿐이다.
- 다 쓴 참조를 해제하는 가장 좋은 방법은 그 참조를 담은 변수를 유효 범위(scope) 밖으로 밀어내는 것이다. 변수의 범위를 최소가 되게 정의하자.

### 자기 메모리를 직접 관리하는 클래스의 메모리 주의 사항

- 이 스택은 (객체 자체가 아니라 객체 참조를 담는) elements 배열로 저장소 풀을 만들어 원소들을 관리한다.
- 배열의 활성 영역에 속한 원소들이 사용되고 비활성 영역은 쓰이지 않는다. 문제는, 가비지 컬렉터는 이 사실을 알 길이 없다는 것이다.
- 가비지 컬렉터가 보기에는 비활성 영역에서 참조하는 객체도 똑같이 유효한 객체다. 비활성 영역의 객체가 더 이상 쓸모없다는 건 프로그래머만 아는 사실이다.
- 따라서, 프로그래머는 비활성 영역이 되는 순간 null 처리해서 해당 객체를 더는 쓰지 않을 것임을 가비지 컬렉터에 알려야 한다.
- 일반적으로 **자기 메모리를 직접 관리하는 클래스라면 항시 메모리 누수에 주의해야 한다.** 원소를 다 사용한 즉시 그 원소가 참조한 객체들을 다 null 처리해줘야 한다.

## 캐시에서의 메모리 누수

- **캐시 역시 메모리 누수를 일으키는 주범이다.**
- 캐시 외부에서 키(key)를 참조하는 동안만(값이 아니다) 엔트리가 살아 있는 캐시가 필요한 상황이라면 WeakHashMap을 사용해 캐시를 만들자.
- 다 쓴 엔트리는 그 즉시 자동으로 제거될 것이다. 단, WeakHashMap은 이러한 상황에서만 유용하다.

### 캐시에서의 메모리 누수를 방지하는 방식

- 캐시를 만들 때 보통은 캐시 엔트리의 유효 기간을 정확히 정의하기 어렵다. 따라서, 시간이 지날수록 엔트리의 가치를 떨어뜨리는 방식을 흔히 사용한다.
- 이런 방식에서는 쓰지 않는 엔트리를 이따금 청소해줘야 한다.
- ScheduledThreadPoolExecutor 같은 백그라운드 스레드를 활용하거나 캐시에 새 엔트리를 추가할 때 부수 작업으로 수행하는 방법이 있다.
- LinkedHashMap은 removeEldestEntry 메서드를 써서 후자의 방식으로 처리한다.
- 더 복잡한 캐시를 만들고 싶다면 java.lang.ref 패키지를 직접 활용해야 할 것이다.

## 리스너(listener) 혹은 콜백(callback)에서의 메모리 누수

- 클라이언트가 콜백을 등록만 하고 명확히 해지하지 않는다면, 뭔가 조치해주지 않는 한 콜백은 계속 쌓여갈 것이다.
- 이럴 때 콜백을 약한 참조(weak reference)로 저장하면 가비지 컬렉터가 즉시 수거해간다. (ex. WeakHashMap에 키로 저장하면 된다.)

## 핵심 정리

메모리 누수는 겉으로 잘 드러나지 않아 시스템에 수년간 잠복하는 사례도 있다. 이런 누수는 철저한 코드 리뷰나 힙 프로파일러 같은 디버깅 도구를 동원해야만 발견되기도 한다. 그래서 이런 종류의 문제는 예방법을
익혀두는 것이 매우 중요하다.

---

## 참고자료

- [Java Reference와 GC](https://d2.naver.com/helloworld/329631)
- [[JVM] 객체 재사용 - Reference 종류, WeakHashMap](https://m.blog.naver.com/PostView.nhn?isHttpsRedirect=true&blogId=kbh3983&logNo=221001751572)
- [[JVM] WARN! Collections & Memory Leak](https://blog.naver.com/kbh3983/220999674350)
