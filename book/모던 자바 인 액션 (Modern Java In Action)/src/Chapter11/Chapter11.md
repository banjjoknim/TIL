# Chapter11. null 대신 Optional 클래스
- 1965년 `토니 호어(Tony Hoare)`라는 영국 컴퓨터과학자가 힙에 할당되는 레코드를 사용하며 형식을 갖는 최초의 프로그래밍 언어 중 하나인 `알골(ALGOL W)`을 설계하면서 처음 `null` 참조가 등장했다.
- 그 당시에는 `null` 참조 및 예외로 값이 없는 상황을 가장 단순하게 구현할 수 있다고 판단했고 결과적으로 `null` 및 관련 예외가 탄생했다.

## 11.1 값이 없는 상황을 어떻게 처리할까?
다음처럼 자동차와 자동차 보험을 갖고 있는 사람 객체를 중첩 구조로 구현했다고 하자.

###### 예제 11-1. Person/Car/Insurance 데이터 모델
```java
public class Person {
    private Car car;

    public Car getCar() {
        return car;
    }
}

public class Car {
    private Insurance insurance;

    public Insurance getInsurance() {
        return insurance;
    }
}

public class Insurance {
    private String name;

    public String getName() {
        return name;
    }
}
```

다음 코드에서는 어떤 문제가 발생할까?

```java
public String getCarInsuranceName(Person person) {
    return person.getCar().getInsurance().getName();
}
```

코드에는 아무 문제가 없는 것처럼 보이지만 차를 소유하지 않은 사람도 많다. 이때 `getCar`를 호출하면 `null` 참조를 반환하는 방식으로 자동차를 소유하고 있지 않음을 표현할 것이다. 그러면 `getInsurance`는 `null` 참조의 보험 정보를 반환하려 할 것이므로 런타임에 `NullPointerException`이 발생하면서 프로그램 실행이 중단된다. 또 다른 문제도 있다. 만약 `Person`이 `null`이라면 어떻게 될까? 아니면 `getInsurance`가 `null`을 반환한다면 어떻게 될까?

### 11.1.1 보수적인 자세로 NullPointerException 줄이기
예기치 않은 `NullPointerException`을 피하려면 어떻게 해야 할까? 다음은 `null` 확인 코드를 추가해서 `NullPointerException`을 줄이려는 코드다.

###### 예제 11-2 null 안전 시도 1: 깊은 의심
```java
public String getCarInsuranceName(Person person) {
    if (person != null) { // null 확인 코드
        Car car = person.getCar();
        if (car != null) { // null 확인 코드
            Insurance insurance = car.getInsurance();
            if(insurance != null) { // null 확인 코드
                return insurance.getName();
            }
        }
    }
    return "Unknown";
}
```

위 코드에서는 변수를 참조할 때마다 `null`을 확인하며 중간 과정에 하나라도 `null` 참조가 있으면 `Unknown`이라는 문자열을 반환한다. 

`예제 11-2`의 메서드에서는 모든 변수가 `null`인지 의심하므로 변수를 접근할 때마다 중첩된 `if`가 추가되면서 코드 들여쓰기 수준이 증가한다. 따라서 이와 같은 `반복 패턴(recurring pattern)` 코드를 `깊은 의심(deep doubt)`이라고 부른다. 즉, 변수가 `null`인지 의심되어 중첩 `if` 블록을 추가하면 코드 들여쓰기 수준이 증가한다. 이를 반복하다보면 코드의 구조가 엉망이 되고 가독성도 떨어진다. 따라서 뭔가 다른 해결 방법이 필요하다. 다음 예제는 다른 방법으로 이 문제를 해결하는 코드다.

###### 예제 11-3. null 안전 시도 2: 너무 많은 출구
```java
public String getCarInsuranceName(Person person) {
    if (person == null) {
        return "Unknown";
    }
    Car car = person.getCar();
    if (car == null) {
        return "Unknown";
    }
    Insurance insurance = car.getInsurance();
    if (insurance == null) {
        return "Unknown";
    }
    return insurance.getName();
}
```

위 코드는 조금 다른 방법으로 중첨 `if` 블록을 없앴다. 즉, `null` 변수가 있으면 즉시 `Unknown`을 반환한다. 하지만 이 예제도 그렇게 좋은 코드는 아니다. 메서드에 네 개의 출구가 생겼기 때문이다. 출구 때문에 유지보수가 어려워진다. 게다가 `null`일 때 반환되는 기본값 `Unknown`이 세 곳에서 반복되고 있는데 같은 문자열을 반복하면서 오타 등의 실수가 생길 수 있다. 물론 `Unknown`이라는 문자열을 상수로 만들어서 이 문제를 해결할 수 있다.

앞의 코드는 쉽게 에러를 일으킬 수 있다. 만약 누군가가 `null`일 수 있다는 사실을 깜빡 잊었다면 어떤 일이 일어날까?

### 11.1.2 null 때문에 발생하는 문제
자바에서 `null` 참조를 사용하면서 발생할 수 있는 이론적, 실용적 문제를 확인하자.

- **에러의 근원이다** : `NullPointerException`은 자바에서 가장 흔히 발생하는 에러다.
- **코드를 어지럽힌다** : 때로는 중첩된 `null` 확인 코드를 추가해야 하므로 `null` 때문에 코드 가독성이 떨어진다.
- **아무 의미가 없다** : `null`은 아무 의미도 표현하지 않는다. 특히 정적 형식 언어에서 값이 없음을 표현하는 방법으로는 적절하지 않다.
- **자바 철학에 위배된다** : 자바는 개발자로부터 모든 포인터를 숨겼다. 하지만 예외가 있는데 그것이 바로 `null` 포인터다.
- **형식 시스템에 구멍을 만든다** : `null`은 무형식이며 정보를 포함하고 있지 않으므로 모든 참조 형식에 `null`을 할당할 수 있다. 이런 식으로 `null`이 할당되기 시작하면서 시스템의 다른 부분으로 `null`이 퍼졌을 때 애초에 `null`이 어떤 의미로 사용되었는지 알 수 없다.

