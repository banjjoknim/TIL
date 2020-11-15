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

>**컬렉션 프레임워크 유연성 : 같은 연산도 다양한 방식으로 수행할 수 있다.**
>
>`reducing` 컬렉터를 사용한 이전 예제에서 람다 표현식 대신 `Integer` 클래스의 `sum` 메서드 참조를 이용하면 코드를 좀 더 단순화할 수 있다. 다음 코드를 확인하자.
>
>```java
>int totalCalories = menu.stream()
>                        .collect(reducing(0, // 초깃값
>                         Dish::getCalories, // 변환 함수
>                         Integer::sum); // 합계 함수
>```
>
>```java
>int totalCalories = menu.stream()
>                        .map(Dish::getCalories)
>                        .reduce(Integer::sum).get(); 
>// 한 개의 인수를 갖는 `reduce`는 빈 스트림과 관련한 널 문제를 피할 수 있도록 
>// int가 아닌 Optional<Integer>를 반환한다. 
>// 그리고 get으로 Optional 객체 내부의 값을 추출했다.
>```
>
>```java
>int totalCalories = menu.stream()
>                        .mapToInt(Dish::getCalories)
>                        .sum();
>```

>**자신의 상황에 맞는 최적의 해법 선택**
>
>스트림 인터페이스에서 직접 제공하는 메서드를 이용하는 것에 비해 컬렉터를 이용하는 코드가 더 복잡하지만, 대신에 재사용성과 커스터마이즈 가능성을 제공하는 높은 수준의 추상화와 일반화를 얻을 수 있다. 문제를 해결할 수 있는 다양한 해결 방법을 확인한 다음에 가장 일반적으로 문제에 특화된 해결책을 고르는 것이 바람직하다. 이렇게 해서 가독성과 성능이라는 두 마리 토끼를 잡을 수 있다.

---

## 6.3 그룹화

데이터 집합을 하나 이상의 특성으로 분류해서 그룹화하는 연산도 데이터베이스에서 많이 수행되는 작업이다. 자바 8의 함수형을 이용하면 가독성 있는 한 줄의 코드로 그룹화를 구현할 수 있다. 다음처럼 팩토리 메서드 `Collectors.groupingBy`를 이용해서 쉽게 메뉴를 그룹화할 수 있다.

```java
Map<Dish.Type, List<Dish>> dishesByType =
        menu.stream().collect(groupingBy(Dish::getType));
```

다음은 `Map`에 포함된 결과다.

`{FISH=[prawns, salmon], OTHER=[french fries, rice, season fruit, pizza], MEAT=[pork, beef, chicken]}`

스트림의 각 요리에서 `Dish.Type`과 일치하는 모든 요리를 추출하는 함수를 `groupingBy` 메서드로 전달했다. 이 함수를 기준으로 스트림이 그룹화되므로 이를 **분류함수**라고 부른다. 그룹화 연산의 결과로 그룹화 함수가 반환하는 키 그리고 각 키에 대응하는 스트림의 모든 항목 리스트를 값으로 갖는 맵이 반환된다. 메뉴 그룹화 예제에서 키는 요리 종류고, 값은 해당 종류에 포함되는 모든 요리다.

단순한 속성 접근자 대신 더 복잡한 분류 기준이 필요한 상황에서는 메서드 참조를 분류 함수로 사용할 수 없다. `Dish` 클래스에는 이러한 연산에 필요한 메서드가 없으므로 메서드 참조를 분류 함수로 사용할 수 없다. 따라서 다음 예제에서 보여주는 것처럼 메서드 참조 대신 람다 표현식으로 필요한 로직을 구현할 수 있다.

```java
public enum CaloricLevel {DIET, NORMAL, FAT}

Map<CaloricLevel, List<Dish>> dishesByCaloricLevel = 
        menu.stream().collect(
            groupingBy(dish -> {
                if (dish.getCalories() <= 400) return CaloricLevel.DIET;
                else if (dish.getCalories() <= 700) return CaloricLevel.NORMAL;
                else return CaloricLevel.FAT;
            })
        );
```

#### 6.3.1 그룹화된 요소 조작

요소를 그룹화 한 다음에는 각 결과 그룹의 요소를 조작하는 연산이 필요하다. 예를 들어 500 칼로리가 넘는 요리만 필터한다고 가정하자. 다음 코드처럼 그룹화를 하기 전에 프레디케이트로 필터를 적용해 문제를 해결할 수 있다고 생각할 것이다.

```java
Map<Dish.Type, List<Dish>> caloricDishesByType =
        menu.stream().filter(dish -> dish.getCalories() > 500)
                     .collect(groupingBy(Dish::getType));
```

위 코드로 문제를 해결할 수 있지만 단점도 존재한다. 우리의 메뉴 요리는 다음처럼 맵 형태로 되어 있으므로 우리 코드에 위 기능을 사용하려면 맵에 코드를 적용해야한다.

`{OTHER=[french fries, pizza], MEAT=[pork, beef]}`

여기서 문제는, `FISH` 키가 사라졌다는 것이다. 우리의 필터 프레디케이트를 만족하는 `FISH` 종류 요리는 없으므로 결과 맵에서 해당 키 자체가 사라진다. `Collectors` 클래스는 일반적인 분류 함수에 `Collector` 형식의 두 번째 인수를 갖도록 `groupingBy` 팩토리 메서드를 오버로드해 이 문제를 해결한다. 다음 코드에서 보여주는 것처럼 두 번째 `Collector` 안으로 필터 프레디케이트를 이동함으로 이 문제를 해결할 수 있다.

```java
Map<Dish.Type, List<Dish>> caloricDishesByType =
    menu.stream()
        .collect(groupingBy(Dish::getType,
        filtering(dish -> dish.getCalories() > 500, toList())));
```

