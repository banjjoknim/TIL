# Chapter3. 람다 표현식

## 3.1 람다란 무엇인가?

동작 파리미터화를 이용해서 변화하는 요구사항에 효과적으로 대응하는 코드를 구현할 수 있고, 
정의한 코드 블록을 다른 메서드로 전달할 수 있다. 
정의한 코드 블록을 특정 이벤트(예를 들면 마우스 클릭)가 발생할 때 실행되도록 설정하거나 
알고리즘의 일부(`150g 이상의 사과`와 같은 프레디케이트)로 실행되도록 설정할 수 있다.
따라서 동작 파라미터화를 이용하면 더 유연하고 재사용할 수 있는 코드를 만들 수 있다.

*람다 표현식은 메서드로 전달할 수 있는 익명 함수를 단순화한 것이라고 할 수 있다.*

- 익명 : 보통의 메서드와 달리 이름이 없으므로 **익명**이라 표현한다. 구현해야 할 코드에 대한 걱정거리가 줄어든다.
- 함수 : 람다는 메서드처럼 특정 클래스에 종속되지 않으므로 함수라고 부른다. 하지만 메서드처럼 파라미터 리스트, 바디, 반환 형식, 가능한 예외리스트를 포함한다.
- 전달 : 람다 표현식을 메서드 인수로 전달하거나 변수로 저장할 수 있다.
- 간결성 : 익명 클래스처럼 많은 자질구레한 코드를 구현할 필요가 없다.

```java
(Apple a1, Apple a2) -> a1.getWeight().compareTo(a2.getWeight());
```

- 파라미터 리스트 : Comparator의 compare 메서드 파라미터(사과 두 개)
- 화살표 : 화살표(->)는 람다의 파라미터 리스트와 바디를 구분한다.
- 람다 바디 : 두 사과의 무게를 비교한다. 람다의 반환값에 해당하는 표현식이다.

---

## 3.2 어디에, 어떻게 람다를 사용하는가?

```java
List<Apple> greenApples = filter(inventory, (Apple a) -> GREEN.equals(a.getColor()));
```
위 예제는 함수형 인터페이스 Predicate<T>를 기대하는 `filter` 메서드의 두 번째 인수로 람다 표현식을 전달했다.

```java
public interface Predicate<T> {
    int compare(T o1, T o2);
}
```
간단히 말해 함수형 인터페이스는 정확히 하나의 추상 메서드를 지정하는 인터페이스다.

람다 표현식으로 함수형 인터페이스의 추상 메서드 구현을 직접 전달할 수 있으므로 **전체 표현식을 함수형 인터페이스의 인스턴스로 취급**(기술적으로 따지면 함수형 인터페이스를 **구현한** 클래스의 인스턴스)할 수 있다.

함수형 인터페이스의 추상 메서드 시그니처는 람다 표현식의 시그니처를 가리킨다.
람다 표현식의 시그니처를 서술하는 메서드를 **함수 디스크립터**라고 부른다. 예를 들어 **Runnable** 인터페이스의 유일한 추상 메서드 **run**은 인수와 반환값이 없으므로(void 반환) Runnable 인터페이스는 인수와 반환값이 없는 시그니처로 생각할 수 있다.

>@FunctionalInterface는 무엇인가?
>
>함수형 인터페이스임을 가리키는 어노테이션이다.
>@FunctionalInterface로 인터페이스를 선언했지만 실제로 함수형 인터페이스가 >아니면 컴파일러가 에러를 발생시킨다.

---

## 3.3 람다활용 : 실행 어라운드 패턴

실제 자원을 처리하는 코드를 설정과 정리 두 과정이 둘러싸는 형태를 **실행 어라운드 패턴**이라고 부른다.

>| 초기화/준비 코드 | 작업 A | 정리/마무리 코드 |
>| 초기화/준비 코드 | 작업 B | 정리/마무리 코드 |

### 3.3.1 1단계 : 동작 파리미터화를 기억하라

