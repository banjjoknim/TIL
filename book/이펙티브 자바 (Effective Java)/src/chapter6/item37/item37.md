# 아이템 37. ordinal 인덱싱 대신 EnumMap을 사용하라

이따금 배열이나 리스트에서 원소를 꺼낼 때 ordinal 메서드로 인덱스를 얻는 코드가 있다.

## ordinal()을 배열 인덱스로 사용 - 따라 하지 말 것!

```java
public class Plant {
    enum LifeCycle {ANNUAL, PERENNIAL, BIENNIAL}

    final String name;
    final LifeCycle lifeCycle;

    public Plant(String name, LifeCycle lifeCycle) {
        this.name = name;
        this.lifeCycle = lifeCycle;
    }

    @Override
    public String toString() {
        return name;
    }

    public static void main(String[] args) {
        Set<Plant>[] plantsByLifeCycle = (Set<Plant>[]) new Set[LifeCycle.values().length];
        for (int i = 0; i < plantsByLifeCycle.length; i++) {
            plantsByLifeCycle[i] = new HashSet<>();
        }
        List<Plant> garden = new ArrayList<>(); // 편의상 빈 리스트로 초기화 했다.
        for (Plant plant : garden) {
            plantsByLifeCycle[plant.lifeCycle.ordinal()].add(plant);
        }

        // 결과 출력
        for (int i = 0; i < plantsByLifeCycle.length; i++) {
            System.out.printf("%s : %s%n", Plant.LifeCycle.values()[i], plantsByLifeCycle[i]);
        }
    }
}
```

- 동작은 하지만 문제가 많다.
    - 배열은 제네릭과 호환되지 않으니 비검사 형변환을 수행해야 하고 깔끔히 컴파일되지 않을 것이다.
    - 배열은 각 인덱스의 의미를 모르니 출력 결과에 직접 레이블을 달아야 한다.
    - 정확한 정숫값을 사용한다는 것을 우리가 직접 보증해야 한다는 것이 가장 큰 문제다. 정수는 열거 타입과 달리 타입 안전하지 않기 때문이다.
        - 잘못된 값을 사용하면 잘못된 동작을 묵묵히 수행하거나, 운이 좋다면 ArrayIndexOutOfBoundsException을 던질 것이다.

## EnumMap을 사용해 데이터와 열거 타입을 매핑한다.

- 위 예제에서 배열은 실질적으로 열거 타입 상수를 값으로 매핑하는 역할을 한다. 따라서 Map을 사용할 수도 있을 것이다.
- EnumMap은 열거 타입을 키로 사용하도록 설계한 아주 빠른 Map 구현체이다. 아래는 EnumMap을 사용한 예시다.

```java
public class Plant {

    // ...

    public static void main(String[] args) {
        Map<Plant.LifeCycle, Set<Plant>> plantsByLifeCycle = new EnumMap<>(Plant.LifeCycle.class);
        for (Plant.LifeCycle lifeCycle : Plant.LifeCycle.values()) {
            plantsByLifeCycle.put(lifeCycle, new HashSet<>());
        }
        List<Plant> garden = new ArrayList<>(); // 편의상 빈 리스트로 초기화 했다.
        for (Plant plant : garden) {
            plantsByLifeCycle.get(plant.lifeCycle).add(plant);
        }
        System.out.println(plantsByLifeCycle);
    }
}
```

- 더 짧고 명료하고 안전하고 성능도 원래 버전과 비등하다.
- 안전하지 않은 형변환은 쓰지 않고, 맵의 키인 열거 타입이 그 자체로 출력용 문자열을 제공하기 때문에 출력 결과에 직접 레이블을 달 일도 없다.
- 배열 인덱스를 계산하는 과정에서 오류가 날 가능성도 원천봉쇄된다.
- EnumMap의 성능이 ordinal을 쓴 배열에 비견되는 이유는 그 내부에서 배열을 사용하기 때문이다.
    - 내부 구현 방식을 안으로 숨겨서 Map의 타입 안전성과 배열의 성능을 모두 얻었다.
    - EnumMap의 생성자가 받는 키 타입의 Class 객체는 한정적 타입 토큰으로, 런타임 제네릭 타입 정보를 제공한다.

```java
public class EnumMap<K extends Enum<K>, V> extends AbstractMap<K, V> implements java.io.Serializable, Cloneable {
    // ...
    public EnumMap(EnumMap<K, ? extends V> m) { // EnumMap의 생성자
        keyType = m.keyType;
        keyUniverse = m.keyUniverse;
        vals = m.vals.clone();
        size = m.size;
    }
}
```

### 스트림을 사용한 코드 1 - EnumMap을 사용하지 않는다!

```java
public class Plant {

    // ...

    public static void main(String[] args) {
        List<Plant> garden = new ArrayList<>(); // 편의상 빈 리스트로 초기화 했다.
        System.out.println(garden.stream().collect(groupingBy(plant -> plant.lifeCycle)));
    }
}
```

- 위 코드는 EnumMap이 아닌 고유한 맵 구현체를 사용했기 때문에 EnumMap을 써서 얻은 공간과 성능 이점이 사라진다.

### 스트림을 사용한 코드 2 - EnumMap을 이용해 데이터와 열거 타입을 매핑한다.