`filtering` 메서드는 `Collectors` 클래스의 또 다른 정적 팩토리 메서드로 프레디케이트를 인수로 받는다. 이 프레디케이트로 각 그룹의 요소와 필터링 된 요소를 재그룹화 한다. 이렇게 해서 아래 결과 맵에서 볼 수 있는 것처럼 목록이 비어 있는 `FISH`도 항목으로 추가된다.

`{OTHER=[french fries, pizza], MEAT=[pork, beef], FISH=[]}`

그룹화된 항목을 조작하는 다른 유용한 기능 중 또 다른 하나로 맵핑 함수를 이용해 요소를 변환하는 작업이 있다. `filtering` 컬렉터와 같은 이유로 `Collectors` 클래스는 매핑 함수와 각 항목에 적용한 함수를 모으는 데 사용하는 또 다른 컬렉터를 인수로 받는 `mapping` 메서드를 제공한다. 예를 들어 이 함수를 이용해 그룹의 각 요리를 관련 이름 목록으로 변환할 수 있다.

```java
Map<Dish.Type, List<String>> dishNamesByType = 
    menu.stream()
        .collect(groupingBy(Dish::getType, mapping(Dish::getName, toList())));
```

`groupingBy`와 연계해 세 번째 컬렉터를 사용해서 일반 맵이 아닌 `flatMap` 변환을 수행할 수 있다. 태그 목록을 가진 각 요리로 구성된 맵이 있다고 가정했을 때, `flatMapping` 컬렉터를 이용하면 각 형식의 요리의 태그를 간편하게 추출할 수 있다.

```java
Map<Dish.Type, Set<String>> dishNamesByType = 
    menu.stream()
        .collect(groupingBy(Dish::getType,
                 flatMapping(dish -> dishTags.get(dish.getName()).stream(), toSet())));
```

현재는 각 요리에서 태그 리스트를 얻어야 한다. 따라서 두 수준의 리스트를 한 수준으로 평면화하려면 `flatMap`을 수행해야 한다. 이전처럼 각 그룹에 수행한 `flatMapping` 연산 결과를 수집해서 리스트가 아니라 집합으로 그룹화해 중복 태그를 제거한다는 사실을 주목하자. 다음은 위 연산의 결과다.

`{MEAT=[salty, greasy, roasted, fried, crisp], FISH=[roasted, tasty, fresh, delicious], OTHER=[salty, greasy, natural, light, tasty, fresh, fried]}`

두 가지 이상의 기준을 동시에 적용할 수 있으며 효과적으로 조합할 수 있다는 것이 그룹화의 장점이다.

#### 6.3.2 다수준 그룹화

두 인수를 받는 팩토리 메서드 `Collectors.groupingBy`를 이용해서 항목을 다수준으로 그룹화할 수 있다. `Collectors.groupingBy`는 일반적인 분류 함수와 컬렉터를 인수로 받는다. 즉, 바깥쪽 `groupingBy` 메서드에 스트림의 항목을 분류할 두 번째 기준을 정의하는 내부 `groupingBy`를 전달해서 두 수준으로 스트림의 항목을 그룹화할 수 있다.

```java
// 다수준 그룹화
Map<Dish.Type, Map<CaloricLevel, List<Dish>>> dishesByTypeCaloricLevel =
menu.stream().collect(
    groupingBy(Dish::getType, // 첫 번째 수준의 분류 함수
        groupingBy(dish -> { // 두 번째 수준의 분류 함수
            if (dish.getCalories() <= 400)
                return CaloricLevel.DIET;
            else if (dish.getCalories() <= 700)
                return CaloricLevel.NORMAL;
            else return CaloricLevel.FAT;
        }))
);
```

그룹화의 결과로 다음과 같은 두 수준의 맵이 만들어진다.

`{MEAT={DIET=[chicken], NORMAL=[beef], FAT=[pork]}, FISH={DIET=[prawns], NORMAL=[salmon]}, OTHER={DIET=[rice, seasonal fruit], NORMAL=[french fries, pizza]}}`

외부 맵은 첫 번째 수준의 분류 함수에서 분류한 키값 `FISH`, `MEAT`, `OHTER`를 갖는다. 그리고 외부 맵의 값은 두 번째 수준의 분류 함수의 기준 `NORMAL`, `DIET`, `FAT`을 키값으로 갖는다. 최종적으로 두 수준의 맵은 첫 번째 키와 두 번째 키의 기준에 부합하는 요소 리스트를 값(`salmon`, `pizza` 등)으로 갖는다. 다수준 그룹화 연산은 다양한 수준으로 확장할 수 있다. 즉, `n`수준 그룹화의 결과는 `n`수준 트리 구조로 표현되는 `n`수준 맵이 된다.

보통 `groupingBy`의 연산을 `버킷(양동이)` 개념으로 생각하면 쉽다. 첫 번째 `groupingBy`는 각 키의 버킷을 만든다. 그리고 준비된 각각의 버킷을 서브스트림 컬렉터로 채워가기를 반복하면서 `n`수준 그룹화를 달성한다.

#### 6.3.3 서브그룹으로 데이터 수집

사실 첫 번째 `groupingBy`로 넘겨주는 컬렉터의 형식은 제한이 없다. 예를 들어 다음 코드처럼 `groupingBy` 컬렉터에 두 번째 인수로 `counting` 컬렉터를 전달해서 메뉴에서 요리의 수를 종류별로 계산할 수 있다.

```java
Map<Dish.Type, Long> typeCount = menu.stream()
                                     .collect(groupingBy(Dish::getType, counting()));
```

다음은 결과 맵이다.

`{MEAT=3, FISH=2, OTHER=4}`

분류 함수 한 개의 인수를 갖는 `groupingBy(f)`는 사실 `groupingBy(f, toList())`의 축약형이다.

요리의 **종류**를 분류하는 컬렉터로 메뉴에서 가장 높은 칼로리를 가진 요리를 찾는 프로그램도 다시 구현할 수 있다.

