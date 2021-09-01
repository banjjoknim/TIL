# 아이템 45. 스트림은 주의해서 사용하라

- 스트림 API는 다량의 데이터 처리 작업(순차적이든 병렬적이든)을 위해 자바 8에 추가되었다.
- 스트림 API가 제공하는 추상 개념 중 핵심은 두 가지다.
    - 첫 번째. 스트림(stream)은 데이터 원소의 유한 혹은 무한 시퀀스(sequence)를 뜻한다.
    - 두 번째. 스트림 파이프라인(stream pipeline)은 이 원소들로 수행하는 연산 단계를 표현하는 개념이다.
    - 스트림 안의 데이터 원소들은 객체 참조나 기본 타입 값이다.
    - 기본 타입 값으로는 int, long, double 이렇게 세 가지를 지원한다.

## 스트림 파이프라인

- 스트림 파이프라인은 소스 스트림에서 시작해 종단 연산(terminal operation)으로 끝난다.
- 스트림의 시작과 종단 연산의 사이에 하나 이상의 중간 연산(intermediate operation)이 있을 수 있다.
    - 각 중간 연산은 스트림을 어떠한 방식으로 변환(transform)한다.
- 스트림 파이프라인은 지연 평가(lazy evaluation)된다.
- 평가는 종단 연산이 호출될 때 이뤄지며, 종단 연산에 쓰이지 않는 데이터 원소는 계산에 쓰이지 않는다.
- 종단 연산이 없는 스트림 파이프라인은 아무 일도 하지 않는 명령어인 no-op과 같으므로, 종단 연산은 필수다.
- 스트림 API는 메서드 연쇄를 지원하는 플루언트 API(fluent API)다.
    - 파이프라인 하나를 구성하는 모든 호출을 연결하여 단 하나의 표현식으로 완성할 수 있으며, 파이프라인 여러 개를 연결해 표현식 하나로 만들 수도 있다.
- 기본적으로 스트림 파이프라인은 순차적으로 수행된다.
    - 파이프라인을 병렬로 실행하려면 파이프라인을 구성하는 스트림 중 하나에서 parallel 메서드를 호출해주기만 하면 된다. 다만, 효과를 볼 수 있는 상황은 많지 않다.

### 사전을 하나를 훑어 원소 수가 많은 아나그램 그룹들을 출력한다.

```java
public class Anagrams {
    public static void main(String[] args) {
        Path dictionary = Paths.get(args[0]);
        int minGroupSize = Integer.parseInt(args[1]);

        Map<String, Set<String>> groups = new HashMap<>();
        try (Scanner scanner = new Scanner(dictionary)) {
            while (scanner.hasNext()) {
                String word = scanner.next();
                groups.computeIfAbsent(alphabetize(word), (unused) -> new TreeSet<>()).add(word);
            }
        }

        for (Set<String> group : groups.values()) {
            if (group.size() >= minGroupSize) {
                System.out.println(group.size() + ": " + group);
            }
        }
    }

    private static String alphabetize(String word) {
        char[] a = word.toCharArray();
        Arrays.sort(a);
        return new String(a);
    }
}
```

- 자바 8에서 추가된 computeIfAbsent 메서드가 사용되었다.
- 이 메서드는 맵 안에 키가 있는지 찾은 다음, 있으면 단순히 그 키에 매핑된 값을 반환한다.
- 만약 키가 없으면 건네진 함수 객체를 키에 적용하여 값을 계산하고, 그 키와 값을 매핑한 뒤 계산된 값을 반환한다.

### 스트림을 과하게 사용했다. - 따라 하지 말 것!

```java
public class Anagrams {
    public static void main(String[] args) throws IOException {
        Path dictionary = Paths.get(args[0]);
        int minGroupSize = Integer.parseInt(args[1]);

        // 스트림을 과하게 사용했다. - 따라 하지 말 것!
        try (Stream<String> words = Files.lines(dictionary)) {
            words.collect(groupingBy(word -> word.chars().sorted()
                            .collect(StringBuilder::new, (sb, c) -> sb.append((char) c), StringBuilder::append).toString()))
                    .values().stream()
                    .filter(group -> group.size() >= minGroupSize)
                    .map(group -> group.size() + ": " + group)
                    .forEach(System.out::println);
        }
    }

    private static String alphabetize(String word) {
        char[] a = word.toCharArray();
        Arrays.sort(a);
        return new String(a);
    }
}
```

- **스트림을 과용하면 프로그램이 읽거나 유지보수하기 어려워진다.**

### 스트림을 적절히 사용하면 깔끔하고 명료해진다.

