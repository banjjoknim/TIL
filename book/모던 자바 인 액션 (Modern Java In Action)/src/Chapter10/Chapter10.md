# Chapter10. 람다를 이용한 도메인 전용 언어
- 언어의 주요 목표는 메시지를 명확하고, 안정적인 방식으로 전달하는 것이다.

> *"프로그램은 사람들이 이해할 수 있도록 작성되어야 하는 것이 중요하며 기기가 실행하는 부분은 부차적일 뿐"*
> `-` *하롤드 아벨슨(Harold Abelson)*

- 애플리케이션의 핵심 비즈니스를 모델링하는 소프트웨어 영역에서 읽기 쉽고, 이해하기 쉬운 코드는 특히 중요하다. 개발팀과 도메인 전문가가 공유하고 이해할 수 있는 코드는 생산성과 직결되기 때문이다.
- 도메인 전문가는 소프트웨어 개발 프로세스에 참여할 수 있고 비즈니스 관점에서 소프트웨어가 제대로 되었는지 확인할 수 있다. 결과적으로 버그와 오해를 미리 방지할 수 있다.

- `도메인 전용 언어(DSL: Domain-Specific Languages)`로 애플리케이션의 비즈니스 로직을 표현함으로 이 문제를 해결할 수 있다. `DSL`은 작은, 범용이 아니라 특정 도메인을 대상으로 만들어진 특수 프로그래밍 언어이다.
- `DSL`은 도메인의 많은 특성 용어를 사용한다. `메이븐(Maven)`, `앤트(Ant)` 등을 빌드 과정을 표현하는 `DSL`로 간주할 수 있다.
- `HTML`은 웹페이지의 구조를 정의하도록 특화된 언어이다.

자바로 구현한 데이터베이스를 생각해보자. 데이터베이스 내부에서 주어진 레코드를 디스크의 어디에 저장해야 할지, 테이블의 인덱스를 어떻게 구성할지, 병렬 트랜잭션을 어떻게 처리할지를 계산하는 많은 코드가 존재한다. 

다음과 같이 "메뉴에서 400 칼로리 이하의 모든 요리를 찾으시오" 같은 쿼리를 프로그램으로 구현한다고 가정하자.

```java
while (block != null) {
    read(block, buffer)
    for (every record in buffer) {
        if (record.calorie < 400) {
            System.out.println(record.name);
        }
    }
    block = buffer.next();
}
```
위 코드에는 두 가지 문제가 있다.
- `로킹(locking)`, `I/O`, `디스크 할당` 등과 같은 지식이 필요하므로 경험이 부족한 프로그래머가 구현하기엔 조금 어렵다는 것.
- 더 중요한 문제는 애플리케이션 수준이 아니라 시스템 수준의 개념을 다루어야 한다는 것이다.

사용자를 상대하는 직종으로 새로 입사한 프로그래머는 "SQL의 menu라는 테이블에서 데이터를 찾는 것처럼 `SELECT name FROM menu WHERE calorie < 400`으로 표현하면 안될까요? 그러면 훨씬 효과적일텐데요."라고 물을 수 있다. 이는 자바가 아닌 `DSL`을 이용해 데이터베이스를 조작하자는 의미로 통한다. 기술적으로 이런 종류의 `DSL`을 `외부적(external)`이라 하는데, 이는 데이터베이스가 텍스트로 구현된 SQL 표현식을 파싱하고 평가하는 API를 제공하는 것이 일반적이기 때문이다.

위의 코드를 스트림을 이용해 다음처럼 코드를 간단히 줄일 수 있다.
```java
menu.stream()
    .filter(d -> d.getCalories() < 400)
    .map(Dish::getName)
    .forEach(System.out::println)
```
스트림의 API의 특성인 메서드 체인을 보통 자바의 루프의 복잡함 제어와 비교해 유창함을 의미하는 **플루언트 스타일**(fluent style)이라고 부른다.

이런 스타일은 쉽게 `DSL`에 적용할 수 있다. 위 예제에서 `DSL`은 외부적이 아니라 내부적이다. `내부적(internal) DSL`에서는 위에서 언급한 `SQL`의 `SELECT FROM` 구문처럼 애플리케이션 수준의 기본값이 자바 메서드가 사용할 수 있도록 데이터베이스를 대표하는 한 개 이상의 클래스 형식으로 노출된다.

기본적으로 `DSL`에서는 유창하게 코드를 구현할 수 있도록 적절하게 클래스와 메서드를 노출하는 과정이 필요하다. 외부 `DSL`은 `DSL` 문법 뿐 아니라 `DSL`을 평가하는 파서도 구현해야 한다. 하지만 이를 제대로 설계한다면 숙련도가 떨어지는 프로그래머 일지라도 아름답지만 비전문가가 이해하긴 어려운 기존 시스템 수준 코드 환경에서 새 코드를 빠르고 효과적으로 구현할 수 있다.

---

## 10.1 도메인 전용 언어
`DSL`은 특정 비즈니스 도메인의 문제를 해결하려고 만든 언어이다. 예를 들어 회계 전용 소프트웨어 애플리케이션을 개발한다고 가정하자. 이 상황에서 비즈니스 도메인에는 통장 입출금 내역서, 계좌 통합 같은 개념이 포함된다. 이런 문제를 표현할 수 있는 `DSL`을 만들 수 있다. 자바에서는 도메인을 표현할 수 있는 클래스와 메서드 집합이 필요하다. `DSL`이란 특정 비즈니스 도메인을 인터페이스로 만든 API라고 생각할 수 있다.

`DSL`은 범용 프로그래밍 언어가 아니다. `DSL`에서 동작과 용어는 특정 도메인에 국한되므로 다른 문제는 걱정할 필요가 없고 오직 자신의 앞에 놓인 문제를 어떻게 해결할지에만 집중할 수 있다. `DSL`을 이용하면 사용자가 특정 도메인의 복잡성을 더 잘 다룰 수 있다. 저수준 구현 세부 사항 메서드는 클래스의 비공개로 만들어서 저수준 구현 세부 내용은 숨길 수 있다. 그렇게 하면 사용자 친화적인 `DSL`을 만들 수 있다.

`DSL`은 평문 영어가 아니다. 도메인 전문가가 저수준 비즈니스 로직을 구현하도록 만드는 것은 `DSL`의 역할이 아니다. 다음의 두 가지 필요성을 생각하면서 `DSL`을 개발해야 한다.
- **의사 소통의 왕** : 우리의 코드의 의도가 명확히 전달되어야 하며 프로그래머가 아닌 사람도 이해할 수 있어야 한다. 이런 방식으로 코드가 비즈니스 요구사항에 부합하는지 확인할 수 있다.
- **한 번 코드를 구현하지만 여러 번 읽는다** : 가독성은 유지보수의 핵심이다. 즉 항상 우리의 동료가 이해할 수 있도록 코드를 구현해야 한다.

잘 설계한 `DSL`은 여러 장점을 제공한다. 하지만 `DSL`을 개발하고 사용하는데는 장점과 단점이 있다.

### 10.1.1 DSL의 장점과 단점
`DSL`은 만병통치약이 아니다. `DSL`을 도메인에 이용하면 약이 되거나 독이 될 수 있다. `DSL`은 코드의 비즈니스 의도를 명확하게 하고 가독성을 높인다는 점에서 약이 된다. 반면 `DSL` 구현은 코드이므로 올바로 검증하고 유지보수해야하는 책임이 따른다.

`DSL`은 다음과 같은 장점을 제공한다.
- **간결함** : API는 비즈니스 로직을 간편하게 캡슐화하므로 반복을 피할 수 있고 코드를 간결하게 만들 수 있다.
- **가독성** : 도메인 영역의 용어를 사용하므로 비 도메인 전문가도 코드를 쉽게 이해할 수 있다. 결과적으로 다양한 조직 구성원 간에 코드와 도메인 영역이 공유될 수 있다.
- **유지보수** : 잘 설계된 `DSL`로 구현한 코드는 쉽게 유지보수하고 바꿀 수 있다. 유지보수는 비즈니스 관련 코드 즉 가장 빈번히 바뀌는 애플리케이션 부분에 특히 중요하다.
- **높은 수준의 추상화** : `DSL`은 도메인과 같은 추상화 수준에서 동작하므로 도메인의 문제와 직접적으로 관련되지 않은 세부 사항을 숨긴다.
- **집중** : 비즈니스 도메인의 규칙을 표현할 목적으로 설계된 언어이므로 프로그래머가 특정 코드에 집중할 수 있다. 결과적으로 생산성이 좋아진다.
- **관심사 분리**(Separation of concerns) : 지정된 언어로 비즈니스 로직을 표현함으로 애플리케이션의 인프라구조와 관련된 문제와 독립적으로 비즈니스 관련된 코드에서 집중하기가 용이하다. 결과적으로 유지보수가 쉬운 코드를 구현한다.

반면, `DSL`로 인해 다음과 같은 단점도 발생한다.

