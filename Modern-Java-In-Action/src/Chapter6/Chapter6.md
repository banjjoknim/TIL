# Chapter6. 스트림으로 데이터 수집

스트림의 연산은 `filter` 또는 `map` 같은 중간 연산과 `count`, `findFirst`, `forEach`, `reduce` 등의 최종 연산으로 구분할 수 있다. 중간 연산은 한 스트림을 다른 스트림으로 변환하는 연산으로서, 여러 연산을 연결할 수 있다. 중간 연산은 스트림 파이프라인을 구성하며, 스트림의 요소를 **소비**하지 않는다. 반면 최종 연산은 스트림의 요소를 소비해서 최종 결과를 도출한다. 최종 연산은 스트림 파이프라인을 최적화하면서 계산 과정을 짧게 생략하기도 한다. `reduce`가 그랬던 것처럼 `collect` 역시 다양한 요소 누적 방식을 인수로 받아서 스트림을 최종 결과로 도출하는 리듀싱 연산을 수행할 수 있다. 다양한 요소 누적 방식은 `Collector` 인터페이스에 정의되어 있다.

다음은 `collect`와 컬렉터로 구현할 수 있는 질의 예제다.

- 통화별로 트랜잭션을 그룹화한 다음에 해당 통화로 일어난 모든 트랜잭션 합계를 계산하시오(`Map<Currency, Integer>` 반환).
- 트랜잭션을 비싼 트랜잭션과 저렴한 트랜잭션 두 그룹으로 분류하시오(`Map<Boolean, List<Transaction>>` 반환).
- 트랜잭션을 도시 등 다수준으로 그룹화하시오. 그리고 각 트랜잭션이 비싼지 저렴한지 구분하시오(`Map<String, Map<Boolean, List<Transaction>>>` 반환).

```java
// 통화별로 트랜잭션을 그룹화한 코드(명령형 버전)

Map<Currency, List<Transaction>> transactionsByCurrencies = new HashMap<>(); // 그룹화한 트랜잭션을 저장할 맵을 생성한다.

for (Transaction transaction : transactions) { // 트랜잭션 리스트를 반복한다.
    Currency currency = transaction.getCurrency(); // 트랜잭션의 통화를 추출한다.
    List<Transaction> transactionsForCurrency = transactionsByCurrencies.get(currency);
    if(transactionsForCurrency == null) { // 현재 통화를 그룹화하는 맵에 항목이 없으면 항목을 만든다.
        transactionsForCurrency = new ArrayList<>();
        transactionsByCurrencies.put(currency, transactionsForCurrency);
    }
    transactionsForCurrency.add(transaction); // 같은 통화를 가진 트랜잭션 리스트에 현재 탐색 중인 트랜잭션을 추가한다.
}
```

`Stream`에 `toList`를 사용하는 대신 더 범용적인 컬렉터 파라미터를 `collect` 메서드에 전달함으로써 원하는 연산을 간결하게 구현할 수 있다.

```java
Map<Currency, List<Transactions\>> transactionsByCurrencies = 
    transactions.stream().collect(groupingBy(Transaction::getCurrency));
```

---

## 6.1 컬렉터란 무엇인가?

위 예제는 명령형 프로그래밍에 비해 함수형 프로그래밍이 얼마나 편리한지 명확하게 보여준다. 함수형 프로그래밍에서는 '무엇'을 원하는지 직접 명시할 수 있어서 어떤 방법으로 이를 얻을지는 신경 쓸 필요가 없다. 이전 예제에서 `collect` 메서드로 `Collector` 인터페이스 구현을 전달했다. `Collector` 인터페이스 구현은 스트림의 요소를 어떤 식으로 도출할지 지정한다. 여기서는 `toList`를 `Collector` 인터페이스의 구현으로 사용했다. 여기서는 `groupingBy`를 이용해서 '각 키(통화) 버킷 그리고 각 키 버킷에 대응하는 요소 리스트를 값으로 포함하는 맵을 만들라'는 동작을 수행한다.

#### 6.1.1 고급 리듀싱 기능을 수행하는 컬렉터

`collect`로 결과를 수집하는 과정을 간단하면서도 유연한 방식으로 정의할 수 있다는 점이 컬렉터의 최대 강점이다. 구체적으로 설명해서 스트림에 `collect`를 호출하면 스트림의 요소에(컬렉터로 파라미터화된) 리듀싱 연산이 수행된다. `collect`에서는 리듀싱 연산을 이용해서 스트림의 각 요소를 방문하면서 컬렉터가 작업을 처리한다. 보통 함수를 요소로 변환(`toList`처럼 데이터 자체를 변환하는 것보다는 데이터 저장 구조를 변환할 때가 많다)할 때는 컬렉터를 적용하며 최종 결과를 저장하는 자료구조에 값을 누적한다.