다른 프로그래밍 언어에서는 `null` 참조를 어떻게 해결하는지 살펴보면서 `null` 참조 문제 해결방법의 실마리를 찾아보자.

### 11.1.3 다른 언어는 null 대신 무얼 사용하나?
최근 그루비 같은 언어에서는 `안전 내비게이션 연산자(safe navigation operator) - (?.)`를 도입해서 `null` 문제를 해결했다. 다음은 사람들이 그들의 자동차에 적용한 보험회사의 이름을 가져오는 그루비 코드 예제다.

```groovy
def carInsuranceName = person?.car?.insurance?.name
```

그루비 안전 내비게이션 연산자를 이용하면 `null` 참조 예외 걱정 없이 객체에 접근할 수 있다. 이때 호출 체인에 `null`인 참조가 있으면 결과로 `null`이 반환된다. 그루비의 안전 내비게이션 연산자를 이용하면 부작용을 최소화하면서 `null` 예외 문제를 더 근본적으로 해결할 수 있다.

`하스켈`, `스칼라` 등의 함수형 언어는 아예 다른 관점에서 `null` 문제를 접근한다. `하스켈`은 `선택형값(optional value)`을 저장할 수 있는 `Maybe`라는 형식을 제공한다. `Maybe`는 주어진 형식의 값을 갖거나 아니면 아무 값도 갖지 않을 수 있다. 따라서 `null` 참조 개념은 자연스럽게 사라진다. `스칼라`도 `T` 형식의 값을 갖거나 아무 값도 갖지 않을 수 있는 `Option[T]`라는 구조를 제공한다. 그리고 `Option` 형식에서 제공하는 연산을 사용해서 값이 있는지 여부를 명시적으로 확인해야 한다(즉, `null` 확인). 형식 시스템에서 이를 강제하므로 `null`과 관련한 문제가 일어날 가능성이 줄어든다.

자바 8은 `선택형값` 개념의 영향을 받아서 `java.util.Optional<T>`라는 새로운 클래스를 제공한다. 여기서는 이를 이용해서 값이 없는 상황을 모델링하는 방법을 설명한다. 또한 `null`을 `Optional`로 바꿀 때 우리 도메인 모델에서 선택형값에 접근하는 방법도 달라져야 함을 설명할 것이다.

---

## 11.2 Optional 클래스 소개
자바 8은 `하스켈`과 `스칼라`의 영향을 받아서 `java.util.Optional<T>`라는 새로운 클래스를 제공한다. `Optional`은 선택형값을 캡슐화하는 클래스다. 예를 들어 어떤 사람이 차를 소유하고있지 않다면 `Person` 클래스의 `car` 변수는 `null`을 가져야 할 것이다. 하지만 새로운 `Optional`을 이용할 수 있으므로 `null`을 할당하는 것이 아니라 변수형을 `Optional<Car>`로 설정할 수 있다.

값이 있으면 `Optional` 클래스는 값을 감싼다. 반면 값이 없으면 `Optional.empty` 메서드로 `Optional`을 반환한다. `Optional.empty`는 `Optional`의 특별한 싱글턴 인스턴스를 반환하는 정적 팩토리 메서드이다. `null` 참조와 `Optional.empty()`는 서로 의미상 비슷하지만 실제로는 차이점이 많다. `null`을 참조하려 하면 `NullPointerException`이 발생하지만 `Optional.empty()`는 `Optional` 객체이므로 이를 다양한 방식으로 활용할 수 있다. 

`null` 대신 `Optional`을 사용하면서 `Car` 형식이 `Optional<Car>`로 바뀌었다. 이는 값이 없을 수 있음을 명시적으로 보여준다. 반면 `Car` 형식을 사용했을 때는 `Car`에 `null` 참조가 할당될 수 있는데 이것이 올바른 값인지 아니면 잘못된 값인지 판단할 아무 정보도 없다.

이제 `Optional`을 이용해서 `예제 11-1`의 코드를 다음처럼 고칠 수 있다.

###### 예제 11-4. Optional로 Person/Car/Insurance 데이터 모델 재정의
```java
public class Person {
    private Optional<Car> car; // 사람이 차를 소유했을 수도 소유하지 않았을 수도 있으므로 Optional로 정의한다.

    public Optional<Car> getCar() {
        return car;
    }
}

public class Car {
    private Optional<Insurance> insurance; // 자동차가 보험에 가입되어 있을 수도 가입되어 있지 않았을 수도 있으므로 Optional로 정의한다.

    public Optional<Insurance> getInsurance() {
        return insurance;
    }
}

public class Insurance {
    private String name; // 보험회사에는 반드시 이름이 있다.

    public String getName() {
        return name;
    }
}
```

`Optional` 클래스를 사용하면서 모델의 `의미(sementic)`가 더 명확해졌음을 확인할 수 있다. 사람은 `Optional<Car>`를 참조하며 자동차는 `Optional<Insurance>`를 참조하는데, 이는 사람이 자동차를 소유했을 수도 아닐 수도 있으며, 자동차는 보험에 가입되어 있을 수도 아닐 수도 있음을 명확히 설명한다.

