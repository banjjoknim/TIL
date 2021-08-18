# 아이템 35. ordinal 메서드 대신 인스턴스 필드를 사용하라

- 대부분의 열거 타입 상수는 순서대로 하나의 정숫값에 대응된다.
- 모든 열거 타입은 해당 상수가 그 열거 타입에서 몇 번째 위치인지를 반환하는 ordinal이라는 메서드를 제공한다.

## ordinal을 잘못 사용한 예 - 따라 하지 말 것!

```java
public enum Ensemble {
    SOLO, DUET, TRIO, QUARTET, QUINTET, SEXTET, SEPTET, OCTET, NONET, DECTET;

    public int numberOfMusicians() {
        return ordinal() + 1;
    }
}
```

- 상수 선언 순서를 바꾸는 순간 numberOfMusicians가 오동작한다.
- 이미 사용 중인 정수와 값이 같은 상수는 추가할 방법이 없다. 또한, 값을 중간에 비워둘 수 없다.

## ordinal은 사용하지 말자!

- **열거 타입 상수에 연결된 값은 ordinal 메서드로 얻지 말고 인스턴스 필드에 저장하도록 한다.**

```java
public enum Ensemble {
    SOLO(1),
    DUET(2),
    TRIO(3),
    QUARTET(4),
    QUINTET(5),
    SEXTET(6),
    SEPTET(7),
    OCTET(8),
    DOUBLE_QUARTET(8),
    NONET(9),
    DECTET(10),
    TRIPLE_QUARTET(12);

    private final int numberOfMusicians;

    Ensemble(int numberOfMusicians) {
        this.numberOfMusicians = numberOfMusicians;
    }

    public int numberOfMusicians() {
        return numberOfMusicians;
    }
}
```

Enum의 API 문서에는 이렇게 쓰여있다고 한다.
> "대부분의 프로그래머는 이 메서드를 쓸 일이 없다. 이 메서드는 EnumSet과 EnumMap 같이 열거 타입 기반의 범용 자료구조에 쓸 목적으로 설계되었다."

즉, 위와 같은 용도가 아니라면 ordinal 메서드는 절대 사용하지 않도록 한다.