- **DSL 설계의 어려움** : 간결하게 제한적인 언어에 도메인 지식을 담는 것이 쉬운 작업은 아니다.
- **개발 비용** : 코드에 `DSL`을 추가하는 작업은 초기 프로젝트에 많은 비용과 시간이 소모되는 작업이다.
- **추가 우회 계층** : `DSL`은 추가적인 계층으로 도메인 모델을 감싸며 이 때 계층을 최대한 작게 만들어 성능 문제를 회피한다.
- **새로 배워야 하는 언어** : 요즘에는 한 프로젝트에도 여러가지 언어를 사용하는 추세다. 하지만 `DSL`을 프로젝트에 추가하면서 팀이 배워야 하는 언어가 한 개 더 늘어난다는 부담이 있다. 여러 비즈니스 도메인을 다루는 개별 `DSL`을 사용하는 상황이라면 이들을 유기적으로 동작하도록 합치는 일은 쉬운 일이 아니다. 개별 `DSL`이 독립적으로 진화할 수 있기 때문이다.
- **호스팅 언어 한계** : 일부 자바 같은 범용 프로그래밍 언어는 장황하고 엄격한 문법을 가졌다. 이런 언어로는 사용자 친화적 `DSL`을 만들기가 힘들다. 사실 장황한 프로그래밍 언어를 기반으로 만든 `DSL`은 성가신 문법의 제약을 받고 읽기가 어려워진다. 자바 8의 람다 표현식은 이 문제를 해결할 강력한 새 도구이다.

### 10.1.2 JVM에서 이용할 수 있는 다른 DSL 해결책
`DSL`의 카테고리를 구분하는 가장 흔한 방법은 `마틴 파울러(Martin Fowler)`가 소개한 방법으로 내부 `DSL`과 외부 `DSL`을 나누는 것이다. 내부 `DSL(임베디드 DSL이라고 불림)`은 순수 자바 코드 같은 기존 호스팅 언어를 기반으로 구현하는 반면, `스탠드어론(standalone)`이라 불리는 외부 `DSL`은 호스팅 언어와는 독립적으로 자체의 문법을 가진다.

더욱이 `JVM`으로 인해 내부 `DSL`과 외부 `DSL`의 중간 카테고리에 해당하는 `DSL`이 만들어질 가능성이 생겼다. 스칼라나 그루비 처럼 자바가 아니지만 `JVM`에서 실행되며 더 유연하고 표현력이 강력한 언어도 있다. 우리는 이들을 다중 `DSL`이라는 세 번째 카테고리로 칭한다.

이제부터 세 가지 `DSL`을 순서대로 살펴본다.

#### 내부 DSL
`내부 DSL`이란 자바로 구현한 `DSL`을 의미한다. 람다를 적극적으로 활용하면 익명 내부 클래스를 사용해 `DSL`을 구현하는 것보다 장황함을 크게 줄여 신호 대비 잡음 비율을 적정 수준으로 유지하는 `DSL`을 만들 수 있다. 자바 7 문법으로 문자열 목록을 출력하는 상황과 자바 8의 새 `forEach` 메서드를 이용하는 예제로 신호 대비 잡음 비율이 무엇을 의미하는지 살펴보자.

```java
List<String> numbers = Arrays.asList("one", "two", "three");
numbers.forEach(new Consumer<String>() {
    @Override
    public void accept(String s) {
        System.out.println(s);
    }
});
```
위 코드 예제에서 다음 부분이 코드의 잡음이다. 
```
numbers.forEach
System.out.println(s);
```

나머지 코드는 특별한 기능을 더하지 않고 문법상 필요한 잡음인데 자바 8에서는 이런 잡음이 많이 줄어든다. 다음처럼 익명 내부 클래스를 람다 표현식으로 바꿀 수 있다.

```java
numbers.forEach(s -> System.out.println(s));
```

다음처럼 메서드 참조로 더 간단하게 만들 수 있다.

```java
numbers.forEach(System.out::println);
```

사용자가 기술적인 부분을 염두에 두고 있다면 자바를 이용해 `DSL`을 만들 수 있다. 자바 문법이 큰 문제가 아니라면 순수 자바로 `DSL`을 구현함으로 다음과 같은 장점을 얻을 수 있다.

- 기존 자바 언어를 이용하면 외부 `DSL`에 비해 새로운 패턴과 기술을 배워 `DSL`을 구현하는 노력이 현저하게 줄어든다.
- 순수 자바로 `DSL`을 구현하면 나머지 코드와 함께 `DSL`을 컴파일할 수 있다. 따라서 다른 언어의 컴파일러를 이용하거나 외부 `DSL`을 만드는 도구를 사용할 필요가 없으므로 추가로 비용이 들지 않는다.
- 여러분의 개발 팀이 새로운 언어를 배우거나 또는 익숙하지 않고 복잡한 외부 도구를 배울 필요가 없다.
- `DSL` 사용자는 기존의 자바 IDE를 이용해 자동 완성, 자동 리팩터링 같은 기능을 그대로 즐길 수 있다. 최신 IDE는 다른 유명한 `JVM` 언어도 지원하지만 자바 만큼의 기능을 지원하진 못한다.
- 한 개의 언어로 한 개의 도메인 또는 여러 도메인을 대응하지 못해 추가로 `DSL`을 개발해야 하는 상황에서 자바를 이용한다면 추가 `DSL`을 쉽게 합칠 수 있다.

같은 자바 바이트코드를 사용하는 `JVM` 기반 프로그래밍 언어를 이용함으로 `DSL` 합침 문제를 해결하는 방법도 있다. 이런 언어를 `다중 DSL`이라고 부른다.

#### 다중 DSL
요즘 `JVM`에서 실행되는 언어는 100개가 넘는다. 스칼라, 루비처럼 유명한 언어라면 쉽게 개발자를 찾을 수 있다. `JRuby`나 `Jython`같은 다른 언어도 잘 알려진 `JVM`의 프로그래밍 언어다. 마지막으로 `코틀린(Kotlin)`, `실론(Ceylon)` 같이 스칼라와 호환성을 유지하면서 단순하고 쉽게 배울 수 있다는 강점을 가진 새 언어도 있다. 이들은 모두 자바보다 젊으며 제약을 줄이고, 간편한 문법을 지향하도록 설계되었다. `DSL`은 기반 프로그래밍 언어의 영향을 받으므로 간결한 `DSL`을 만드는 데 새로운 언어의 특성들이 아주 중요하다.

특히 스칼라는 `커링`, `임의 변환` 등 `DSL` 개발에 필요한 여러 특성을 갖췄다. 일단은 간단한 예제를 통해 이들 기능을 어떻게 활용할 수 있는지 보여준다.

주어진 함수 `f`를 주어진 횟수만큼 반복 실행하는 유틸리티 함수를 구현한다고 가정하자. 첫 번째 시도로 다음처럼 반복 실행하는 코드를 스칼라로 구현할 수 있다(여기서 문법은 중요한 것이 아니므로 크게 신경쓰지 말자).

```scala
def times(i: Int, f: => Unit): Unit = {
    f
    if (i > 1) times(i - 1, f) // 횟수가 양수면 횟수를 감소시켜 재귀적으로 times를 실행한다.
}
```

스칼라에서는 `i`가 아주 큰 숫자라 하더라도 자바에서처럼 스택 오버플로 문제가 발생하지 않는다. 스칼라는 꼬리 호출 최적화를 통해 `times` 함수 호출을 스택에 추가하지 않기 때문이다. 이 함수를 이용해 다음처럼 "Hello World"를 세 번 반복 호출할 수 있다.

```scala
times(3, println("Hello World"))
```

`times` 함수를 커링하거나 두 그룹으로 인수를 놓을 수 있다.

```scala
def times(i: Int)(f: => Unit): Unit = {
    f
    if (i > 1 times(i - 1)(f))
}
```

여러 번 실행할 명령을 중괄호 안에 넣어 같은 결과를 얻을 수 있다.

```scala
times(3) {
    println("Hello World")
}
```

마지막으로 스칼라는 함수가 반복할 인수를 받는 한 함수를 가지면서 `Int`를 익명 클래스로 암묵적 변환하도록 정의할 수 있다. 이 예제의 목표는 자바의 한계를 넘는 방법이 무엇인지 살펴보는 것이다.

```scala
implicit def intToTimes(i: Int) = new { // Int를 무명 클래스로 변환하는 암묵적 변환을 정의
    def times(f: => Unit): Unit = { // 이 클래스는 다른 함수 f를 인수로 받는 times 함수 한 개만 정의
        def times(i: Int, f: => Unit): Unit = { // 두 번째 times 함수는 가장 가까운 범주에서 정의한 두 개의 인수를 받는 함수를 이용
            f
            if (i > 1) times(i - 1, f)
        }
        times(i, f) // 내부 times 함수 호출
    }
}
```

이런 방식으로 `작은 스칼라 내장 DSL` 구현 사용자는 다음처럼 "Hellow World"를 세 번 출력하는 함수를 실행할 수 있다.

```scala
3 times {
    println("Hello World")
}
```

예제에서 확인했듯이 결과적으로 문법적 잡음이 전혀 없으며 개발자가 아닌 사람도 코드를 쉽게 이해할 수 있다. 여기서 숫자 3은 자동으로 컴파일러에 의해 클래스 인스턴스로 변환되며 `i 필드`에 저장된다. 점 표기법을 이용하지 않고 `times 함수`를 호출했는데 이때 반복할 함수를 인수로 받는다.

자바로는 비슷한 결과를 얻긴 어렵다. 이는 누가 더 `DSL 친화적`인지를 명확하게 보여준다. 하지만 이 접근 방법은 다음과 같은 불편함도 초래한다.