또한 보험회사 이름은 `Optional<String>`이 아니라 `String` 형식으로 선언되어 있는데, 이는 보험회사는 반드시 이름을 가져야 함을 보여준다. 따라서 보험회사 이름을 참조할 때 `NullPointerException`이 발생할 수도 있다는 정보를 확인할 수 있다. 하지만 보험회사 이름이 `null`인지 확인하는 코드를 추가할 필요는 없다. 오히려 고쳐야 할 문제를 감추는 꼴이 되기 때문이다. 보험회사는 반드시 이름을 가져야 하며 이름이 없는 보험회사를 발견했다면 예외를 처리하는 코드를 추가하는 것이 아니라 보험회사 이름이 없는 이유가 무엇인지 밝혀서 문제를 해결해야 한다. `Optional`을 이용하면 값이 없는 상황이 우리 데이터에 문제가 있는 것인지 아니면 알고리즘의 버그인지 명확하게 구분할 수 있다. 모든 `null` 참조를 `Optional`로 대치하는 것은 바람직하지 않다. `Optional`의 역할은 더 이해하기 쉬운 API를 설계하도록 돕는 것이다. 즉, 메서드의 시그니처만 보고도 선택형값인지 여부를 구별할 수 있다. `Optional`이 등장하면 이를 언랩해서 값이 없을 수 있는 상황에 적절하게 대응하도록 강제하는 효과가 있다.

---

## 11.3 Optional 적용 패턴
`Optional` 형식을 이용해서 도메인 모델의 의미를 더 명확하게 만들 수 있으며 `null` 참조 대신 값이 없는 상황을 표현할 수 있음을 확인했다. 실제로는 `Optional`을 어떻게 활용할 수 있을까?

### 11.3.1 Optional 객체 만들기
`Optional`을 사용하려면 `Optional` 객체를 만들어야 한다. 다양한 방법으로 `Optional` 객체를 만들 수 있다.

#### 빈 Optional
정적 팩토리 메서드 `Optional.empty`로 빈 `Optional` 객체를 얻을 수 있다.

```java
Optional<Car> optCar = Optional.empty();
```

#### null이 아닌 값으로 Optional 만들기
또는 정적 팩토리 메서드 `Optional.of`로 `null`이 아닌 값을 포함하는 `Optional`을 만들 수 있다.

```java
Optional<Car> optCar = Optional.of(car);
```

이제 `car`가 `null`이라면 즉시 `NullPointerException`이 발생한다(`Optional`을 사용하지 않았다면 `car`의 프로퍼티에 접근하려 할 때 에러가 발생했을 것이다).

#### null값으로 Optional 만들기
마지막으로 정적 팩토리 메서드 `Optional.ofNullable`로 `null`값을 저장할 수 있는 `Optional`을 만들 수 있다.

```java
Optional<Car> optCar = Optional.ofNullable(car);
```

`car`가 `null`이면 `빈 Optional` 객체가 반환된다.

그런데 `Optional`에서 어떻게 값을 가져오는지는 아직 살펴보지 않았다. `get` 메서드를 이용해서 `Optional`의 값을 가져올 수 있는데, `Optional`이 비어있으면 `get`을 호출했을 때 예외가 발생한다. 즉, `Optional`을 잘못 사용하면 결국 `null`을 사용했을 때와 같은 문제를 겪을 수 있다. 따라서 먼저 `Optional`로 명시적인 검사를 제거할 수 있는 방법을 살펴본다.

### 11.3.2 맵으로 Optional의 값을 추출하고 변환하기
보통 객체의 정보를 추출할 때는 `Optional`을 사용할 때가 많다. 예를 들어 보험회사의 이름을 추출한다고 가정하자. 다음 코드처럼 이름 정보에 접근하기 전에 `insurance`가 `null`인지 확인해야 한다.

```java
String name = null;
if(insurance != null) {
    name = insurance.getName();
}
```

이런 유형의 패턴에 사용할 수 있도록 `Optional`은 `map` 메서드를 지원한다. 다음 코드를 살펴보자(이 코드에서는 `예제 11-4`에서 소개한 모델을 사용함).

```java
Optional<Insurance> optInsurance = Optional.ofNullable(insurance);
Optional<String> name = optInsurance.map(Insurance::getName);
```

`Optional`의 `map` 메서드는 스트림의 `map` 메서드와 개념적으로 비슷하다. 스트림의 `map`은 스트림의 각 요소에 제공된 함수를 적용하는 연산이다. 여기서 `Optional` 객체를 최대 요소의 개수가 한 개 이하인 데이터 컬렉션으로 생각할 수 있다. `Optional`이 값을 포함하면 `map`의 인수로 제공된 함수가 값을 바꾼다. `Optional`이 비어있으면 아무 일도 일어나지 않는다.

```java
public String getCarInsuranceName(Person person) {
    return person.getCar().getInsurance().getName();
}
```

그러면 여러 메서드를 안전하게 호출하는데, 이 코드를 어떻게 활용할 수 있을까?

이제 `flatMap`이라는 `Optional`의 또 다른 메서드를 살펴보자!

### 11.3.3 flatMap으로 Optional 객체 연결
`map`을 사용하는 방법을 배웠으므로 다음처럼 `map`을 이용해서 코드를 재구현할 수 있다.

```java
Optional<Person> optPerson = Optional.of(person);
Optional<String> name = optPerson.map(Person::getCar)
                                 .map(Car::getInsurance)
                                 .map(Insurance::getName);
```

안타깝게도 위 코드는 컴파일되지 않는다. 왜 그럴까? 변수 `optPeople`의 형식은 `Optional<People>`이므로 `map` 메서드를 호출할 수 있다. 하지만 `getCat`는 `Optional<Car>` 형식의 객체를 반환한다(`예제 11-4` 참조). 즉, `map` 연산의 결과는 `Optional<Optional<Car>>` 형식의 객체다. `getInsurance`는 또 다른 `Optional` 객체를 반환하므로 `getInsurance` 메서드를 지원하지 않는다.