```java
Map<Dish.Type, Optional<Dish>> mostCaloricByType = 
    menu.stream().collect(groupingBy(Dish::getType,
                                     maxBy(comparingInt(Dish::getCalories))));
```

그룹화의 결과로 요리의 종류를 키로, `Optional<Dish>`를 값으로 갖는 맵이 반환된다.
`Optional<Dish>`는 해당 종류의 음식 중 가장 높은 칼로리를 래핑한다.

`{FISH=Optional[salmon], OTHER=Optional[pizza], MEAT=Optional[pork]}`

>**컬렉터 결과를 다른 형식에 적용하기**
>
>마지막 그룹화 연산에서 맵의 모든 값을 `Optional`로 감쌀 필요가 없으므로 `Optional`을 삭제할 수 있다. 즉, 다음처럼 팩토리 메서드 `Collectors.collectingAndThen`으로 컬렉터가 반환한 결과를 다른 형식으로 활용할 수 있다.
>
>```java
>// 각 서브그룹에서 가장 칼로리가 높은 요리 찾기
>Map<Dish.Type, Dish> mostCaloricByType = 
>    menu.stream()
>        .collect(groupingBy(Dish::getType, // 분류 함수
>                 collectingAndThen(
>                     maxBy(comparingInt(Dish::getCalories)), // 감싸인 컬렉터
>                 Optional::get))); // 변환 함수
>```

팩토리 메서드 `collectingAndThen`은 적용할 컬렉터와 변환 함수를 인수로 받아 다른 컬렉터를 반환한다. 반환되는 컬렉터는 기존 컬렉터의 래퍼 역할을 하며 `collect`의 마지막 과정에서 변환 함수로 자신이 반환하는 값을 매핑한다. 이 예제에서는 `maxBy`로 만들어진 컬렉터가 감싸지는 컬렉터며 변환 함수 `Optional::get`으로 반환된 `Optional`에 포함된 값을 추출한다. 이미 언급했듯이 리듀싱 컬렉터는 절대 `Optional.empty()`를 반환하지 않으므로 안전한 코드다. 다음은 맵의 결과다.

`{FISH=salmon, OTHER=pizza, MEAT=pork}`

중첩 컬렉터는 가장 외부 계층에서 안쪽으로 다음과 같은 작업이 수행되면서 작동한다.

- `groupingBy`는 가장 바깥쪽에 위치하면서 요리의 종류에 따라 메뉴 스트림을 세 개의 서브스트림으로 그룹화한다.
- `groupingBy` 컬렉터는 `collectingAndThen` 컬렉터를 감싼다. 따라서 두 번째 컬렉터는 그룹화된 세 개의 서브스트림에 적용된다.
- `collectingAndThen` 컬렉터는 세 번째 컬렉터 `maxBy`를 감싼다.
- 리듀싱 컬렉터가 서브스트림에 연산을 수행한 결과에 `collectingAndThen`의 `Optional::get` 변환 함수가 적용된다.
- `groupingBy` 컬렉터가 반환하는 맵의 분류 키에 대응하는 세 값이 각각의 요리 형식에서 가장 높은 칼로리다.

>**groupingBy와 함께 사용하는 다른 컬렉터 예제**
>
>일반적으로 스트림에서 같은 그룹으로 분류된 모든 요소에 리듀싱 작업을 수행할 때는 팩토리 메서드 `groupingBy`에 두 번째 인수로 전달한 컬렉터를 사용한다. 예를 들어 메뉴에 있는 모든 요리의 칼로리 합계를 구하려고 만든 컬렉터를 재사용할 수 있다. 물론 여기서는 각 그룹으로 분류된 요리에 이 컬렉터를 활용한다.
>
>```java
>Map<Dish.Type, Integer> totalCaloriesByType = 
>    menu.stream().collect(groupingBy(Dish::getType, summingInt(Dish::getCalories)));
>```
>
>이 외에도 `mapping` 메서드로 만들어진 컬렉터도 `groupingBy`와 자주 사용된다. `mapping` 메서드는 스트림의 인수를 변환하는 함수와 변환 함수의 결과 객체를 누적하는 컬렉터를 인수로 받는다. `mapping`은 입력 요소를 누적하기 전에 매핑 함수를 적용해서 다양한 형식의 객체를 주어진 형식의 컬렉터에 맞게 변환하는 역할을 한다. 예를 들어 각 요리 형식에 존재하는 모든 `CaloricLevel`값을 알고 싶다고 가정하자. 다음 코드처럼 `groupingBy`와 `mapping` 컬렉터를 합쳐서 이 기능을 구현할 수 있다.
>
>```java
>Map<Dish.Type, Set<CaloricLevel>> caloricLevelsByType = 
>   menu.stream().collect(
>       groupingBy(Dish::getType, mapping(dish -> {
>               if (dish.getCalories() <= 400) return CaloricLevel.DIET;
>               else if (dish.getCalories() <= 700) return CaloricLevel.NORMAL;
>               else return CaloricLevel.FAT; }, toSet() )));
>```
>
>`mapping` 메서드에 전달한 변환 함수는 `Dish`를 `CaloricLevel`로 매핑한다. 그리고 `CaloricLevel` 결과 스트림은 (`toList`와 비슷한) `toSet` 컬렉터로 전달되면서 리스트가 아닌 집합으로 스트림의 요소가 누적된다(따라서 중복된 값은 저장되지 않는다). 이전 예제와 마찬가지로 그룹화 함수로 생성된 서브스트림에 `mapping` 함수를 적용하면서 다음과 같은 맵 결과가 생성된다.
>
>`{OTHER=[DIET, NORMAL], MEAT=[DIET, NORMAL, FAT], FISH=[DIET, NORMAL]}`
>
>이전 예제에서는 `Set`의 형식이 정해져 있지 않았다. 이때 `toCollection`을 이용하면 원하는 방식으로 결과를 제어할 수 있다. 예를 들어 다음처럼 메서드 참조 `HashSet::new`를 `toCollection`에 전달할 수 있다.
>
>```java
>Map<Dish.Type, Set<CaloricLevel>> caloricLevelsByType =
>   menu.stream().collect(
>        groupingBy(Dish::getType, mapping(dish -> {
>            if (dish.getCalories() <= 400) return CaloricLevel.DIET;
>            else if (dish.getCalories() <= 700) return CaloricLevel.NORMAL;
>            else return CaloricLevel.FAT; }, toCollection(HashSet::new) )));
>```