- 새로운 프로그래밍 언어를 배우거나 또는 팀의 누군가가 이미 해당 기술을 가지고 있어야 한다. 멋진 `DSL`을 만들려면 이미 기존 언어의 고급 기능을 사용할 수 있는 충분한 지식이 필요하기 때문이다.
- 두 개 이상의 언어가 혼재하므로 여러 컴파일러로 소스를 빌드하도록 빌드 과정을 개선해야 한다.
- 마지막으로 `JVM`에서 실행되는 거의 모든 언어가 자바와 백 퍼센트 호환을 주장하고 있지만 자바와 호환성이 완벽하지 않을 때가 많다. 이런 호환성 때문에 성능이 손실될 때도 있다. 예를 들어 스칼라와 자바 컬렉션은 서로 호환되지 않으므로 상호 컬렉션을 전달하려면 기존 컬렉션을 대상 언어의 API에 맞게 변환해야 한다.

#### 외부 DSL
프로젝트에 `DSL`을 추가하는 세 번째 옵션은 `외부 DSL`을 구현하는 것이다. 그러려면 자신만의 문법과 구문으로 새 언어를 설계해야 한다. 새 언어를 파싱하고, 파서의 결과를 분석하고, `외부 DSL`을 실행할 코드를 만들어야 한다. 이들 작업은 일반적인 작업도 아니며 쉽게 기술을 얻을 수도 없다. 정 이 방법을 택해야 한다면 `ANTLR` 같은 자바 기반 파서 생성기를 이용하면 도움이 된다. 더욱이 논리 정연한 프로그래밍 언어를 새로 개발한다는 것은 간단한 작업이 아니다. `외부 DSL`은 쉽게 제어 범위를 벗어날 수 있으며 처음 설계한 목적을 벗어나는 경우가 많다는 점도 문제다.

`외부 DSL`을 개발하는 가장 큰 장점은 `외부 DSL`이 제공하는 무한한 유연성이다. 우리에게 필요한 특성을 완벽하게 제공하는 언어를 설계할 수 있다는 것이 장점이다. 제대로 언어를 설계하면 우리의 비즈니스 문제를 묘사하고 해결하는 가독성 좋은 언어를 얻을 수 있다. 자바로 개발된 `인프라구조 코드`와 `외부 DSL`로 구현한 비즈니스 코드를 명확하게 분리한다는 것도 장점이다. 하지만 이 분리로 인해 `DSL`과 `호스트 언어` 사이에 `인공 계층`이 생기므로 이는 양날의 검과 같다.

먼저 자바 8과 이후에 네이티브 자바 API의 설계에 이런 아이디어가 어떻게 반영되었는지 확인한다.

---

## 10.2 최신 자바 API의 작은 DSL
자바의 새로운 기능의 장점을 적용한 첫 API는 네이티브 자바 API 자신이다. 자바 8 이전의 네이티브 자바 API는 이미 한 개의 추상 메서드를 가진 인터페이스를 갖고 있었다. 하지만 무명 내부 클래스를 구현하려면 불필요한 코드가 추가되어야 한다. 람다와 메서드 참조가 등장하면서 게임의 규칙이 바뀌었다(특히 `DSL`의 관점에서).

자바 8의 `Comparator` 인터페이스에 새 메서드가 추가되었다. `Comparator` 인터페이스 예제를 통해 람다가 어떻게 네이티브 자바 API의 재사용성과 메서드 결합도를 높였는지 확인하자.

사람(`Persons`)을 가리키는 객체 목록을 가지고 있는데 사람의 나이를 기준으로 객체를 정렬한다고 가정하자. 람다가 없으면 내부 클래스로 `Comparator` 인터페이스를 구현해야 한다.

```java
Collections.sort(persons, new Comparator<Person>() {
    @Override
    public int compare(Person p1, Person p2) {
        return p1.getAge() - p2.getAge();
    }
});
```

내부 클래스를 간단한 람다 표현식으로 바꿀 수 있다.

```java
Collections.sort(people, (p1, p2) -> p1.getAge() - p2.getAge());
```

이 기법은 코드의 신호 대비 잡음 비율을 줄이는데 특히 유용하다. 하지만 자바는 `Comparator` 객체를 좀 더 가독성 있게 구현할 수 있는 정적 유틸리티 메서드 집합도 제공한다. 이들 정적 메서드는 `Comparator` 인터페이스에 포함되어 있다. 정적으로 `Comparator.comparing` 메서드를 임포트해 위 예제를 다음처럼 구현할 수 있다.

```java
Collections.sort(persons, comparing(p -> p.getAge()));
```

람다를 메서드 참조로 대신해 코드를 개선할 수 있다.

```java
Collections.sort(persons, comparing(Person::getAge));
```

장점은 여기서 그치지 않는다. 자바 8에서 추가된 `reverse` 메서드를 사용해 사람들을 나이 역순으로 정렬할 수 있다.

```java
Collections.sort(persons, comparing(Person::getAge).reverse());
```

다음으로 이름으로 비교를 수행하는 `Comparator`를 구현해 같은 나이의 사람들을 알파벳 순으로 정렬할 수도 있다.

```java
Collections.sort(persons, comparing(Person::getAge).thenComparing(Person::getName));
```

마지막으로 `List` 인터페이스에 추가된 새 `sort` 메서드를 이용해 코드를 깔끔하게 정리할 수 있다.

```java
persons.sort(comparing(Person::getAge).thenComparing(Person::getName));
```

이 작은 API는 컬렉션 정렬 도메인의 `최소 DSL`이다. 작은 영역에 국한된 예제지만 이미 람다와 메서드 참조를 이용한 `DSL`이 코드의 `가독성`, `재사용성`, `결합성`을 높일 수 있는지 보여준다.

### 10.2.1 스트림 API는 컬렉션을 조작하는 DSL
`Stream` 인터페이스는 네이티브 자바 API에 작은 내부 `DSL`을 적용한 좋은 예다. 사실 `Stream`은 컬렉션의 항목을 `필터`, `정렬`, `변환`, `그룹화`, 조작하는 작지만 강력한 `DSL`로 볼 수 있다. 

로그 파일을 읽어서 `"ERROR"`라는 단어로 시작하는 파일의 첫 40행을 수집하는 작업을 수행한다고 가정하자. 다음 `예제 10-1`과 같이 반복 형식으로 이 작업을 처리할 수 있다.

###### 예제 10-1. 반복 형식으로 예제 로그 파일에서 에러 행을 읽는 코드
```java
List<String> errors = new ArrayList<>();
int errorCount = 0;
BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
String line = bufferedReader.readLine();
while (errorCount < 40 && line != null) {
    if (line.startsWith("ERROR")) {
        errors.add(line);
        errorCount++;
    }
    line = bufferedReader.readLine();
}
```

편의상 에러 처리 코드는 생략했다. 그럼에도 코드는 이미 장황해 의도를 한 눈에 파악하기 어렵다. 문제가 분리되지 않아 가독성과 유지보수성 모두 저하되었다. 같은 의무를 지닌 코드가 여러 행에 분산되어 있다.

- `FileReader`가 만들어짐
- 파일이 종료되었는지 확인하는 `while` 루프의 두 번째 조건
- 파일의 다음 행을 읽는 `while` 루프의 마지막 행

마찬가지로 첫 40행을 수집하는 코드도 세 부분으로 흩어져있다.

- `errorCount` 변수를 초기화하는 코드
- `while` 루프의 첫 번째 조건
- `"ERROR"`을 로그에서 발견하면 카운터를 증가시키는 행

`Stream` 인터페이스를 이용해 함수형으로 코드를 구현하면 다음 `예제 10-2`와 같이 더 쉽고 간결하게 코드를 구현할 수 있다.

###### 예제 10-2. 함수형으로 로그 파일의 에러 행 읽음
```java
List<String> errors = Files.lines(Paths.get(fileName)) // 파일을 열어서 문자열 스트림을 만듦
                           .filter(line -> line.startsWith("ERROR")) // "ERROR"로 시작하는 행을 필터링
                           .limit(40) // 결과를 첫 40행으로 제한
                           .collect(toList()); // 결과 문자열을 리스트로 수집
```

`String`은 파일에서 파싱할 행을 의미하며 `Files.lines`는 정적 유틸리티 메서드로 `Stream<String>`을 반환한다. 파일을 한 행씩 읽는 부분의 코드는 이게 전부다. 마찬가지로 `limit(40)`이라는 코드로 에러 행을 첫 40개만 수집한다. 이보다 더 가독성을 높일 수 있을까?

스트림 API의 플루언트 형식은 잘 설계된 `DSL`의 또 다른 특징이다. 모든 중간 연산은 게으르며 다른 연산으로 파이프라인될 수 있는 스트림으로 반환된다. 최종 연산은 적극적이며 전체 파이프라인이 계산을 일으킨다.

다음으로는 `Stream` 인터페이스의 `collect` 메서드와 함께 사용하는 다른 작은 `DSL` 설계 API 즉 `Collectors API`를 살펴본다.

