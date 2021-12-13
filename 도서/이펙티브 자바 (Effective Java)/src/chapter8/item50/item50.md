# 아이템 50. 적시에 방어적 복사본을 만들라

자바는 안전한 언어이다. 네이티브 메서드를 사용하지 않으니 C, C++ 같이 안전하지 않은 언어에서 흔히 보는 버퍼 오버런, 배열 오버런, 와일드 포인터 같은 메모리 충돌 오류에서 안전하다. 자바로 작성한 클래스는
시스템의 다른 부분에서 무슨 짓을 하든 그 불변식이 지켜진다. 메모리 전체를 하나의 거대한 배열로 다루는 언어에서는 누릴 수 없는 강점이다.

## 방어적 프로그래밍

자바라 해도 다른 클래스로부터의 침범을 아무런 노력 없이 다 막을 수 있는 것은 아니다.

- 따라서, **클라이언트가 불변식을 깨뜨리려한다고 가정하고 방어적으로 프로그래밍해야 한다.**
- 악의적인 공격으로부터 방어하지 못하거나, 또는 실수로 클래스를 오작동하게 만들 수도 있다.
- 어떤 객체든 그 객체의 허락 없이는 외부에서 내부를 수정하는 일은 불가능하다. 하지만 이는 주의를 기울여야 보장할 수 있다.

### 기간을 표현하는 클래스 - 불변식을 지키지 못했다.

```java
public final class Period {
    private final Date start;
    private final Date end;

    public Period(Date start, Date end) {
        if (start.compareTo(end) > 0) {
            throw new IllegalArgumentException(start + "가 " + end + "보다 늦다.");
        }
        this.start = start;
        this.end = end;
    }

    public Date start() {
        return start;
    }

    public Date end() {
        return end;
    }
}
```

- 이 클래스는 Date가 가변이라는 사실을 이용하면 어렵지 않게 불변식을 깨뜨릴 수 있다.

### Period 인스턴스의 내부를 공격해보자.

```java
public final class Period {
    // .. 코드 생략

    public static void main(String[] args) {
        Date start = new Date();
        Date end = new Date();
        Period period = new Period(start, end);
        end.setYear(78); // period의 내부를 수정했다!
    }
}
```

- 자바 8 이후로는 쉽게 해결할 수 있다. Date 대신 불변인 Instant를 사용하면 된다(혹은 LocalDateTime이나 ZonedDateTime을 사용해도 된다)
- **Date는 낡은 API이니 새로운 코드를 작성할 때는 더 이상 사용하면 안 된다.**
- 외부 공격으로부터 Period 인스턴스의 내부를 보호하려면 **생성자에서 받은 가변 매개변수 각각을 방어적으로 복사(defensive copy)해야 한다.** 그 다음 Period 인스턴스 안에서는 원본이 아닌
  복사본을 사용한다.

### 수정한 생성자 - 매개변수의 방어적 복사본을 만든다.

```java
public final class Period {
    private final Date start;
    private final Date end;

    public Period(Date start, Date end) {
        this.start = new Date(start.getTime());
        this.end = new Date(end.getTime());
        if (start.compareTo(end) > 0) {
            throw new IllegalArgumentException(start + "가 " + end + "보다 늦다.");
        }
    }

    // 생략
}
```

- **매개변수의 유효성을 검사하기 전에 방어적 복사본을 만들고, 이 복사본으로 유효성을 검사했다.**
    - 순서가 부자연스러워 보일 수 있으나, 반드시 이렇게 작성해야 한다.
    - 멀티스레딩 환경이라면 원본 객체의 유효성을 검사한 후 복사본을 만드는 그 찰나의 취약한 순간에 다른 스레드가 원본 객체를 수정할 위험이 있기 때문이다.
    - 방어적 복사를 매개변수 유효성 검사 전에 수행하면 이런 위험에서 해방될 수 있다. 컴퓨터 보안 커뮤니티에서는 이를 검사시점/사용시점(time-of-check/time-of-use) 공격 혹은 TOCTOU
      공격이라 한다.
- Date는 final이 아니므로 clone이 Date가 정의한 게 아닐 수 있다. 즉, clone이 악의를 가진 하위 클래스의 인스턴스를 반환할 수도 있다. 따라서 Date의 clone 메서드를 사용하지 않았다.
    - **매개변수가 제3자에 의해 확장될 수 있는 타입이라면 방어적 복사본을 만들 때 clone을 사용해서는 안 된다.**

