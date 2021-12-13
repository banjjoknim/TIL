# Chapter5. 스트림 활용

다음은 데이터 컬렉션 반복을 명시적으로 관리하는 외부 반복 코드다.

```java
List<Dish> vegetarianDishes = new ArrayList<>();
for(Dish d : menu){
    if(d.isVegetarian()){
        vegetarianDishes.add(d);
    }
}
```

명시적 반복 대신 `filter`와 `collect` 연산을 지원하는 스트림 API를 이용해서 데이터 컬렉션 반복을 내부적으로 처리할 수 있다. 다음처럼 `filter` 메서드에 필터링 연산을 인수로 넣어주면 된다.

```java
import static java.util.stream.Collectors.toList;
List<Dish> vegetarianDishes = 
    menu.stream()
        .filter(Dish::isVegetarian)
        .collect(toList());
```

데이터를 어떻게 처리할지는 스트림 API가 관리하므로 편리하게 데이터 관련 작업을 할 수 있다. 따라서 스트림 API 내부적으로 다양한 최적화가 이루어질 수 있다. 스트림 API는 내부 반복 뿐 아니라 코드를 병렬로 실행할지 여부도 결정할 수 있다. 이러한 일은 순차적인 반복을 단일 스레드로 구현하는 외부 반복으로는 달성할 수 없다.

---

## 5.1 필터링

#### 5.1.1 프레디케이트로 필터링

스트림 인터페이스는 `filter` 메서드를 지원한다. `filter` 메서드는 **프레디케이트**(불리언을 반환하는 함수)를 인수로 받아서 프레디케이트와 일치하는 모든 요소를 포함하는 스트림을 반환한다. 예를 들어 메뉴에서 채식요리를 필터링해서 채식 메뉴를 만들 수 있다.

```java
List<Dish> vegetarianMenu = menu.stream()
                                .filter(Dish::isVegetarian) // 채식 요리인지 확인하는 메서드 참조
                                .collect(toList());
```

#### 5.1.2 고유 요소 필터링

스트림은 고유 요소로 이루어진 스트림을 반환하는 `distinct` 메서드도 지원한다(고유 여부는 스트림에서 만든 객체의 `hashcode`, `equals`로 결정된다). 예를 들어 다음 코드는 리스트의 모든 짝수를 선택하고 중복을 필터링한다.

```java
List<Integer> numbers = Arrays.asList(1, 2, 1, 3, 3, 2, 4);
numbers.stream()
       .filter(i -> i % 2 == 0)
       .distinct()
       .forEach(System.out::println);
```
---

## 5.2 스트림 슬라이싱

스트림의 요소를 선택하거나 스킵하는 다양한 방법을 설명한다. 프레디케이트를 이용하는 방법, 스트림의 처음 몇 개의 요소를 무시하는 방법, 특정 크기로 스트림을 줄이는 방법 등 다양한 방법을 이용해 효율적으로 이런 작업을 수행할 수 있다.

#### 5.2.1 프레디케이트를 이용한 슬라이싱

자바 9은 스트림의 요소를 효과적으로 선택할 수 있또록 `takeWhile`, `dropWhile` 두 가지 새로운 메서드를 지원한다.

>**TAKEWHILE 활용**
>
>다음과 같은 특별한 요리 목록을 갖고 있다고 가정하자.
>```java
>List<Dish> specialMenu = Arrays.List(
>    new Dish("seasonal fruit", true, 120, Dish.Type.OTHER),
>    new Dish("prawns", false, 300, Dish.Type.FISH),
>    new Dish("rice", true, 350, Dish.Type.OTHER),
>    new Dish("chicken", false, 400, Dish.Type.MEAT),
>    new Dish("french fries", true, 530, Dish.Type.OTHER));
>```
>
>어떻게 320칼로리 이하의 요리를 선택할 수 있을까?
>
>```java
>List<Dish> filteredMenu = 
>       specialMenu.stream()
>                  .filter(dish -> dish.getCalories() < 320)
>                  .collect(toList()); // seasonal fruit, prawns 목록
>```
>위 리스트는 이미 칼로리 순으로 정렬되어 있다는 사실에 주목하자. `filter` 연산을 이용하면 전체 스트림을 반복하면서 각 요소에 프레디케이트를 적용하게 된다. 따라서 리스트가 이미 정렬되어 있다는 사실을 이용해 320칼로리보다 크거나 같은 요리가 나왔을 때 반복 작업을 중단할 수 있다. 바로 `takeWhile` 연산을 이용하면 이를 간단하게 처리할 수 있다. `takeWhile`을 이용하면 무한 스트림을 포함한 모든 스트림에 프레디케이트를 적용해 스트림을 슬라이스할 수 있다.
>
>```java
>List<Dish> sliceMenu1 = 
>      specialMenu.stream()
>                 .takeWhile(dish -> dish.getCalories() < 320)
>                 .collect(toList()); // Seasonal fruit, prawns 목록
>```