이 문제를 어떻게 해결할 수 있을까? 스트림의 `flatMap`과 같이 우리도 이차원 `Optional`을 일차원 `Optional`로 평준화해야 한다. `flatMap` 메서드를 통해 이차원 `Optional`이 일차원 `Optional`로 바뀐다.

#### Optional로 자동차의 보험회사 이름 찾기
`Optional`의 `map`과 `flatMap`을 살펴봤으니 이제 이를 실제로 사용해보자. `예제 11-2`와 `예제 11-3`에서 구현했던 빈틈이 많은 코드를 `예제 11-4`에서 보여준 `Optional` 기반 데이터 모델로 재구현할 수 있다.

###### 예제 11-5. Optional로 자동차의 보험회사 이름 찾기
```java
public String getCarInsuranceName(Optional<Person> person) {
    return person.flatMap(Person::getCar)
                 .flatMap(Car::getInsurance)
                 .map(Insurance::getName)
                 .orElse("Unknown"); // 결과 Optional이 비어있으면 기본값 사용
}
```

`예제 11-5`를 `예제 11-2`, `예제 11-3`과 비교하면서 `Optional`을 이용해서 값이 없는 상황을 처리하는 것이 어떤 장점을 제공하는지 확인할 수 있다. 즉, `null`을 확인하느라 조건 분기문을 추가해서 코드를 복잡하게 만들지 않으면서도 쉽게 이해할 수 있는 코드를 완성했다.

우선 `예제 11-2`와 `예제 11-3`의 `getCarInsuranceName` 메서드의 시그니처를 고쳤다. 주어진 조건에 해당하는 사람이 없을 수 있기 때문이다. 예를 들어 `id`로 사람을 검색했는데 `id`에 맞는 사람이 없을 수 있다. 따라서 `Person` 대신 `Optional<Person>`을 사용하도록 메서드 인수 형식을 바꿨다.

또한 `Optional`을 사용하므로 도메인 모델과 관련한 암묵적인 지식에 의존하지 않고 명시적으로 형식 시스템을 정의할 수 있었다. 정확한 정보 전달은 언어의 가장 큰 목표 중 하나다(물론 프로그래밍 언어도 예외는 아니다). `Optional`을 인수로 받거나 `Optional`을 반환하는 메서드를 정의한다면 결과적으로 이 메서드를 사용하는 모든 사람에게 이 메서드가 빈 값을 받거나 빈 결과를 반환할 수 있음을 잘 문서화해서 제공하는 것과 같다.

#### Optional을 이용한 Person/Car/Insurance 참조 체인
지금까지 `Optional<Person>`으로 시작해서 `Person`의 `Car`, `Car`의 `Insurance`, `Insurance`의 이름 문자열을 참조(`map`, `flatMap`을 이용)하는 방법을 살펴봤다.

우선 `Person`을 `Optional`로 감싼 다음에 `flatMap(Person::getCar)`를 호출했다. 이미 설명한 것처럼 이 호출을 두 단계의 논리적 과정으로 생각할 수 있다. 첫 번째 단계에서는 `Optional` 내부의 `Person`에 `Function`을 적용한다. 여기서는 `Person`의 `getCar` 메서드가 `Function`이다. `getCar` 메서드는 `Optional<Car>`를 반환하므로 `Optional` 내부의 `Person`이 `Optional<Car>`로 변환되면서 중첩 `Optional`이 생성된다. 따라서 `flatMap` 연산으로 `Optional`을 평준화한다. 평준화 과정이란 이론적으로 두 `Optional`을 합치는 기능을 수행하면서 둘 중 하나라도 `null`이면 빈 `Optional`을 생성하는 연산이다. `flatMap`을 빈 `Optional`에 호출하면 아무 일도 일어나지 않고 그대로 반환된다. 반면 `Optional`이 `Person`을 감싸고 있다면 `flatMap`에 전달된 `Function`이 `Person`에 적용된다. `Function`을 적용한 결과가 이미 `Optional`이므로 `flatMap` 메서드는 결과를 그대로 반환할 수 있다.

두 번째 단계도 첫 번째 단계와 비슷하게 `Optional<Car>`를 `Optional<Insurance>`로 변환한다. 세 번째 단계에서 `Insurance.getName()`은 `String`을 반환하므로 `flatMap`을 사용할 필요가 없다.

호출 체인 중 어떤 메서드가 `빈 Optional`을 반환한다면 전체 결과로 `빈 Optional`을 반환하고 아니면 관련 보험회사의 이름을 포함하는 `Optional`을 반환한다. 이제 반환된 `Optional`의 값을 어떻게 읽을 수 있을까? 호출 체인의 결과로 `Optional<String>`이 반환되는게 여기에 회사 이름이 저장되어 있을 수도 있고 없을 수도 있다. `예제 11-5`에서는 `Optional`이 비어있을 때 기본값(default value)을 제공하는 `orElse`라는 메서드를 사용했다. `Optional`은 기본값을 제공하거나 `Optional`을 언랩(unwrap)하는 다양한 메서드를 제공한다.

### 11.3.4 Optional 스트림 조작
자바 9에서는 `Optional`을 포함하는 스트림을 쉽게 처리할 수 있도록 `Optional`에 `stream()` 메서드를 추가했다. `Optional` 스트림을 값을 가진 스트림으로 변환할 때 이 기능을 유용하게 활용할 수 있다. 여기서는 다른 예제를 이용해 `Optional` 스트림을 어떻게 다루고 처리하는지 설명한다.

