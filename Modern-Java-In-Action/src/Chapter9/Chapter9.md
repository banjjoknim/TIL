# Chapter9. 리팩터링, 테스팅, 디버깅

람다 표현식을 이용해 가독성과 유연성을 높이려면 기존 코드를 어떻게 리팩터링 해야하는지 설명한다. 또한 람다 표현식으로 전략, 템플릿 메서드, 옵저버, 의무 체인, 팩토리 등의 객체지향 디자인 패턴을 어떻게 간소화할 수 있는지도 살펴본다. 마지막으로 람다 표현식과 스트림 API를 사용하는 코드를 테스트하고 디버깅하는 방법을 설명한다.

---

## 9.1 가독성과 유연성을 개선하는 리팩터링
람다 표현식은 익명 클래스보다 코드를 좀 더 간결하게 만든다. 메서드 참조를 이용해서 람다보다 더 간결한 코드를 구현할 수도 있다. 그뿐만 아니라 람다 표현식은 동작 파라미터화의 형식을 지원하므로 람다 표현식을 이용한 코드는 더 큰 유연성을 갖출 수 있다. 즉, 람다 표현식을 이용한 코드는 다양한 요구사항 변화에 대응할 수 있도록 동작을 파라미터화 한다.

#### 9.1.1 코드 가독성 개선
일반적으로 코드 가독성이 좋다는 것은 '어떤 코드를 다른 사람도 쉽게 이해할 수 있음'을 의미한다. 즉, 코드 가독성을 개선한다는 것은 우리가 구현한 코드를 다른 사람이 쉽게 이해하고 유지보수할 수 있게 만드는 것을 의미한다.

자바 8의 새 기능을 이용해 코드의 가독성을 높일 수 있다. 코드를 간결하고 이해하기 쉽게 만들 수 있다. 또한 메서드 참조와 스트림 API를 이용해 코드의 의도를 명확하게 보여줄 수 있다.

람다, 메서드 참조, 스트림을 활용해서 코드 가독성을 개선할 수 있는 간단한 세 가지 리팩터링 예제를 통해 살펴보자.

- **익명 클래스를 람다 표현식으로 리팩터링하기**
- **람다 표현식을 메서드 참조로 리팩터링하기**
- **명령형 데이터 처리를 스트림으로 리팩터링하기**

#### 9.1.2 익명 클래스를 람다 표현식으로 리팩터링하기
하나의 추상 메서드를 구현하는 익명 클래스는 람다 표현식으로 리팩터링할 수 있다. 하지만 모든 익명 클래스를 람다 표현식으로 변환할 수 있는 것은 아니다. 첫째, 익명 클래스에서 사용한 `this`와 `super`는 람다 표현식에서 다른 의미를 갖는다. 익명 클래스에서 `this`는 익명클래스 자신을 가리키지만 람다에서 `this`는 람다를 감싸는 클래스를 가리킨다. 둘째, 익명 클래스는 감싸고 있는 클래스의 변수를 가릴 수 있다(섀도 변수). 하지만 다음 코드에서 보여주는 것처럼 람다 표현식으로는 변수를 가릴 수 없다(아래 코드는 컴파일되지 않는다).

```java
int a = 10;

Runnable r1 = () -> {
    int a = 2; // 컴파일 에러
    System.out.println(a);
}

Runnable r2 = new Runnable(){
    public void run() {
        int a = 2; // 모든 것이 잘 작동한다.
        System.out.println(a);
    }
}
```
마지막으로 익명 클래스를 람다 표현식으로 바꾸면 콘텍스트 오버로딩에 따른 모호함이 초래될 수 있다. 익명 클래스는 인스턴스화할 때 명시적으로 형식이 정해지는 반면 람다의 형식은 콘텍스트에 따라 달라지기 때문이다. 다음은 이와 같은 문제가 일어날 수 있음을 보여주는 예제 코드다. 아래 코드에서는 `Task`라는 `Runnable`과 같은 시그니처를 갖는 함수형 인터페이스를 선언한다(실제 업무에서는 `Task`라는 이름을 자주 사용한다).

```java
interface Task {
    public void execute();
}
public static void doSomething(Runnable r) {
    r.run();
}
public static void doSomething(Task a) {
    a.execute();
}
```
`Task`를 구현하는 익명 클래스를 전달할 수 있다.

```java
doSomething(new Task() {
    public void execute() {
        System.out.println("Danger danger!!");
    }
});
```
하지만 익명 클래스를 람다 표현식으로 바꾸면 메서드를 호출할 때 `Runnable`과 `Task` 모두 대상 형식이 될 수 있으므로 문제가 생긴다.

```java
doSomething(() -> System.out.println("Danger danger!!"));
```
즉, `doSomething(Runnable)`과 `doSomething(Task)` 중 어느 것을 가리키는지 알 수 없는 모호함이 발생한다.

명시적 형변환 `(Task)`를 이용해서 모호함을 제거할 수 있다.

```java
doSomething((Task)() -> System.out.println("Danger danger!!"));
```
또한 `넷빈즈`와 `IntelliJ` 등을 포함한 대부분의 통합 개발 환경(IDE)에서 제공하는 리팩터링 기능을 이용하면 이와 같은 문제가 자동으로 해결된다.

#### 9.1.3 람다 표현식을 메서드 참조로 리팩터링하기
람다 표현식은 쉽게 전달할 수 있는 짧은 코드다. 하지만 람다 표현식 대신 메서드 참조를 이용하면 가독성을 높일 수 있다. 메서드 참조의 메서드명으로 코드의 의도를 명확하게 알릴 수 있기 때문이다. 예를 들어 다음은 칼로리 수준으로 요리를 그룹화하는 코드다.