람다를 이용해서 동작을 전달할 수 있다. processFile 메서드가 한 번에 두 행을 읽게 하려면 코드를 어떻게 고쳐야 할까? 우선 BufferedReader를 인수로 받아서 String을 반환하는 람다가 필요하다. 다음은 BufferedReader에서 두 행을 출력하는 코드다.

```java
String result = processFile((BufferedReader br) -> br.readLine() + br.readLine());
```
### 3.3.2 2단계 : 함수형 인터페이스를 이용해서 동작 전달

함수형 인터페이스 자리에 람다를 사용할 수 있다. 따라서 `BufferedReader -> String`과 `IOException`을 던질 수 있는 시그니처와 일치하는 함수형 인터페이스를 만들어야 한다. 이 인터페이스를 `BufferedReaderProcessor`라고 정의하자.

```java
@FunctionalInterface
public interface BufferedReaderProcessor {
    String process(BufferedReader b) throws IOException;
}
```

정의한 인터페이스를 `processFile` 메서드의 인수로 전달할 수 있다.

```java
public String processFile(BufferedReaderProcessor p) throws IOException {
    ...
}
```

#### 3.3.3 3단계 : 동작 실행

이제 `BufferedReaderProcessor`에 정의된 `process` 메서드의 시그니처 `(BufferedReader -> String)`와 일치하는 람다를 전달할 수 있다. 람다 표현식으로 함수형 인터페이스의 추상 메서드 구현을 직접 전달할 수 있으며 전달된 코드는 함수형 인터페이스의 인스턴스로 전달된 코드와 같은 방식으로 처리한다. 따라서 `processFile` 바디 내에서 `BufferedReaderProcessor` 객체의 `process`를 호출할 수 있다.

```java
public String processFile(BufferedReaderProcessor p) throws IOException {
    try (BufferedReader br = new BufferedReader(new FileReader("data.txt"))) {
        return p.process(br);
    }
}
```

#### 3.3.4 4단계 : 람다 전달

이제 람다를 이용해서 다양한 동작을 `processFile` 메서드로 전달할 수 있다.

```java
String oneLine = processFile((BufferedReader br) -> br.readLine());
String twoLine = processFile((BufferedReader br) -> br.readLine() + br.readLine());
```
---

## 3.4 함수형 인터페이스 사용

#### 3.4.1 Predicate

`java.util.function.Predicate<T>` 인터페이스는 `test`라는 추상 메서드를 정의하며 `test`는 제네릭 형식 `T`의 객체를 인수로 받아 불리언을 반환한다.

```java
@FunctionalInterface
public interface Predicate<T> {
    boolean test(T t);
}
```

#### 3.4.2 Consumer
`java.util.function.Consumer<T>` 인터페이스는 제네릭 형식 T 객체를 받아서 `void`를 반환하는 `accept`라는 추상 메서드를 정의한다.

```java
@FunctionalInterface
public interface Consumer<T> {
    void accept(T t);
}
```

#### 3.4.3 Function
`java.util.function.Function<T, R>` 인터페이스는 제네릭 형식 T 객체를 받아서 제네릭 형식 `R` 객체를 반환하는 추상 메서드 `apply`를 정의한다.

```java
@FunctionalInterface
public interface Function<T, R> {
    R apply(T t);
}
```

>**기본형 특화**(특화된 형식의 함수형 인터페이스)
>
>- 기본형을 참조형으로 변환하는 기능 -> **박싱**
>- 참조형을 기본형으로 변환하는 기능 -> **언박싱**

예를 들어 아래 예제에서 `IntPredicate`는 1000이라는 값을 박싱하지 않지만, `Predicate<Integer>`는 1000이라는 값을 `Integer` 객체로 박싱한다.
```java
public interface IntPredicate {
    boolean test(int t);
}

IntPredicate evenNumbers = (int i) -> i % 2 == 0;
evenNumbers.test(1000);

Predicate<Integer> oddNumbers = (Integer i) -> i % 2 != 0;
oddNumbers.test(1000);
```