통화 예제에서 보여주는 것처럼 `Collector` 인터페이스의 메서드를 어떻게 구현하느냐에 따라 스트림에 어떤 리듀싱 연산을 수행할지 결정된다. `Collectors` 유틸리티 클래스는 자주 사용하는 컬렉터 인스턴스를 손쉽게 생성할 수 있는 정적 팩토리 메서드를 제공한다. 예를 들어 가장 많이 사용하는 직관적인 정적 메서드로 `toList`를 꼽을 수 있다. `toList`는 스트림의 모든 요소를 리스트로 수집한다.

```java
List<Transaction> transactions = transactionStream.collect(Collectors.toList());
```

#### 6.1.2 미리 정의된 컬렉터

`Collectors`에서 제공하는 메서드의 기능은 크게 세 가지로 구분할 수 있다.

- 스트림 요소를 하나의 값으로 리듀스하고 요약
- 요소 그룹화
- 요소 분할

**분할**은 그룹화의 특별한 연산인데, 한 개의 인수를 받아 불리언을 반환하는 함수, 즉 프레디케이트를 그룹화 함수로 사용한다.

---

## 6.2 리듀싱과 요약

컬렉터(`Stream.collect` 메서드의 인수)로 스트림의 항목을 컬렉션으로 재구성할 수 있다. 좀 더 일반적으로 말해 컬렉터로 스트림의 모든 항목을 하나의 결과로 합칠 수 있다.

첫 번째 예제로 `counting()`이라는 팩토리 메서드가 반환하는 컬렉터로 메뉴에서 요리 수를 계산한다.

```java
long howManyDishes = menu.stream().collect(Collectors.counting());
```

다음처럼 불필요한 과정을 생략할 수 있다.

```java
long howManyDishes = menu.stream().count();
```

`counting` 컬렉터는 다른 컬렉터와 함께 사용할 때 위력을 발휘한다.

지금부터는 `Collectors` 클래스의 정적 팩토리 메서드를 모두 임포트했다고 가정한다.

```java
import static java.util.stream.Collectors.*;
```

#### 6.2.1 스트림값에서 최댓값과 최솟값 검색

메뉴에서 칼로리가 가장 높은 요리를 찾는다고 가정하자. `Collectors.maxBy`, `Collectors.minBy` 두 개의 메서드를 이용해서 스트림의 최댓값과 최솟값을 계산할 수 있다. 두 컬렉터는 스트림의 요소를 비교하는 데 사용할 `Comparator`를 인수로 받는다. 다음은 칼로리로 요리를 비교하는 `Comparator`를 구현한 다음에 `Collectors.maxBy`로 전달하는 코드다.

```java
Comparator<Dish> dishCaloriesComparator = 
    Comparator.comparingInt(Dish::getCalories);

Optional<Dish> mostCalorieDish = 
    menu.stream()
        .collect(maxBy(dishCaloriesComparator));
```

또한 스트림에 있는 객체의 숫자 필드의 합계나 평균 등을 반환하는 연산에도 리듀싱 기능이 자주 사용된다. 이러한 연산을 **요약** 연산이라 부른다.

#### 6.2.2 요약 연산

`Collectors` 클래스는 `Collectors.summingInt`라는 특별한 요약 팩토리 메서드를 제공한다. `summingInt`는 객체를 `int`로 매핑하는 함수를 인수로 받는다. `summingInt`의 인수로 전달된 함수는 객체를 `int`로 매핑한 컬렉터를 반환한다. 그리고 `summingInt`가 `collect` 메서드로 전달되면 요약 작업을 수행한다. 다음은 메뉴 리스트의 총 칼로리를 계산하는 코드다.

```java
int totalCalories = menu.stream().collect(summingInt(Dish::getCalories));
```

`Collectors.summingLong`과 `Collectors.summingDouble` 메서드는 같은 방식으로 동작하며 각각 `long` 또는 `double` 형식의 데이터로 요약한다는 점만 다르다.

이러한 단순 합계 외에 평균값 계산 등의 연산도 요약 기능으로 제공된다. 즉, `Collectors.averagingInt`, `averagingLong`, `averagingDouble` 등으로 다양한 형식으로 이루어진 숫자 집함의 평균을 계산할 수 있다.

```java
double avgCalories = 
       menu.stream().collect(averagingInt(Dish::getCalories));
```

종종 두 개 이상의 연산을 한 번에 수행해야 할 때도 있다. 이런 상황에서는 팩토리 메서드 `summarizingInt`가 반환하는 컬렉터를 사용할 수 있다. 예를 들어 다음은 하나의 요약 연산으로 메뉴에 있는 요소 수, 요리의 칼로리 합계, 평균, 최댓값, 최솟값 등을 계산하는 코드다.