```java
Map<CaloricLevel, List<Dish>> dishesByCaloricLevel = 
    menu.stream().collect(
        groupingBy(dish -> {
            if (dish.getCalories() <= 400) return CaloricLevel.DIET;
            else if (dish.getCalories() <= 700) return CaloricLevel.NORMAL;
            else return CaloricLevel.FAT;
        })
    );
```
람다 표현식을 별도의 메서드로 추출한 다음에 `groupingBy`에 인수로 전달할 수 있다. 다음처럼 코드가 간결해지고 의도도 명확해진다.

```java
Map<CaloricLevel, List<Dish>> dishesByCaloricLevel = 
    menu.stream().collect(groupingBy(Dish::getCaloricLevel)); // 람다 표현식을 메서드로 추출했다.
```
이제 `Dish` 클래스에 `getCaloricLevel` 메서드를 추가해야 한다.

```java
public class Dish{
    ...
    public CaloricLevel getCaloricLevel() {
        if (this.getCalories() <= 400) return CaloricLevel.DIET;
        else if (this.getCalories() <= 700) return CaloricLevel.NORMAL;
        else return CaloricLevel.FAT;
    }
}
```
또한 `comparing`과 `maxBy` 같은 정적 헬퍼 메서드를 활용하는 것도 좋다. 이들은 메서드 참조와 조화를 이루도록 설계되었다. 람다 표현식보다는 메서드 참조가 코드의 의도를 더 명확하게 보여준다.

```java
inventory.sort( // 비교 구현에 신경써야 한다.
    (Apple a1, Apple a2) -> a1.getWeight().compareTo(a2.getWeight())); 
inventory.sort(comparing(Apple::getWeight)); // 코드가 문제 자체를 설명한다.
```
`sum`,` maximum` 등 자주 사용하는 리듀싱 연산은 메서드 참조와 함께 사용할 수 있는 내장 헬퍼 메서드를 제공한다. 예를 들어 최댓값이나 합계를 계산할 때 람다 표현식과 저수준 리듀싱 연산을 조합하는 것보다 `Collectors` API를 사용하면 코드의 의도가 더 명확해진다. 다음은 저수준 리듀싱 연산을 조합한 코드다.

```java
int totalCalories = 
    menu.stream().map(Dish::getCalories)
                 .reduce(0, (c1, c2) -> c1 + c2);
```
내장 컬렉터를 이용하면 코드 자체로 문제를 더 명확하게 설명할 수 있다. 다음 코드에서는 컬렉터 `summingInt`를 사용했다(자신이 어떤 동작을 수행하는지 메서드 이름으로 설명함).

```java
int totalCalories = 
    menu.stream().collect(summingInt(Dish::getCalories));
```

#### 9.1.4 명령형 데이터 처리를 스트림으로 리팩터링하기
이론적으로는 반복자를 이용한 기존의 모든 컬렉션 처리 코드를 스트림 API로 바꿔야 한다. 스트림 API는 데이터 처리 파이프라인의 의도를 더 명확하게 보여준다. 스트림은 쇼트서킷과 게으름이라는 강력한 최적화뿐 아니라 멀티코어 아키텍처를 활용할 수 있는 지름길을 제공한다.

예를 들어 다음 명령형 코드는 두 가지 패턴(필터링과 추출)으로 엉킨 코드다. 이 코드를 접한 프로그래머는 전체 구현을 자세히 살펴본 이후에야 전체 코드의 의도를 이해할 수 있다. 게다가 이 코드를 병렬로 실행시키는 것은 매우 어렵다.

```java
List<String> dishNames = new ArrayList<>();
for(Dish dish : menu) {
    if(dish.getCalories() > 300) {
        dishNames.add(dish.getName());
    }
}
```
스트림 API를 이용하면 문제를 더 직접적으로 기술할 수 있을 뿐 아니라 쉽게 병렬화할 수 있다.

```java
menu.parallelStream()
    .filter(d -> d.getCalories() > 300)
    .map(Dish::getName)
    .collect(toList());
```
명령형 코드의 `break`, `continue`, `return` 등의 제어 흐름문을 모두 분석해서 같은 기능을 수행하는 스트림 연산으로 유추해야 하므로 명령형 코드를 스트림 API로 바꾸는 것은 쉬운 일이아니다. 다행히도 명령형 코드를 스트림 API로 바꾸도록 도움을 주는 몇 가지 도구가 있다.

#### 9.1.5 코드 유연성 개선
다양한 람다를 전달해서 다양한 동작을 표현할 수 있다. 따라서 변화되는 요구사항에 대응할 수 있는 코드를 구현할 수 있다.

**함수형 인터페이스 적용**

먼저 람다 표현식을 이용하려면 함수형 인터페이스가 필요하다. 따라서 함수형 인터페이스를 코드에 추가해야 한다. 이번에는 조건부 연기 실행과 실행 어라운드, 즉 두 가지 자주 사용하는 패턴으로 람다 표현식 리팩터링을 살펴본다.

**조건부 연기 실행**

실제 작업을 처리하는 코드 내부에 제어 흐름문이 복잡하게 얽힌 코드를 흔히 볼 수 있다. 흔히 보안 검사나 로깅 관련 코드가 이처럼 사용된다. 다음은 내장 자바 `Logger` 클래스를 사용하는 예제다.

```java
if (logger.isLoggable(Log.FINER)) {
    logger.finer("problem: " + generateDiagnostic());
}
```
위 코드는 다음과 같은 사항에 문제가 있다.
- `logger`의 상태가 `isLoggable`이라는 메서드에 의해 클라이언트 코드로 노출된다.
- 메시지를 로깅할 때마다 `logger` 객체의 상태를 매번 확인해야 할까? 이들은 코드를 어지럽힐 뿐이다.

다음처럼 메시지를 로깅하기 전에 `logger` 객체가 적절한 수준으로 설정되었는지 내부적으로 확인하는 `log` 메서드를 사용하는 것이 바람직하다.