일반적으로 특정 형식을 입력으로 받는 함수형 인터페이스의 이름 앞에는 `DoublePredicate, IntConsumer, LongBinaryFunction` 등의 다양한 출력 형식 파라미터를 제공한다.

---

## 3.5 형식 검사, 형식 추론, 제약

#### 3.5.1 형식 검사 
람다가 사용되는 콘텍스트를 이용해서 람다의 형식을 추론할 수 있다. 어떤 콘텍스트(예를 들면 람다가 전달될 메서드 파라미터나 람다가 할당되는 변수 등)에서 기대되는 람다 표현식의 형식을 `대상 형식`이라고 부른다.

```java
List<Apple> heavierThan150g = 
filter(inventory, (Apple apple) -> apple.getWeight() > 150);
```

다음과 같은 순서로 형식 확인 과정이 진행된다.
1. `filter` 메서드의 선언을 확인한다.
2. `filter` 메서드는 두 번째 파라미터로 `Predicate<Apple> 형식(대상 형식)을 기대한다.
3. `Predicate<Apple>`은 `test`라는 한 개의 추상 메서드를 정의하는 함수형 인터페이스다.
4. `test` 메서드는 `Apple`을 받아 `boolean`을 반환하는 함수 디스크립터를 묘사한다.
5. `filter` 메서드로 전달된 인수는 이와 같은 요구사항을 만족해야 한다.

람다 표현식이 예외를 던질 수 있다면 추상 메서드도 같은 예외를 던질 수 있도록 `throws`로 선언해야 한다.

#### 3.5.2 같은 람다, 다른 함수형 인터페이스

`대상 형식`이라는 특징 때문에 같은 람다 표현식이더라도 호환되는 추상 메서드를 가진 다른 함수형 인터페이스로 사용될 수 있다. 할당문 콘텍스트, 메서드 호출 콘텍스트(파라미터, 반환값), 형변환 콘텍스트 등으로 람다 표현식의 형식을 추론할 수 있다.

```java
@FunctionalInterface
    public interface Action {
        void act();
    }

    // Runnable과 Action의 함수 디스크립터가 같으므로 누구를 가리키는지가 명확하지 않다. 
    // 게다가 Object는 함수형 인터페이스가 아니다. 
    // 따라서 컴파일이 불가능하다.
    Object o1 = () -> System.out.println("Tricky example");

    // 아래와 같이 () -> void 형식의 함수 디스크립터를 갖는 Runnable로 캐스팅해서 대상 형식을 바꾸는 것으로 문제를 해결할 수도 있다.
    Runnable r = () -> {
        System.out.println("Tricky example");
    };

    Object o2 = (Runnable) () -> {
        System.out.println("Tricky example");
    };
    Object o3 = (Action) () -> {
        System.out.println("Tricky example");
    };
    // cast를 통해서 어떤 함수형 인터페이스의 메서드 시그니처가 사용되어야 하는지 명시적으로 구분하도록 람다를 캐스팅할 수 있다. 