### 10.2.2 데이터를 수집하는 DSL인 Collectors
`Stream` 인터페이스를 데이터 리스트를 조작하는 `DSL`로 간주할 수 있음을 확인했다. 마찬가지로 `Collector` 인터페이스는 데이터 수집을 수행하는 `DSL`로 간주할 수 있다. 또한 `Collectors` 클래스에서 제공하는 `정적 팩토리 메서드`를 이용해 필요한 `Collector` 객체를 만들고 합칠 수도 있다. 이제 `DSL` 관점에서 어떻게 이들 메서드가 설계되었는지 확인한다. 특히 `Comparator` 인터페이스는 다중 필드 정렬을 지원하도록 합쳐질 수 있으며 `Collectors`는 다중 수준 그룹화를 달성할 수 있도록 합쳐질 수 있다. 예를 들어 다음 예제처럼 자동차를 브랜드별 그리고 색상별로 그룹화할 수 있다.

```java
Map<String, Map<Color, List<Car>>> carsByBrandAndColor = 
        cars.stream()
            .collect(groupingBy(Car::getBrand, groupingBy(Car::getColor)));
```

두 `Comparators`를 연결하는 것과 비교할 때 무엇이 다른가? 두 `Comparator`를 플루언트 방식으로 연결해서 다중 필드 `Comparator`를 정의했다.

```java
Comparator<Person> comparator = comparing(Person::getAge).thenComparing(Person::getName);
```

반면 `Collectors API`를 이용해 `Collectors`를 중첩함으로 다중 수준 `Collector`를 만들 수 있다.

```java
Collector<? super Car, ?, Map<Brand, Map<Color, List<Car>>>> carGroupingCollector = 
groupingBy(Car::getBrand, groupingBy(Car::getColor));
```

특히 셋 이상의 컴포넌트를 조합할 때는 보통 플루언트 형식이 중첩 형식에 비해 가독성이 좋다. 형식이 그렇게 중요할까? 사실 가장 안쪽의 `Collector`가 첫 번째로 평가되어야 하지만 논리적으로는 최종 그룹화에 해당하므로 서로 다른 형식은 이를 어떻게 처리하는지를 상반적으로 보여준다. 예제에서 플루언트 형식으로 `Collector`를 연결하지 않고 `Collector` 생성을 여러 정적 메서드로 중첩함으로 안쪽 그룹화가 처음 평가되고 코드에서는 반대로 가장 나중에 등장하게 된다.

다음 `예제 10-3`에서 보여주는 것처럼 `groupingBy` 팩터리 메서드에 작업을 위임하는 `GroupingBuilder`를 만들면 문제를 더 쉽게 해결할 수 있다. `GroupingBuilder`는 유연한 방식으로 여러 그룹화 작업을 만든다.

###### 예제 10-3. 유연한 그룹화 컬렉터 빌더
```java
import static java.util.stream.Collectors.groupingBy;

public class GroupingBuilder<T, D, K> {
    private final Collector<? super T, ?, Map<K, D>> collector;

    public GroupingBuilder(Collector<? super T, ?, Map<K, D>> collector) {
        this.collector = collector;
    }

    public Collector<? super T, ?, Map<K, D>> get() {
        return collector;
    }

    public <J> GroupingBuilder<T, Map<K, D>, J> after(Function<? super T, ? extends J> classifier) {
        return new GroupingBuilder<T, Map<K, D>, J>(groupingBy(classifier, collector));
    }

    public static <T, D, K> GroupingBuilder<T, List<T>, K> groupOn(Function<? super T, ? extends K> classifier) {
        return new GroupingBuilder<T, List<T>, K>(groupingBy(classifier));
    }
}
```

플루언트 형식 빌더에 어떤 문제가 있을까? 문제를 잘 보여주는 다음 코드를 살펴보자.

```java
Collector<? super Car, ?, Map<Brand, Map<Color, List<Car>>>> carGroupingCollector = 
groupOn(Car::getColor).after(Car::getBrand).get();
```

중첩된 그룹화 수준에 반대로 그룹화 함수를 구현해야 하므로 유틸리티 사용 코드가 직관적이지 않다.

다음으로는 효과적인 `DSL`을 개발하는 기법을 살펴본다.

---

## 10.3 자바로 DSL을 만드는 패턴과 기법
`DSL`은 특정 도메인 모델에 적용할 친화적이고 가독성 높은 API를 제공한다. 따라서 우리는 먼저 간단한 도메인 모델을 정의하면서 이 절을 시작할것이다. 그리고 앞으로 사용할 `DSL`을 만드는 패턴을 살펴본다.

예제 도메인 모델은 세 가지로 구성된다. 첫 번째는 주어진 시장에 주식 가격을 모델링하는 순수 자바 빈즈다.

```java
public class Stock {

    private String symbol;
    private String market;

    public String getSymbol() {
        return symbol;
    }

    public String getMarket() {
        return market;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setMarket(String market) {
        this.market = market;
    }
}
```

두 번째는 주어진 가격에서 주어진 양의 주식을 사거나 파는 거래(trade)다.

```java
public class Trade {
    public enum Type {BUY, SELL}

    private Type type;
    private Stock stock;
    private int quantity;
    private double price;

    public Type getType() {
        return type;
    }

    public Stock getStock() {
        return stock;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getValue() {
        return quantity * price;
    }
}
```

마지막으로 고객이 요청한 한 개 이상의 거래 주문이다.

```java
public class Order {

    private String customer;
    private List<Trade> trades = new ArrayList<>();

    public void addTrade(Trade trade) {
        trades.add(trade);
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public double getValue() {
        return trades.stream()
                .mapToDouble(Trade::getValue)
                .sum();
    }
}
```

도메인 모델은 직관적이다. 주문을 의미하는 객체를 만드는 것은 조금 귀찮은 작업이다. 다음 `예제 10-4`처럼 `BigBank`라는 고객이 요청한 두 거래를 포함하는 주문을 만들어보자.

###### 예제 10-4. 도메인 객체의 API를 직접 이용해 주식 거래 주문을 만든다
```java
Order order = new Order();
order.setCustomer("BigBank");

Trade trade1 = new Trade();
trade1.setType(Trade.Type.BUY);

Stock stock1 = new Stock();
stock1.setSymbol("IBM");
stock1.setMarket("NYSE");

trade1.setStock(stock1);
trade1.setPrice(125.00);
trade1.setQuantity(80);
order.addTrade(trade1);

Trade trade2 = new Trade();
trade1.setType(Trade.Type.BUY);

Stock stock2 = new Stock();
stock1.setSymbol("GOOGLE");
stock1.setMarket("NASDAQ");

trade1.setStock(stock2);
trade1.setPrice(375.00);
trade1.setQuantity(50);
order.addTrade(trade2);
```

위 코드는 상당히 장황한 편이다. 비개발자인 도메인 전문가가 위 코드를 이해하고 검증하기를 기대할 수 없기 때문이다. 조금 더 직접적이고, 직관적으로 도메인 모델을 반영할 수 있는 `DSL`이 필요하다. 다양한 방법으로 이를 달성할 수 있다. 

다음으로는 각 접근 방법의 장단점을 배운다.

### 10.3.1 메서드 체인
`DSL`에서 가장 흔한 방식 중 하나를 살펴보자. 이 방법을 사용하면 한 개의 메서드 호출 체인으로 거래 주문을 정의할 수 있다.

###### 예제 10-5. 메서드 체인으로 주식 거래 주문 만들기
```java
Order order = forCustomer("BigBank")
        .buy(80)
        .stock("IBM")
        .on("NYSE")
        .at(125.00)
        .sell(50)
        .stock("GOOGLE")
        .on("NASDAQ")
        .at(375.00)
        .end();
```

상당히 코드가 개선되었다. 여러분의 도메인 전문가도 코드를 쉽게 이해할 수 있을 것이다. 이 결과를 달성하려면 어떻게 `DSL`을 구현해야 할까? 플루언트 API로 도메인 객체를 만드는 몇개의 빌더를 구현해야 한다. 다음 `예제 10-6`에서 보여주는 것처럼 최상위 수준 빌더를 만들고 주문을 감싼 다음 한 개 이상의 거래를 주문에 추가할 수 있어야 한다.

###### 예제 10-6. 메서드 체인 DSL을 제공하는 주문 빌더
```java
public class MethodChainingOrderBuilder {

    public final Order order = new Order(); // 빌더로 감싼 주문

    private MethodChainingOrderBuilder(String customer) {
        order.setCustomer(customer);
    }

    public static MethodChainingOrderBuilder forCustomer(String customer) {
        return new MethodChainingOrderBuilder(customer); // 고객의 주문을 만드는 정적 팩토리 메서드
    }

    public TradeBuilder buy(int quantity) {
        return new TradeBuilder(this, Trade.Type.BUY, quantity); // 주식을 사는 TradeBuilder 만들기
    }

    public TradeBuilder sell(int quantity) {
        return new TradeBuilder(this, Trade.Type.SELL, quantity); // 주식을 파는 TradeBuilder 만들기
    }

    public MethodChainingOrderBuilder addTrader(Trade trade) {
        order.addTrade(trade); // 주문에 주식 추가
        return this; // 유연하게 추가 주문을 만들어 추가할 수 있도록 주문 빌더 자체를 반환
    }

    public Order end() {
        return order; // 주문 만들기를 종료하고 반환
    }
}
```

주문 빌더의 `buy()`, `sell()` 메서드는 다른 주문을 만들어 추가할 수 있도록 자신을 만들어 반환한다.

