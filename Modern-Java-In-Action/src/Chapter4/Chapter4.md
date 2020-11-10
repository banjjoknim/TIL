# Chapter4. 스트림 소개

---

## 4.1 스트림이란 무엇인가?

**스트림**은 자바 8 API에 새로 추가된 기능이다. 스트림을 이용하면 선언형(즉, 데이터를 처리하는 임시 구현 코드 대신 질의로 표현할 수 있다)으로 컬렉션 데이터를 처리할 수 있다.
또한 스트림을 이용하면 멀티스레드 코드를 구현하지 않아도 데이터를 **투명하게** 병렬로 처리할 수 있다.

다음은 기존 코드다(자바 7)
```java
List<Dish> lowCaloricDishes = new ArrayList<>();
for(Dish dish : menu) {
    if(dish.getCalories() < 400>) {
        lowCaloricDishes.add(dish);
    }
}
Collections.sort(lowCaloricDishes, new Comparator<Dish>() {
    public int compare(Dish dish1, Dish dish2) {
        return Integer.compare(dish1.getCalories(), dish2.getCalories());
    }
});
List<String> lowCaloricDishesName = new ArrayList<>();
for(Dish dish : lowCaloricDishes) {
    lowCaloriDishesName.add(dish.getName());
}
```

다음은 최신 코드다(자바 8)
```java
import static java.util.Comparator.comparing;
import static java.util.Collectors.toList;
List<String> lowCaloricDishesName = 
            menu.stream()
                .filter(d -> d.getCalories() < 400) // 400칼로리 이하의 요리 선택
                .sorted(comparing(Dish::getCalories)) // 칼로리로 요리 정렬
                .map(Dish::getName) // 요리명 추출
                .collect(toList()); // 모든 요리명을 리스트에 저장
```

`stream()`을 `parallelStream()`으로 바꾸면 이 코드를 멀티코어 아키텍쳐에서 병렬로 실행할 수 있다.
```java
List<String> lowCaloricDishesName = 
            menu.parallelStream()
                .filter(d -> d.getCalories() < 400)
                .sorted(comparing(Dish::getCalories))
                .map(Dish::getName)
                .collect(toList());
```

**스트림의 새로운 기능은 소프트웨어공학적으로 다음의 다양한 이득을 가져다 준다.**
- 선언형으로 코드를 구현할 수 있다. 즉, 루프와 `if 조건문` 등의 제어 블록을 사용해서 어떻게 동작을 구현할지 지정할 필요 없이 '저칼로리의 요리만 선택하라' 같은 동작의 수행을 지정할 수 있다. 선언형 코드와 동작 파라미터화를 활용하면 변하는 요구사항에 쉽게 대응할 수 있다. 즉, 기존 코드를 복사하여 붙여 넣는 방식을 사용하지 않고 람다 표현식을 이용해서 저칼로리 대신 고칼로리의 요리만 필터링하는 코드도 쉽게 구현할 수 있다.
- `filter, sorted, map, collect` 같은 여러 빌딩 블록 연산을 연결해서 복잡한 데이터 처리 파이프라인을 만들 수 있다. 여러 연산을 파이프라인으로 연결해도 여전히 가독성과 명확성이 유지된다. `filter` 메서드의 결과는 `sorted` 메서드로, 다시 `sorted` 결과는 `map` 메서드로, `map` 메서드의 결과는 `collect`로 연결된다.

자바 8의 스트림 API의 특징을 다음처럼 요약할 수 있다.
- **선언형** : 더 간결하고 가독성이 좋아진다.
- **조립할 수 있음** : 유연성이 좋아진다.
- **병렬화** : 성능이 좋아진다.

---

## 4.2 스트림 시작하기

스트림이란 정확히 뭘까?(스트림의 인터페이스 정의는 `java.util.stream.Stream 참고`)
스트림이란 '**데이터 처리 연산을 지원하도록 소스에서 추출된 연속된 요소**'로 정의할 수 있다. 이 정의를 하나씩 살펴보자.

- **연속된 요소** : 컬렉션과 마찬가지로 스트림은 특정 요소 형식으로 이루어진 연속된 값 집합의 인터페이스를 제공한다. 컬렉션은 자료구조이므로 컬렉션에서는 (예를 들어 `ArrayList`를 사용할 것인지 아니면 `LinkedList`를 사용할 것인지에 대한) 시간과 공간의 복잡성과 관련된 요소 저장 및 접근 연산이 주를 이룬다. 반면 스트림은 `filter, sorted, map`처럼 표현 계산식이 주를 이룬다. 즉, 컬렉션의 주제는 데이터고 스트림의 주제는 계산이다.
- **소스** : 스트림은 `컬렉션`, `배열`, `I/O` 자원 등의 데이터 제공 소스로부터 데이터를 소비한다. 정렬된 컬렉션으로 스트림을 생성하면 정렬이 그대로 유지된다. 즉, 리스트로 스트림을 만들면 스트림의 요소는 리스트의 요소와 같은 순서를 유지한다.
- **데이터 처리 연산** : 스트림은 함수형 프로그래밍 언어에서 일반적으로 지원하는 연산과 데이터베이스와 비슷한 연산을 지원한다. 예를 들어 `filter, map, reduce, find, match, sort` 등으로 데이터를 조작할 수 있다. 스트림 연산은 순차적으로 또는 병렬로 실행할 수 있다.