```java
logger.log(Level.FINER, "Problem :" + generateDiagnostic());
```
덕분에 불필요한 `if`문을 제거할 수 있으며 `logger`의 상태를 노출할 필요도 없으므로 위 코드가 더 바람직한 구현이다. 안타깝게도 위 코드로 모든 문제가 해결된 것은 아니다. 즉, 인수로 전달된 메시지 수준에서 `logger`가 활성화되어 있지 않더라도 항상 로깅 메시지를 평가하게 된다.

람다를 이용하면 이 문제를 쉽게 해결할 수 있다. 특정 조건(예제에서는 `logger` 수준을 `FINER`로 설정)에서만 메시지가 생성될 수 있도록 메시지 생성 과정을 연기할 수 있어야 한다. 자바 8 API 설계자는 이와 같은 `logger` 문제를 해결할 수 있도록 `Supplier`를 인수로 갖는 오버로드된 `log`메서드를 제공했다. 다음은 새로 추가된 `log` 메서드의 시그니처다.

```java
public void log(Level level, Supplier<String> msgSupplier)
```
다음처럼 `log` 메서드를 호출할 수 있다.

```java
logger.log(Level.FINER, () -> "Problem :" + generateDiagnostic());
```
`log` 메서드는 `logger`의 수준이 적절하게 설정되어 있을 때만 인수로 넘겨진 람다를 내부적으로 실행한다. 다음은 `log` 메서드의 내부 구현 코드다.

```java
public void log(Level level, Supplier<String> msgSupplier) {
    if(logger.isLoggable(level)) {
        log(level, msgSupplier.get()); // 람다 실행
    }
}
```
이 기법으로 어떤 문제를 해결할 수 있을까? 만일 클라이언트 코드에서 객체 상태를 자주 확인하거나(예를 들면 `logger`의 상태), 객체의 일부 메서드를 호출하는 상황(예를 들면 메시지 로깅)이라면 내부적으로 객체의 상태를 확인한 다음에 메서드를 호출(람다나 메서드 참조를 인수로 사용)하도록 새로운 메서드를 구현하는 것이 좋다. 그러면 코드 가독성이 좋아질 뿐 아니라 캡슐화도 강화된다(객체 상태가 클라이언트 코드로 노출되지 않는다).

**실행 어라운드**

매번 같은 준비, 종료 과정을 반복적으로 수행하는 코드가 있다면 이를 람다로 변환할 수 있다. 준비, 종료 과정을 처리하는 로직을 재사용함으로써 코드 중복을 줄일 수 있다.

이 코드는 파일을 열고 닫을 때 같은 로직을 사용했지만 람다를 이용해서 다양한 방식으로 파일을 처리할 수 있도록 파라미터화되었다.

```java
String oneLine = 
    processFile((BufferedReader b) -> b.readLine()); // 람다 전달
String twoLine = 
    processFile((BufferedReader b) -> b.readLine() + b.readLine()); // 다른 람다 전달
public static String processFile(BufferedReaderProcessor p) throws IOException {
    try(BufferedReader br = new BufferedReader(new FileReader("ModernJavaInAction/chap9/data.txt"))) {
        return p.process(br); // 인수로 전달된 BufferedReaderProcessor를 실행
    }
} // IOException을 던질 수 있는 람다의 함수형 인터페이스
public interface BufferedReaderProcessor {
    String process(BufferedReader b) throws IOException;
}
```
람다로 `BufferedReader` 객체의 동작을 결정할 수 있는 것은 함수형 인터페이스 `BufferedReader` 덕분이다.

---

## 9.2 람다로 객체지향 디자인 패턴 리팩터링하기
다양한 패턴을 유형별로 정리한 것이 디자인 패턴이다. 디자인 패턴은 공통적인 소프트웨어 문서를 설계할 때 재사용할 수 있는, 검증된 청사진을 제공한다. 디자인 패턴은 재사용할 수 있는 부품으로 여러 가지 다리를 건설하는 엔지니어링에 비유할 수 있다. 예를 들어 구조체와 동작하는 알고리즘을 서로 분리하고 싶을 때 방문자 디자인 패턴을 사용할 수 있다. 또 다른 예제로 싱글턴 패턴을 이용해서 클래스 인스턴스화를 하나의 객체로 제한할 수 있다.

디자인 패턴에 람다 표현식이 더해지면 색다른 기능을 발휘할 수 있다. 즉, 람다를 이용하면 이전에 디자인 패턴으로 해결하던 문제를 더 쉽고 간단하게 해결할 수 있다. 또한 람다 표현식으로 기존의 많은 객체지향 디자인 패턴을 제거하거나 간결하게 재구현할 수 있다.

여기서는 다음 다섯 가지 패턴을 살펴본다.
- **전략(strategy)**
- **템플릿 메서드(template method)**
- **옵저버(observer)**
- **의무 체인(chain of responsibility)**
- **팩토리(factory)**

각 디자인 패턴에서 람다를 어떻게 사용할 수 있는지 설명한다.

#### 9.2.1 전략
전략 패턴은 한 유형의 알고리즘을 보유한 상태에서 런타임에 적절한 알고리즘을 선택하는 기법이다. 다양한 기준을 갖는 입력값을 검증하거나, 다양한 파싱 방법을 사용하거나, 입력 형식을 설정하는 등 다양한 시나리오에 전략 패턴을 활용할 수 있다.

전략 패턴은 세 부분으로 구성된다.
- **알고리즘을 나타내는 인터페이스(Strategy 인터페이스)**
- **다양한 알고리즘을 나타내는 한 개 이상의 인터페이스 구현(ConcreteStrategyA, ConcreteStrategyB 같은 구체적인 구현 클래스)**
- **전략 객체를 사용하는 한 개 이상의 클라이언트**

예를 들어 오직 소문자 또는 숫자로 이루어져야 하는 등 텍스트 입력이 다양한 조건에 맞게 포맷되어 있는지 검증한다고 가정하자. 먼저 `String` 문자열을 검증하는 인터페이스부터 구현한다.