```java
public class TradeBuilder {
    private final MethodChainingOrderBuilder builder;
    public final Trade trade = new Trade();

    public TradeBuilder(MethodChainingOrderBuilder builder, Trade.Type type, int quantity) {
        this.builder = builder;
        trade.setType(type);
        trade.setQuantity(quantity);
    }

    public StockBuilder stock(String symbol) {
        return new StockBuilder(builder, trade, symbol);
    }
}
```

빌더를 계속 이어가려면 `Stock` 클래스의 인스턴스를 만드는 `TradeBuilder`의 공개 메서드를 이용해야 한다.

```java
public class StockBuilder {
    private final MethodChainingOrderBuilder builder;
    private final Trade trade;
    private final Stock stock = new Stock();

    public StockBuilder(MethodChainingOrderBuilder builder, Trade trade, String symbol) {
        this.builder = builder;
        this.trade = trade;
        stock.setSymbol(symbol);
    }

    public TradeBuilderWithStock on(String market) {
        stock.setMarket(market);
        trade.setStock(stock);
        return new TradeBuilderWithStock(builder, trade);
    }
}
```

`StockBuilder`는 주식의 시작을 지정하고, 거래에 주식을 추가하고, 최종 빌더를 반환하는 `on()` 메서드 한 개를 정의한다.

```java
public class TradeBuilderWithStock {
    private final MethodChainingOrderBuilder builder;
    private final Trade trade;

    public TradeBuilderWithStock(MethodChainingOrderBuilder builder, Trade trade) {
        this.builder = builder;
        this.trade = trade;
    }

    public MethodChainingOrderBuilder at(double price) {
        trade.setPrice(price);
        return builder.addTrade(trade);
    }
}
```

한 개의 공개 메서드 `TradeBuilderWithStock`은 거래되는 주식의 단위 가격을 설정한 다음 원래 주문 빌더를 반환한다. 코드에서 볼 수 있듯이 `MethodChainingOrderBuilder`가 끝날 때까지 다른 거래를 플루언트 방식으로 추가할 수 있다. 여러 빌드 클래스 특히 두 개의 거래 빌더를 따로 만듦으로써 사용자가 미리 지정된 절차에 따라 플루언트 API 메서드를 호출하도록 강제한다. 덕분에 사용자가 다음 거래를 설정하기 전에 기존 거래를 올바로 설정하게 된다. 이 접근 방법은 주문에 사용한 파라미터가 빌더 내부로 국한된다는 다른 잇점도 제공한다. 이 접근 방법은 정적 메서드 사용을 최소화하고 메서드 이름이 인수의 이름을 대신하도록 만듦으로 이런 형식의 `DSL`의 가독성을 개선하는 효과를 더한다. 마지막으로 이런 기법을 적용한 `플루언트 DSL`에는 분법적 잡음이 최소화된다.

안타깝게도 빌더를 구현해야 한다는 것이 메서드 체인의 단점이다. 상위 수준의 빌더를 하위 수준의 빌더와 연결할 접착 많은 접착 코드가 필요하다. 도메인의 객체의 중첩 구조와 일치하게 들여쓰기를 강제하는 방법이 없다는 것도 단점이다.

다음으로 다른 특징을 갖는 두 번째 `DSL` 패턴을 살펴본다.

### 10.3.2 중첩된 함수 이용
**`중첩된 함수 DSL 패턴`** 은 이름에서 알 수 있듯이 다른 함수 안에 함수를 이용해 도메인 모델을 만든다.

다음 `예제 10-7`은 이 접근 방법을 적용한 `DSL`을 보여준다.

###### 예제 10-7. 중첩된 함수로 주식 거래 만들기
```java
Order order = order("BigBank",
                    buy(80,
                        stock("IBM", on("NYSE")), at(125.00)),
                    sell(50,
                        stock("GOOGLE", on("NASDAQ")), at(375.00))
                    );
```

이 방식의 `DSL`을 구현하는 코드는 직전의 코드와 비해 간단하다.

다음 `예제 10-8`의 코드의 `NestedFunctionOrderBuilder`는 이런 `DSL` 형식으로 사용자에게 API를 제공할 수 있음을 보여준다(모든 정적 메서드를 임포트했다고 가정한다).

###### 예제 10-8. 중첩된 함수 DSL을 제공하는 주문 빌더
```java
public class NestedFunctionOrderBuilder {

    public static Order order(String customer, Trade... trades) {
        Order order = new Order();
        order.setCustomer(customer);
        Stream.of(trades).forEach(order::addTrade); // 주문에 모든 거래 추가
        return order;
    }

    public static Trade buy(int quantity, Stock stock, double price) {
        return buildTrade(quantity, stock, price, Trade.Type.BUY); // 주식 매수 거래 만들기
    }

    public static Trade sell(int quantity, Stock stock, double price) {
        return buildTrade(quantity, stock, price, Trade.Type.SELL); // 주식 매도 거래 만들기
    }

    private static Trade buildTrade(int quantity, Stock stock, double price, Trade.Type buy) {
        Trade trade = new Trade();
        trade.setQuantity(quantity);
        trade.setType(buy);
        trade.setStock(stock);
        trade.setPrice(price);
        return trade;
    }

    public static double at(double price) { // 거래된 주식의 단가를 정의하는 더미 메서드
        return price;
    }

    public static Stock stock(String symbol, String market) {
        Stock stock = new Stock(); // 거래된 주식 만들기
        stock.setSymbol(symbol);
        stock.setMarket(market);
        return stock;
    }

    public static String on(String market) { // 주식이 거래된 시장을 정의하는 더미 메서드 정의
        return market;
    }
}
```

메서드 체인에 비해 함수의 중첩 방식이 도메인 객체 계층 구조에 그대로 반영(예제에서 주문은 한 개 이상의 거래를 포함하며 각 거래는 한 개의 주식을 참조)된다는 것이 장점이다.

안타깝게 이 방식에도 문제점이 있다. `결과 DSL`에 더 많은 괄호를 사용해야 한다는 사실이다. 더욱이 인수 목록을 정적 메서드에 넘겨줘야 한다는 제약도 있다. 도메인 객체에 선택 사항 필드가 있으면 인수를 생략할 수 있으므로 이 가능성을 처리할 수 있도록 여러 메서드 오버라이드를 구현해야 한다. 마지막으로 인수의 의미가 이름이 아니라 위치에 의해 정의되었다. `NestedFunctionOrderBuilder`의 `at()`, `on()` 메서드에서 했던 것처럼 인수의 역할을 확실하게 만드는 여러 더미 메서드를 이용해 마지막 문제를 조금은 완화할 수 있다.

지금까지 살펴본 두 가지 `DSL` 패턴에 람다 표현식은 사용하지 않았다. 

다음으로는 자바 8에서 추가된 함수형 기능을 활용하는 세 번째 기법을 설명한다.

### 10.3.3 람다 표현식을 이용한 함수 시퀀싱
다음 `DSL` 패턴은 람다 표현식으로 정의한 함수 시퀀스를 사용한다. 이 형식의 `DSL`을 이용해 기존 주식 거래 예제의 거래를 다음 `예제 10-9`처럼 정의할 수 있다.

###### 예제 10-9. 함수 시퀀싱으로 주식 거래 주문 만들기
```java
Order order = order(o -> {
    o.forCustomer("BigBank");
    o.buy(t -> {
        t.quantity(80);
        t.price(125.00);
        t.stock(s -> {
            s.symbol("IBM");
            s.market("NYSE");
        });
    });
    o.sell(t -> {
        t.quantity(50);
        t.price(375.00);
        t.stock(s -> {
            s.symbol("GOOGLE");
            s.market("NASDAQ");
        });
    });
});
```

이런 `DSL`을 만들려면 람다 표현식을 받아 실행해 도메인 모델을 만들어 내는 여러 빌더를 구현해야 한다. `DSL` 구현에서 했던 방식과 마찬가지로 이들 빌더는 메서드 체인 패턴을 이용해 만들려는 객체의 중간 상태를 유지한다. 메서드 체인 패턴에는 주문을 만드는 최상위 수준의 빌더를 가졌지만 이번에는 `Consumer` 객체를 빌더가 인수로 받음으로 `DSL` 사용자가 람다 표현식으로 인수를 구현할 수 있게 했다. 다음 `예제 10-10`은 지금까지 설명을 실제 어떻게 구현하는지 보여준다.

###### 예제 10-10. 함수 시퀀싱 DSL을 제공하는 주문 빌더
```java
public class LambdaOrderBuilder {

    private Order order = new Order(); // 빌더로 주문을 감쌈

    public static Order order(Consumer<LambdaOrderBuilder> consumer) {
        LambdaOrderBuilder builder = new LambdaOrderBuilder();
        consumer.accept(builder); // 주문 빌더로 전달된 람다 표현식 실행
        return builder.order; // OrderBuilder의 Consumer를 실행해 만들어진 주문을 반환
    }

    public void forCustomer(String customer) {
        order.setCustomer(customer); // 주문을 요청한 고객 설명
    }

    public void buy(Consumer<TradeBuilder> consumer) {
        trade(consumer, Trade.Type.BUY); // 주식 매수 주문을 만들도록 TradeBuilder 소비
    }

    public void sell(Consumer<TradeBuilder> consumer) {
        trade(consumer, Trade.Type.SELL); // 주식 매도 주문을 만들도록 TradeBuilder 소비
    }

    private void trade(Consumer<TradeBuilder> consumer, Trade.Type type) {
        TradeBuilder builder = new TradeBuilder();
        builder.trade.setType(type);
        consumer.accept(builder); // TradeBuilder로 전달할 람다 표현식 실행
        order.addTrade(builder.trade); // TradeBuilder의 Consumer를 실행해 만든 거래를 주문에 추가
    }
}
```