`예제 11-6`은 `예제 11-4`에서 정의한 `Person/Car/Insurance` 도메인 모델을 사용한다. `List<Person>`을 인수로 받아 자동차를 소유한 사람들이 가입한 보험 회사의 이름을 포함하는 `Set<String>`을 반환하도록 메서드를 구현해야 한다.

###### 예제 11-6. 사람 목록을 이용해 가입한 보험 회사 이름 찾기
```java
public Set<String> getCarInsuranceNames(List<Person> persons) {
    return persons.stream()
                  .map(Person::getCar) // 사람 목록을 각 사람이 보유한 자동차의 Optional<Car> 스트림으로 변환
                  .map(optCar -> optCar.flatMap(Car::getInsurance)) // flatMap 연산을 이용해 Optional<Car>을 해당 Optional<Insurance>로 변환
                  .map(optIns -> optIns.map(Insurance::getName)) // Optional<Insurance>를 해당 이름의 Optional<String>으로 매핑
                  .flatMap(Optional::stream) // Stream<Optional<String>>을 현재 이름을 포함하는 Stream<String>으로 변환
                  .collect(toSet()); // 결과 문자열을 중복되지 않은 값을 갖도록 집합으로 수집
}
```

보통 스트림 요소를 조작하려면 변환, 필터 등의 일련의 여러 긴 체인이 필요한데 이 예제는 `Optional` 값이 감싸있으므로 이 과정이 조금 더 복잡해졌다. 예제에서 `getCar()` 메서드가 단순히 `Car`가 아니라 `Optional<Car>`를 반환하므로 사람이 자동차를 가지지 않을 수도 있는 상황임을 기억하자. 따라서 첫 번째 `map` 변환을 수행하고 `Stream<Optional<Car>>`를 얻는다. 이어지는 두 개의 `map` 연산을 이용해 `Optional<Car>`를 `Optional<Insurance>`로 변환한 다음 `예제 11-5`에서 했던 것처럼 스트림이 아니라 각각의 요소에 했던 것처럼 각각을 `Optional<String>`로 변환한다.

세 번의 변환 과정을 거친 결과 `Stream<Optional<String>>`를 얻는데 사람이 차를 갖고 있지 않거나 또는 차가 보험에 가입되어 있지 않아 결과가 비어있을 수 있다. `Optional` 덕분에 이런 종류의 연산을 `null` 걱정없이 안전하게 처리할 수 있지만 마지막 결과를 얻으려면 `빈 Optional`을 제거하고 값을 언랩해야 한다는 것이 문제다. 다음 코드처럼 `filter`, `map`을 순서적으로 이용해 결과를 얻을 수 있다.

```java
Stream<Optional<String>> stream = ...
Set<String> result = stream.filter(Optional::isPresent)
                           .map(Optional::get)
                           .collect(toSet());
```

하지만 `예제 11-6`에서 확인했듯이 `Optional` 클래스의 `stream()` 메서드를 이용하면 한 번의 연산으로 같은 결과를 얻을 수 있다. 이 메서드는 각 `Optional`이 비어있는지 아닌지에 따라 `Optional`을 0개 이상의 항목을 포함하는 스트림으로 변환한다. 따라서 이 메서드의 참조를 스트림의 한 요소에서 다른 스트림으로 적용하는 함수로 볼 수 있으며 이를 원래 스트림에 호출하는 `flatMap` 메서드로 전달할 수 있다. 지금까지 배운것처럼 이런 방법으로 스트림의 요소를 두 수준인 스트림의 스트림으로 변환하고 다시 한 수준인 평면 스트림으로 바꿀 수 있다. 이 기법을 이용하면 한 단계의 연산으로 값을 포함하는 `Optional`을 언랩하고 비어있는 `Optional`은 건너뛸 수 있다.

### 11.3.5 디폴트 액션과 Optional 언랩
`빈 Optional`인 상황에서 기본값을 반환하도록 `orElse`로 `Optional`을 읽었다. `Optional` 클래스는 이 외에도 `Optional` 인스턴스에 포함된 값을 읽는 다양한 방법을 제공한다.

- `get()`은 값을 읽는 가장 간단한 메서드면서 동시에 가장 안전하지 않은 메서드다. 메서드 `get`은 래핑된 값이 있으면 해당 값을 변환하고 값이 없으면 `NoSuchElementException`을 발생시킨다. 따라서 `Optional`에 값이 반드시 있다고 가정할 수 있는 상황이 아니면 `get` 메서드를 사용하지 않는 것이 바람직하다. 결국 이 상황은 중첩된 `null` 확인 코드를 넣는 상황과 크게 다르지 않다.
- `예제 11-5`에서는 `orElse(T other)`를 사용했다. `orElse` 메서드를 이용하면 `Optional`이 값을 포함하지 않을 때 기본값을 제공할 수 있다.
- `orElseGet(Supplier<? extends T> other)`는 `orElse` 메서드에 대응하는 게으른 버전의 메서드다. `Optional`에 값이 없을 때만 `Supplier`가 실행되기 때문이다. 디폴트 메서드를 만드는 데 시간이 걸리거나(효율성 때문에) `Optional`이 비어있을 때만 기본값을 생성하고 싶다면(기본 값이 반드시 필요한 상황) `orElseGet(Supplier<? extends T> other)`를 사용해야 한다.
- `orElseThrow(Supplier<? extends X> exceptionSupplier)`는 `Optional`이 비어있을 때 예외를 발생시킨다는 점에서 `get` 메서드와 비슷하다. 하지만 이 메서드는 발생시킬 예외의 종류를 선택할 수 있다.
- `ifPersent(Consumer<? super T> consumer)`를 이용하면 값이 존재할 때 인수로 넘겨준 동작을 실행할 수 있다. 값이 없으면 아무 일도 일어나지 않는다.