---

## 6.4 분할

분할은 **분할 함수**라 불리는 프레디케이트를 분류 함수로 사용하는 특수한 그룹화 기능이다. 분할 함수는 불리언을 반환하므로 맵의 키 형식은 `Boolean`이다. 결과적으로 그룹화 맵은 최대 (참 아니면 거짓의 값을 갖는) 두 개의 그룹으로 분류된다. 예를 들어 채식주의자 친구를 저녁에 초대했다고 가정하자. 그러면 이제 모든 요리를 채식 요리와 채식이 아닌 요리로 분류해야 한다.

```java
Map<Boolean, List<Dish>> partitionedMenu = 
             menu.stream().collect(partitioningBy(Dish::isVegetarian)); // 분할 함수
```

위 코드를 실행하면 다음과 같은 맵이 반환된다.

`{false=[pork, beef, chicken, prawns, salmon], true=[french fries, rice, season fruit, pizza]}`

이제 참값의 키로 맵에서 모든 채식 요리를 얻을 수 있다.

```java
List<Dish> vegetarianDishes = partitionedMenu.get(true);
```

물론 메뉴 리스트로 생성한 스트림을 이전 예제에서 사용한 프레디케이트로 필터링한 다음에 별도의 리스트에 결과를 수집해도 같은 결과를 얻을 수 있다.

```java
List<Dish> vegetarianDishes = menu.stream().filter(Dish::isVegetarian).collect(toList());
```

#### 6.4.1 분할의 장점

분할 함수가 반환하는 참, 거짓 두 가지 요소의 스트림 리스트를 모두 유지한다는 것이 분할의 장점이다. 이전 예제에서 `partitionedMenu` 맵에 거짓 키를 이용해서(즉, 프레디케이트와 그 결과를 반전시키는 두 가지 필터링 연산을 적용해서) 채식이 아닌 모든 요리 리스트를 얻을 수 있다. 또한 다음 코드에서 보여주는 것처럼 컬렉터를 두 번째 인수로 전달할 수 있는 오버로드된 버전의 `partitioningBy` 메서드도 있다.

```java
Map<Boolean, Map<Dish.Type, List<Dish>>> vegetarianDishesByType = 
    menu.stream()
        .collect(partitioningBy(Dish::isVegetarian, // 분할 함수
        groupingBy(Dish::getType))); // 두 번째 컬렉터
```        

다음은 위 코드를 실행한 두 수준의 맵 결과다.

`{false={FISH=[prawns, salmon], MEAT=[pork, beef, chicken]}, true={OTHER=[french fries, rice, season fruit, pizza]}}`

결과에서 확인할 수 있는 것처럼 채식 요리의 스트림과 채식이 아닌 요리의 스트림을 각각 요리 종류로 그룹화해서 두 수준의 맵이 반환되었다. 또한 이전 코드를 활용하면 채식 요리와 채식이 아닌 요리 각각의 그룹에서 가장 칼로리가 높은 요리도 찾을 수 있다.

```java
Map<Boolean, Dish> mostCaloricPartionedByVegetarian =
    menu.stream().collect(partitioningBy(Dish::isVegetarian, 
    collectingAndThen(maxBy(comparingInt(Dish::getCalories)),
        Optional::get)));
```

다음은 프로그램 실행 결과다.

`{false=pork, true=pizza}`

`partitioningBy`가 반환한 맵 구현은 참과 거짓 두 가지 키만 포함하므로 더 간결하고 효과적이다. 사실 내부적으로 `partitioningBy`는 특수한 맵과 두 개의 필드로 구현되었다. 이 외에도 `groupingBy`와 `partitioningBy` 컬렉터의 비슷한 점이 또 있다. 다수준으로 그룹화를 수행했던 것처럼 다수준으로 분할하는 기법도 있다.

#### 6.4.2 숫자를 소수와 비소수로 분할하기

정수 `n`을 인수로 받아서 `2`에서 `n`까지의 자연수를 소수와 비소수로 나누는 프로그램을 구현하자. 먼저 주어진 수가 소수인지 아닌지 판단하는 프레디케이트를 구현하면 편리할 것이다.

```java
public boolean isPrime(int candidate) {
    return IntStream.range(2, candidate) // 2부터 candidate 미만 사이의 자연수를 생성한다.
                    .noneMatch(i -> candidate % i == 0); // 스트림의 모든 정수로 candidate를 나눌 수 없으면 참을 반환한다.
}
```

다음처럼 소수의 대상을 주어진 수의 제곱근 이하의 수로 제한할 수 있다.

```java
public boolean isPrime(int candidate) {
    int candidateRoot = (int) Math.sqrt((double)candidate);
    return IntStream.rangeClosed(2, candidateRoot)
                    .noneMatch(i -> candidate % i == 0);
}
```

이제 `n`개의 숫자를 포함하는 스트림을 만든 다음에 `isPrime` 메서드를 프레디케이트로 이용하고 `partitioningBy` 컬렉터로 리듀스해서 숫자를 소수와 비소수로 분류할 수 있다.

```java
public Map<Boolean, List<Integer>> partitionPrimes(int n) {
    return IntStream.rangeClosed(2, n)
                    .boxed()
                    .collect(partitioningBy(candidate -> isPrime(candidate)));
}
```
---

## 6.5 Collector 인터페이스