주문 빌더의 `buy()`, `sell()` 메서드는 두 개의 `Consumer<TradeBuilder>` 람다 표현식을 받는다. 이 람다 표현식을 실행하면 다음처럼 주식 매수, 주식 매도 거래가 만들어진다.

```java
public class TradeBuilder {
    private Trade trade = new Trade();

    public void quantity(int quantity) {
        trade.setQuantity(quantity);
    }

    public void price(double price) {
        trade.setPrice(price);
    }

    public void stock(Consumer<StockBuilder> consumer) {
        StockBuilder builder = new StockBuilder();
        consumer.accept(builder);
        trade.setStock(builder.stock);
    }
}
```

마지막으로 `TradeBuilder`는 세 번째 빌더의 `Consumer` 즉 거래된 주식을 받는다.

```java
public class StockBuilder {
    private Stock stock = new Stock();

    public void symbol(String symbol) {
        stock.setSymbol(symbol);
    }

    public void market(String market) {
        stock.setMarket(market);
    }
}
```

이 패턴은 이전 두 가지 `DSL` 형식의 두 가지 장점을 더한다. 메서드 체인 패턴처럼 플루언트 방식으로 거래 주문을 정의할 수 있다. 또한 중첩 함수 형식처럼 다양한 람다 표현식의 중첩 수준과 비슷하게 도메인 객체의 계층 구조를 유지한다.

안타깝게도 많은 설정 코드가 필요하며 `DSL` 자체가 자바 8 람다 표현식 문법에 의한 잡음의 영향을 받는다는 것이 이 패턴의 단점이다.

어떤 `DSL` 형식을 사용할 것인지는 각자의 기호에 달렸다. 자신이 만들려는 도메인 언어에 어떤 도메인 모델이 맞는지 찾으려면 실험을 해봐야 한다. 더욱이 다음으로 설명하는 것처럼 두 개 이상의 형식을 한 `DSL`에 조합할 수도 있기 때문이다.

### 10.3.4 조합하기
지금까지 살펴본 것처럼 세가지 `DSL` 패턴 각자가 장단점을 갖고 있다. 하지만 한 `DSL`에 한 개의 패턴만 사용하라는 법은 없다. 다음 `예제 10-11`에서 보여주는 것처럼 새로운 `DSL`을 개발해 주식 거래 주문을 정의할 수 있다.

###### 예제 10-11. 여러 DSL 패턴을 이용해 주식 거래 주문 만들기
```java
Order order = 
    forCustomer("BigBank",
                buy(t -> t.quantity(80)
                          .stock("IBM")
                          .on("NYSE")
                          .at(125.00)),
                sell(t -> t.quantity(50)
                           .stock("GOOGLE")
                           .on("NASDAQ")
                           .at(125.00)));
```

이 예제에서 중첩된 함수 패턴을 람다 기법과 혼용했다. `TradeBuilder`의 `Consumer`가 만든 각 거래는 다음 `예제 10-12`에서 보여주는 것처럼 람다 표현식으로 구현된다.

###### 예제 10-12. 여러 형식을 혼합한 DSL을 제공하는 주문 빌더
```java
public class MixedBuilder {

    public static Order forCustomer(String customer, TradeBuilder... tradeBuilders) {
        Order order = new Order();
        order.setCustomer(customer);
        Stream.of(tradeBuilders).forEach(b -> order.addTrade(b.trade));
        return order;
    }

    public static TradeBuilder buy(Consumer<TradeBuilder> consumer) {
        return buildTrade(consumer, Trade.Type.BUY);
    }

    public static TradeBuilder sell(Consumer<TradeBuilder> consumer) {
        return buildTrade(consumer, Trade.Type.SELL);
    }

    private static TradeBuilder buildTrade(Consumer<TradeBuilder> consumer, Trade.Type buy) {
        TradeBuilder builder = new TradeBuilder();
        builder.trade.setType(buy);
        consumer.accept(builder);
        return builder;
    }
}
```

마지막으로 헬퍼 클래스 `TradeBuilder`와 `StockBuilder`는 내부적으로 메서드 체인 패턴을 구현해 플루언트 API를 제공한다. 이제 람다 표현식 바디를 구현해 가장 간단하게 거래를 구현할 수 있다.

```java
public class TradeBuilder {
    private Trade trade = new Trade();

    public TradeBuilder quantity(int quantity) {
        trade.setQuantity(quantity);
        return this;
    }

    public TradeBuilder at(double price) {
        trade.setPrice(price);
        return this;
    }

    public StockBuilder stock(String symbol) {
        return new StockBuilder(this, trade, symbol);
    }
}

public class StockBuilder {
    private final TradeBuilder builder;
    private final Trade trade;
    private final Stock stock = new Stock();

    private StockBuilder(TradeBuilder builder, Trade trade, String symbol) {
        this.builder = builder;
        this.trade = trade;
        stock.setSymbol(symbol);
    }

    public TradeBuilder on(String market) {
        stock.setMarket(market);
        trade.setStock(stock);
        return builder;
    }
}
```

`예제 10-12`는 여기서 설명한 세 가지 `DSL` 패턴을 혼용해 가독성 있는 `DSL`을 만드는 방법을 보여준다. 여러 패턴의 장점을 이용할 수 있지만 이 기법에도 결점이 있다. 결과 `DSL`이 여러 가지 기법을 혼용하고 있으므로 한 가지 기법을 적용한 `DSL`에 비해 사용자가 `DSL`을 배우는데 오랜 시간이 걸린다는 것이다.

지금까지는 람다 표현식을 사용했지만 메서드 참조를 이용하면 많은 `DSL`의 가독성을 높일 수 있다.

다음으로는 주식 거래 도메인 모델에 메서드 참조를 사용하는 실용적인 예제를 통해 이를 설명한다.

### 10.3.5 DSL에 메서드 참조 사용하기
여기서는 주식 거래 도메인 모델에 다른 간단한 기능을 추가한다. 다음`예제 10-13`에서 보여주는 것처럼 주문의 총 합에 0개 이상의 세금을 추가해 최종값을 계산하는 기능을 추가한다.

###### 예제 10-13. 주문의 총 합에 적용할 세금
```java
public class Tax {
    public static double regional(double value) {
        return value * 1;
    }

    public static double general(double value) {
        return value * 1.3;
    }

    public static double surcharge(double value) {
        return value * 1.05;
    }
}
```

`예제 10-14`에서 보여주는 것처럼 세금을 적용할 것인지 결정하는 불리언 플래그를 인수로 받는 정적 메서드를 이용해 간단하게 해결할 수 있다.

###### 예제 10-14. 불리언 플래그 집합을 이용해 주문에 세금 적용
```java
public static double calculate(Order order, boolean useRegional, boolean useGeneral, boolean useSurcharge) {
    double value = order.getValue();
    if (useRegional) {
        value = Tax.regional(value);
    }
    if (useGeneral) {
        value = Tax.general(value);
    }
    if (useSurcharge) {
        value = Tax.surcharge(value);
    }
    return value;
}
```

이제 다음 코드처럼 지역 세금과 추가 요금을 적용하고 일반 세금은 뺀 주문의 최종값을 계산할 수 있다.

```java
double value = calculate(order, true, false, true);
```

이 구현의 가독성의 문제는 쉽게 드러난다. 불리언 변수의 올바른 순서를 기억하기도 어려우며 어떤 세금이 적용되었는지도 파악하기 어렵다. 이 문제는 다음 `예제 10-15`에서 보여주는 것처럼 유창하게 불리언 플래그를 설정하는 최소 `DSL`을 제공하는 `TaxCalculator`를 이용하는 것이 더 좋은 방법이다.

###### 예제 10-15. 적용할 세금을 유창하게 정의하는 세금 계산기
```java
public class TaxCalculator {
    private boolean useRegional;
    private boolean useGeneral;
    private boolean useSurcharge;

    public TaxCalculator withTaxRegional() {
        useRegional = true;
        return this;
    }

    public TaxCalculator withTaxGeneral() {
        useGeneral = true;
        return this;
    }

    public TaxCalculator withTaxSurcharge() {
        useSurcharge = true;
        return this;
    }

    public double calculate(Order order) {
        return Tax.calculate(order, useRegional, useGeneral, useSurcharge);
    }
}
```

다음 코드처럼 `TaxCalculator`는 지역 세금과 추가 요금은 주문에 추가하고 싶다는 점을 명확하게 보여준다.

```java
double value = new TaxCalculator().withTaxRegional()
                                  .withTaxSurcharge()
                                  .calculate(order);
```

코드가 장황하다는 것이 이 기법의 문제다. 도메인의 각 세금에 해당하는 불리언 필드가 필요하므로 확장성도 제한적이다. 자바의 함수형 기능을 이용하면 더 간결하고 유연한 방식으로 같은 가독성을 달성할 수 있다. 다음 `예제 10-16`은 `TaxCalculator`를 어떻게 리팩터링할 수 있는지 보여준다.