```java
public interface ValidationStrategy {
    boolean execute(String s);
}
```
이번에는 위에서 정의한 인터페이스를 구현하는 클래스를 하나 이상 정의한다.

```java
public class IsAllLowerCase implements ValidationStrategy {
    public boolean execute(String s) {
        return s.matches("[a-z]+");
    }
}

public class IsNumeric implements ValidationStrategy {
    public boolean execute(String s) {
        return s.matches("\\d+");
    }
}
```
지금까지 구현한 클래스를 다양한 검증 전략으로 활용할 수 있다.

```java
public class Validator {
    private final ValidationStrategy strategy;

    public Validator(ValidationStrategy v) {
        this.strategy = v;
    }

    public boolean validate(String s) {
        return strategy.execute(s);
    }
}

Validator numericValidator = new Validator(new IsNumeric());
boolean b1 = numericValidator.validate("aaaa"); // false 반환
Validator lowerCaseValidator = new Validator(new IsAllLowerCase());
boolean b2 = lowerCaseValidator.validate("bbbb"); // true 반환
```

**람다 표현식 사용**

`ValidationStrategy`는 함수형 인터페이스며 `Predicate<String>`과 같은 함수 디스크립터를 갖고 있음을 파악했을 것이다. 따라서 다양한 전략을 구현하는 새로운 클래스를 구현할 필요 없이 람다 표현식을 직접 전달하면 코드가 간결해진다.

```java
Validator numericValidator =
    new Validator((String s) -> s.matches("[a-z]+"));
boolean b1 = numericValidator.validate("aaaa");
Validator lowerCaseValidator = 
    new Validator((String s) -> s.matches("\\b+"));
boolean b2 = lowerCaseValidator.validate("bbbb");
```
위 코드에서 확인할 수 있듯이 람다 표현식을 이용하면 전략 디자인 패턴에서 발생하는 자잘한 코드를 제거할 수 있다. 람다 표현식은 코드 조각(또는 전략)을 캡슐화한다. 즉, 람다 표현식으로 전략 디자인 패턴을 대신할 수 있다. 따라서 이와 비슷한 문제에서는 람다 표현식을 사용할것을 추천한다.

#### 9.2.2 템플릿 메서드
알고리즘의 개요를 제시한 다음에 알고리즘의 일부를 고칠 수 있는 유연함을 제공해야 할 때 템플릿 메서드 디자인 패턴을 사용한다. 다시 말해, 템플릿 메서드는 '이 알고리즘을 사용하고 싶은데 그대로는 안 되고 조금 고쳐야 하는' 상황에 적합하다.

간단한 온라인 뱅킹 애플리케이션을 구현한다고 가정하자. 사용자가 고객 ID를 애플리케이션에 입력하면 은행 데이터베이스에서 고객 정보를 가져오고 고객이 원하는 서비스를 제공할 수 있다. 예를 들어 고객 계좌에 보너스를 입금한다고 가정하자. 은행마다 다양한 온라인 뱅킹 애플리케이션을 사용하며 동작 방법도 다르다. 다음은 온라인 뱅킹 애플리케이션의 동작을 정의하는 추상 클래스다.

```java
abstract class OnlineBanking {
    public void processCustomer(int id) {
        Customer c = Database.getCustomerWithId(id);
        makeCustomerHappy(c);
    }
    abstract void makeCustomerHappy(Customer c);
}
```
`processCustomer` 메서드는 온라인 뱅킹 알고리즘이 해야 할 일을 보여준다. 우선 주어진 고객 ID를 이용해서 고객을 만족시켜야 한다. 각각의 지점은 `OnlineBanking` 클래스를 상속받아 `makeCustomerHappy` 메서드가 원하는 동작을 수행하도록 구현할 수 있다.

**람다 표현식 사용**

이번에도 해결사 람다를 이용해서 문제를 해결할 수 있다(즉, 알고리즘의 개요를 만든 다음에 구현자가 원하는 기능을 추가할 수 있게 만들어보자). 람다나 메서드 참조로 알고리즘에 추가할 다양한 컴포넌트를 구현할 수 있다.

이전에 정의한 `makeCustomerHappy`의 메서드 시그니처와 일치하도록 `Consumer<Customer>` 형식을 갖는 두 번째 인수를 `processCustomer`에 추가한다.

```java
public void processCustomer(int id, Consumer<Customer> makeCustomerHappy) {
    Customer c = Database.getCustomerWithId(id);
    makeCustomerHappy.accept(c);
}
```
이제 `OnlineBanking` 클래스를 상속받지 않고 직접 람다 표현식을 전달해서 다양한 동작을 추가할 수 있다.

```java
new OnlineBankingLambda().processCustomer(1337, (Customer c) -> System.out.println("Hello " + c.getName()));
```
람다 표현식을 이용하면 템플릿 메서드 디자인 패턴에서 발생하는 자잘한 코드도 제거할 수 있다.

#### 9.2.3 옵저버
어떤 이벤트가 발생했을 때 한 객체(**주제**라 불리는)가 다른 객체 리스트(**옵저버**라 불리는)에 자동으로 알림을 보내야 하는 상황에서 옵저버 디자인 패턴을 사용한다. GUI 애플리케이션에서 옵저버 패턴이 자주 등장한다. 버튼 같은 GUI 컴포넌트에 옵저버를 설정할 수 있다. 그리고 사용자가 버튼을 클릭하면 옵저버에 알림이 전달되고 정해진 동작이 수행된다. 꼭 GUI에서만 옵저버 패턴을 사용하는 것은 아니다. 예를 들어 주식의 가격(주제) 변동에 반응하는 다수의 거래자(옵저버) 예제에서도 옵저버 패턴을 사용할 수 있다.

