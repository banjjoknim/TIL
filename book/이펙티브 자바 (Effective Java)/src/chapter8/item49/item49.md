# 아이템 49. 매개변수가 유효한지 검사하라

메서드와 생성자 대부분은 입력 매개변수의 값이 특정 조건(불변식)을 만족했을 때 제대로 동작해야 한다. 그리고 이런 제약은 반드시 문서화해야 하며 메서드 몸체가 시작되기 전에 검사해야 한다.
"오류는 가능한 한 빨리 (발생한 곳에서) 잡아야 한다"는 일반 원칙의 한 사례이기도 하다. 오류를 발생한 즉시 잡지 못하면 해당 오류를 감지하기 어려워지고, 감지하더라도 오류의 발생 지점을 찾기 어려워진다.

## 매개변수 검사

### 매개변수 검사를 제대로 하지 못하면 문제가 생길 수 있다.

- 메서드가 수행되는 중간에 모호한 예외를 던지며 실패할 수 있다.
    - 메서드는 잘 수행되었지만 잘못된 결과를 반환하는 경우도 있는데, 미래에 이 메서드와는 관련 없는 오류를 낼 위험이 있다.
    - 즉, 매개변수 검사에 실패하면 실패 원자성(failure atomicity)을 어기는 결과를 낳을 수 있다.
- public과 protected 메서드는 매개변수 값이 잘못됐을 때 던지는 예외를 문서화해야 한다(@throws 자바독 태그를 사용하면 된다).
    - 매개변수의 제약을 문서화한다면 그 제약을 어겼을 때 발생하는 예외도 함께 기술해야 한다. 아래는 그 예시다.

```java
public class Item49 {
    /**
     * (현재 값 mod m) 값을 반환한다. 이 메서드는
     * 항상 음이 아닌 BigInteger를 반환한다는 점에서 remainder 메서드와 다르다.
     * @param m 계수(양수여야 한다)
     * @return 현재 값 mod m
     * @throws ArithmeticException m이 0보다 작거나 같으면 발생한다.
     */
    public BigInteger mod(BigInteger m) {
        if (m.signum() <= 0) {
            throw new ArithmeticException("계수(m)는 양수여야 합니다. " + m);
        }
        // 계산 수행
        return null; // 편의상 null을 리턴했다.
    }
}
```

- 이 메서드에서 m이 null이면 NullPointerException을 던진다는 말은 설명에 없다. 그 이유는 이 설명을 개별 메서드가 아닌 BigInteger 클래스 수준에서 기술했기 때문이다.
    - 클래스 수준 주석은 그 클래스의 모든 public 메서드에 적용되므로 각 메서드에 일일이 기술하는 것보다 훨씬 깔끔한 방법이다.
    - **자바 7에 추가된 java.util.Objects.requireNonNull 메서드는 유연하고 사용하기도 편하니, 더 이상 null 검사를 수동으로 하지 않아도 된다.**
        - 원하는 예외 메시지도 지정할 수 있고, 입력을 그대로 반환하므로 값을 사용하는 동시에 null 검사를 수행할 수 있다. 아래는 Objects 클래스의 requireNonNull 메서드다.

```java
public class Objects {
    // ...

    /**
     * Checks that the specified object reference is not {@code null}. This
     * method is designed primarily for doing parameter validation in methods
     * and constructors, as demonstrated below:
     * <blockquote><pre>
     * public Foo(Bar bar) {
     *     this.bar = Objects.requireNonNull(bar);
     * }
     * </pre></blockquote>
     *
     * @param obj the object reference to check for nullity
     * @param <T> the type of the reference
     * @return {@code obj} if not {@code null}
     * @throws NullPointerException if {@code obj} is {@code null}
     */
    public static <T> T requireNonNull(T obj) {
        if (obj == null)
            throw new NullPointerException();
        return obj;
    }

    // ...

    public static <T> T requireNonNull(T obj, String message) {
        if (obj == null)
            throw new NullPointerException(message);
        return obj;
    }
}
```

#### 자바의 null 검사 기능 사용하기