```java
public class Plant {

    // ...

    public static void main(String[] args) {
        List<Plant> garden = new ArrayList<>(); // 편의상 빈 리스트로 초기화 했다.
        System.out.println(garden.stream().collect(
                groupingBy(plant -> plant.lifeCycle,
                        () -> new EnumMap<>(LifeCycle.class), toSet())));
    }
}
```

- 매개변수 3개짜리 Collectors.groupingBy 메서드는 mapFactory 매개변수에 원하는 맵 구현체를 명시해 호출할 수 있다.
- 스트림을 사용하면 EnumMap만 사용했을 때와는 살짝 다르게 동작한다.
    - EnumMap 버전은 언제나 모든 키값(LifeCycle)에 대해서 하나씩의 중첩 맵을 만들지만, 스트림 버전에서는 해당 키값에 속하는 원소가 있을 때만 만든다.
        - 만약 위 예제에서 원소가 존재하는 키값이 2개 뿐이라면, EnumMap 버전에서는 Map을 3개 만들고 스트림 버전에서는 2개만 만든다.

## ordinal()은 가능한 사용하지 않도록 한다.

### 배열들의 배열(배열속의 배열)의 인덱스에 ordinal() 사용 - 따라 하지 말 것!

- 아래는 두 가지의 상태(Phase)와 상전이(Transition)를 매핑하도록 구현한 코드다.

```java
public enum Phase {
    SOLID, LIQUID, GAS;

    public enum Transition {
        MELT, FREEZE, BOIL, CONDENSE, SUBLIME, DEPOSIT;

        // 행은 from의 ordinal을, 열은 to의 ordinal을 인덱스로 쓴다.
        private static final Transition[][] TRANSITIONS = {
                {null, MELT, SUBLIME},
                {FREEZE, null, BOIL},
                {DEPOSIT, CONDENSE, null}
        };

        // 한 상태에서 다른 상태로의 전이를 반환한다.
        public static Transition from(Phase from, Phase to) {
            return TRANSITIONS[from.ordinal()][to.ordinal()];
        }
    }
}
```

- 컴파일러는 ordinal과 배열 인덱스의 관계를 알 방법이 없다.
    - 따라서 ordinal()을 인덱스로 사용한 배열은 하나하나 값을 입력해주어야 한다.
    - 즉, ordinal을 사용한 배열로 만든 코드를 수정할 때는 열거 타입을 수정하면서 배열 인덱스도 함께 수정하지 않거나 잘못 수정하면 런타임 오류가 날 것이다.

### 중첩 EnumMap으로 데이터와 열거 타입 쌍을 연결했다.

```java
public enum Phase {
    SOLID, LIQUID, GAS;

    public enum Transition {
        MELT(SOLID, LIQUID), FREEZE(LIQUID, SOLID),
        BOIL(LIQUID, GAS), CONDENSE(GAS, LIQUID),
        SUBLIME(SOLID, GAS), DEPOSIT(GAS, SOLID);

        private final Phase from;
        private final Phase to;

        Transition(Phase from, Phase to) {
            this.from = from;
            this.to = to;
        }

        // 상전이 맵을 초기화한다.
        private static final Map<Phase, Map<Phase, Transition>> m = Stream.of(values())
                .collect(groupingBy(transition -> transition.from,
                        () -> new EnumMap<>(Phase.class),
                        toMap(t -> t.to, t -> t, (x, y) -> y, () -> new EnumMap<>(Phase.class))));

        public static Transition from(Phase from, Phase to) {
            return m.get(from).get(to);
        }
    }
}
```

- 이 경우, EnumMap을 사용하는 것이 훨씬 낫다. 맵 2개를 중첩하면 쉽게 해결할 수 있다.
- EnumMap을 사용한다면 필요한 요소와 함께 원하는 값을 추가하는 것으로 쉽게 변경할 수 있다.
- 실제 내부에서는 맵들의 맵이 배열들의 배열로 구현되니 낭비되는 공간과 시간도 거의 없이 명확하고 안전하고 유지보수하기 좋다.

### EnumMap 버전에 새로운 상태 추가하기

```java
public enum Transition {
    MELT(SOLID, LIQUID), FREEZE(LIQUID, SOLID),
    BOIL(LIQUID, GAS), CONDENSE(GAS, LIQUID),
    SUBLIME(SOLID, GAS), DEPOSIT(GAS, SOLID),
    IONIZE(GAS, PLASMA), DEIONIZE(PLASMA, GAS); // 새롭게 추가되었다.

    // ...
}
```

- 위 코드에서는 상태 목록에 PLASMA를 추가하고, 전이 목록에 IONIZE(GAS, PLASMA)와 DEIONIZE(PLASMA, GAS)만 추가하는 것으로 수정이 끝났다.
    - 만약 ordinal()을 사용한 배열이었다면 9개의 원소를 16개짜리로 교체해야 했을 것이다.

## 핵심 정리

- **배열의 인덱스를 얻기 위해 ordinal을 쓰는 것은 일반적으로 좋지 않다. 대신 EnumMap을 사용하도록 한다.**
- 다차원 관계는 `EnumMap<..., EnumMap<...>>`으로 표현하도록 한다.
- 애플리케이션 프로그래머는 Enum.ordinal을 웬만해서는 사용하지 말아야 한다.