`Collector` 인터페이스는 리듀싱 연산(즉, 컬렉터)을 어떻게 구현할지 제공하는 메서드 집합으로 구성된다. 물론 우리가 `Collector` 인터페이스를 구현하는 리듀싱 연산을 만들 수도 있다. 즉, `Collector` 인터페이스를 직접 구현해서 더 효율적으로 문제를 해결하는 컬렉터를 만드는 방법을 살펴본다. 여기서는 `toList`(스트림의 요소를 리스트로 수집)를 자세히 확인하면서 이해하도록 한다.

다음 코드는 `Collector` 인터페이스의 시그니처와 다섯 개의 메서드 정의를 보여준다.

```java
// Collector 인터페이스
public interface Collector<T, A, R> {
    Supplier<A> supplier();
    BiConsumer<A, T> accumulator();
    Function<A, R> finisher();
    BinaryOperator<A> combiner();
    Set<Characteristics> characteristics();
}
```

위 코드를 다음처럼 설명할 수 있다.

- `T`는 수집될 스트림 항목의 제네릭 형식이다.
- `A`는 누적자, 즉 수집 과정에서 중간 결과를 누적하는 객체의 형식이다.
- `R`은 수집 연산 결과 객체의 형식(항상 그런 것은 아니지만 대개 컬렉션 형식)이다.

예를 들어 `Stream<T>`의 모든 요소를 `List<T>`로 수집하는 `ToListCollector<T>`라는 클래스를 구현할 수 있다.

```java
public class ToListCollector<T> implements Collector<T, List<T>, List<T>>
```

이때 누적 과정에서 사용되는 객체가 수집 과정의 최종 결과로 사용된다.

#### 6.5.1 Collector 인터페이스의 메서드 살펴보기

이제 `Collector` 인터페이스에 정의된 다섯 개의 메서드를 하나씩 살펴보자. 먼저 살펴볼 네 개의 메서드는 `collect` 메서드에서 실행하는 함수를 반환하는 반면, 다섯 번째 메서드 `characteristics`는 `collect` 메서드가 어떤 최적화(예를 들면 병렬화 같은)를 이용해서 리듀싱 연산을 수행할 것인지 결정하도록 돕는 힌트 특성 집합을 제공한다.

>**supplier 메서드 : 새로운 결과 컨테이너 만들기**
>
>`supplier` 메서드는 빈 결과로 이루어진 `Supplier`를 반환해야 한다. 즉, `supplier`는 수집 과정에서 빈 누적자 인스턴스를 만드는 파라미터가 없는 함수다. `ToListCollector`처럼 누적자를 반환하는 컬렉터에서는 빈 누적자가 비어있는 스트림의 수집 과정의 결과가 될 수 있다. `ToListCollector`에서 `supplier`는 다음처럼 빈 리스트를 반환한다.
>
>```java
>public Supplier<List<T>> supplier() {
>    return () -> new ArrayList<T>();
>}
>```
>
>생성자 참조를 전달하는 방법도 있다.
>
>```java
>public Supplier<List<T>> supplier() {
>    return ArrayList::new;
>}
>```

>**accumulator 메서드 : 결과 컨테이너에 요소 추가하기**
>
>`accumulator` 메서드는 리듀싱 연산을 수행하는 함수를 반환한다. 스트림에서 `n`번째 요소를 탐색할 때 두 인수, 즉 누적자(스트림의 첫 `n-1`개 항목을 수집한 상태)와 `n`번째 요소를 함수에 적용한다. 함수의 반환값은 `void`, 즉 요소를 탐색하면서 적용하는 함수에 의해 누적자 내부상태가 바뀌므로 누적자가 어떤 값일지 단정할 수 없다. `ToListCollector`에서 `accumulator`가 반환하는 함수는 이미 탐색한 항목을 포함하는 리스트에 현재 항목을 추가하는 연산을 수행한다.
>
>```java
>public BiConsumer<List<T>, T> accumulator() {
>    return (list, item) -> list.add(item);
>}
>```
>
>다음처럼 메서드 참조를 이용하면 코드가 더 간결해진다.
>
>```java
>public BiConsumer<List<T>, T> accumulator() {
>   return List::add;
>}
>```

>**finisher 메서드 : 최종 변환값을 결과 컨테이너로 적용하기**
>
>`finisher` 메서드는 스트림 탐색을 끝내고 누적자 객체를 최종 결과로 변환하면서 누적 과정을 끝낼때 호출할 함수를 반환해야 한다. 때로는 `ToListCollector`에서 볼 수 있는 것처럼 누적자 객체가 이미 최종 결과인 상황도 있다. 이런 때는 변환 과정이 필요하지 않으므로 `finisher` 메서드는 항등 함수를 반환한다.
>
>```java
>public Function<List<T>, List<T>> finisher() {
>    return Function.identity();
>}
>```

>**combiner 메서드 : 두 결과 컨테이너 병합**
>
>리듀싱 연산에서 사용할 함수를 반환하는 네 번째 메서드 `combiner`는 스트림의 서로 다른 서브파트를 병렬로 처리할 때 누적자가 이 결과를 어떻게 처리할지 정의한다. `toList`의 `combiner`는 비교적 쉽게 구현할 수 있다. 즉, 스트림의 두 번째 서브파트에서 수집한 항목 리스트를 첫 번째 서브파트 결과 리스트의 뒤에 추가하면 된다.
>
>```java
>public BinaryOperator<List<T>> combiner() {
>    return (list1, list2) -> {
>       list1.addAll(list2);
>       return list1;
>    }
>}
>```