```

#### 3.5.3 형식 추론

자바 컴파일러는 람다 표현식이 사용된 콘텍스트(대상 형식)를 이용해서 람다 표현식과 관련된 함수형 인터페이스를 추론한다. 즉, 대상 형식을 이용해서 함수 디스크립터를 알 수 있으므로 컴파일러는 람다의 시그니처도 추론할 수 있다. 결과적으로 컴파일러는 람다 표현식의 파라미터 형식에 접근할 수 있으므로 람다 문법에서 이를 생략할 수 있다.

```java
// 파라미터 apple에 형식을 명시적으로 지정하지 않았다.
List<Apple> greenApples = filter(inventory, apple -> GREEN.equals(apple.getColor()));
```

#### 3.5.4 지역 변수 사용

람다 표현식에서는 익명 함수가 하는 것처럼 **자유 변수**(파라미터로 넘겨진 변수가 아닌 외부에서 정의된 변수)를 활용할 수 있다. 이와 같은 동작을 **람다 캡처링**이라고 부른다. 다음은 `portNumber` 변수를 캡처하는 람다 예제다.

```java
int portNumber = 1337;
Runnable r = () -> System.out.println(portNumber);
```

하지만 자유 변수에도 약간의 제약이 있다. 람다는 인스턴스 변수와 정적 변수를 자유롭게 캡처(자신의 바디에서 참조할 수 있도록)할 수 있다. 하지만 그러려면 지역 변수는 명시적으로 `final`로 선언되어 있어야 하거나 실질적으로 `final`로 선언된 변수와 똑같이 사용되어야 한다. 즉, 람다 표현식은 한 번만 할당할 수 있는 지역 변수를 캡처할 수 있다(참고 : 인스턴스 변수 캡처는 `final` 지역 변수 `this`를 캡처하는 것과 마찬가지다). 예를 들어 다음 예제는 `portNumber`에 값을 두 번 할당하므로 컴파일할 수 없는 코드다.

```java
int portNumber = 1337;
Runnable r = () -> System.out.println(portNumber);
portNumber = 31337;
```

>**지역 변수의 제약**
>
>우선 내부적으로 인스턴스 변수와 지역 변수는 태생부터 다르다. 인스턴스 변수는 힙에 저장되는 반면 지역 변수는 스택에 위치한다. 람다에서 지역 변수에 바로 접근할 수 있다는 가정하에 람다가 스레드에서 실행된다면 변수를 할당한 스레드가 사라져서 변수 할당이 해제되었는데도 람다를 실행하는 스레드에서는 해당 변수에 접근하려 할 수 있다. 따라서 자바 구현에서는 원래 변수에 접근을 허용하는 것이 아니라 자유 지역 변수의 복사본을 제공한다. 따라서 복사본의 값이 바뀌지 않아야 하므로 지역 변수에는 한 번만 값을 할당해야 한다는 제약이 생긴 것이다.
>
>또한 지역 변수의 제약 때문에 외부 변수를 변화시키는 일반적인 명령형 프로그래밍 패턴(병렬화를 방해하는 요소)에 제동을 걸 수 있다.

---

## 메서드 참조

#### 3.6.1 요약

메서드 참조를 이용하면 기존의 메서드 정의를 재활용해서 람다처럼 전달할 수 있다. 때로는 람다 표현식보다 메서드 참조를 사용하는 것이 더 가독성이 좋으며 자연스러울 수 있다.

다음은 기존 코드다.

```java
inventory.sort((Apple a1, Apple a2) -> a1.getWeight().compareTo(a2.getWeight()));
```

다음은 메서드 참조와 `java.util.Comparator.comparing`을 활용한 코드다.

```java
inventory.sort(comparing(Apple::getWeight));
```

이처럼 명시적으로 메서드명을 참조함으로써 가독성을 높일 수 있다.


```java
// 람다와 메서드 참조 단축 표현 예제
(Apple apple) -> apple.getWeight() ==> Apple::getWeight()
() -> Thread.currentThread().dumpStack() ==> Thread.currentThread()::dumpStack
(str, i) -> str.substring(i) ==> String::substring
(String s) -> System.out.println(s) ==> System.out::println
(String s) -> this.isValidName(s) ==> this::isValidName
```

**메서드 참조를 만드는 방법**
1. **정적 메서드 참조** : 예를 들어 `Integer`의 `parseInt` 메서드는 `Integer::parseInt`로 표현할 수 있다.
2. **다양한 형식의 인스턴스 메서드 참조** : 예를 들어 `String`의 `length` 메서드는 `String::length`로 표현할 수 있다.
3. **기존 객체의 인스턴스 메서드 참조** : 예를 들어 `Transaction` 객체를 할당 받은 `expensiveTransaction` 지역 변수가 있고, `Transaction` 객체에는 `getValue` 메서드가 있다면, 이를 `expensiveTransaction::getValue`라고 표현할 수 있다.

컴파일러는 람다 표현식의 형식을 검사하던 방식과 비슷한 과정으로 메서드 참조가 주어진 함수형 인터페이스와 호환하는지 확인한다. 즉, 메서드 참조는 콘텍스트의 형식과 일치해야 한다.

```java
ToIntFunction<String> stringToInt = (String s) -> Integer.parseInt(s);
ToIntFunction<String> stringToIntAnswer = Integer::parseInt;