>**DROPWHILE 활용**
>
>나머지 요소를 선택하려면 어떻게 해야 할까? `dropWhile`을 이용해 이 작업을 완료할 수 있다.
>
>```java
>List<Dish> sliceMenu2 = 
>      specialMenu.stream()
>                 .dropWhile(dish -> dish.getCalories() < 320)
>                 .collect(toList()); // rice, chicken, french fries 목록
>```
>
>`dropWhile`은 `takeWhile`과 정반대의 작업을 수행한다. `dropWhile`은 프레디케이트가 처음으로 거짓이 되는 지점까지 발견된 요소를 버린다. 프레디케이트가 거짓이 되면 그 지점에서 작업을 중단하고 남은 모든 요소들을 반환한다. `dropWhile`은 무한한 남은 요소를 가진 무한 스트림에서도 동작한다.

#### 5.2.2 스트림 축소

스트림은 주어진 값 이하의 크기를 갖는 새로운 스트림을 반환하는 `limit(n)` 메서드를 지원한다. 스트림이 정렬되어 있으면 최대 요소 `n`개를 반환할 수 있다. 예를 들어 다음처럼 300칼로리 이상의 세 요리를 선택해서 리스트를 만들 수 있다.

```java
List<Dish> dishes = specialMenu.stream()
                               .filter(dish -> dish.getCalories() > 320)
                               .limit(3)
                               .collect(toList()); // rice, chicken, french fries 목록
```
소스가 정렬되어 있지 않았다면 `limit`의 결과도 정렬되지 않은 상태로 반환된다.

#### 5.2.3 요소 건너뛰기

스트림은 처음 `n`개의 요소를 제외한 스트림을 반환하는 `skip(n)` 메서드를 지원한다. `n`개 이하의 요소를 포함하는 스트림에 `skip(n)`을 호출하면 빈 스트림이 반환된다. `limit(n)`과 `skip(n)`은 상호 보완적인 연산을 수행한다. 예를 들어 다음 코드는 300칼로리 이상의 처음 두 요리를 건너뛴 다음에 300칼로리가 넘는 나머지 요리를 반환한다.

```java
List<Dish> dishes = menu.stream()
                        .filter(d -> d.getCalories() > 300)
                        .skip(2)
                        .collect(toList());
```

--- 

## 5.3 매핑

특정 객체에서 특정 데이터를 선택하는 작업은 데이터 처리 과정에서 자주 수행되는 연산이다. 스트림 API의 `map`과 `flatMap` 메서드는 특정 데이터를 선택하는 기능을 제공한다.

#### 5.3.1 스트림의 각 요소에 함수 적용하기

스트림은 함수를 인수로 받는 `map` 메서드를 지원한다. 인수로 제공된 함수는 각 요소에 적용되며 함수를 적용한 결과가 새로운 요소로 매핑된다(이 과정은 기존의 값을 '고친다'라는 개념보다는 '새로운 버전을 만든다'라는 개념에 가까우므로 '**변환**'에 가까운 '**매핑**'이라는 단어를 사용한다). 예를 들어 다음은 `Dish::getName`을 `map` 메서드로 전달해서 스트림의 요리명을 추출하는 코드다.

```java
List<String> dishNames = menu.stream()
                             .map(Dish::getName)
                             .collect(toList());
```

`getName`은 문자열을 반환하므로 `map` 메서드의 출력 스트림은 `Stream<String>` 형식을 갖는다.

다음은 단어 리스트가 주어졌을 때 각 단어가 포함하는 글자 수의 리스트를 반환하는 예제이다.

```java
List<String> words = Arrays.asList("Modern", "Java", "In", "Action");
List<Integer> wordLengths = words.stream()
                                 .map(String::length)
                                 .collect(toList());
```

결론적으로 메서드 참조 `String::length`를 `map`에 전달해서 문제를 해결할 수 있다.

```java
List<Integer> dishNameLengths = menu.stream()
                                    .map(Dish::getName)
                                    .map(String::length)
                                    .collect(toList());
```

#### 5.3.2 스트림 평면화

메서드 `map`을 이용해서 리스트의 각 단어의 길이를 반환하는 방법을 확인했다. 이를 응용해서 리스트에서 **고유 문자**로 이루어진 리스트를 반환해보자. 예를 들어 ["Hello", "World"] 리스트가 있다면 결과로 ["H", "e", "l", "o", "W", "r", "d"]를 포함하는 리스트가 반환되어야 한다.

```java
words.stream()
     .map(word -> word.split(""))
     .distinct()
     .collect(toList());
```

위 코드에서 `map`으로 전달한 람다는 각 단어의 `String[]`(문자열 배열)을 반환한다는 점이 문제다. 우리가 원하는 것은 문자열의 스트림을 표현할 `Stream<String>`이다. 다행히 `flatMap`이라는 메서드를 이용해서 이 문제를 해결할 수 있다.