```java
public class Item49 {

    private String strategy;

    public void changeStrategy(String strategy) {
        this.strategy = Objects.requireNonNull(strategy, "전략");
    }
}
```

- 반환값은 무시하고 필요한 곳 어디서든 순수한 null 검사 목적으로 사용해도 된다.
- 자바 9에서는 Objects에 범위 검사 기능도 더해졌다.
    - checkFromIndexSize, checkFromToIndex, checkIndex라는 메서드들이 있다.
    - 유용하지만 예외 메시지를 지정할 수 없고, 리스트와 배열 전용으로 설계됐다. 또한 닫힌 범위(closed range; 양 끝단 값을 포함하는)는 다루지 못한다.

### 단언문(assert)을 사용한 매개변수 유효성 검증

공개되지 않은 메서드라면 메서드가 호출되는 상황을 통제할 수 있다. 즉, public이 아닌 메서드라면 단언문(assert)을 사용해 매개변수 유효성을 검증할 수 있다.

#### 재귀 정렬용 private 도우미 함수

```java
public class Item49 {

    private static void sort(long a[], int offset, int length) {
        assert a != null;
        assert offset >= 0 && offset <= a.length;
        assert length >= 0 && length <= a.length - offset;
        // 계산 수행
    }
}
```

- 이 단언문들은 자신이 단언한 조건이 무조건 참이라고 선언한다. 참이 아닐경우 AssertionError가 발생한다.
- 단언문은 일반적인 유효성 검사와 다르다.
    - 첫 번째. 실패하면 AssertionError를 던진다.
    - 두 번째. 런타임에 아무런 효과도, 아무런 성능 저하도 없다(단, java를 실행할 때 명령줄에서 -ea 혹은 --enableassertions 플래그 설정하면 런타임에 영향을 준다).

### 나중에 쓰기 위해 저장하는 매개변수의 유효성을 검사하라

- 메서드가 직접 사용하지는 않으나 나중에 쓰기 위해 저장하는 매개변수는 특히 더 신경써서 검사해야 한다. 한참 뒤에서야 문제가 발생할 수 있다.
- 생성자는 "나중에 쓰려고 저장하는 매개변수의 유효성을 검사하라"는 원칙의 특수한 사례다.
    - 생성자 매개변수의 유효성 검사는 클래스 불변식을 어기는 객체가 만들어지지 않게 하는 데 꼭 필요하다.
- "메서드 몸체 실행 전에 매개변수 유효성을 검사해야 한다"는 규칙에도 예외가 있다. 유효성 검사 비용이 지나치게 높거나 상용적이지 않을 때, 혹은 계산 과정에서 암묵적으로 검사가 수행될 때다.
    - 다만, 암묵적 유효성 검사에 너무 의존하면 실패 원자성을 해칠 수 있으니 주의해야 한다.

### API 문서에 정의된 예외와 다른 예외가 발생할 경우

- 계산 중 잘못된 매개변수 값을 사용해 발생한 예외가 API 문서에서 던지기로 한 예외와 다를 수 있다.
    - 이럴 때는 예외 번역(exception translate, 또는 예외 전환) 관용구를 사용하여 API 문서에 기재된 예외로 번역해줘야 한다.

## 핵심 정리

- 매개변수에 제약을 두는 게 좋다고 해석하면 안 된다. 메서드는 최대한 범용적으로 설계해야 한다. 메서드가 건네받은 값으로 제대로 동작할 수 있다면, 매개변수 제약은 적을수록 좋다.
- 메서드나 생성자를 작성할 때면 그 매개변수들에 어떤 제약이 있을지 생각해야 하며, 그 제약들을 문서화하고 메서드 코드 시작 부분에서 명시적으로 검사해야 한다.

## 참고자료

- [Enabling and Disabling Assertions](https://docs.oracle.com/javase/8/docs/technotes/guides/language/assert.html#enable-disable)
- [[아이템 49] 단언문을 사용한 유효성 검증](https://github.com/Java-Bom/ReadingRecord/issues/118)
- [Java의 예외 처리](https://johngrib.github.io/wiki/java/exception-handling/)