BiPredicate<List<String>, String> contains = (list, element) -> list.contains(element);
BiPredicate<List<String>, String> containsAnswer = List::contains;

Predicate<String> startsWithNumber = (String string) -> this.startsWithNumber(string);
Predicate<String> startsWithNumberAnswer = this::startsWithNumber;
```

#### 3.6.2 생성자 참조

`ClassName::new`처럼 클래스명과 `new` 키워드를 이용해서 기존 생성자의 참조를 만들 수 있다. 이것은 정적 메서드의 참조를 만드는 방법과 비슷하다.

```java
Supplier<Apple> c1 = Apple::new;
Apple a1 = c1.get();

Function<Integer, Apple> c2 = Apple::new;
Apple a2 = c2.apply(110);

BiFunction<Integer, String, Apple> c3 = Apple::new;
Apple a3 = c3.apply(110, GREEN);
```

만약 `Color(int, int, int)`처럼 인수가 세 개인 생성자의 생성자 참조를 사용하려면 어떻게 해야 할까?

```java
// 생성자 참조를 사용하려면 생성자 참조와 일치하는 시그니처를 갖는 함수형 인터페이스가 필요하다.
// 현재 이런 시그니처를 갖는 함수형 인터페이스는 제공되지 않는다.
// 따라서 직접 다음과 같은 함수형 인터페이스를 만들어야 한다.
public interface TriFunction<T, U, V, R> {
        R apply(T t, U u, V v);
    }

TriFunction<Integer, Integer, Integer, Color> colorFactory = Color::new;
```

---

## 3.7 람다, 메서드 참조 활용하기

#### 3.7.1 1단계 : 코드 전달

```java
void sort(Comparator<? super E> c)

public class AppleComparator implements Comparator<Apple> {
    public int compare(Apple a1, Apple a2){
        return a1.getWeight().compareTo(a2.getWeight());
    }
}
inventory.sort(new AppleComparator());
```

#### 3.7.2 2단계 : 익명 클래스 사용
한 번만 사용할 `Comparator`를 위 코드처럼 구현하는 것보다는 `익명 클래스`를 이용하는 것이 좋다.

```java
inventory.sort(new Comparator<Apple>() {
    public int compare(Apple a1, Apple a2){
        return a2.getWeight().compareTo(a2.getWeight());
    }
});
```

#### 3.7.3 3단계 : 람다 표현식 사용

추상 메서드의 시그니처(**함수 디스크립터**라 불림)는 람다 표현식의 시그니처를 정의한다. `Comparator`의 함수 디스크립터는 `(T, T) -> int`다. 이제 다음처럼 코드를 개선할 수 있다.

```java
inventory.sort((Apple a1, Apple a2) -> a1.getWeight().compareTo(a2.getWeight()));
```

자바 컴파일러는 람다 표현식이 사용된 콘텍스트를 활용해서 람다의 파라미터 형식을 추론한다고 설명했다. 따라서 코드를 다음처럼 더 줄일 수 있다.

```java
inventory.sort((a1, a2) -> a1.getWeight().compareTo(a2.getWeight()));
```

`Comparator`는 `Comparable` 키를 추출해서 `Comparator` 객체로 만드는 `Function` 함수를 인수로 받는 정적 메서드 `comparing`을 포함한다.
다음처럼 `comparing` 메서드를 사용할 수 있다(람다 표현식은 사과를 비교하는 데 사용할 키를 어떻게 추출할 것인지 지정ㅎ라는 한 개의 인수만 포함한다).

```java
Comparator<Apple> c = Comparator.comparing((Apple a) -> a.getWeight());
```

이제 코드를 다음처럼 간소화할 수 있다.

```java
import static java.util.Comparator.comparing;
inventory.sort(comparing(apple -> apple.getWeight()));
```

#### 3.7.4 4단계 : 메서드 참조 사용

메서드 참조를 이용하면 람다 표현식의 인수를 더 깔끔하게 전달할 수 있다.

```java
inventory.sort(comparing(Apple::getWeight()));
```

## 3.8 람다 표현식을 조합할 수 있는 유용한 메서드

자바 8 API의 몇몇 함수형 인터페이스는 다양한 유틸리티 메서드를 포함한다. 예를 들어 `Comparator, Function, Predicate` 같은 함수형 인터페이스는 람다 표현식을 조합할 수 있도록 유틸리티 메서드를 제공한다.

#### 3.8.1 Comparator 조합

정적 메서드 `Comparator.comparing`을 이용해서 비교에 사용할 키를 추출하는 `Function` 기반의 `Comparator`를 반환할 수 있다.

```java
// 기존 코드
inventory.sort(Comparator.comparing(Apple::getWeight));