자바 9에서는 다음의 인스턴스 메서드가 추가되었다.
- `ifPresentOrElse(Consumer<? super T> action, Runnable emptyAction)`. 이 메서드는 `Optional`이 비었을 때 실행할 수 있는 `Runnable`을 인수로 받는다는 점만 `ifPresent`와 다르다.

### 11.3.6 두 Optional 합치기
이제 `Person`과 `Car` 정보를 이용해서 가장 저렴한 보험료를 제공하는 보험회사를 찾는 몇몇 복잡한 비즈니스 로직을 구현한 외부 서비스가 있다고 가정하자.

```java
public Insurance findCheapestInsurance(Person person, Car car) {
    // 다양한 보험회사가 제공하는 서비스 조회
    // 모든 결과 데이터 비교
    return cheapestCompany;
}
```

이제 두 `Optional`을 인수로 받아서 `Optional<Insurance>`를 반환하는 `null 안전 버전(nullsafe version)`의 메서드를 구현해야 한다고 가정하자. 인수로 전달한 값 중 하나라도 비어있으면 `빈 Optional<Insurance>`를 반환한다. `Optional` 클래스는 `Optional`이 값을 포함하는지 여부를 알려주는 `isPresent`라는 메서드도 제공한다. 따라서 `isPresent`를 이용해서 다음처럼 코드를 구현할 수 있다.

```java
public Optional<Insurance> nullSafeFindCheapestInsurance(Optional<Person> person, Optional<Car> car) {
    if (person.isPersent() && car.isPersent()) {
        return Optional.of(findCheapestInsurance(person.get(), car.get()));
    } else {
        return Optional.empty();
    }
}
```

이 메서드의 장점은 `person`과 `car`의 시그니처만으로 둘 다 아무 값도 반환하지 않을 수 있다는 정보를 명시적으로 보여준다는 것이다. 안타깝게도 구현 코드는 `null` 확인 코드와 크게 다른 점이 없다. `Optional` 클래스에서 제공하는 기능을 이용해서 이 코드를 더 자연스럽게 개선할 수 없을까? `퀴즈 11-1`을 살펴보면서 더 멋진 해결책을 찾아보자.

`Optional` 클래스와 `Stream` 인터페이스는 `map`과 `flatMap` 메서드 이외에도 다양한 비슷한 기능을 공유한다. 다음으로는 세 번째 메서드 `filter`를 살펴본다.

##### 퀴즈 11-1. Optional 언랩하지 않고 두 Optional 합치기
`map`과 `flatMap` 메서드를 이용해서 기존의 `nullSafeFindCheapestInsurance()` 메서드를 한 줄의 코드로 재구현하시오.

```java
// 정답
public Optional<Insurance> nullSafeFindCheapestInsurance(Optional<Person> person, Optional<Car> car) {
        return person.flatMap(p -> car.map(c -> findCheapestInsurance(p, c)));
}
```

### 11.3.7 필터로 특정값 거르기
종종 객체의 메서드를 호출해서 어떤 프로퍼티를 확인해야 할 때가 있다. 예를 들어 보험회사 이름이 'CambridgeInsurance'인지 확인해야 한다고 가정하자. 이 작업을 안전하게 수행하려면 다음 코드에서 보여주는 것처럼 `Insurance` 객체가 `null`인지 여부를 확인한 다음에 `getName` 메서드를 호출해야 한다.

```java
Insurance insurance = ...;
if(insurance != null && "CambridgeInsurance".equals(insurance.getName())) {
    System.out.println("ok");
}
```

`Optional` 객체에 `filter` 메서드를 이용해서 다음과 같이 코드를 재구현할 수 있다.

```java
Optional<Insurance> optInsurance = ...;
optInsurance.filter(insurance -> "CambridgeInsurance".equals(insurance.getName()))
            .ifPresent(x -> System.out.println("ok"));
```

`filter` 메서드는 프레디케이트를 인수로 받는다. `Optional` 객체가 값을 가지며 프레디케이트와 일치하면 `filter` 메서드는 그 값을 반환하고 그렇지 않으면 `빈 Optional` 객체를 반환한다. `Optional`은 최대 한 개의 요소를 포함할 수 있는 스트림과 같다고 설명했으므로 이 사실을 적용하면 `filter` 연산의 결과를 쉽게 이해할 수 있다. `Optional`이 비어있다면 `filter` 연산은 아무 동작도 하지 않는다. `Optional`에 값이 있으면 그 값에 프레디케이트를 적용한다. 프레디케이트 적용 결과가 `true`면 `Optional`에는 아무 변화도 일어나지 않는다. 하지만 결과가 `false`면 값은 사라져버리고 `Optional`은 빈 상태가 된다.

##### 퀴즈 11-2. Optional 필터링
우리의 `Person/Car/Insurance` 모델을 구현하는 `Person` 클래스에는 사람의 나이 정보를 가져오는 `getAge`라는 메서드도 있었다. 다음 시그니처를 이용해서 `예제 11-5`의 `getCarInsuranceName` 메서드를 고치시오.

```java
public String getCarInsuranceName(Optional<Person> person, int minAge)
```

즉, 인수 `person`이 `minAge` 이상의 나이일 때만 보험회사 이름을 반환한다.

```java
// 정답
public String getCarInsuranceName(Optional<Person> person, int minAge) {
    return person.filter(p -> p.getAge() >= minAge)
                 .flatMap(Person::getCar)
                 .flatMap(Car::getInsurance)
                 .map(Insurance::getName)
                 .orElse("Unknown");
}
```