```java
public class Anagrams {
    public static void main(String[] args) throws IOException {
        Path dictionary = Paths.get(args[0]);
        int minGroupSize = Integer.parseInt(args[1]);

        // 스트림을 적절히 사용하면 깔끔하고 명료해진다.
        try (Stream<String> words = Files.lines(dictionary)) {
            words.collect(groupingBy(word -> alphabetize(word)))
                    .values().stream()
                    .filter(group -> group.size() >= minGroupSize)
                    .forEach(g -> System.out.println(g.size() + ": " + g));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String alphabetize(String word) {
        char[] a = word.toCharArray();
        Arrays.sort(a);
        return new String(a);
    }
}
```

- 람다 매개변수의 이름은 주의해서 정해야 한다.
- **람다에서는 타입 이름을 자주 생략하므로 매개변수 이름을 잘 지어야 스트림 파이프라인의 가독성이 유지된다.**
- 도우미 메서드를 적절히 활용하는 일의 중요성은 일반 반복 코드에서보다 스트림 파이프라인에서 훨씬 크다. 파이프라인에서는 타입 정보가 명시되지 않거나 임시 변수를 자주 사용하기 때문이다.
- 자바는 기본 타입인 char용 스트림을 지원하지 않는다.
    - char은 int 값을 갖기 때문이고, 그 덕에 int 스트림을 반환하면 헷갈릴 수 있다. 올바르게 동작하게 하려면 명시적으로 형변환을 해줘야 한다.
    - 따라서 **char 값들을 처리할 때는 스트림을 삼가는 편이 낫다.**
- 스트림이 언제나 가독성과 유지보수 측면으로 뛰어난 것은 아니다.
    - 스트림과 반복문을 적절히 조합하는 게 최선이다.
    - 따라서 **기존 코드는 스트림을 사용하도록 리팩터링하되, 새 코드가 더 나아 보일 때만 반영하도록 한다.**

## 반복 코드 vs 스트림 파이프라인

- 스트림 파이프라인은 되풀이되는 계산을 함수 객체(주로 람다나 메서드 참조)로 표현한다.
- 반복 코드에서는 코드 블록을 사용해 표현한다.

### 함수 객체로는 할 수 없지만 코드 블록으로는 할 수 있는 일

- 코드 블록에서는 범위 안의 지역변수를 읽고 수정할 수 있다.
    - 람다에서는 final이거나 사실상 final인 변수만 읽을 수 있고, 지역변수를 수정하는 건 불가능하다.
- 코드 블록에서는 return 문을 사용해 메서드에서 빠져나가거나, break나 continue 문으로 블록 바깥의 반복문을 종료하거나 반복을 한 번 건너뛸 수 있다.
- 또한, 메서드 선언에 명시된 검사 예외를 던질 수 있다.
    - 람다로는 이 중 어떤 것도 할 수 없다.

계산 로직에서 이상의 일들을 수행해야 한다면 스트림과는 맞지 않는 것이다.

### 스트림이 안성맞춤인 일

- 원소들의 시퀀스를 일관되게 변환한다.
- 원소들의 시퀀스를 필터링한다.
- 원소들의 시퀀스를 하나의 연산을 사용해 결합한다(더하기, 연결하기, 최솟값 구하기 등).
- 원소들의 시퀀스를 컬렉션에 모은다(아마도 공통된 속성을 기준으로 묶어가며).
- 원소들의 시퀀스에서 특정 조건을 만족하는 원소를 찾는다.

### 스트림으로 처리하기 어려운 일

- 한 데이터가 파이프라인의 여러 단계(stage)를 통과할 때 이 데이터의 각 단계에서의 값들에 동시에 접근하는 것은 처리하기 어렵다.
    - 스트림 파이프라인은 일단 한 값을 다른 값에 매핑하고 나면 원래의 값은 잃는 구조이기 때문이다.

### 스트림과 반복 중 어느 쪽을 써야 할까?

#### 데카르트 곱 계산을 반복 방식으로 구현

```java
public class Item45 {
    private static List<Card> newDeck() {
        List<Card> result = new ArrayList<>();
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                result.add(new Card(suit, rank));
            }
        }
        return result;
    }
}
```

#### 데카르트 곱 계산을 스트림 방식으로 구현

```java
import java.util.stream.Stream;

public class Item45 {
    private static List<Card> newDeck() {
        return Stream.of(Suit.values())
                .flatMap(suit -> Stream.of(Rank.values())
                        .map(rank -> new Card(suit, rank)))
                .collect(toList());
    }
}
```

- 중간 연산으로 사용한 flatMap은 스트림의 원소 각각을 하나의 스트림으로 매핑한 다음 그 스트림들을 다시 하나의 스트림으로 합친다.
- 이러한 작업을 평탄화(flattening)라고도 한다.

## 핵심 정리

- 스트림과 반복 방식은 각각에 알맞은 일이 있다.
- 수 많은 작업은 이 둘을 조합했을 때 가장 멋지게 해결된다.
- **만약 스트림과 반복 중 어느 쪽이 나은지 확신하기 어렵다면 둘 다 해보고 더 나은 쪽을 선택하도록 한다.**