> **map과 Arrays.stream 활용**
>
>우선 배열 스트림 대신 문자열 스트림이 필요하다. 다음 코드에서 보여주는 것처럼 문자열 배열을 받아 스트림을 만드는 `Arrays.stream()` 메서드가 있다.
>
>```java
>String[] arrayOfWords = {"Goodbye", "World"};
>Stream<String> streamOfwords = Arrays.stream(arrayOfWords);
>```
>위 예제의 파이프라인에 `Arrays.stream()` 메서드를 적용해보자.
>```java
>words.stream()
>      .map(word -> word.split(""))
>      .map(Arrays::stream)
>      .distinct()
>      .collect(toList());
>```
>결국 스트림 리스트(엄밀히 따지면 `List<Stream<String>>)`가 만들어지면서 >문제가 해결되지 않았다. 문제를 해결하려면 먼저 각 단어를 개별 문자열로 이루어진 배열로 만든 다음에 각 배열을 별도의 스트림으로 만들어야 한다.

>**flatMap 사용**
>
>`flatMap`을 사용하면 다음처럼 문제를 해결할 수 있다.
>
>```java
>List<String> uniqueCharacters = 
>    words.stream()
>         .map(word -> word.split("")) // 각 단어를 개별 문자를 포함하는 배열로 >변환
>         .flatMap(Arrays::stream) // 생성된 스트림을 하나의 스트림으로 평면화
>         .distinct()
>         .collect(toList());
>```         
>`flatMap`은 각 배열을 스트림이 아니라 스트림의 콘텐츠로 매핑한다. 즉, `map(Arrays::stream)`과 달리 `flatMap`은 하나의 평면화된 스트림을 반환한다. 요약하면 `flatMap` 메서드는 스트림의 각 값을 다른 스트림으로 만든 다음에 모든 스트림을 하나의 스트림으로 연결하는 기능을 수행한다.

---

## 5.4 검색과 매칭

특정 속성이 데이터 집합에 있는지 여부를 검색하는 데이터 처리도 자주 사용된다. 스트림 API는 `allMatch`, `anyMatch`, `noneMatch`, `findFirst`, `findAny`등 다양한 유틸리티 메서드를 제공한다.

#### 5.4.1 프레디케이트가 적어도 한 요소와 일치하는지 확인

프레디케이트가 주어진 스트림에서 적어도 한 요소와 일치하는지 확인할 때 `anyMatch` 메서드를 이용한다.

```java
if(menu.stream().anyMatch(Dish::isVegetarian)) {
    System.out.println("The menu is (somewhat) vegetarian friendly!!");
}
```

`anyMatch`는 불리언을 반환하므로 최종 연산이다.

#### 5.4.2 프레디케이트가 모든 요소와 일치하는지 검사

`allMatch` 메서드는 `anyMatch`와 달리 스트림의 모든 요소가 주어진 프레디케이트와 일치하는지 검사한다.

```java
boolean isHealthy = menu.stream()
                        .allMatch(dish -> dish.getCalories() < 1000);
```

>**NONEMATCH**
>
>`noneMatch`는 `allMatch`와 반대 연산을 수행한다. 즉, `noneMatch`는 주어진 프레디케이트와 일치하는 요소가 없는지 확인한다.
>
>```java
>boolean isHealthy = menu.stream()
>                         .noneMatch(d -> d.getCalories() >= 1000);
>```
>`anyMatch`, `allMatch`, `noneMatch` 세 메서드는 스트림 **쇼트서킷** 기법, 즉 자바의 `&&`, `||`와 같은 연산을 활용한다.

>**쇼트서킷** 
>
>때로는 전체 스트림을 처리하지 않았더라도 결과를 반환할 수 있는데, 예를 들어 >여러 `and` 연산으로 연결된 커다란 불리언 표현식을 평가한다고 가정했을 때, >표현식에서 하나라도 거짓이라는 결과가 나오면 나머지 표현식의 결과와 상관없이 >전체 결과도 거짓이 된다. 이러한 상황을 **쇼트서킷**이라고 부른다. `allMatch`, >`noneMatch`, `findFirst`, `findAny` 등의 연산은 모든 스트림의 요소를 처리하지 >않고도 결과를 반환할 수 있다. 원하는 요소를 찾았으면 즉시 결과를 반환할 수 >있다. 마찬가지로 스트림의 모든 요소를 처리할 필요 없이 주어진 크기의 스트림을 생성하는 `limit`도 쇼트서킷 연산이다.

#### 5.4.3 요소 검색

`findAny` 메서드는 현재 스트림에서 임의의 요소를 반환한다. `findAny` 메서드를 다른 스트림연산과 연결해서 사용할 수 있다. 예를 들어 다음 코드처럼 `filter`와 `findAny`를 이용해서 채식요리를 선택할 수 있다.

```java
Optional<Dish> dish = 
    menu.stream()
        .filter(Dish::isVegetarian)
        .findAny();
```

스트림 파이프라인은 내부적으로 단일 과정으로 실행할 수 있도록 최적화된다. 즉, 쇼트서킷을 이용해서 결과를 찾는 즉시 실행을 종료한다.

>**Optional이란?**
>
>`Optional<T>` 클래스`(java.util.Optional)`는 값의 존재나 부재 여부를 표현하는 컨테이너 클래스다. 이전 예제에서 `findAny`는 아무 요소도 반환하지 않을 수도 있다. `null`은 쉽게 에러를 일으킬 수 있으므로 자바 8 라이브러리 설계자는 `Optional<T>`를 만들었다. `Optional`은 값이 존재하는지 확인하고 값이 없을 때 어떻게 처리할지 강제하는 기능을 제공한다.
> - `isPresent()`는 `Optional`이 값을 포함하면 참(`true`)을 반환하고, 값을 포함하지 않으면 거짓(`false`)을 반환한다.
> - `ifPresent(Consumer<T> block)`은 값이 있으면 주어진 블록을 실행한다. `Consumer` 함수형 인터페이스에는 `T` 형식의 인수를 받으며 `void`를 반환하는 람다를 전달할 수 있다.
> - `T get()`은 값이 존재하면 값을 반환하고, 값이 없으면 `NoSuchElementException`을 일으킨다.
> - `T orElse(T other)`는 값이 있으면 값을 반환하고, 값이 없으면 기본값을 반환한다.
>
>예를 들어 이전 예제의 `Optional<Dish>`에서는 요리명이 `null`인지 검사할 필요가 없었다.
>
>```java
>menu.stream()
>    .filter(Dish::isVegetarian)
>    .findAny() // Optional<Dish> 반환
>    .ifPresent(dish -> System.out.println(dish.getName()));
>```

#### 5.4.4 첫 번째 요소 찾기

리스트 또는 정렬된 연속 데이터로부터 생성된 스트림처럼 일부 스트림에는 **논리적인 아이템 순서**가 정해져 있을 수 있다. 이런 스트림에서 첫 번째 요소를 찾으려면 어떻게 해야 할까? 예를 들어 숫자 리스트에서 3으로 나누어떨어지는 첫 번째 제곱값을 반환하는 다음 코드를 살펴보자.

```java
List<Integer> someNumbers = Arrays.asList(1, 2, 3, 4, 5);
Optional<Integer> firstSquareDivisibleByThree =
    someNumbers.stream()
               .map(n -> n * n)
               .filter(n -> n % 3 == 0)
               .findFirst(); // 9
```

---

## 5.5 리듀싱

'메뉴의 모든 칼로리의 합계를 구하시오', '메뉴에서 칼로리가 가장 높은 요리는?' 같이 모든 스트림 요소를 처리해서 값으로 도출하는 연산을 **리듀싱 연산**이라고 한다. 함수형 프로그래밍 언어 용어로는 이 과정이 마치 종이(우리의 스트림)를 작은 조각이 될 때까지 반복해서 접는 것과 비슷하다는 의미로 **폴드**라고 부른다.

#### 5.5.1 요소의 합

`reduce` 메서드를 살펴보기 전에 `for-each` 루프를 이용해서 리스트의 숫자 요소를 더하는 코드를 확인하자.

```java
int sum = 0;
for (int x : numbers) {
    sum += x;
}
```
`numbers`의 각 요소는 결과에 반복적으로 더해진다. 리스트에서 하나의 숫자가 남을 때까지 `reduce` 과정을 반복한다. 코드에는 파라미터를 두 개 사용했다.

- `sum` 변수의 초깃값 `0`
- 리스트의 모든 요소를 조합하는 연산(`+`)

이런 상황에서 `reduce`를 이용하면 애플리케이션의 반복된 패턴을 추상화할 수 있다. `reduce`를 이용해서 다음처럼 스트림의 모든 요소를 더할 수 있다.

```java
int sum = numbers.stream().reduce(0, (a, b) -> a + b);
```

`reduce`는 두 개의 인수를 갖는다.

- 초깃값 `0`
- 두 요소를 조합해서 새로운 값을 만드는 `BinaryOperator<T>`. 예제에서는 람다 표현식 `(a, b) -> a + b`를 사용했다.

`reduce`로 다른 람다, 즉 `(a, b) -> a * b`를 넘겨주면 모든 요소에 곱셈을 적용할 수 있다.

```java
int product = numbers.stream().reduce(1, (a, b) -> a * b);
```

스트림이 하나의 값으로 줄어들 때까지 람다는 각 요소를 반복해서 조합한다.

메서드 참조를 이용해서 이 코드를 좀 더 간결하게 만들 수 있다. 자바 8에서는 `Integer` 클래스에 두 숫자를 더하는 정적 `sum` 메서드를 제공한다. 따라서 직접 람다 코드를 구현할 필요가 없다.

```java
int sum = numbers.stream().reduce(0, Integer::sum);
```

>**초깃값 없음**
>
>초깃값을 받지 않도록 오버로드된 `reduce`도 있다. 그러나 이 `reduce`는 `Optional` 객체를 반환한다.
>```java
>Optional<Integer> sum = numbers.stream().reduce((a, b) -> (a + b));
>```
>
>왜 `Optional<Integer>`를 반환하는 걸까? 스트림에 아무 요소도 없는 상황을 생각해보자. 이런 상황이라면 초깃값이 없으므로 `reduce`는 합계를 반환할 수 없다. 따라서 합계가 없음을 가리킬 수 있도록 `Optional` 객체로 감싼 결과를 반환한다.

#### 5.5.2 최댓값과 최솟값

최댓값과 최솟값을 찾을 때도 `reduce`를 활용할 수 있다. `reduce`를 이용해서 스트림에서 최댓값과 최솟값을 찾는 방법을 살펴보자. `reduce`는 두 인수를 받는다.

- 초깃값
- 스트림의 두 요소를 합쳐서 하나의 값으로 만드는 데 사용할 람다

`reduce` 연산은 새로운 값을 이용해서 스트림의 모든 요소를 소비할 때까지 람다를 반복 수행하면서 값을 생산한다. 다음처럼 `reduce`를 이용해서 스트림의 최댓값을 찾을 수 있다.

```java
Optional<Integer> max = numbers.stream().reduce(Integer::max);
```

`Integer.max` 대신 `Integer.min`을 `reduce`로 넘겨주면 최솟값을 찾을 수 있다.

```java
Optional<Integer> min = numbers.stream().reduce(Integer::min);
```

`Integer::min` 대신 람다 표현식 `(x, y) -> x < y ? x : y`를 사용해도 무방하지만 메서드 참조 표현이 더 읽기 쉽다.

---

## 5.6 실전 연습

스트림은 최댓값이나 최솟값을 계산하는 데 사용할 키를 지정하는 `Comparator`를 인수로 받는 `min`과 `max` 메서드를 제공한다. 따라서 `min`과 `max`를 이용하면 더 쉽게 문제를 해결할 수 있다.
- [연습코드 참고](https://github.com/banjjoknim/TIL/blob/master/Modern-Java-In-Action/src/Chapter5/Practice5_6.java)
---

## 5.7 숫자형 스트림

예를 들어 다음처럼 메뉴의 칼로리 합계를 계산할 수 있다.

```java
int calories = menu.stream()
                   .map(Dish::getCalories)
                   .reduce(0, Integer::sum);
```

사실 위 코드에는 박싱 비용이 숨어있다. 내부적으로 합계를 계산하기 전에 `Integer`를 기본형으로 언박싱해야 한다. 다음 코드처럼 직접 `sum` 메서드를 호출할 수 있다면 더 좋지 않을까?

```java
int calories = menu.stream()
                   .map(Dish::getCalories)
                   .sum();
```                   

하지만 위 코드처럼 `sum` 메서드를 직접 호출할 수 없다. `map` 메서드가 `Stream<T>`를 생성하기 때문이다. 스트림의 요소 형식은 `Integer`지만 인터페이스에는 `sum` 메서드가 없다. 왜 `sum` 메서드가 없을까? 예를 들어 `menu`처럼 `Stream<Dish>` 형식의 요소만 있다면 `sum`이라는 연산을 수행할 수 없기 때문이다. 다행히도 스트림 API 숫자 스트림을 효율적으로 처리할 수 있도록 **기본형 특화 스트림**을 제공한다.

#### 5.7.1 기본형 특화 스트림

자바 8에서는 세 가지 기본형 특화 스트림을 제공한다. 스트림 API는 박싱 비용을 피할 수 있도록 `int` 요소에 특화된 `IntStream`, `double` 요소에 특화된 `DoubleStream`, `long` 요소에 특화된 `LongStream`을 제공한다. 각각의 인터페이스는 숫자 스트림의 합계를 계산하는 `sum`, 최댓값 요소를 검색하는 `max` 같이 자주 사용하는 숫자 관련 리듀싱 연산 수행 메서드를 제공한다. 또한 필요할 때 다시 객체 스트림으로 복원하는 기능도 제공한다. 특화 스트림은 오직 박싱 과정에서 일어나는 효율성과 관련 있으며 스트림에 추가 기능을 제공하지는 않는다.

>**숫자 스트림으로 매핑**
>
>스트림을 특화 스트림으로 변환할 때는 `mapToInt`, `mapToDouble`, `mapToLong` 세 가지 메서드를 가장 많이 사용한다. 이들 메서드는 `map`과 정확히 같은 기능을 수행하지만, `Stream<T>` 대신 특화된 스트림을 반환한다.
>
>```java
>int calories = manu.stream()
>                    .mapToInt(Dish::getCalories)
>                    .sum();
>```
>`mapToInt` 메서드는 각 요리에서 모든 칼로리(`Integer` 형식)를 추출한 다음에 `IntStream`(`Stream<Integer>`가 아님)을 반환한다. 따라서 `IntStream` 인터페이스에서 제공하는 `sum` 메서드를 이용해서 칼로리 합계를 계산할 수 있다. 스트림이 비어있으면 `sum`은 기본값 `0`을 반환한다. `IntStream`은 `max`, `min`, `average` 등 다양한 유틸리티 메서드도 지원한다.

>**객체 스트림으로 복원하기**
>
>`IntStream`은 기본형의 정수값만 만들 수 있다. `IntStream`의 `map` 연산은 `int`를 인수로 받아서 `int`를 반환하는 람다(`IntUnaryOperator`)를 인수로 받는다. 하지만 정수가 아닌 `Dish` 같은 다른 값을 반환하고 싶다면 스트림 인터페이스에 정의된 일반적인연산을 사용해야 한다.
>
>```java
>IntStream intStream = menu.stream().mapToInt(Dish::getCalories); // 스트림을 숫자 스트림으로 변환
>Stream<Integer> stream = intStream.boxed(); // 숫자 스트림을 스트림으로 변환
>```

>**기본값 : OptionalInt**
>
>`Optional`을 `Integer`, `String` 등의 참조 형식으로 파라미터화할 수 있다. 또한 `OptionalInt`, `OptionalDouble`, `OptionalLong` 세 가지 기본형 특화 스트림 버전도 제공한다.
>
>예를 들어 다음처럼 `OptionalInt`를 이용해서 `IntStream`의 최댓값 요소를 찾을 수 있다.
>
>```java
>OptionalInt maxCalories = menu.stream()
>                              .mapToInt(Dish::getCalories)
>                              .max();
>```
>
>`OptionalInt`를 이용해서 최댓값이 없는 상황에 사용할 기본값을 명시적으로 정의할 수 있다.
>```java
>int max = maxCalories.orElse(1); // 값이 없을 때 기본 최댓값을 명시적으로 설정
>```

#### 5.7.2 숫자 범위

자바 8의 `IntStream`과 `LongStream`에서는 `range`와 `rangeClosed`라는 두 가지 정적 메서드를 제공한다. 두 메서드 모두 첫 번째 인수로 시작값을, 두 번째 인수로 종료값을 갖는다. `range` 메서드는 시작값과 종료값이 결과에 포함되지 않는 반면 `rangeClosed`는 시작값과 종료값이 결과에 포함된다는 점이 다르다.

```java
IntStream evenNumbers = IntStream.rangeClosed(1, 100) // [1, 100]의 범위를 나타낸다.
                                 .filter(n -> n % 2 == 0);
System.out.println(evenNumbers.count()); // 1부터 100까지에는 50개의 짝수가 있다.
```

위 코드처럼 `rangeClosed`를 이용해서 `1`부터 `100`까지의 숫자를 만들 수 있다. `rangeClosed`의 결과는 스트림으로 `filter` 메서드를 이용해서 짝수만 필터링할 수 있다. `filter`를 호출해도 실제로는 아무 계산도 이루어지지 않는다. 최종적으로 결과 스트림에 `count`를 호출한다. `count`는 최종 연산이므로 스트림을 처리해서 `1`부터 `100`까지의 숫자 범위에서 짝수 `50`개를 반환한다. 이때 `rangeClosed` 대신에 `IntStream.range(1, 100)`을 사용하면 `100`을 포함하지 않으므로 짝수 `49`개를 반환한다.

## 5.8 스트림 만들기

#### 5.8.1 값으로 스트림 만들기

임의의 수를 인수로 받는 정적 메서드 `Stream.of`를 이용해서 스트림을 만들 수 있다.

```java
Stream<String> stream = Stream.of("Modern", "Java", "In", "Action");
stream.map(String::toUpperCase).forEach(System.out::println);
```

다음처럼 `empty` 메서드를 이용해서 스트림을 비울 수 있다.

```java
Stream<String> emptyStream = Stream.empty();
```

#### 5.8.2 null이 될 수 있는 객체로 스트림 만들기

자바 9에서는 `null`이 될 수 있는 객체를 스트림으로 만들 수 있는 새로운 메소드가 추가되었다. 때로는 `null`이 될 수 있는 객체를 스트림(객체가 `null`이라면 빈 스트림)으로 만들어야 할 수 있다. 예를 들어 `System.getProperty`는 제공된 키에 대응하는 속성이 없으면 `null`을 반환한다. 이런 메소드를 스트림에 활용하려면 다음처럼 `null`을 명시적으로 확인해야 했다.

```java
String homeValue = System.getProperty("home");
Stream<String> homeValueStream = homeValue == null ? Stream.empty() : Stream.of(value);
```

`Stream.ofNullable`을 이용해 다음처럼 코드를 구현할 수 있다.

```java
Stream<String> homeValueStream = Stream.ofNullable(System.getProperty("home"));
```

`null`이 될 수 있는 객체를 포함하는 스트림값을 `flatMap`과 함께 사용하는 상황에서는 이 패턴을 더 유용하게 사용할 수 있다.

```java
Stream<String> values = Stream.of("config", "homg", "user")
                              .flatMap(key -> Stream.ofNullable(System.getProperty(key)));
```

#### 5.8.3 배열로 스트림 만들기

배열을 인수로 받는 정적 메서드 `Arrays.stream`을 이용해서 스트림을 만들 수 있다. 예를 들어 다음처럼 기본형 `int`로 이루어진 배열을 `IntStream`으로 변환할 수 있다.

```java
int[] numbers = {2, 3, 5, 7, 11, 13};
int sum = Arrays.stream(numbers).sum(); // 합계는 41
```

#### 5.8.4 파일로 스트림 만들기

파일을 처리하는 등의 `I/O` 연산에 사용하는 자바의 `NIO API`(비블록 `I/O`)도 스트림 API를 활용할 수 있도록 업데이트되었다. `java.nio.file.Files`의 많은 정적 메서드가 스트림을 반환한다. 예를 들어 `Files.lines`는 주어진 파일의 행 스트림을 문자열로 반환한다. 지금까지 배운 스트림 연산을 활용하면 다음 코드처럼 파일에서 고유한 단어 수를 찾는 프로그램을 만들 수 있다.

```java
long uniqueWords = 0;
    try (Stream<String> lines =
                Files.lines(Paths.get("data.txt"), Charset.defaultCharset())) { // 스트림은 자원을 자동으로 해제할 수 있는 AutoCloseable이므로 try-finally가 필요없다.
            uniqueWords = lines.flatMap(line -> Arrays.stream(line.split(" "))) // 단어 스트림 생성
                            .distinct() // 중복 제거
                            .count(); // 고유 단어 수 계산
        } catch (IOException e) { // 파일을 열다가 예외가 발생하면 처리한다.

        }
```

`Files.lines`로 파일의 각 행 요소를 반환하는 스트림을 얻을 수 있다. 스트림의 소스가 `I/O` 자원이므로 이 메소드를 `try/catch` 블록으로 감쌌고 메모리 누수를 막으려면 자원을 닫아야 한다. 기존에는 `finally` 블록에서 자원을 닫았다. `Stream` 인터페이스는 `AutoCloseable` 인터페이스를 구현한다. 따라서 `try` 블록 내의 자원은 자동으로 관리된다.

#### 5.8.5 함수로 무한 스트림 만들기

스트림 API는 함수에서 스트림을 만들 수 있는 두 정적 메서드 `Stream.iterate`와 `Stream.generate`를 제공한다. 두 연산을 이용해서 **무한 스트림**, 즉 고정된 컬렉션에서 고정된 크기로 스트림을 만들었던 것과는 달리 크기가 고정되지 않은 스트림을 만들 수 있다. `iterate`와 `generate`에서 만든 스트림은 요청할 때마다 주어진 함수를 이용해서 값을 만든다. 따라서 무제한으로 값을 계산할 수 있다. 하지만 보통 무한한 값을 출력하지 않도록 `limit(n)` 함수를 함께 연결해서 사용한다.

>**iterate 메서드**
>
>먼저 `iterate`를 사용하는 방법을 살펴보자.
>
>```java
>Stream.iterate(0, n -> n + 2)
>       .limit(10)
>       .forEach(System.out::println);
>```       
>`iterate` 메서드는 초깃값(예제에서는 0)과 람다(예제에서는 `UnaryOperator<T>` 사용)를 인수로 받아서 새로운 값을 끊임없이 생산할 수 있다. 기본적으로 `iterate`는 기존 결과에 의존해서 순차적으로 연산을 수행한다. `iterate`는 요청할 때마다 값을 생산할 수 있으며 끝이 없으므로 **무한 스트림**을 만든다. 이러한 스트림을 **언바운드 스트림**이라고 표현한다. 바로 이런 특징이 스트림과 컬렉션의 가장 큰 차이점이다. 예제에서는 `limit` 메서드를 이용해서 스트림의 크기를 명시적으로 처음 10개의 짝수로 제한한다. 그리고 최종 연산인 `forEach`를 호출해서 스트림을 소비하고 개별 요소를 출력한다.
>
>일반적으로 연속된 일련의 값을 만들 때는 `iterate`를 사용한다. 예를 들어 1월 13일, 2월 1일 등의 날짜를 생성할 수 있다.
>
>다음은 `iterate` 메서드로 피보나치수열을 출력하는 예제다. 
>
>```java
>        // 피보나치수열 집합
>        Stream.iterate(new int[]{0, 1}, (arr) -> new int[]{arr[1], arr[0] + arr[1]})
>                .limit(20)
>                .forEach(t -> System.out.println("(" + t[0] + ", " + t[1] + ")"));
>
>        //피보나치수열
>        Stream.iterate(new int[]{0, 1}, (arr) -> new int[]{arr[1], arr[0] + arr[1]})
>                .limit(20)
>                .map(arr -> arr[0])
>                .forEach(System.out::println);
>```
>
>자바 9의 `iterate` 메소드는 프레디케이트를 지원한다. 예를 들어 `0`에서 시작해서 `100`보다 크면 숫자 생성을 중단하는 코드를 다음처럼 구현할 수 있다.
>
>```java
>IntStream.iterate(0, n < 100, n -> n + 4)
>         .forEach(System.out::println);
>```
>
>`iterater` 메소드는 두 번째 인수로 프레디케이트를 받아 언제까지 작업을 수행할 것인지의 기준으로 사용한다. `filter` 동작으로도 같은 결과를 얻을 수 있다고 생각할 수 있다.
>
>```java
>IntStream.iterate(0, n -> n + 4)
>         .filter(n -> n < 100)
>         .forEach(System.out::println);
>```         
>
>하지만 이와 같은 방법으로는 같은 결과를 얻을 수 없다. 실제로 위 코드는 종료되지 않는다. `filter` 메소드는 언제 이 작업을 중단해야 하는지를 알 수 없기 때문이다. 스트림 쇼트서킷을 지원하는 `takeWhile`을 이용하는 것이 해법이다.
>
>```java
>IntStream.iterate(0, n -> n + 4)
>         .takeWhile(n -> n < 100)
>         .forEach(System.out::println);
>```

>**generate 메서드**
>
>`iterate`와 비슷하게 `generate`도 요구할 때 값을 계산하는 무한 스트림을 만들 수 있다. 하지만 `iterate`와 달리 `generate`는 생산된 각 값을 연속적으로 계산하지 않는다. `generate`는 `Supplier<T>`를 인수로 받아서 새로운 값을 생산한다.
>
>```java
>Stream.generate(Math::random)
>      .limit(5)
>      .forEach(System.out::println);
>```
>
>이 코드는 `0`에서 `1` 사이에서 임의의 더블 숫자 다섯 개를 만든다.
>`Math.random`은 임의의 새로운 값을 생성하는 정적 메서드다. 이번에도 명시적으로 `limit` 메서드를 이용해서 스트림의 크기를 한정했다. `limit`가 없다면 스트림은 언바운드 상태가 된다.
>
>여기서 사용한 발행자(`Supplier : 메서드 참조 Math.random`)는 상태가 없는 메서드, 즉 나중에 계산에 사용할 어떤 값도 저장해두지 않는다. 하지만 발행자에 꼭 상태가 없어야 하는 것은 아니다. 발행자가 상태를 저장한 다음에 스트림의 다음 값을 만들 때 상태를 고칠 수도 있다. 하지만 병렬 코드에서는 발행자에 상태가 있으면 안전하지 않다. 따라서 가급적 피하는 것이 좋다.
>
>`IntStream`을 이용하면 박싱 연산 문제를 피할 수 있다. `IntStream`의 `generate` 메서드는 `Supplier<T>` 대신에 `IntSupplier`를 인수로 받는다. 다음은 무한 스트림을 생성하는 코드다.
>
>```java
>IntStream ones = IntStream.generate(() -> 1);
>```
>
>다음처럼 `IntSupplier` 인터페이스에 정의된 `getAsInt`를 구현하는 객체를 명시적으로 전달할 수도 있다.
>
>```java
>IntStream twos = IntStream.generate(new IntSupplier(){
>    public int getAsInt(){
>       return 2;
>    }
>});
>```
>
>`generate` 메서드는 주어진 발행자를 이용해서 `2`를 반환하는 `getAsInt` 메서드를 반복적으로 호출할 것이다. 여기서 사용한 익명 클래스와 람다는 비슷한 연산을 수행하지만 익명 클래스에서는 `getAsInt` 메서드의 연산을 커스터마이즈할 수 있는 상태 필드를 정의할 수 있다는 점이 다르다. 바로 부작용이 생길 수 있음을 보여주는 예제다. 지금까지 살펴본 람다는 부작용이 없었다. 즉, 람다는 상태를 바꾸지 않는다.
>
>아래는 다음 피보나치 요소를 반환하도록 `IntSupplier`를 구현한 코드다.
>```java
>//generate를 이용한 피보나치수열
>        IntSupplier fib = new IntSupplier() {
>            private int previous = 0;
>            private int current = 1;
>            @Override
>            public int getAsInt() {
>                int oldPrevious = this.previous;
>                int nextValue = this.previous + this.current;
>                this.previous = this.current;
>                this.current = nextValue;
>                return oldPrevious;
>            }
>        };
>        IntStream.generate(fib)
>                .limit(10)
>                .forEach(System.out::println);
>```
>
>위 코드에서는 `IntSupplier` 인스턴스를 만들었다. 만들어진 객체는 기존 피보나치 요소와 두 인스턴스 변수에 어떤 피보나치 요소가 들어있는지 추적하므로 **가변** 상태 객체다. `getAsInt`를 호출하면 객체 상태가 바뀌며 새로운 값을 생산한다. `iterate`를 사용했을 때는 각 과정에서 새로운 값을 생성하면서도 기존 상태를 바꾸지 않는 순수한 **불변** 상태를 유지했다. 스트림을 병렬로 처리하면서 올바른 결과를 얻으려면 **불변 상태 기법**을 고수해야 한다.

무한한 크기를 가진 스트림을 처리할때는 `limit`를 이용해서 명시적으로 스트림의 크기를 제한해야 한다. 그렇지 않으면 최종 연산(예제에서는 `forEach`)을 수행했을 때 아무 결과도 계산되지 않는다. 마찬가지로 무한 스트림의 요소는 무한적으로 계산이 반복되므로 정렬하거나 리듀스할 수 없다.

---
