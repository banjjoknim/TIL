# 아이템 16. public 클래스에서는 public 필드가 아닌 접근자 메서드를 사용하라

## 캡슐화

### 이점

- API를 수정하지 않고 내부 표현을 바꿀 수 있다.
- 불변식을 보장할 수 있다.
- 외부에서 필드에 접근할 때 부수 작업을 수행할 수 있다.

### 접근자와 변경자를(mutator) 메서드를 활용해 데이터를 캡슐화한다.

```java
public class Point {
    private double x;
    private double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
}
```

- **패키지 바깥에서 접근할 수 있는 클래스라면 접근자를 제공**함으로써 클래스 내부 표현 방식을 언제든 바꿀 수 있는 유연성을 얻을 수 있다.
    - public 클래스가 필드를 공개하면 이를 사용하는 클라이언트가 생겨날 것이므로 내부 표현 방식을 마음대로 바꿀 수 없게 된다.
- **package-private 클래스 혹은 private 중첩 클래스라면 데이터 필드를 노출한다 해도 문제가 없다.** 그 클래스가 표현하려는 추상 개념만 올바르게 표현해주면 된다.
    - 패키지 바깥 코드는 전혀 손대지 않고 데이터 표현 방식을 바꿀 수 있다. private 중첩 클래스의 경우라면 수정 범위가 더 좁아져서 이 클래스를 포함하는 외부 클래스까지로 제한된다.

**그러니까, public 클래스의 필드를 직접 노출하지 마라.**

### 불변 필드를 노출한 public 클래스 - 과연 좋은가?

```java
public final class Time {
    private static final int HOURS_PER_DAY = 24;
    private static final int MINUTES_PER_HOUR = 60;

    public final int hour;
    public final int minute;

    public Time(int hour, int minute) {
        validateTime(hour, minute);
        this.hour = hour;
        this.minute = minute;
    }

    private void validateTime(int hour, int minute) {
        // .. 유효성 검증 로직, 불변식 보장
    }

    // ...
}
```

- 여전히 API를 변경하지 않고는 표현 방식을 바꿀 수 없다.
- 여전히 필드를 읽을 때 부수 작업을 수행할 수 없다.
- 단, 불변식은 보장할 수 있게 된다.

## 핵심 정리

public 클래스는 절대 가변 필드를 직접 노출해서는 안 된다. 불변 필드라면 노출해도 덜 위험하지만 완전히 안심할 수는 없다. 하지만 package-private 클래스나 private 중첩 클래스에서는 종종 (
불변이든 가변이든) 필드를 노출하는 편이 나을 때도 있다.