```java
IntSummaryStatistics menuStatistics = 
    menu.stream().collect(summarizingInt(Dish::getCalories));
```

위 코드를 실행하면 `IntSummaryStatics` 클래스로 모든 정보가 수집된다. `menuStatistics` 객체를 출력하면 다음과 같은 정보를 확인할 수 있다.

```java
IntSummaryStatistics{count=9, sum=4300, min=120, average=477.777778, max=800}
```

마찬가지로 `int`뿐 아니라 `long`이나 `double`에 대응하는 `summarizingLong`, `summarizingDouble` 메서드와 관련된 `LongSummaryStatistics`, `DoubleSummaryStatistics` 클래스도 있다.

#### 6.2.3 문자열 연결

컬렉터에 `joining` 팩토리 메서드를 이용하면 스트림의 각 객체에 `toString` 메서드를 호출해서 추출한 모든 문자열을 하나의 문자열로 연결해서 반환한다. 즉, 다음은 메뉴의 모든 요리명을 연결하는 코드다.

```java
String shortMenu = menu.stream().map(Dish::getName).collect(joining());
```

`joining` 메서드는 내부적으로 `StringBuilder`를 이용해서 문자열을 하나로 만든다. `Dish` 클래스가 요리명을 반환하는 `toString` 메서드를 포함하고 있다면 다음 코드에서 보여주는 것처럼 `map`으로 각 요리의 이름을 추출하는 과정을 생략할 수 있다.

```java
String shortMenu = menu.stream().collect(joining());
```

위 두 코드 모두 다음과 같은 결과를 도출한다.

```java
porkbeefchickenfrench friesriceseason fruitpizzaprawnssalmon
```

하지만 결과 문자열을 해석할 수가 없다. 다행히 연결된 두 요소 사이에 구분 문자열을 넣을 수 있도록 오버로드된 `joining` 팩토리 메서드도 있다. 따라서 다음 코드처럼 요리명 리스트를 콤마로 구분할 수 있다.

```java
String shortMenu = menu.stream().map(Dish::getName).collect(joining(", "));
```

다음은 위 코드를 실행한 결과다.

```java 
pork, beef, chicken, french fries, rice, season fruit, pizza, prawns, salmon
```

#### 6.2.4 범용 리듀싱 요약 연산

지금까지 살펴본 모든 컬렉터는 `reducing` 팩토리 메서드로도 정의할 수 있다. 즉, 범용 `Collectors.reducing`으로도 구현할 수 있다. 그럼에도 이전 예제에서 범용 팩토리 메서드 대신 특화된 컬렉터를 사용한 이유는 프로그래밍적 편의성 때문이다(하지만 프로그래머의 편의성 뿐만 아니라 가독성도 중요하다는 사실을 기억하자!). 예를 들어 다음 코드처럼 `reducing` 메서드로 만들어진 컬렉터로도 메뉴의 모든 칼로리 합계를 계산할 수 있다.

```java
int totalCalories = menu.stream().collect(reducing(0, Dish::getCalories, (i, j) -> i + j));
```

`reducing`은 인수 세 개를 받는다.

- 첫 번째 인수는 리듀싱 연산의 시작값이거나 스트림에 인수가 없을 때는 반환값이다(숫자 합계에서는 인수가 없을 때 반환값으로 `0`이 적합하다).
- 두 번째 인수는 요리를 칼로리 정수로 변환할 때 사용한 변환 함수다.
- 세 번째 인수는 같은 종류의 두 항목을 하나의 값으로 더하는 `BinaryOperator`다. 예제에서는 두 개의 `int`가 사용되었다.

다음처럼 한 개의 인수를 가진 `reducing` 버전을 이용해서 가장 칼로리가 높은 요리를 찾는 방법도 있다.

```java
Optional<Dish> mostCalorieDish = 
    menu.stream().collect(reducing((d1, d2) -> d1.getCalories > d2.getCalories() ? d1 : d2));
```

한 개의 인수를 갖는 `reducing` 팩토리 메서드는 세 개의 인수를 갖는 `reducing` 메서드에서 스트림의 첫 번째 요소를 시작 요소, 즉 첫 번째 인수로 받으며, 자신을 그대로 반환하는 **항등 함수**를 두 번째 인수로 받는 상황에 해당한다. 즉, 한 개의 인수를 갖는 `reducing` 컬렉터는 시작값이 없으므로 빈 스트림이 넘겨졌을 때 시작값이 설정되지 않는 상황이 벌어진다. 그래서 한 개의 인수를 갖는 `reducing`은 `Optional<Dish>` 객체를 반환한다.