또한 스트림에는 다음과 같은 두 가지 중요 특징이 있다.
- **파이프라이닝** : 대부분의 스트림 연산은 스트림 연산끼리 연결해서 커다란 파이프라인을 구성할 수 있도록 스트림 자신을 반환한다. 그 덕분에 **게으름, 쇼트서킷** 같은 최적화도 얻을 수 있다. 연산 파이프라인은 데이터 소스에 적용하는 데이터베이스 질의와 비슷하다.
- **내부 반복** : 반복자를 이용해서 명시적으로 반복하는 컬렉션과 달리 스트림은 내부 반복을 지원한다.

```java
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class Dish {
    private final String name;
    private final boolean vegetarian;
    private final int calories;
    private final Type type;

    private final List<Dish> menu = Arrays.asList(
            new Dish("pork", false, 800, Type.MEAT),
            new Dish("beef", false, 700, Type.MEAT),
            new Dish("chicken", false, 400, Type.MEAT),
            new Dish("french fries", true, 530, Type.OTHER),
            new Dish("rice", true, 350, Type.OTHER),
            new Dish("season fruit", true, 120, Type.OTHER),
            new Dish("pizza", true, 550, Type.OTHER),
            new Dish("prawns", false, 300, Type.FISH),
            new Dish("salmon", false, 450, Type.FISH)
    );

    public Dish(String name, boolean vegetarian, int calories, Type type) {
        this.name = name;
        this.vegetarian = vegetarian;
        this.calories = calories;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public boolean isVegetarian() {
        return vegetarian;
    }

    public int getCalories() {
        return calories;
    }

    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return name;
    }

    public enum Type {MEAT, FISH, OTHER} // todo: enum이 뭐지?

    List<String> threeHighCaloricDishNames =
            menu.stream()
                    .filter(dish -> dish.getCalories() > 300)
                    .map(Dish::getName)
                    .limit(3)
                    .collect(toList());
    System.out.println(threeHighCaloricDishNames); // 결과는 [pork, beef, chicken]이다.
}
```

`filter, map, limit, collect`는 각각 다음 작업을 수행한다.
- **filter** : 람다을 인수로 받아 스트림에서 특정 요소를 제외시킨다.
- **map** : 람다를 이용해서 한 요소를 다른 요소로 변환하거나 정보를 추출한다.
- **limit** : 정해진 개수 이상의 요소가 스트림에 저장되지 못하게 스트림 크기를 축소 `truncate`한다.
- **collect** : 스트림을 다른 형식으로 변환한다.

---

## 4.3 스트림과 컬렉션

자바의 기존 컬렉션과 새로운 스트림 모두 연속된 요소 형식의 값을 저장하는 자료구조의 인터페이스를 제공한다. 여기서 '**연속된**'이라는 표현은 순서와 상관없이 아무 값에나 접속하는 것이 아니라 순차적으로 값에 접근한다는 것을 의미한다.

데이터를 **언제** 연산하느냐가 컬렉션과 스트림의 가장 큰 차이다. 컬렉션은 현재 자료구조가 포함하는 **모든** 값을 메모리에 저장하는 자료구조다. 즉, 컬렉션의 모든 요소는 컬렉션에 추가하기 전에 계산되어야 한다(컬렉션에 요소를 추가하거나 컬렉션의 요소를 삭제할 수 있다. 이런 연산을 수행할 때마다 컬렉션의 모든 요소를 메모리에 저장해야 하며 컬렉션에 추가하려는 요소는 미리 계산되어야 한다).

반면 스트림은 이론적으로 **요청할 때만 요소를 계산**하는 고정된 자료구조다(스트림에 요소를 추가하거나 스트림에서 요소를 제거할 수 없다). 사용자가 요청하는 값만 스트림에서 추출한다는 것이 핵심이며, 결과적으로 스트림은 생산자와 소지바 관계를 형성한다. 또한 스트림은 게으르게 만들어지는 컬렉션과 같다. 즉, 사용자가 데이터를 요청할 때만 값을 계산한다(경영학에서는 이를 요청 중심 제조 또는 즉석 제조라고 부른다). 반면 컬렉션은 적극적으로 생성된다(생산자 중심 : 팔기도 전에 창고를 가득 채움).

- '**적극적 생성**'이란 모든 값을 계산할 때까지 기다린다는 의미다.
- '**게으른 생성**'이란 필요할 때만 값을 계산한다는 의미다.