### Period 인스턴스를 향한 접근자 메서드 공격

```java
public final class Period {

    // 코드 생략 ..

    public static void main(String[] args) {
        Date start = new Date();
        Date end = new Date();
        Period period = new Period(start, end);
        period.end().setYear(78); // period의 내부를 변경했다!
    }
}
```

- 접근자 메서드가 내부의 가변 정보를 직접 드러내기 때문에 Period 인스턴스는 아직도 변경 가능하다.
    - 이 공격을 막아내려면 단순히 접근자가 **가변 필드의 방어적 복사본을 반환하면 된다.**

### 수정한 접근자 - 필드의 방어적 복사본을 반환한다.

```java
public final class Period {

    public Date start() {
        return new Date(start.getTime());
    }

    public Date end() {
        return new Date(end.getTime());
    }
}
```

- 생성자와 달리 접근자 메서드에서는 방어적 복사에 clone을 사용해도 된다. Period가 가지고 있는 Date 객체는 java.util.Date임이 확실하기 때문이다(신뢰할 수 없는 하위 클래스가 아니다).
- 그렇더라도 인스턴스를 복사하는 데는 일반적으로 생성자나 정적 팩터리를 쓰는 게 좋다.
- 매개변수를 방어적으로 복사하는 목적에는 불변 객체를 만들기 위해서만은 아니다.
    - 클라이언트가 제공한 객체의 참조를 내부의 자료구조에 보관해야 할 때면 항시 그 객체가 잠재적으로 변경될 수 있는지를 생각해야 한다.
    - 변경될 수 있는 객체라면 그 객체가 클래스에 넘겨진 뒤 임의로 변경되어도 그 클래스가 문제없이 동작할지를 따지고, 확신할 수 없다면 복사본을 만들어 저장해야 한다.
- 클래스가 불변이든 가변이든, 가변인 내부 객체를 클라이언트에 반환할 때는 반드시 심사숙고해야 한다. 안심할 수 없다면 (원본을 노출하지 말고) 방어적 복사본을 반환해야 한다.
- **길이가 1 이상인 배열은 무조건 가변이다. 따라서 내부에서 사용하는 배열을 클라이언트에 반환할 때는 항상 방어적 복사를 수행해야 한다.**
- 자바 8 이상에서는 Instant(혹은 LocalDateTime이나 ZonedDateTime)를 사용하도록 한다.
    - 이전 버전의 자바를 사용한다면 Date 참조 대신 Date.getTime()이 반환하는 long 정수를 사용하는 방법을 써도 된다.

### 방어적 복사의 생략

- 방어적 복사에는 성능 저하가 따르고, 또 항상 쓸 수 있는 것도 아니다. (같은 패키지에 속하는 등의 이유로) 호출자가 컴포넌트 내부를 수정하지 않으리라 확신하면 방어적 복사를 생략할 수 있다.
    - 호출자에서 해당 매개변수나 반환값을 수정하지 말아야 함을 명확히 문서화하는 게 좋다.
- 다른 패키지에서 사용한다고 해서 넘겨받은 가변 매개변수를 항상 방어적으로 복사해 저장해야 하는 것은 아니다.
    - 메서드나 생성자의 매개변수로 넘기는 행위가 그 객체의 통제권을 명백히 이전함을 뜻하기도 한다.
    - 통제권을 이전하는 메서드를 호출하는 클라이언트는 해당 객체를 더 이상 직접 수정하는 일이 없다고 약속해야 한다.
    - 클라이언트가 건네주는 가변 객체의 통제권을 넘겨받는다고 기대하는 메서드나 생성자에서도 그 사실을 확실히 문서에 기재해야 한다.
    - 통제권을 넘겨 받기로 한 메서드, 생성자를 가진 클래스들은 취약하다. 따라서 해당 클래스와 그 클라이언트가 상호 신뢰할 수 있을 때, 혹은 불변식이 깨지더라도 그 영향이 오직 호출한 클라이언트로 국한될
      때로 한정해서 방어적 복사를 생략해도 된다.

## 핵심 정리

- 클래스가 클라이언트로부터 받는 혹은 클라이언트로 반환하는 구성요소가 가변이라면 그 요소는 반드시 방어적으로 복사해야 한다.
- 복사 비용이 너무 크거나 클라이언트가 그 요소를 잘못 수정할 일이 없음을 신뢰한다면 방어적 복사를 수행하는 대신 해당 구성요소를 수정했을 때의 책임이 클라이언트에 있음을 문서에 명시하도록 한다.