실제 코드로 옵저버 패턴이 어떻게 동작하는지 살펴보자. 옵저버 패턴으로 트위터 같은 커스터마이즈된 알림 시스템을 설계하고 구현할 수 있다. 우선 다양한 옵저버를 그룹화할 `Observer` 인터페이스가 필요하다. `Observer` 인터페이스는 새로운 트윗이 있을 때 주제(`Feed`)가 호출할 수 있도록 `notify`라고 하는 하나의 메서드를 제공한다.

```java
interface Observer {
    void notify(String tweet);
}
```
이제 트윗에 포함된 다양한 키워드에 다른 동작을 수행할 수 있는 여러 옵저버를 정의할 수 있다.

```java
class NYTimes implements Observer {
    public void notify(String tweet) {
        if(tweet != null && tweet.contains("money")) {
            System.out.println("Breaking news in NY! "+ tweet);
        }
    }
}
class Guardian implements Observer {
    public void notify(String tweet) {
        if(tweet != null && tweet.contains("queen")) {
            System.out.println("Yet more news from London... "+ tweet);
        }
    }
}
class LeMonde implements Observer {
    public void notify(String tweet) {
        if(tweet != null && tweet.contains("wine")) {
            System.out.println("Today cheese, wine and news! "+ tweet);
        }
    }
}
```
그리고 주제도 구현해야 한다. 다음은 `Subject` 인터페이스의 정의다.

```java
interface Subject {
    void registerObserver(Observer o);
    void notifyObserver(String tweet);
}
```
주제는 `registerObserver` 메서드로 새로운 옵저버를 등록한 다음에 `notifyObservers` 메서드로 트윗의 옵저버에 이를 알린다.

```java
class Feed implements Subject {
    private final List<Observer> observers = new ArrayList<>();

    @Override
    public void registerObserver(Observer o) {
        this.observers.add(o);
    }

    @Override
    public void notifyObservers(String tweet) {
        observers.forEach(o -> o.notify(tweet));
    }
}
```
구현은 간단하다. `Feed`는 트윗을 받았을 때 알림을 보낼 옵저버 리스트를 유지한다. 이제 주제와 옵저버를 연결하는 데모 애플리케이션을 만들 수 있다.

```java
Feed f = new Feed();
f.registerObserver(new NYTimes());
f.registerObserver(new Guardian());
f.registerObserver(new LeMonde());
f.notifyObservers("The queen said her favourite book is Modern Java In Action!");
```

**람다 표현식 사용하기**

여기서 `Observer` 인터페이스를 구현하는 모든 클래스는 하나의 메서드 `notify`를 구현했다. 즉, 트윗이 도착했을 때 어떤 동작을 수행할 것인지 감싸는 코드를 구현한 것이다. 지금까지 살펴본 것처럼 람다는 불필요한 감싸는 코드 제거 전문가다. 즉, 세 개의 옵저버를 명시적으로 인스턴스화하지 않고 람다 표현식을 직접 전달해서 실행할 동작을 저장할 수 있다.

```java
f.registerObserver((String tweet) -> {
    if(tweet != null && tweet.contains("money")) {
            System.out.println("Breaking news in NY! "+ tweet);
    }
});
f.registerObserver((String tweet) -> {
    if(tweet != null && tweet.contains("queen")) {
            System.out.println("Yet more news from London... "+ tweet);
    }
});
```
이 예제에서는 실행해야 할 동작이 비교적 간단하므로 람다 표현식으로 불필요한 코드를 제거하는 것이 바람직하다. 하지만 옵저버가 상태를 가지며, 여러 메서드를 정의하는 등 복잡하다면 람다 표현식보다 기존의 클래스 구현방식을 고수하는 것이 바람직할 수도 있다.

#### 9.2.4 의무 체인
작업 처리 객체의 체인(동작 체인 등)을 만들 때는 의무 체인 패턴을 사용한다. 한 객체가 어떤 작업을 처리한 다음에 다른 객체로 결과를 전달하고, 다른 객체도 해야 할 작업을 처리한 다음에 또 다른 객체로 전달하는 식이다. 일반적으로 다음으로 처리할 객체 정보를 유지하는 필드를 포함하는 작업 처리 추상 클래스로 의무 체인 패턴을 구성한다. 작업 처리 객체가 자신의 작업을 끝냈으면 다음 작업 처리 객체로 결과를 전달한다. 다음은 작업 처리 객체 예제 코드다.

```java
public abstract class ProcessingObject<T> {
    protected ProcessingObject<T> successor;

    public void setSuccessor(ProcessingObject<T> successor) {
        this.successor = successor;
    }
    public T handle(T input) {
        T r = handleWork(input);
        if(successor != null) {
            return successor.handle(r);
        }
        return r;
    }
    abstract protected T handleWork(T input);
}
```
`handle` 메서드는 일부 작업을 어떻게 처리해야 할지 전체적으로 기술한다. `ProcessingObject` 클래스를 상속받아 `handleWork` 메서드를 구현하여 다양한 종류의 작업 처리 객체를 만들 수 있다. 

이 패턴을 어떻게 활용할 수 있는지 실질적인 예제를 살펴보자. 다음의 두 작업 처리 객체는 텍스트를 처리하는 예제다.

```java
public class HeaderTextProcessing extends ProcessingObject<String> {
    @Override
    protected String handleWork(String text) {
        return "From Raoul, Mario and Alan: " + text;
    }
}

public class SpellCheckerProcessing extends ProcessingObject<String> {
    @Override
    protected String handleWork(String text) {
        return text.replaceAll("labda", "lambda");
    }
}
```
두 작업 처리 객체를 연결해서 작업 체인을 만들 수 있다.

```java
ProcessingObject<String> p1 = new HeaderTextProcessing();
ProcessingObject<String> p2 = new SpellCheckerProcessing();
p1.setSuccessor(p2); // 두 작업 처리 객체를 연결한다.
String result = p1.handle("Aren't labdas really sexy?!!");
System.out.println(result);
```

**람다 표현식 사용**