##### 표 11-1. Optional 클래스의 메서드
|메서드|설명|
|-|-|
|empty|`빈 Optional` 인스턴스 반환|
|filter|값이 존재하며 프레디케이트와 일치하면 값을 포함하는 `Optional`을 반환하고, 값이 없거나 프레디케이트와 일치하지 않으면` 빈 Optional`을 반환함|
|flatMap|값이 존재하면 인수로 제공된 함수를 적용한 `결과 Optional`을 반환하고, 값이 없으면 `빈 Optional`을 반환함|
|get|값이 존재하면 `Optional이 감싸고 있는 값`을 반환하고, 값이 없으면 `NoSuchElementException`이 발생함|
|ifPersent|값이 존재하면 지정된 `Consumer를 실행`하고, 값이 없으면 아무 일도 일어나지 않음|
|ifPersentOrElse|값이 존재하면 지정된 `Consumer를 실행`하고, 값이 없으면 아무 일도 일어나지 않음|
|isPersent|값이 존재하면 `true`를 반환하고, 값이 없으면 `false`를 반환함|
|map|값이 존재하면 제공된 매핑 함수를 적용함|
|of|값이 존재하면 `값을 감싸는 Optional`을 반환하고, 값이 `null`이면 `NullPointerException`을 발생함|
|ofNullable|값이 존재하면 `값을 감싸는 Optional`을 반환하고, 값이 `null`이면 `빈 Optional`을 반환함|
|or|값이 존재하면 `같은 Optional`을 반환하고, 값이 없으면 `Supplier`에서 만든 `Optional`을 반환|
|orElse|값이 존재하면 값을 반환하고, 값이 없으면 기본값을 반환|
|orElseGet|값이 존재하면 값을 반환하고, 값이 없으면 `Supplier에서 제공하는 값`을 반환|
|orElseThrow|값이 존재하면 값을 반환하고, 값이 없으면 `Supplier에서 생성한 예외`를 발생함|
|stream|값이 존재하면 `존재하는 값만 포함하는 스트림을 반환`하고, 값이 없으면 빈 스트림을 반환|

---

## 11.4 Optional을 사용한 실용 예제
`Optional` 클래스를 효과적으로 사용하려면 잠재적으로 존재하지 않는 값의 처리 방법을 바꿔야 한다. 즉, 코드 구현만 바꾸는 것이 아니라 네이티브 자바 API와 상호작용하는 방식도 바꿔야 한다. `Optional` 기능을 활용할 수 있도록 우리 코드에 작은 유틸리티 메서드를 추가하는 방식으로 이 문제를 해결할 수 있다.

### 11.4.1 잠재적으로 null이 될 수 있는 대상을 Optional로 감싸기
기존의 자바 API에서는 `null`을 반환하면서 요청한 값이 없거나 어떤 문제로 계산에 실패했음을 알린다. 예를 들어 `Map`의 `get` 메서드는 요청한 키에 대응하는 값을 찾지 못했을 때 `null`을 반환한다. 지금까지 살펴본 것처럼 `null`을 반환하는 것보다는 `Optional`을 반환하는 것이 더 바람직하다. `get` 메서드의 시그니처는 우리가 고칠 수 없지만 `get` 메서드의 반환값은 `Optional`로 감쌀 수 있다. `Map<String, Object>` 형식의 맵이 있는데, 다음처럼 `key`로 값에 접근한다고 가정하자.

```java
Object value = map.get("key");
```

문자열 `key`에 해당하는 값이 없으면 `null`이 반환될 것이다. `map`에서 반환하는 값을 `Optional`로 감싸서 이를 개선할 수 있다. 코드가 복잡하기는 하지만 기존처럼 `if-then-else`를 추가하거나, 아니면 아래와 같이 깔끔하게 `Optional.ofNullable`을 이용하는 두 가지 방법이 있다.

```java
Optional<Object> value = Optional.ofNullable(map.get("key"));
```

이와 같은 코드를 이용해서 `null`일 수 있는 값을 `Optional`로 안전하게 변환할 수 있다.

### 11.4.2 예외와 Optional 클래스
자바 API는 어떤 이유에서 값을 제공할 수 없을 때 `null`을 반환한느 대신 예외를 발생시킬 때도 있다. 이것에 대한 전형적인 예가 문자열을 정수로 변환하는 정적 메서드 `Integer.parseInt(String)`다. 이 메서드는 문자열을 정수로 바꾸지 못할 때 `NumberFormatException`을 발생시킨다. 즉, 문자열이 숫자가 아니라는 사실을 예외로 알리는 것이다. 기존에 값이 `null`일 수 있을때는 `if`문으로 `null` 여부를 확인했지만 예외를 발생시키는 메서드에서는 `try/catch 블록`을 사용해야 한다는 점이 다르다.

정수로 변환할 수 없는 문자열 문제를 `빈 Optional`로 해결할 수 있다. 즉, `parseInt`가 `Optional`을 반환하도록 모델링할 수 있다. 물론 기존 자바 메서드 `parseInt`를 직접 고칠 수는 없지만 다음 코드처럼 `parseInt`를 감싸는 작은 유틸리티 메서드를 구현해서 `Optional`을 반환할 수 있다.

###### 예제 11-7. 문자열을 정수 Optional로 변환
```java
public static Optional<Integer> stringToInt(String s) {
    try {
        return Optional.of(Integer.parseInt(s)); // 문자열을 정수로 변환할 수 있으면 정수로 변환된 값을 포함하는 Optional을 반환한다.
    } catch (NumberFormatException e) {
        return Optional.empty(); // 그렇지 않으면 빈 Optional을 반환한다.
    }
}
```

