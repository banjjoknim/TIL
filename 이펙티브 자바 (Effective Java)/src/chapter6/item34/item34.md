# 아이템 34. int 상수 대신 열거 타입을 사용하라

열거 타입은 일정 개수의 상수 값을 정의한 다음, 그 외의 값은 허용하지 않는 타입이다.

## 정수 열거 패턴

- 정수 열거 패턴(int enum pattern)에는 단점이 많다. 타입 안전을 보장할 수 없고 표현력도 좋지 않다.
- 자바는 정수 열거 패턴을 위한 별도 이름공간(namespace)을 지원하지 않는다.
- 정수 열거 패턴을 사용한 프로그램은 깨지기 쉽다.
    - 평범한 상수를 나열한 것뿐이라 컴파일하면 그 값이 클라이언트 파일에 그대로 새겨진다.
    - 따라서 상수의 값이 바뀌면 클라이언트도 반드시 다시 컴파일해야 한다.
- 정수 상수는 문자열로 출력하기가 다소 까다롭다.
    - 디버거로 살펴봐도 단지 숫자로만 보여서 썩 도움이 되지 않는다.
- 정수 대신 문자열 상수를 사용하는 변형 패턴도 있다.
    - 문자열 열거 패턴(string enum pattern)이라 하는 이 변형은 문자열 상수의 이름 대신 문자열 값을 그대로 하드코딩하게 만들기 때문에 더 좋지 않다.

## 열거 타입

자바는 열거 패턴의 단점을 말끔히 씻어주는 동시에 여러 장점을 안겨주는 열거 타입(enum type)을 제시했다.

### 가장 단순한 열거 타입

```java
public enum Apple {JUJI, PIPPIN, GRANNY_SMITH}

public enum Orange {NAVEL, TEMPLE, BLOOD}
```

- 자바의 열거 타입은 완전한 형태의 클래스이다.
- 자바 열거 타입 자체는 클래스이며, 상수 하나당 자신의 인스턴스를 하나씩 만들어 public static final필드로 공개한다.
- 열거 타입은 밖에서 접근할 수 있는 생성자를 제공하지 않으므로 사실상 final이다. 즉, 클라이언트가 인스턴스를 직접 생성하거나 확장할 수 없기 때문에 열거 타입 선언으로 만들어진 인스턴스들은 딱 하나씩만
  존재함이 보장된다.
    - 열거 타입은 인스턴스 통제된다. 싱글턴은 원소가 하나뿐인 열거타입이라 할 수 있고, 열거 타입은 싱글턴을 일반화한 형태라고 볼 수 있다.

### 열거 타입은 컴파일타임 타입 안전성을 제공한다.

- 열거 타입에 다른 타입의 값을 넘기려 하면 컴파일 오류가 난다.
- 타입이 다른 열거 타입 변수에 할당하려 하거나 다른 열거 타입의 값끼리 == 연산자로 비교하려는 것과 같기 때문이다.

### 열거 타입에는 각자의 이름공간이 있다.

- 열거 타입에는 각자의 이름공간이 있기 때문에 이름이 같은 상수도 평화롭게 공존한다.
- 열거 타입에 새로운 상수를 추가하거나 순서를 바꿔도 다시 컴파일하지 않아도 된다.
    - 공개되는 것이 오직 필드의 이름뿐이라, 상수 값이 클라이언트로 컴파일되어 각인되지 않기 때문이다.

### 열거 타입은 출력하기 좋다.

- 열거 타입의 toString 메서드는 출력하기에 적합한 문자열을 내어준다.

## 열거 타입은 클래스이다.

- 열거 타입은 단순하게는 상수 모임일 뿐이지만, 실제로는 클래스이기 때문이다.
- 열거 타입 상수 각각을 특정 데이터와 연결지으려면 생성자에서 데이터를 받아 인스턴스 필드에 저장하면 된다.
- 열거 타입은 근본적으로 불변이라 모든 필드는 final이어야 한다.
    - 필드를 public으로 선언해도 되지만 private로 두고 별도의 public 접근자 메서드를 두는 게 낫다.
- 열거 타입은 자신 안에 정의된 상수들의 값을 배열에 담아 반환하는 정적 메서드인 values를 제공한다.
    - 값들은 순서대로 저장되며, 각 열거 타입 값의 toString 메서드는 상수 이름을 문자열로 반환한다.
- 열거 타입에서 상수를 제거해도 해당 상수를 참조하지 않는 클라이언트에는 아무 영향이 없다.
- 널리 쓰이는 열거 타입은 톱레벨 클래스로 만들고, 특정 톱레벨 클래스에서만 쓰인다면 해당 클래스의 멤버 클래스로 만들도록 한다.