이 패턴은 함수 체인(즉, 함수 조합)과 비슷하다. 작업 처리 객체를 `Function<String, String>`, 더 정확히 표현하자면 `UnaryOperator<String>` 형식의 인스턴스로 표현할 수 있다. `andThen` 메서드로 이들 함수를 조합해서 체인을 만들 수 있다.

```java
UnaryOperator<String> headerProcessing =
    (String text) -> "From Raoul, Mario and Alan: " + text; // 첫 번째 작업 처리 객체
UnaryOperator<String> spellCheckerProcessing = 
    (String text) -> text.replaceAll("labda", "lambda"); // 두 번째 작업 처리 객체
Function<String, String> pipeline = 
    headerProcessing.andThen(spellCheckerProcessing); // 동작 체인으로 두 함수를 조합한다.
String result = pipeline.apply("Aren't labdas really sexy?!!");
```

#### 9.2.5 팩토리
인스턴스화 로직을 클라이언트에 노출하지 않고 객체를 만들 때 팩토리 디자인 패턴을 사용한다. 예를 들어 우리가 은행에서 일하고 있는데 은행에서 취급하는 대출, 채권, 주식 등 다양한 상품을 만들어야 한다고 가정하자.

다음 코드에서 보여준느 것처럼 다양한 상품을 만드는 `Factory` 클래스가 필요하다.

```java
public class ProductFactory {
    public static Product createProduct(String name) {
        switch (name) {
            case "loan" : return new Loan();
            case "stock" : return new Stock();
            case "bond" : return new Bond();
            default: throw new RuntimeException("No such product" + name); 
        }
    }
}
```
여기서 `Loan`, `Stock`, `Bond`는 모두 `Product`의 서브형식이다. `createProduct` 메서드는 생산된 상품을 설정하는 로직을 포함할 수 있다. 이는 부가적인 기능일 뿐 위 코드의 진짜 장점은 생성자와 설정을 외부로 노출하지 않음으로써 클라이언트가 단순하게 상품을 생산할 수 있다는 것이다.

```java
Product p = ProductFactory.createProduct("loan");
```

**람다 표현식 사용**
생성자도 메서드 참조처럼 접근할 수 있다. 예를 들어 다음은 `Loan` 생성자를 사용하는 코드다.

```java
Supplier<Product> loanSupplier = Loan::new;
Loan loan = loanSupplier.get();
```
이제 다음 코드처럼 상품명을 생성자로 연결하는 `Map`을 만들어서 코드를 재구현할 수 있다.

```java
final static Map<String, Supplier<Product>> map = new HashMap<>();
static {
    map.put("loan", Loan::new);
    map.put("stock", Stock::new);
    map.put("bond", Bond::new);
}
```
이제 `Map`을 이용해서 팩토리 디자인 패턴에서 했던 것처럼 다양한 상품을 인스턴스화할 수 있다.

```java
public static Product createProduct(String name) {
    Supplier<Product> p = map.get(name);
    if(p != null) return p.get();
    throw new IllegalArgumentException("No such product " + name);
}
```
팩토리 패턴이 수행하던 작업을 자바 8의 새로운 기능으로 깔끔하게 정리했다. 하지만 팩토리 메서드 `createProduct`가 상품 생성자로 여러 인수를 전달하는 상황에서는 이 기법을 적용하기 어렵다. 단순한 `Supplier` 함수형 인터페이스로는 이 문제를 해결할 수 없다.

예를 들어 세 인수(`Integer` 둘, 문자열 하나)를 받는 상품의 생성자가 있다고 가정하자. 세 인수를 지원하려면 `TriFunction`이라는 특별한 함수형 인터페이스를 만들어야 한다. 결국 다음 코드처럼 `Map`의 시그니처가 복잡해진다.

```java
public interface TriFunction<T, U, V, R> {
    R apply(T t, U u, V v);
}
Map<String, TriFunction<Integer, Integer, String, Product>> map = new HashMap<>();
```

---

## 9.3 람다 테스팅
이제 람다 표현식을 실무에 적용해서 멋지고 간단한 코드를 구현할 수 있다. 하지만 개발자의 최종 업무 목표는 제대로 작동하는 코드를 구현하는 것이지 깔끔한 코드를 구현하는 것이 아니다.

일반적으로 좋은 소프트웨어 공학자라면 프로그램이 의도대로 동작하는지 확인할 수 있는 **단위 테스팅**을 진행한다. 우리는 소스 코드의 일부가 예상된 결과를 도출할 것이라 단언하는 테스트 케이스를 구현한다. 예를 들어 다음처럼 그래픽 애플리케이션의 일부인 `Point` 클래스가 있다고 가정하자.

```java
public class Point {
    private final int x;
    private final int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Point moveRightBy(int x) {
        return new Point(this.x + x, this.y);
    }
}
```
다음은 `moveRightBy` 메서드가 의도한 대로 동작하는지 확인하는 단위 테스트다.

```java
@Test
public void testMoveRightBy() throws Exception {
    Point p1 = new Point(5, 5);
    Point p2 = p1.moveRightBy(10);
    assertEquals(15, p2.getX());
    assertEquals(5, p2.getY());
}
```

#### 9.3.1 보이는 람다 표현식의 동작 테스팅
`moveRightBy`는 `public`이므로 위 코드는 문제없이 작동한다. 따라서 테스트 케이스 내부에서 `Point` 클래스 코드를 테스트할 수 있다. 하지만 람다는 익명(결국 익명 함수)이므로 테스트 코드 이름을 호출할 수 없다.

따라서 필요하다면 람다를 필드에 저장해서 재사용할 수 있으며 람다의 로직을 테스트할 수 있다. 그리고 메서드를 호출하는 것처럼 람다를 사용할 수 있다. 예를 들어 `Point` 클래스에 `compareByXAndThenY`라는 정적 필드를 추가했다고 가정하자(`compareByXAndThenY`를 이용하면 메서드 참조로 생성한 `Comparator` 객체에 접근할 수 있다).