>**스트림의 병렬 리듀싱 수행과정**
>
>네 번째 메서드를 이용하면 스트림의 리듀싱을 병렬로 수행할 수 있다. 스트림의 리듀싱을 병렬로 수행할 때 자바 7의 포크/조인 프레임워크와 `Spliterator`를 사용한다. 다음은 스트림의 병렬 리듀싱 수행과정이다.
> - 스트림을 분할해야 하는지 정의하는 조건이 거짓으로 바뀌기 전까지 원래 스트림을 재귀적으로 분할한다(보통 분산된 작업의 크기가 너무 작아지면 병렬 수행의 속도는 순차 수행의 속도보다 느려진다. 즉, 병렬 수행의 효과가 상쇄된다. 일반적으로 프로세싱 코어의 개수를 초과하는 병렬작업은 효율적이지 않다).
> - 모든 서브스트림의 각 요소에 리듀싱 연산을 순차적으로 적용해서 서브스트림을 병렬로 처리할 수 있다.
> - 마지막에는 컬렉터의 `combiner` 메서드가 반환하는 함수로 모든 부분결과를 쌍으로 합친다. 즉, 분할된 모든 서브스트림의 결과를 합치면서 연산이 완료된다.

>**characteristics 메서드**
>
>마지막으로 `characteristics` 메서드는 컬렉터의 연산을 정의하는 `Characteristics` 형식의 불변 집합을 반환한다. `Characteristics`는 스트림을 병렬로 리듀스할 것인지 그리고 병렬로 리듀스한다면 어떤 최적화를 선택해야 할지 힌트를 제공한다. `Characteristics`는 다음 세 항목을 포함하는 열거형이다.
> - `UNORDERED` : 리듀싱 결과는 스트림 요소의 방문 순서나 누적 순서에 영향을 받지 않는다.
> - `CONCURRENT` : 다중 스레드에서 `accumulator` 함수를 동시에 호출할 수 있으며 이 컬렉터는 스트림의 병렬 리듀싱을 수행할 수 있다. 컬렉터의 플래그에 `UNORDERED`를 함께 설정하지 않았다면 데이터 소스가 정렬되어 있지 않은(즉, 집합처럼 요소의 순서가 무의미한) 상황에서만 병렬 리듀싱을 수행할 수 있다.
> - `IDENTITY_FINISH` : `finisher` 메서드가 반환하는 함수는 단순히 `identity`를 적용할 뿐이므로 이를 생략할 수 있다. 따라서 리듀싱 과정의 최종 결과로 누적자 객체를 바로 사용할 수 있다. 또한 누적자 `A`를 결과 `R`로 안전하게 형변환할 수 있다.
>
>`ToListCollector`에서 스트림의 요소를 누적하는 데 사용한 리스트가 최종 결과 형식이므로 추가 변환이 필요 없다. 따라서 `ToListCollector`는 `IDENTITY_FINISH`다. 하지만 리스트의 순서는 상관이 없으므로 `UNORDERED`다. 마지막으로 `ToListCollector`는 `CONCURRENT`다. 하지만 이미 설명했듯이 요소의 순서가 무의미한 데이터 소스여야 병렬로 실행할 수 있다.

#### 6.5.2 응용하기

지금까지 살펴본 다섯 가지 메서드를 이용해서 자신만의 커스텀 `ToListCollector`를 구현할 수 있다.

```java
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import static java.util.stream.Collector.Characteristics.CONCURRENT;
import static java.util.stream.Collector.Characteristics.IDENTITY_FINISH;

public class ToListCollector<T> implements Collector<T, List<T>, List<T>> {

    @Override
    public Supplier<List<T>> supplier() {
        return ArrayList::new; // 수집 연산의 시발점
    }

    @Override
    public BiConsumer<List<T>, T> accumulator() {
        return List::add; // 탐색한 항목을 누적하고 바로 누적자를 고친다.
    }

    @Override
    public BinaryOperator<List<T>> combiner() {
        return (list1, list2) -> { // 두 번째 콘텐츠와 합쳐서 첫 번째 누적자를 고친다.
            list1.addAll(list2); // 변경된 첫 번째 누적자를 반환한다.
            return list1;
        };
    }

    @Override
    public Function<List<T>, List<T>> finisher() {
        return Function.identity(); // 항등 함수
    }

    @Override
    public Set<Characteristics> characteristics() {
        // 컬렉터의 플래그를 IDENTITY_FINISH, CONCURRENT로 설정한다.
        return Collections.unmodifiableSet(EnumSet.of(IDENTITY_FINISH, CONCURRENT));
    }
}
```

위 구현이 `Collectors.toList` 메서드가 반환하는 결과와 완전히 같은 것은 아니지만 사소한 최적화를 제외하면 대체로 비슷하다. 특히 자바 API에서 제공하는 컬렉터는 싱글턴 `Collector.emptyList()`로 빈 리스트를 반환한다. 이제 자바에서 제공하는 API 대신 우리가 만든 컬렉터를 메뉴 스트림의 모든 요리를 수집하는 예제에 사용할 수 있다.

```java
List<Dish> dishes = menuStream.collect(new ToListCollector<Dish>);
```

다음은 기존의 코드다.

```java
List<Dish> dishes = menuStream.collect(toList());
```

기존 코드의 `toList`는 팩토리지만 `ToListCollector`는 `new`로 인스턴스화한다는 점이 다르다.

>**컬렉터 구현을 만들지 않고도 커스텀 수집 수행하기**
>`IDENTITY_FINISH` 수집 연산에서는 `Collector` 인터페이스를 완전히 새로 구현하지 않고도 같은 결과를 얻을 수 있다. `Stream`은 세 함수(발행, 누적, 합침)를 인수로 받는 `collect` 메서드를 오버로드하며 각각의 메서드는 `Collector` 인터페이스의 메서드가 반환하는 함수와 같은 기능을 수행한다. 예를 들어 다음처럼 스트림의 모든 항목을 리스트에 수집하는 방법도 있다.
>
>```java
>List<Dish> dishes = menuStream.collect(ArrayList::new, // 발행
>                                       List::add, // 누적
>                                       List::addAll); // 합침
>```