// 무게를 내림차순으로 정렬
inventory.sort(Comparator.comparing(Apple::getWeight).reversed()); 

// 무게를 내림차순으로 정렬, 두 사과의 무게가 같으면 색깔별로 정렬
inventory.sort(Comparator.comparing(Apple::getWeight) // 
         .reversed()
         .thenComparing(Apple::getColor));
```

#### 3.8.2 Predicate 조합

`Predicate` 인터페이스는 복잡한 프레디케이트를 만들 수 있도록 `negate, and, or` 세 가지 메서드를 제공한다.

```java
// 빨간색인 사과
Predicate<Apple> isRedApple = apple -> RED.equals(apple.getColor());

// 빨간색이 아닌 사과
Predicate<Apple> notRedApple = isRedApple.negate(); // Predicate 반전. ~~가 아닌 것.

// 빨간색이고 무거운 사과
Predicate<Apple> redAndHeavyApple = isRedApple.and(apple -> apple.getWeight() > 150);

// 빨간색이고 무겁거나 초록색인 사과
Predicate<Apple> redAndHeavyAppleOrGreen = 
isRedApple.and(apple -> apple.getWeight() > 150)
.or(apple -> GREEN.equals(apple.getColor()));
// a.or(b).and(c) 는 (a || b) && c 와 같다.
```

#### 3.8.3 Function 조합

`Function` 인터페이스에서 제공하는 람다 표현식도 조합할 수 있다. `Function` 인터페이스는 `Function` 인스턴스를 반환하는 `andThen, compose` 두 가지 디폴트 메서드를 제공한다.

```java
Function<Integer, Integer> f = x -> x + 1;
Function<Integer, Integer> g = g -> g * 2;

// f연산 후 그 값으로 g연산을 한다.
Function<Integer, Integer> h = f.andThen(g); 

// g연산 후 그 값으로 f연산을 한다.
Function<Integer, Integer> h2 = f.compose(g); 

int result = h.apply(1); // 4
int result2 = h2.apply(1); // 3

public static class Letter {
    public static String addHeader(String text) {
        return "From Raoul, Mario and Alan: " + text;
    }

    public static String addFooter(String text) {
        return text + " Kind regards";
    }

    public static String checkSpelling(String text) {
        return text.replaceAll("labda", "lambda");
    }
}

Function<String, String> addHeader = Letter::addHeader;
Function<String, String> transformationPipeline = addHeader.andThen(Letter::checkSpelling).andThen(Letter::addFooter);
Function<String, String> transformationPipeline2 = addHeader.andThen(Letter::addFooter);
```