## 상수별 메서드 구현을 활용한 열거 타입

- switch 문을 이용해서 구현할 수도 있지만, 상수별로 메서드 구현을 사용할 수 있다.
- 추상 메서드를 이용했다면 재정의하지 않았을 경우 컴파일 오류로 알려준다.

```java
public enum Operation {
    PLUS {
        public double apply(double x, double y) {
            return x + y;
        }
    },
    MINUS {
        public double apply(double x, double y) {
            return x - y;
        }
    },
    TIMES {
        public double apply(double x, double y) {
            return x * y;
        }
    },
    DIVIDE {
        public double apply(double x, double y) {
            return x / y;
        }
    };

    public abstract double apply(double x, double y);
}
```

### 상수별 메서드 구현을 상수별 데이터와 결합할 수도 있다.

```java
public enum Operation {
    PLUS("+") {
        public double apply(double x, double y) {
            return x + y;
        }
    },
    MINUS("-") {
        public double apply(double x, double y) {
            return x - y;
        }
    },
    TIMES("*") {
        public double apply(double x, double y) {
            return x * y;
        }
    },
    DIVIDE("/") {
        public double apply(double x, double y) {
            return x / y;
        }
    };

    private final String symbol;

    Operation(String symbol) {
        this.symbol = symbol;
    }

    public abstract double apply(double x, double y);
}
```

- 열거 타입에는 상수 이름을 입력받아 그 이름에 해당하는 상수를 반환해주는 valueOf(String) 메서드가 자동 생성된다.
    - toString이 반환하는 문자열을 해당 열거 타입 상수로 변환해주는 fromString 메서드도 함께 제공하는 것도 고려해보자.

```java
public enum Operation {

    // ...

    public abstract double apply(double x, double y);

    public static final Map<String, Operation> stringToEnum = Stream.of(values())
            .collect(toMap(Object::toString, e -> e));

    // 지정한 문자열에 해당하는 Operation을 (존재한다면) 반환한다.
    public static Optional<Operation> fromString(String symbol) {
        return Optional.ofNullable(stringToEnum.get(symbol));
    }
}
```

- Operation 상수가 stringToEnum 맵에 추가되는 시점은 열거 타입 상수 생성 후 정적 필드가 초기화될 때다.
- 열거 타입 상수는 생성자에서 자신의 인스턴스를 맵에 추가할 수 없다.
    - 만약 이 방식이 허용된다면 런타임에 NullPointerException이 발생했을 것이다.
    - 열거 타입의 정적 필드 중 열거 타입의 생성자에서 접근할 수 있는 것은 상수 변수뿐이다.
    - 열거 타입 생성자가 실행되는 시점에는 정적 필드들이 아직 초기화되기 전이기 때문에 자기 자신을 추가하지 못하게 하는 제약이 반드시 필요하다.
    - 열거 타입 생성자에서는 같은 열거 타입의 다른 상수에도 접근할 수 없다.
        - 열거 타입의 상수는 해당 열거 타입의 인스턴스를 public static final 필드로(정적 필드로) 선언한 것이기 때문이다.
        - 즉, static이므로 열거 타입 생성자에서 정적 필드에 접근할 수 없다는 제약이 적용된다.

### 값에 따라 분기하여 코드를 공유하는 열거 타입 - 좋은 방법인가?

```java
public enum PayrollDay {
    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY;

    private static final int MINS_PER_SHIFT = 8 * 60;

    int pay(int minutesWorked, int payRate) {
        int basePay = minutesWorked * payRate;

        int overtimePay;
        switch (this) {
            case SATURDAY:
            case SUNDAY: // 주말
                overtimePay = basePay / 2;
                break;
            default: // 주중
                overtimePay = minutesWorked <= MINS_PER_SHIFT ? 0 : (minutesWorked - MINS_PER_SHIFT) * payRate / 2;
        }

        return basePay + overtimePay;
    }
}
```

- 만약 새로운 값을 열거 타입에 추가하려면 case 문을 잊지말고 함께 변경해주어야 한다.
- 상수별 메서드 구현으로 해결하려면 두 가지 방법이 있다.
    - 첫 번째, 계산 로직을 모든 상수에 중복해서 넣는다.
    - 두 번째, 계산 로직을 평일용과 주말용으로 나눠서 각각을 도우미 메서드로 작성한 뒤 각 상수가 자신에게 필요한 메서드를 적절히 호출한다.
        - 하지만 위의 두 방식 모두 가독성이 떨어지고 오류 발생 가능성이 높아진다.