###### 예제 10-16. 유창하게 세금 함수를 적용하는 세금계산기
```java
public class TaxCalculator {
    public DoubleUnaryOperator taxFunction = d -> d; // 주문값에 적용된 모든 세금을 계산하는 함수

    public TaxCalculator with(DoubleUnaryOperator f) {
        taxFunction = taxFunction.andThen(f); // 새로운 세금 계산 함수를 얻어서 인수로 전달된 함수와 현재 함수를 합침
        return this;
    }

    public double calculate(Order order) {
        return taxFunction.applyAsDouble(order.getValue()); // 주문의 총 합에 세금 계산 함수를 적용해 최종 주문값을 계산
    }
}
```
이 기법은 주문의 총 합에 적용할 함수 한 개의 필드만 필요로하며 `TaxCalculator` 클래스를 통해 모든 세금 설정이 적용된다. 이 함수의 시작값은 확인 함수다. 처음 시점에서는 세금이 적용되지 않았으므로 최종값은 총합과 같다. `with()` 메서드로 새 세금이 추가되면 현재 세금 계산 함수에 이 세금이 조합되는 방식으로 한 함수에 모든 추가된 세금이 적용된다. 마지막으로 주문을 `calculate()` 메서드에 전달하면 다양한 세금 설정의 결과로 만들어진 세금 계산 함수가 주문의 합계에 적용된다. 리팩터링한 `TaxCalculator`는 다음처럼 사용할 수 있다.

```java
double value = new TaxCalculator().with(Tax::regional)
                                  .with(Tax::surcharge)
                                  .calculate(order);
```

위 예제에서 메서드 참조를 사용했는데 메서드 참조는 읽기 쉽고 코드를 간결하게 만든다. 새로운 세금 함수를 `Tax` 클래스에 추가해도 함수형 `TaxCalculator`를 바꾸지 않고 바로 사용할 수 있는 유연성도 제공한다.

---

## 10.4 실생활의 자바 8 DSL

### DSL 패턴의 장점과 단점
다음은 지금까지 배운 내용을 요약한다.

#### 메서드 체인
- 장점
    - 메서드 이름이 키워드 인수 역할을 한다.
    - 선택형 파라미터와 잘 동작한다.
    - `DSL` 사용자가 정해진 순서로 메서드를 호출하도록 강제할 수 있다.
    - 정적 메서드를 최소화하거나 없앨 수 있다.
    - 문법적 잡음을 최소화한다.
- 단점
    - 구현이 장황하다.
    - 빌드를 연결하는 접착 코드가 필요하다.
    - 들여쓰기 규칙으로만 도메인 객체 계층을 정의한다.

#### 중첩 함수
- 장점
    - 구현의 장황함을 줄일 수 있다.
    - 함수 중첩으로 도메인 객체 계층을 반영한다.
- 단점
    - 정적 메서드의 사용이 빈번하다.
    - 이름이 아닌 위치로 인수를 정의한다.
    - 선택형 파라미터를 처리할 메서드 오버로딩이 필요하다.

#### 람다를 이용한 함수 시퀀싱
- 장점
    - 선택형 파라미터와 잘 동작한다.
    - 정적 메서드를 최소화하거나 없앨 수 있다.
    - 람다 중첩으로 도메인 객체 계층을 반영한다.
    - 빌더의 접착 코드가 없다.
- 단점
    - 구현이 장황하다.
    - 람다 표현식으로 인한 문법적 잡음이 `DSL`에 존재한다.

세 가지의 유명한 자바 라이브러리에 지금까지 살펴본 패턴이 얼마나 사용되고 있는지 살펴보면서 배운 내용을 정리한다. `SQL 매핑 도구`, `동작 주도(behavior-driven) 개발 프레임워크`, `엔터프라이즈 통합 패턴(Enterprise Integration Patterns)`을 구현하는 도구 세 가지 자바 라이브러리를 확인한다.

### 10.4.1 jOOQ
`jOOQ`는 `SQL`을 구현하는 내부적 `DSL`로 자바에 직접 내장된 형식 안전 언어이다. 데이터베이스 스키마를 역공학하는 소스코드 생성기 덕분에 자바 컴파일러가 복잡한 `SQL` 구문의 형식을 확인할 수 있다. 역공학 프로세스 제품이 생성한 정보를 기반으로 우리는 데이터베이스 스키마를 탐색할 수 있다. 다음 `SQL` 질의 예를 살펴보자.

```
SELECT * FROM BOOK
WHERE BOOK.PUBLISHED_IN = 2016
ORDER BY BOOK.TITLE
```

`jOOQ DSL`을 이용해 위 질의를 다음처럼 구현할 수 있다.

```java
create.selectFrom(BOOK)
      .where(BOOK.PUBLISHED_IN.eq(2016))
      .orderBy(BOOK.TITLE)
```

스트림 API와 조합해 사용할 수 있다는 것이 `jOOQ DSL`의 또다른 장점이다. 이 기능 덕분에 다음 `예제 10-17`에서 보여주는 것처럼 `SQL` 질의 실행으로 나온 결과를 한 개의 플루언트 구문으로 데이터를 메모리에서 조작할 수 있다.

###### 예제 10-17. jOOQ DSL을 이용해 데이터베이스에서 책 선택
```java
Class.forName("org.h2.Driver");
try (Connection c = getConnection("jdbc:h2:~/sql-goodies-with-mapping", "sa", "")) { // SQL 데이터베이스 연결 만들기
    DSL.using(c)
       .select(BOOK.AUTHOR, BOOK.TITLE) // 만들어진 데이터베이스 연결을 이용해 jOOQ SQL 문 시작
       .where(BOOK.PUBLISHED_IN.eq(2016))
       .orderBy(BOOK.TITLE)
    .fetch() // jOOQ DSL로 SQL 문 정의
    .stream() // 데이터베이스에서 데이터 가져오기. jOOQ 문은 여기서 종료
    .collect(groupingBy( // 스트림 API로 데이터베이스에서 가져온 데이터 처리 시작
        r -> r.getValue(BOOK.AUTHOR),
        LinkedHashMap::new,
        mapping(r -> r.getValue(BOOK.TITLE), toList())))
        .forEach((author, titles) -> // 저자의 이름 목록과 각 저자가 집필한 책들을 출력
        System.out.println(author + " is author of " + titles));
}
```

`jOOQ DSL`을 구현하는 데 메서드 체인 패턴을 사용했음을 쉽게 파악할 수 있다. 잘 만들어진 `SQL` 질의 문법을 흉내내려면 메서드 체인 패턴의 여러 특성(선택적 파라미터를 허용하고 미리 정해진 순서로 특정 메서드가 호출될 수 있게 강제)이 반드시 필요하기 때문이다.

### 10.4.2 큐컴버
`동작 주도개발(Behavior-driven-development: BDD)`은 테스트 주도 개발의 확장으로 다양한 비즈니스 시나리오를 구조적으로 서술하는 간단한 `도메인 전용 스크립팅 언어`를 사용한다. `큐컴버(Cucumber)`는 다른 `BDD` 프레임워크와 마찬가지로 이들 명령문을 실행할 수 있는 테스트 케이스로 변환한다. 결과적으로 이 개발 기법으로 만든 스크립트 결과물은 실행할 수 있는 테스트임과 동시에 비즈니스 기능의 수용 기준이 된다. `BDD`는 우선 순위에 따른, 확인할 수 있는 비즈니스 가치를 전달하는 개발 노력에 집중하며 비즈니스 어휘를 공유함으로 도메인 전문가와 프로그래머 사이의 간격을 줄인다.

개발자가 비즈니스 시나리오를 평문 영어로 구현할 수 있도록 도와주는 `BDD 도구`인 `큐컴버`를 이용한 실용적인 예제를 통해 이 추상적 개념을 조금 더 명확하게 정리할 수 있다. 다음은 `큐컴버 스크립팅 언어`로 간단한 비즈니스 시나리오를 정의한 예제다.

```
Feature: Buy stock
  Scenario: Buy 10 IBM stocks
    Given the price of a "IBM" stock is 125$
    When I buy 10 "IBM"
    Then the order value should be 1250$
```

큐컴버는 세 가지로 구분되는 개념을 사용한다. `전제 조건 정의(Given)`, `시험하려는 도메인 객체의 실제 호출(When)`, `테스트 케이스의 결과를 확인하는 어설션-assertion(Then)`.

테스트 시나리오를 정의하는 스크립트는 제한된 수의 키워드를 제공하며 자유로운 형식으로 문장을 구현할 수 있는 외부 `DSL`을 활용한다. 이들 문장은 테스트 케이스의 변수를 캡쳐하는 정규 표현식으로 매칭되며 테스트 자체를 구현하는 메서드로 이를 전달한다. 다음 `예제 10-18`이 보여주는 것처럼 큐컴버로 주식 거래 주문의 값이 제대로 계산되었는지 확인하는 테스트 케이스를 개발할 수 있다.