위 두 번째 코드가 이전 코드에 비해 좀 더 간결하고 축약되어 있지만 가독성은 떨어진다. 적절한 클래스로 커스텀 컬렉터를 구현하는 편이 중복을 피하고 재사용성을 높이는 데 도움이 된다. 또한 두 번째 메서드로는 `Characteristics`를 전달할 수 없다. 즉, 두 번째 `collect` 메서드는 `IDENTITY_FINISH`와 `CONCURRENT`지만 `UNORDERED`는 아닌 컬렉터로만 동작한다.

---

## 6.6 커스텀 컬렉터를 구현해서 성능 개선하기

이전에 분할을 설명하면서 `Collectors` 클래스가 제공하는 다양한 팩토리 메서드 중 하나를 이용해서 커스텀 컬렉터를 만들었다. 다음 코드에서 보여주는 것처럼 커스텀 컬렉터로 `n`까지의 자연수를 소수와 비소수로 분할할 수 있다.

```java
// n 이하의 자연수를 소수와 비소수로 분할하기
public Map<Boolean, List<Integer>> partitionPrimes(int n) {
    return IntStream.rangeClosed(2, n)
                    .boxed()
                    .collect(partitioningBy(candidate -> isPrime(candidate)));
}
```

그리고 제곱근 이하로 대상의 숫자 범위를 제한해서 `isPrime` 메서드를 개선했다.

```java
public boolean isPrime(int candidate) {
    int candidateRoot = (int) Math.sqrt((double) candidate);
    return IntStream.rangeClosed(2, candidateRoot)
                    .noneMatch(i -> candidate % i == 0);
}
```

여기서 커스텀 컬렉터를 이용하면 성능을 더 개선할 수 있다.

#### 6.6.1 소수로만 나누기

우선 소수로 나누어떨어지는지 확인해서 대상의 범위를 좁힐 수 있다. 제수가 소수가 아니면 소용없으므로 제수를 현재 숫자 이하에서 발견한 소수로 제한할 수 있다. 주어진 숫자가 소수인지 아닌지 판단해야 하는데, 그러려면 지금까지 발견한 소수 리스트에 접근해야 한다. 하지만 우리가 살펴본 컬렉터로는 컬렉터 수집 과정에서 부분결과에 접근할 수 없다. 바로 커스텀 컬렉터 클래스로 이 문제를 해결할 수 있다.

중간 결과 리스트가 있다면 `isPrime` 메서드로 중간 결과 리스트를 전달하도록 다음과 같이 코드를 구현할 수 있다.

```java
public static boolean isPrime(List<Integer> primes, int candidate) {
    return primes.stream().noneMatch(i -> candidate % i == 0);
}
```

대상 숫자의 제곱근보다 작은 소수만 사용하도록 코드를 최적화해야 한다. 즉, 다음 소수가 대상의 루트보다 크면 소수로 나누는 검사를 멈춰야 한다. 안타깝게도 스트림 API에는 이런 기능을 제공하는 메서드가 없다. `filter(p -> p <= candidate)`를 이용해서 대상의 루트보다 작은 소수를 필터링할 수 있지만 결국 `filter`는 전체 스트림을 처리한 다음에 결과를 반환하게 된다. 소수 리스트와 대상 숫자의 범위가 아주 크다면 성능 문제가 발생할 수 있다. 대상의 제곱보다 큰 소수를 찾으면 검사를 중단함으로써 성능 문제를 없앨 수 있다. 따라서 다음 코드처럼 정렬된 리스트와 프레디케이트를 인수로 받아 리스트의 첫 요소에서 시작해서 프레디케이트를 만족하는 가장 긴 요소로 이루어진 리스트를 반환하는 `takeWhile`이라는 메서드를 구현한다.

```java
public static boolean isPrime(List<Integer> primes, int candidate) {
    int candidateRoot = (int) Math.sqrt((double) candidate);
    return primes.stream()
                 .takeWhile(i -> i <= candidateRoot)
                 .noneMatch(i -> candidate % i == 0);
}
```

새로운 `isPrime` 메서드를 구현했으니 본격적으로 커스텀 컬렉터를 구현하자. 우선 `Collector` 인터페이스를 구현하는 새로운 클래스를 선언한 다음에 `Collector` 인터페이스에서 요구하는 메서드 다섯 개를 구현한다.

>**1단계 : Collector 클래스 시그니처 정의**
>
>다음의 `Collector` 인터페이스 정의를 참고해서 클래스 시그니처를 만들자.
>
>```java
>public interface Collector<T, A, R>
>```
>위 코드에서 `T`는 스트림 요소의 형식, `A`는 중간 결과를 누적하는 객체의 형식, `R`은 `collect` 연산의 최종 결과 형식을 의미한다. 우리는 정수로 이루어진 스트림에서 누적자와 최종 결과의 형식이 `Map<Boolean, List<Integer>>`인 컬렉터를 구현해야 한다. 즉, `Map<Boolean, List<Integer>>`는 참과 거짓을 키로, 소수와 소수가 아닌 수를 값으로 갖는다.
>
>```java
>public class PrimeNumbersCollector 
>        implements Collector<Integer, // 스트림 요소의 형식
>            Map<Boolean, List<Integer>>, // 누적자 형식
>            Map<Boolean, List<Integer>>> // 수집 연산의 결과 형식
>```