위와 같은 메서드를 포함하는 유틸리티 클래스 `OptionalUtility`를 만들어서 필요할 때 `OptionalUtility.stringToInt`를 이용해서 문자열을 `Optional<Integer>`로 변환할 수 있다. 기존처럼 거추장스러운 `try/catch` 로직을 사용할 필요가 없다.

### 11.4.3 기본형 Optional을 사용하지 말아야 하는 이유
스트림처럼 `Optional`도 기본형으로 특화된 `OptionalInt`, `OptionalLong`, `OptionalDouble` 등의 클래스를 제공한다. 예를 들어 `예제 11-7`에서 `Optional<Integer>` 대신 `OptionalInt`를 반환할 수 있다. 하지만 스트림과는 달리 `Optional`의 최대 요소 수는 한 개이므로 `Optional`에서는 기본형 특화 클래스로 성능을 개선할 수 없다.

기본형 특화 `Optional`은 `Optional` 클래스의 유용한 메서드 `map`, `flatMap`, `filter` 등을 지원하지 않으므로 기본형 특화 `Optional`을 사용할 것을 권장하지 않는다. 게다가 스트림과 마찬가지로 기본형 특화 `Optional`로 생성한 결과는 다른 일반 `Optional`과 혼용할 수 없다. 예를 들어 `예제 11-7`이 `OptionalInt`를 반환한다면 이를 다른 `Optional`의 `flatMap`에 메서드 참조로 전달할 수 없다.

### 11.4.4 응용
`Optional` 클래스의 메서드를 실제 업무에서 어떻게 활용할 수 있는지 살펴보자. 예를 들어 프로그램의 설정 인수로 `Properties`를 전달한다고 가정하자. 그리고 다음과 같은 `Properties`로 우리가 만든 코드를 테스트할 것이다.

```java
Properties props = new Properties();
props.setProperty("a", "5");
props.setProperty("b", "true");
props.setProperty("c", "-3");
```

이제 프로그램에서는 `Properties`를 읽어서 값을 초 단위의 지속 시간(duration)으로 해석한다. 다음과 같은 메서드 시그니처로 지속 시간을 읽을 것이다.

```java
public int readDuration(Properties props, String name)
```

지속 시간은 양수여야 하므로 문자열이 양의 정수를 가리키면 해당 정수를 반환하지만 그 외에는 0을 반환한다. 이를 다음처럼 `JUnit` 어설션(assertion)으로 구현할 수 있다.

```java
assertEquals(5, readDuration(param, "a"));
assertEquals(0, readDuration(param, "b"));
assertEquals(0, readDuration(param, "c"));
assertEquals(0, readDuration(param, "d"));
```

이들 어설션은 다음과 같은 의미를 갖는다. 프로퍼티 `a`는 양수로 변환할 수 있는 문자열을 포함하므로 `readDuration` 메서드는 5를 반환한다. 프로퍼티 `b`는 숫자로 변환할 수 없는 문자열을 포함하므로 0을 반환한다. 프로퍼티 `c`는 음수 문자열을 포함하므로 0을 반환한다. `d`라는 이름의 프로퍼티는 없으므로 0을 반환한다.

###### 예제 11-8. 프로퍼티에서 지속 시간을 읽는 명령형 코드
```java
public int readDuration(Properties props, String name) {
    String value = props.getProperty(name);
    if (value != null) { // 요청한 이름에 해당하는 프로퍼티가 존재하는지 확인한다.
        try {
            int i = Integer.parseInt(value); // 문자열 프로퍼티를 숫자로 변환하기 위해 시도한다.
            if (i > 0) { // 결과 숫자가 양수인지 확인한다.
                return i;
            }
        } catch (NumberFormatException nfe) {

        }
    }
    return 0; // 하나의 조건이라도 실패하면 0을 반환한다.
}
```

예상대로 `if`문과 `try/catch` 블록이 중첩되면서 구현 코드가 복잡해졌고 가독성도 나빠졌다.

##### 퀴즈 11-3. Optional로 프로퍼티에서 지속 시간 읽기
지금까지 배운 `Optional` 클래스의 기능과 `예제 11-7`의 유틸리티 메서드를 이용해서 `예제 11-8`의 명령형 코드를 하나의 유연한 코드로 재구현하시오.

다음은 간단하게 구현한 정답 코드다.

```java
public int readDuration(Properties props, String name) {
    return Optional.ofNullable(props.getProperty(name))
                   .flatMap(OptionalUtility::stringToInt)
                   .filter(i -> i > 0)
                   .orElse(0);
}
```

`Optional`과 스트림에서 사용한 방식은 여러 연산이 서로 연결되는 데이터베이스 질의문과 비슷한 형식을 갖는다.

---

## 11.5 마치며
- 역사적으로 프로그래밍 언어에서는 `null` 참조로 값이 없는 상황을 표현해왔다.
- 자바 8에서는 값이 있거나 없음을 표현할 수 있는 클래스 `java.util.Optional<T>`를 제공한다.
- 팩토리 메서드 `Optional.empty`, `Optional.of`, `Optional.ofNullable` 등을 이용해서 `Optional` 객체를 만들 수 있다.
- `Optional` 클래스는 스트림과 비슷한 연산을 수행하는 `map`, `flatMap`, `filter` 등의 메서드를 제공한다.
- `Optional`로 값이 없는 상황을 적절하게 처리하도록 강제할 수 있다. 즉, `Optional`로 예상치 못한 `null` 예외를 방지할 수 있다.
- `Optional`을 활용하면 더 좋은 API를 설계할 수 있다. 즉, 사용자는 메서드의 시그니처만 보고도 `Optional`값이 사용되거나 반환되는지 예측할 수 있다.

---