```java
public class Point {
    public final static Comparator<Point> compareByXAndThenY = 
        comparing(Point::getX).thenComparing(Point::getY);
    ...
}
```
람다 표현식은 함수형 인터페이스의 인스턴스를 생성한다는 사실을 기억하자. 따라서 생성된 인스턴스의 동작으로 람다 표현식을 테스트할 수 있다. 다음은 `Comparator` 객체 `compareByXAndThenY`에 다양한 인수로 `compare` 메서드를 호출하면서 예상대로 동작하는지 테스트하는 코드다.

```java
@Test
public void testComparingTwoPoints() throws Exception {
    Point p1 = new Point(10, 15);
    Point p2 = new Point(10, 20);
    int result = Point.compareByXAndThenY.compare(p1, p2);
    assertTrue(result < 0);
}
```

#### 9.3.2 람다를 사용하는 메서드의 동작에 집중하라
람다의 목표는 정해진 동작을 다른 메서드에서 사용할 수 있도록 하나의 조각으로 캡슐화하는 것이다. 그러려면 세부 구현을 포함하는 람다 표현식을 공개하지 말아야 한다. 람다 표현식을 사용하는 메서드의 동작을 테스트함으로써 람다를 공개하지 않으면서도 람다 표현식을 검증할 수 있다. 예를 들어 다음 `moveAllPointsRightBy` 메서드를 살펴보자.

```java
public static List<Point> moveAllPointsRightBy(List<Point> points, int x) {
    return points.stream()
            .map(point -> new Point(point.getX() + x, point.getY()))
            .collect(toList());
}
```
위 코드에 람다 표현식 `point -> new Point(p.getX() + x, point.getY());`를 테스트하는 부분은 없다. 그냥 `moveAllPointsRightBy` 메서드를 구현한 코드일 뿐이다. 이제 `moveAllPointsRightBy` 메서드의 동작을 확인할 수 있다.

```java
@Test
public void testMoveAllPointsRightBy() throws Exception {
    List<Point> points = 
        Arrays.asList(new Point(5, 5), new Point(10, 5));
    List<Point> expectedPoints = 
        Arrays.asList(new Point(15, 5), new Point(20, 5));
    List<Point> newPoints = Point.moveAllPointsRightBy(points, 10);
    assertEquals(expectedPoints, newPoints);
}
```
위 단위 테스트에서 보여주는 것처럼 `Point` 클래스의 `equals` 메서드는 중요한 메서드다. 따라서 `Object`의 기본적인 `equals` 구현을 그대로 사용하지 않으려면 `equals` 메서드를 적절하게 구현해야 한다.

#### 9.3.3 복잡한 람다를 개별 메서드로 분할하기
그런데 테스트 코드에서 람다 표현식을 참조할 수 없는데, 복잡한 람다 표현식을 어떻게 테스트할 것인가? 한 가지 해결책은 람다 표현식을 메서드 참조로 바꾸는 것이다(새로운 일반 메서드 선언). 그러면 일반 메서드를 테스트하듯이 람다 표현식을 테스트할 수 있다.

#### 9.3.4 고차원 함수 테스팅
함수를 인수로 받거나 다른 함수를 반환하는 메서드(이를 고차원 함수라고 한다)는 좀 더 사용하기 어렵다. 메서드가 람다를 인수로 받는다면 다른 람다로 메서드의 동작을 테스트할 수 있다. 예를 들어 다양한 프레디케이트로 `filter` 메서드를 테스트할 수 있다.

```java
@Test
public void testFilter() throws Exception {
    List<Integer> numbers = Arrays.asList(1, 2, 3, 4);
    List<Integer> even = filter(numbers, i -> i % 2 == 0);
    List<Integer> smallerThanThree = filter(numbers, i -> i < 3);
    assertEquals(Arrays.asList(2, 4), even);
    assertEquals(Arrays.asList(1, 2), smallerThanThree);
}
```
테스트해야 할 메서드가 다른 함수를 반환한다면 어떻게 해야 할까? 이때는 `Comparator`에서 살펴봤던 것처럼 함수형 인터페이스의 인스턴스로 간주하고 함수의 동작을 테스트할 수 있다.

---

## 9.4 디버깅
문제가 발생한 코드를 디버깅할 때 개발자는 다음 두 가지를 가장 먼저 확인해야 한다.

- **스택 트레이스**
- **로깅**

하지만 람다 표현식과 스트림은 기존의 디버깅 기법을 무력화한다. 다음은 람다 표현식과 스트림 디버깅 방법을 살펴본다.

#### 9.4.1 스택 트레이스 확인
예를 들어 예외 발생으로 프로그램 실행이 갑자기 중단되었다면 먼저 어디에서 멈췄고 어떻게 멈추게 되었는지 살펴봐야 한다. 바로 스택 프레임에서 이 정보를 얻을 수 있다. 프로그램이 메서드를 호출할 때마다 프로그램에서의 호출 위치, 호출할 때의 인수값, 호출된 메서드의 지역 변수 등을 포함한 호출 정보가 생성되며 이들 정보는 스택 프레임에 저장된다.

따라서 프로그램이 멈췄다면 프로그램이 어떻게 멈추게 되었는지 프레임별로 보여주는 스택 트레이스를 얻을 수 있다. 즉, 문제가 발생한 지점에 이르게 된 메서드 호출 리스트를 얻을 수 있다. 메서드 호출 리스트를 통해 문제가 어떻게 발생했는지 이해할 수 있다.

**람다와 스택 트레이스**

유감스럽게도 람다 표현식은 이름이 없기 때문에 조금 복잡한 스택 트레이스가 생성된다. 다음은 고의적으로 문제를 일으키도록 구현한 간단한 코드다.