>**2단계 : 리듀싱 연산 구현**
>
>이번에는 `Collector` 인터페이스에 선언된 다섯 메서드를 구현해야 한다. `supplier` 메서드는 누적자를 만드는 함수를 반환해야 한다.
>
>```java
>public Supplier<Map<Boolean, List<Integer>>> supplier() {
>   return () -> new HashMap<Boolean, List<Integer>>() {
>       put(true, new ArrayList<Integer>());
>       put(false, new ArrayList<Integer>());
>   }};
>}
>```
>
>위 코드에서는 누적자로 사용할 맵을 만들면서 `true`, `false` 키와 빈 리스트로 초기화를 했다. 수집 과정에서 빈 리스트에 각각 소수와 비소수를 추가할 것이다. 스트림의 요소를 어떻게 수집할지 결정하는 것은 `accumulator` 메서드이므로 우리 컬렉터에서 가장 중요한 메서드라 할 수 있다. 물론 이미 설명한 것처럼 `accumulator`는 최적화의 핵심이기도 하다. 이제 언제든지 원할 때 수집 과정의 중간 결과, 즉 지금까지 발견한 소수를 포함하는 누적자에 접근할 수 있다.
>
>```java
>public BiConsumer<Map<Boolean, List<Integer>>, Integer> accumulator() {
>     return (Map<Boolean, List<Integer>> acc, Integer candidate) -> {
>         acc.get(isPrime(acc.get(true), candidate)) // isPrime의 결과에 따라 소수 리스트와 비소수 리스트를 만든다.
>            .add(candidate); // candidate를 알맞은 리스트에 추가한다.
>     };
>}
>```
>
>위 코드에서 지금까지 발견한 소수 리스트(누적 맵의 `true` 키로 이들 값에 접근할 수 있다)와 소수 여부를 확인하는 `candidate`를 인수로 `isPrime` 메서드를 호출했다. `isPrime`의 호출 결과로 소수 리스트 또는 비소수 리스트 중 알맞은 리스트로 `candidate`를 추가한다.

>**3단계 : 병렬 실행할 수 있는 컬렉터 만들기(가능하다면)**
>
>이번에는 병렬 수집 과정에서 두 부분 누적자를 합칠 수 있는 메서드를 만든다. 예제에서는 단순하게 두 번째 맵의 소수 리스트와 비소수 리스트의 모든 수를 첫 번째 맵에 추가하는 연산이면 충분하다.
>
>```java
>public BinaryOperator<Map<Boolean, List<Integer>>> combiner() {
>    return (Map<Boolean, List<Integer>> map1, Map<Boolean, List<Integer>> map2) -> {
>    map1.get(true).addAll(map2.get(true));
>    map1.get(false).addAll(map2.get(false));
>    return map1;
>    };
>}
>```
>
>참고로 알고리즘 자체가 순차적이어서 컬렉터를 실제 병렬로 사용할 순 없다. 따라서 `combiner` 메서드는 호출될 일이 없으므로 빈 구현으로 남겨둘 수 있다(또는 `UnsupportedOperationException`을 던지도록 구현하는 방법도 좋다). 실제로 이 메서드는 사용할 일이 없지만 학습을 목적으로 구현한 것이다.

>**4단계 : finisher 메서드와 컬렉터의 characteristics 메서드**
>
>나머지 두 메서드는 쉽게 구현할 수 있다. 이전에 설명했듯이 `accumulator`의 형식은 컬렉터 결과 형식과 같으므로 변환 과정이 필요 없다. 따라서 항등 함수 `identity`를 반환하도록 `finisher` 메서드를 구현한다.
>
>```java
>public Function<Map<Boolean, List<Integer>>, Map<Boolean, List<Integer>>> finisher() {
>     return Function.identity();
>}
>```
>
>이미 언급한 것처럼 커스텀 컬렉터는 `CONCURRENT`도 아니고 `UNORDERED`도 아니지만 `IDENTITY_FINISH`이므로 다음처럼 `characteristics` 메서드를 구현할 수 있다.
>
>```java
>public Set<Characteristics> characteristics() {
>     return Collections.unmodifiableSet(EnumSet.of(IDENTITY_FINISH));
>}
>```

다음은 `PrimeNumbersCollector`의 최종 구현 코드다.

```java
public class PrimeNumbersCollector implements Collector<Integer, Map<Boolean, List<Integer>>, Map<Boolean, List<Integer>>> {

    @Override
    public Supplier<Map<Boolean, List<Integer>>> supplier() {
        return () -> new HashMap<Boolean, List<Integer>>() {{ // 두 개의 빈 리스트를 포함하는 맵으로 수집 동작을 시작한다.
            put(true, new ArrayList<>());
            put(false, new ArrayList<>());
        }};
    }

    @Override
    public BiConsumer<Map<Boolean, List<Integer>>, Integer> accumulator() {
        return (Map<Boolean, List<Integer>> acc, Integer candidate) -> {
            acc.get(isPrime(acc.get(true), // 지금까지 발견한 소수 리스트를 isPrime 메서드로 전달한다.
                    candidate))
                    .add(candidate); // isPrime 메서드의 결과에 따라 맵에서 알맞은 리스트를 받아 현재 candidate를 추가한다.
        };
    }

    @Override
    public BinaryOperator<Map<Boolean, List<Integer>>> combiner() {
        return (Map<Boolean, List<Integer>> map1, Map<Boolean, List<Integer>> map2) -> { // 두 번째 맵을 첫 번째 맵에 병합한다.
            map1.get(true).addAll(map2.get(true));
            map1.get(false).addAll(map2.get(false));
            return map1;
        };
    }

    @Override
    public Function<Map<Boolean, List<Integer>>, Map<Boolean, List<Integer>>> finisher() {
        return Function.identity(); // 최종 수집 과정에서 데이터 변환이 필요하지 않으므로 항등 함수를 반환한다.
    }

    @Override
    public Set<Characteristics> characteristics() {
        // 발견한 소수의 순서에 의미가 있으므로 컬렉터는 IDENTITY_FINISH지만 UNORDERED, CONCURRENT는 아니다.
        return Collections.unmodifiableSet(EnumSet.of(IDENTITY_FINISH));
    }
}
```

팩토리 메서드 `partitioningBy`를 이용했던 예제를 다음처럼 우리가 만든 커스텀 컬렉터로 교체할 수 있다.

```java
public Map<Boolean, List<Integer>> partitionPrimesWithCustomCollector(int n) {
    return IntStream.rangeClosed(2, n)
                    .boxed()
                    .collect(new PrimeNumbersCollector());
}
```
---