### 전략 열거 타입 패턴

- 가장 깔끔한 변경 방법은 새로운 상수를 추가할 때 잔업수당 '전략을' 선택하도록 하는 것이다.
- 잔업수당 계산을 private 중첩 열거 타입(PayType)으로 옮기고 PayrollDay 열거 타입의 생성자에서 이중 적당한 것을 선택한다.
    - PayrollDay 열거 타입은 잔업수당 계산을 그 전략 열거 타입에 위임하여, switch 문이나 상수별 메서드 구현이 필요 없게 된다.
    - 이 패턴은 switch 문보다 복잡하지만 더 안전하고 유연하다.

```java
public enum PayrollDay {
    MONDAY(PayType.WEEKDAY),
    TUESDAY(PayType.WEEKDAY),
    WEDNESDAY(PayType.WEEKDAY),
    THURSDAY(PayType.WEEKDAY),
    FRIDAY(PayType.WEEKDAY),
    SATURDAY(PayType.WEEKEND),
    SUNDAY(PayType.WEEKEND);

    private final PayType payType;

    PayrollDay(PayType payType) {
        this.payType = payType;
    }

    int pay(int minutesWorked, int payRate) {
        return payType.pay(minutesWorked, payRate);
    }

    // 전략 열거 타입
    enum PayType {
        WEEKDAY {
            int overtimePay(int minsWorked, int payRate) {
                return minsWorked <= MINS_PER_SHIFT ? 0 : (minsWorked - MINS_PER_SHIFT) * payRate / 2;
            }
        },
        WEEKEND {
            int overtimePay(int minsWorked, int payRate) {
                return minsWorked * payRate / 2;
            }
        };

        abstract int overtimePay(int mins, int payRate);

        private static final int MINS_PER_SHIFT = 8 * 60;

        int pay(int minsWorked, int payRate) {
            int basePay = minsWorked * payRate;
            return basePay + overtimePay(minsWorked, payRate);
        }
    }
}
```

- switch 문은 열거 타입의 상수별 동작을 구현하는 데 적합하지 않다.
- 하지만 **기존 열거 타입에 상수별 동작을 혼합해 넣을 때는 switch 문이 좋은 선택이 될 수 있다.**

```java
enum Operation {
    // ...

    public abstract double apply(double x, double y);

    public static final Map<String, Operation> stringToEnum = Stream.of(values())
            .collect(toMap(Object::toString, e -> e));

    // 지정한 문자열에 해당하는 Operation을 (존재한다면) 반환한다.
    public static Optional<Operation> fromString(String symbol) {
        return Optional.ofNullable(stringToEnum.get(symbol));
    }

    public static Operation inverse(Operation operation) {
        switch (operation) {
            case PLUS:
                return Operation.PLUS;
            case MINUS:
                return Operation.MINUS;
            case TIMES:
                return Operation.TIMES;
            case DIVIDE:
                return Operation.DIVIDE;
            default:
                throw new AssertionError("알 수 없는 연산: " + operation);
        }
    }
}
```

- 추가하려는 메서드가 의미상 열거 타입에 속하지 않는다면 직접 만든 열거 타입이라도 이 방식을 적용하는 게 좋다.
- 종종 쓰이지만 열거 타입 안에 포함할 만큼 유용하지는 않은 경우도 마찬가지다.
- 대부분의 경우 열거 타입의 성능은 정수 상수와 별반 다르지 않다. 열거 타입을 메모리에 올리는 공간과 초기화하는 시간이 들긴 하지만 체감될 정도는 아니다.

- **필요한 원소를 컴파일타임에 다 알 수 있는 상수 집합이라면 항상 열거 타입을 사용하도록 한다.**
- **열거 타입에 정의된 상수 개수가 영원히 고정 불변일 필요는 없다.**
    - 열거 타입은 나중에 상수가 추가되어도 바이너리 수준에서 호환되도록 설계되었다.

## 핵심 정리

- 열거 타입은 확실히 정수 상수보다 뛰어나고, 더 읽기 쉽고 안전하고 강력하다.
- 대다수 열거 타입은 명시적 생성자나 메서드 없이 쓰이지만, 각 상수를 특정 데이터와 연결짓거나 상수마다 다르게 동작하게 할 때는 필요하다.
- 만약 하나의 메서드가 상수별로 다르게 동작해야할 경우, 이런 열거 타입에서는 switch 문 대신 상수별 메서드 구현을 사용하도록 한다.
- 열거 타입 상수 일부가 같은 동작을 공유한다면 전략 열거 타입 패턴을 사용하도록 한다.