```java
public class Debugging {
    public static void main(String[] args) {
        List<Point> points = Arrays.asList(new Point(12, 2), null);
        points.stream()
                .map(point -> point.getX())
                .forEach(System.out::println);
    }
}
```
프로그램을 실행하면 다음과 같은 스택 트레이스가 출력된다.

```java
Exception in thread "main" java.lang.NullPointerException
	at Chapter9.Debugging.lambda$main$0(Debugging.java:10) // 여기 '$0'은 무슨 의미일까?
	at java.base/java.util.stream.ReferencePipeline$3$1.accept(ReferencePipeline.java:195)
	at java.base/java.util.Spliterators$ArraySpliterator.forEachRemaining(Spliterators.java:948)
	...
```
물론 의도했던 대로 `points` 리스트의 둘째 인수가 `null`이므로 프로그램의 실행이 멈췄다. 스트림 파이프라인에서 에러가 발생했으므로 스트림 파이프라인의 작업과 관련된 전체 메서드 호출 리스트가 출력되었는데, 다음처럼 수수께끼 같은 정보도 포함되어 있다.

```java
at Chapter9.Debugging.lambda$main$0(Debugging.java:10)
```
이와 같은 이상한 문자는 람다 표현식 내부에서 에러가 발생했음을 가리킨다. 람다 표현식은 이름이 없으므로 컴파일러가 람다를 참조하는 이름을 만들어낸 것이다. `lambda$main$0`는 다소 생소한 이름이다. 클래스에 여러 람다 표현식이 있을 때는 꽤 골치 아픈 일이 벌어진다.

메서드 참조를 사용해도 스택 트레이스에는 메서드명이 나타나지 않는다. 기존의 람다 표현식 `point -> point.getX()`를 메서드 참조 `Point::getX`로 고쳐도 여전히 스택 트레이스로는 이상한 정보가 출력된다.

메서드 참조를 사용하는 클래스와 같은 곳에 선언되어 있는 메서드를 참조할 때는 메서드 참조 이름이 스택 트레이스에 나타난다. 다음 예제를 살펴보자.

```java
public class Debugging {
    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3);
        numbers.stream()
                .map(Debugging::divideByZero)
                .forEach(System.out::println);
    }

    public static int divideByZero(int n) {
        return n / 0;
    }
}
```
`divideByZero` 메서드는 스택 트레이스에 제대로 표시된다.

```java
at Chapter9.Debugging.divideByZero(Debugging.java:19) // 스택 트레이스에 divideByZero가 보인다.
	at java.base/java.util.stream.ReferencePipeline$3$1.accept(ReferencePipeline.java:195)
	...
```
따라서 람다 표현식과 관련한 스택 트레이스는 이해하기 어려울 수 있다는 점을 염두에 두자.

#### 9.4.2 정보 로깅
스트림의 파이프라인 연산을 디버깅한다고 가정하자. 다음처럼 `forEach`로 스트림 결과를 출력하거나 로깅할 수 있다.

```java
List<Integer> numbers = Arrays.asList(2, 3, 4, 5);

numbers.stream()
            .map(x -> x + 17)
            .filter(x -> x % 2 ==0)
            .limit(3)
            .forEach(System.out::println);
```
다음은 프로그램 출력 결과다.

>20
>22

안타깝게도 `forEach`를 호출하는 순간 전체 스트림이 소비된다. 스트림 파이프라인에 적용된 각각의 연산(`map`, `filter`, `limit`)이 어떤 결과를 도출하는지 확인할 수 있다면 좋을 것 같다. 바로 `peek`이라는 스트림 연산을 활용할 수 있다. `peek`은 스트림의 각 요소를 소비한 것처럼 동작을 실행한다. 하지만 `forEach`처럼 실제로 스트림의 요소를 소비하지는 않는다. `peek`은 자신이 확인한 요소를 파이프라인의 다음 연산으로 그대로 전달한다. 다음 코드에서는 `peek`으로 스트림 파이프라인의 각 동작 전후의 중간값을 출력한다.

```java
numbers.stream()
            .peek(x -> System.out.println("from stream : " + x)) // 소스에서 처음 소비한 요소를 출력한다.
            .map(x -> x + 17)
            .peek(x -> System.out.println("after map : " + x)) // map 동작 실행 결과를 출력한다.
            .filter(x -> x % 2 == 0)
            .peek(x -> System.out.println("after filter : " + x)) // filter 동작 후 선택된 숫자를 출력한다.
            .limit(3)
            .peek(x -> System.out.println("after limit : " + x)) // limit 동작 후 선택된 숫자를 출력한다.
            .collect(toList());
```

---

## 9.5 마치며

- **람다 표현식으로 가독성이 좋고 더 유연한 코드를 만들 수 있다.**
- **익명 클래스는 람다 표현식으로 바꾸는 것이 좋다. 하지만 이때 `this`, `변수 섀도` 등 미묘하게 의미상 다른 내용이 있음을 주의하자**
- **메서드 참조로 람다 표현식보다 더 가독성이 좋은 코드를 구현할 수 있다.**
- **반복적으로 컬렉션을 처리하는 루틴은 스트림 API로 대체할 수 있을지 고려하는 것이 좋다**
- **람다 표현식으로 전략, 템플릿 메서드, 옵저버, 의무 체인, 팩토리 등의 객체지향 디자인 패턴에서 발생하는 불필요한 코드를 제거할 수 있다.**
- **람다 표현식도 단위 테스트를 수행할 수 있다. 하지만 람다 표현식 자체를 테스트하는 것보다는 람다 표현식이 사용되는 메서드의 동작을 테스트하는 것이 바람직하다**
- **복잡한 람다 표현식은 일반 메서드로 재구현할 수 있다.**
- **람다 표현식을 사용하면 스택 트레이스를 이해하기 어려워진다.**
- **스트림 파이프라인에서 요소를 처리할 때 `peek` 메서드로 중간값을 확인할 수 있다.**

---