#### 4.3.1 딱 한 번만 탐색할 수 있다

반복자와 마찬가지로 스트림도 한 번만 탐색할 수 있다. 즉, 탐색된 스트림의 요소는 소비된다. 반복자와 마찬가지로 한 번 탐색한 요소를 다시 탐색하려면 초기 데이터 소스에서 새로운 스트림을 만들어야 한다(그러려면 컬렉션처럼 반복 사용할 수 있는 데이터 소스여야 한다. 만일 데이터 소스가 `I/O` 채널이라면 소스를 반복 사용할 수 없으므로 새로운 스트림을 만들 수 없다).

```java
List<String> title = Arrays.asList("Java8", "In", "Action");
Stream<String> s = title.stream();
s.forEach(System.out::println); // title의 각 단어를 출력
s.forEach(System.out::println); // 스트림이 이미 소비되었거나 닫힘
```

#### 4.3.2 외부 반복과 내부 반복
컬렉션 인터페이스를 사용하려면 사용자가 직접 요소를 반복해야 한다(예를 들면 for-each등을 사용해서). 이를 **외부 반복**이라고 한다. 반면 스트림 라이브러리는 (반복을 알아서 처리하고 결과 스트림값을 어딘가에 저장해주는) **내부 반복**을 사용한다.

```java
// 예제 4-1 컬렉션 : for-each 루프를 이용하는 외부 반복
List<String> names = new ArrayList<>();
for(Dish dish : menu) { // 메뉴 리스트를 명시적으로 순차 반복한다.
    names.add(dish.getName()); // 이름을 추출해서 리스트에 추가한다.
}

// 예제 4-2 컬렉션 : 내부적으로 숨겨졌던 반복자를 사용한 외부 반복
List<String> names = new ArrayList<>();
Iterator<String> itreator = menu.iterator();
while(iterator.hasNext()) { // 명시적 반복
    Dish dish = iterator.next();
    names.add(dish.getName());
}

// 예제 4-3 스트림 : 내부 반복
List<String> names = menu.stream()
                        .map(Dish::getName) // map 메서드를 getName 메서드로 파라미터화해서 요리명을 추출한다
                        .collect(toList()); // 파이프라인을 실행한다. 반복자는 필요 없다.
```

>**내부 반복과 외부 반복의 차이**
>- 컬렉션은 **외부적으로** 반복, 즉 명시적으로 컬렉션 항목을 하나씩 가져와서 처리한다. 반면 내부 반복을 이용하면 작업을 투명하게 병렬로 처리하거나 더 최적화된 다양한 순서로 처리할 수 있다.
>- 스트림 라이브러리의 내부 반복은 데이터 표현과 하드웨어를 활용한 병렬성 구현을 자동으로 선택한다. 반면 `for-each`를 이용하는 외부 반복에서는 병렬성을 **스스로 관리**해야 한다.

---

## 4.4 스트림 연산

`java.util.stream.Stream` 인터페이스는 많은 연산을 정의한다. 스트림 인터페이스의 연산을 크게 두 가지로 구분할 수 있다.

```java
List<String> threeHighCaloricDishNames =
            menu.stream()
                    .filter(dish -> dish.getCalories() > 300)
                    .map(Dish::getName)
                    .limit(3)
                    .collect(toList());
```                    

- `filter, map, limit`는 서로 연결되어 파이프라인을 형성한다.
- `collect`로 파이프라인을 실행한 다음에 닫는다.

연결할 수 있는 스트림 연산을 **중간 연산**이라고 하며, 스트림을 닫는 연산을 **최종 연산**이라고 한다.

#### 4.4.1 중간 연산

`filter`나 `sorted` 같은 중간 연산은 다른 스트림을 반환한다. 따라서 여러 중간 연산을 연결해서 질의를 만들 수 있다. 중간 연산의 중요한 특징은 단말 연산을 스트림 파이프라인에 실행하기 전까지는 아무 연산도 수행하지 않는다는 것, 즉 게으르다는 것이다. 중간 연산을 합친 다음에 합쳐진 중간 연산을 최종 연산으로 한 번에 처리하기 때문이다.

- **쇼트서킷** : `limit` 연산과 비슷한 개념인듯하다..
- **루프 퓨전** : `filter`와 `map`과 같이 서로 다른 연산을 한 과정으로 병합하는 기법.

#### 4.4.2 최종 연산

최종 연산은 스트림 파이프라인에서 결과를 도출한다. 보통 최종 연산에 의해 `List, Integer, void` 등 스트림 이외의 결과가 반환된다.

#### 4.4.3 스트림 이용하기

스트림 이용 과정은 다음과 같이 세 가지로 요약할 수 있다.

- **질의를 수행할 (컬렉션 같은) 데이터 소스**
- **스트림 파이프라인을 구성할 중간 연산 연결**
- **스트림 파이프라인을 실행하고 결과를 만들 최종 연산**

---