###### 예제 10-18. 큐컴버 어노테이션을 이용해 테스트 시나리오 구현
```java
public class BuyStocksSteps {
    private Map<String, Integer> stockUnitPrices = new HashMap<>();
    private Order order = new Order();

    @Given("^the price of a \"(.*?)\" stock is (\\d+)\\$$") // 시나리오의 전제 조건인 주식 단가 정의
    public void setUnitPrice(String stockName, int unitPrice) {
        stockUnitPrices.put(stockName, unitPrice); // 주식 단가 저장
    }

    @When("^I buy (\\d+) \"(.*?)\"$") // 테스트 대상인 도메인 모델에 행할 액션 정의
    public void buyStocks(int quantity, String stockName) {
        Trade trade = new Trade(); // 적절하게 도메인 모델 도출
        trade.setType(Trade.Type.BUY);

        Stock stock = new Stock();
        stock.setSymbol(stockName);

        trade.setStock(stock);
        trade.setPrice(stockUnitPrices.get(stockName));
        trade.setQuantity(quantity);
        order.addTrade(trade);
    }

    @Then("^the order value should be (\\d+)\\$$")
    public void checkOrderValue(int expectedValue) { // 예상되는 시나리오 결과 정의
        assertEquals(expectedValue, order.getValue()); // 테스트 어설션 확인
    }
}
```

자바 8이 람다 표현식을 지원하면서 두 개의 인수 메서드(기존에 어노테이션 값을 포함한 정규 표현식과 테스트 메서드를 구현하는 람다)를 이용해 어노테이션을 제거하는 다른 문법을 큐컴버로 개발할 수 있다. 두 번째 형식의 표기법을 이용해 테스트 시나리오를 다음처럼 다시 구현할 수 있다.

```java
public class BuyStocksSteps implements cucumber.api.java8.En {
    private Map<String, Integer> stockUnitPrices = new HashMap<>();
    private Order order = new Order();
    public BuyStocksSteps() {
        Given("^the price of a \"(.*?)\" stock is (\\d+)\\$$",
            (String stockName, int unitPrice) -> {
                stockUnitValues.put(stockName, unitPrice);
            });
            // ... When과 Then 람다는 편의상 생략
    }
}
```

두 번째 문법은 코드가 더 단순해진다는 명백한 장점이 있다. 특히 테스트 메서드가 무명 람다로 바뀌면서 의미를 가진 메서드 이름(보통 이 과정은 테스트 시나리오의 가독성에 아무 도움이 되지 않음)을 찾는 부담이 없어졌다.

큐컴버의 `DSL`은 아주 간단하지만 외부적 `DSL`과 내부적 `DSL`이 어떻게 효과적으로 합쳐질 수 있으며 람다와 함께 가독성 있는 함축된 코드를 구현할 수 있는지를 잘 보여준다.

### 10.4.3 스프링 통합
**스프링 통합**(Spring Integration)은 유명한 엔터프라이즈 통합 패턴을 지원할 수 있도록 의존성 주입에 기반한 스프링 프로그래밍 모델을 확장한다. 스프링 통합의 핵심 목표는 복잡한 엔터프라이즈 통합 솔루션을 구현하는 단순한 모델을 제공하고 비동기, 메시지 주도 아키텍처를 쉽게 적용할 수 있게 돕는 것이다.

스프링 통합은 스프링 기반 애플리케이션 내의 경량의 원격, 메시징, 스케쥴링을 지원한다. 단비같은 풍부하고 유창한 `DSL`을 통해 기존의 스프링 `XML` 설정 파일 기반에도 이들 기능을 지원한다.

스프링 통합은 `채널`, `엔드포인트(endpoints)`, `폴러(pollers)`, `채널 인터셉터(channel interceptors)` 등 메시지 기반의 애플리케이션에 필요한 가장 공통 패턴을 모두 구현한다. 가독성이 높아지도록 엔드포인트는 `DSL`에서 동사로 구현하며 여러 엔드포인트를 한 개 이상의 메시지 흐름으로 조합해서 통합 과정이 구성된다. `예제 10-19`는 스프링 통합이 어떻게 동작하는지를 보여주는 단순하지만 완성된 예제다.

###### 예제 10-19. 스프링 통합 DSL을 이용해 스프링 통합 흐름 설정하기
```java
@Configuration
@EnableIntegration
public class MyConfiguration {

    @Bean
    public MessageSource<?> integerMessageSource() {
        MethodInvokingMessageSource source = new MethodInvokingMessageSource(); // 호출 시 AtomicInteger를 증가시키는 새 MessageSource를 생성
        source.setObject(new AtomicInteger());
        source.setMethodName("getAndIncrement");
        return source;
    }

    @Bean
    public DirectChannel inputChannel() {
        return new DirectChannel(); // MessageSource에서 도착하는 데이터를 나르는 채널
    }

    @Bean
    public IntegrationFlow myFlow() {
        return IntegrationFlows
                .from(this.integerMessageSource(), // 기존에 정의한 MessageSource를 IntegrationFlow의 입력으로 사용
                    c -> c.poller(Pollers.fixedRate(10))) // MessageSource를 폴링하면서 MessageSource가 나르는 데이터를 가져옴
                .channel(this.inputChannel())
                .filter((Integer p) -> p % 2 == 0) // 짝수만 거름
                .transform(Object::toString) // MessageSource에서 가져온 정수를 문자열로 변환
                .channel(MessageChannels.queue("queueChannel")) // queueChannel을 IntegrationFlow의 결과로 설정
                .get(); // IntegrationFlow 만들기를 끝나고 반환
    }
}
```

스프링 통합 `DSL`을 이용해 `myFlow()`는 `IntegrationFlow`를 만든다. 예제는 메서드 체인 패턴을 구현하는 `IntegrationFlows` 클래스가 제공하는 유연한 빌더를 사용한다. 그리고 결과 플로는 고정된 속도로 `MessageSource`를 폴링하면서 일련의 정수를 제공하고, 짝수만 거른 다음, 문자열로 변환해 최종적으로 결과를 자바 8 스트림 API와 비슷한 방법으로 출력 채널에 전달한다. `inputChannel` 이름만 알고 있다면 이 API를 이용해 플로 내의 모든 컴포넌트로 메시지를 전달할 수 있다.

```java
@Bean
public IntegrationFlow myFlow() {
    return flow -> flow.filter((Integer p) -> p % 2 == 0)
                       .transform(Object::toString)
                       .handle(System.out::println);
}
```

확인할 수 있는 것처럼 스프링 통합 `DSL`에서 가장 널리 사용하는 패턴은 메서드 체인이다. 이 패턴은 `IntegrationFlow` 빌더의 주요 목표인 전달되는 메시지 흐름을 만들고 데이터를 변환하는 기능에 적합하다. 하지만 마지막 예제에서 확인할 수 잇듯이 최상위 수준의 객체를 만들 때(그리고 내부의 복잡한 메서드 인수에도)는 함수 시퀀싱과 람다 표현식을 사용한다.

---

## 10.5 마치며
- `DSL`의 주요 기능은 개발자와 도메인 전문가 사이의 간격을 좁히는 것이다. 애플리케이션의 비즈니스 로직을 구현하는 코드를 만든 사람이 프로그램이 사용될 비즈니스 필드의 전문 지식을 갖추긴 어렵다. 개발자가 아닌 사람도 이해할 수 있는 언어로 이런 비즈니스 로직을 구현할 수 있다고 해서 도메인 전문가가 프로그래머가 될 수 있는 것은 아니지만 적어도 로직을 읽고 검증하는 역할은 할 수 있다.
- `DSL`은 크게 `내부적 DSL`(`DSL`이 사용될 애플리케이션을 개발한 언어를 그대로 활용)과 `외부적 DSL`(직접 언어를 설계해 사용함)로 분류할 수 있다. 내부적 `DSL`은 개발 노력이 적게 드는 반면 호스팅 언어의 문법 제약을 받는다. 외부적 `DSL`은 높은 유연성을 제공하지만 구현하기가 어렵다.
- `JVM`에서 이용할 수 있는 스칼라, 그루비 등의 다른 언어로 다중 `DSL`을 개발할 수 있다. 이들 언어는 자바보다 유연하며 간결한 편이다. 하지만 이들을 자바와 통합하려면 빌드과정이 복잡해지며 자바와의 상호 호환성 문제도 생길 수 있다.
- 자바의 장황함과 문법적 엄격함 때문에 보통 자바는 `내부적 DSL`을 개발하는 언어로는 적합하지 않다. 하지만 자바 8의 람다 표현식과 메서드 참조 덕분에 상황이 많이 개선되었다.
- 최신 자바는 자체 API에 `작은 DSL`을 제공한다. 이들 `Stream`, `Collectors` 클래스 등에서 제공하는 `작은 DSL`은 특히 컬렉션 데이터의 `정렬`, `필터링`, `변환`, `그룹화`에 유용하다.
- 자바로 `DSL`을 구현할 때 보통 `메서드 체인`, `중첩 함수`, `함수 시퀀싱` 세 가지 패턴이 사용된다. 각각의 패턴은 장단점이 있지만 모든 기법을 한 개의 `DSL`에 합쳐 장점만을 누릴 수 있다.
- 많은 자바 프레임워크와 라이브러리를 `DSL`을 통해 이용할 수 있다. `SQL 매핑 도구`인 `jOOQ`, `BDD 프레임워크 큐컴버`, `엔터프라이즈 통합 패턴`을 구현한 스프링 확장인 스프링 통합을 살펴봤다.

---