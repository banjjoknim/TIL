# Chapter14. 자바 모듈 시스템
- 자바 모듈 시스템에 대해서, [`니콜라이 팔로그 저, 『The Java Module System』(Manning Publications, 2019)`](https://www.manning.com/books/the-java-module-system) 를 살펴보길 권장한다.

---

## 14.1 압력 : 소프트웨어 유추
- 모듈화란 무엇인가?
- 모듈 시스템은 어떤 문제를 해결할 수 있는가?
- 궁극적으로 소프트웨어 아키텍처 즉 고수준에서는 기반 코드를 바꿔야 할 때 유추하기 쉬우므로 생산성을 높일 수 있는 소프트웨어 프로젝트가 필요하다.
- 추론하기 쉬운 소프트웨어를 만드는 데 도움을 주는 **`관심사분리(separation fo concerns)`**와 **`정보 은닉(information hiding)`**을 살펴보자.

### 14.1.1 관심사분리
`관심사분리(SoC, Separation of concerns)`는 컴퓨터 프로그램을 고유의 기능으로 나누는 동작을 권장하는 원칙이다.
- `SoC`를 적용함으로 각각의 기능들을 모듈이라는 각각의 부분 즉, 서로 거의 겹치지 않는 코드 그룹으로 분리할 수 있다.
- 다시 말해 클래스를 그룹화한 모듈을 이용해 애플리케이션의 클래스 간의 관계를 시각적으로 보여줄 수 있다.
- 자바 9 모듈은 클래스가 어떤 다른 클래스를 볼 수 있는지를 컴파일 시간에 정교하게 제어할 수 있다. 특히 자바 패키지는 모듈성을 지원하지 않는다(자바 패키지와 모듈은 다르다는 것을 말하는 듯 하다).

`SoC` 원칙은 모델, 뷰, 컨트롤러 같은 아키텍처 관점 그리고 복구 기법을 비즈니스 로직과 분리하는 등의 하위 수준 접근 등의 상황에 유용하다. `SoC` 원칙은 다음과 같은 장점을 제공한다.
- 개별 기능을 따로 작업할 수 있으므로 팀이 쉽게 협업할 수 있다.
- 개별 부분을 재사용하기 쉽다.
- 전체 시스템을 쉽게 유지보수할 수 있다.

### 14.1.2 정보 은닉
**`정보 은닉`**은 세부 구현을 숨기도록 장려하는 원칙이다.
- 소프트웨어를 개발할 때 요구사항은 자주 바뀐다. 세부 구현을 숨김으로 프로그램의 어떤 부분을 바꿨을 때 다른 부분까지 영향을 미칠 가능성을 줄일 수 있다.
- 즉 코드를 관리하고 보호하는 데 유용한 원칙이다.
- **`캡슐화(encapsulation)`**는 특정 코드 조각이 애플리케이션의 다른 부분과 고립되어 있음을 의미한다.
- 캡슐화된 코드의 내부적인 변화가 의도치 않게 외부에 영향을 미칠 가능성이 줄어든다.
- 자바에서는 클래스 내의 컴포넌트에 적절하게 `private` 키워드를 사용했는지를 기준으로 컴파일러를 이용해 캡슐화를 확인할 수 있다.
- 하지만 자바 9 이전까지는 **클래스와 패키지가 의도된 대로 공개되었는지**를 컴파일러로 확인할 수 있는 기능이 없었다.

### 14.1.3 자바 소프트웨어
잘 설계된 소프트웨어를 만들려면 이 두 가지 원칙을 따르는 것이 필수다.
- 자바는 객체 지향 언어로 클래스, 인터페이스를 이용한다.
- 특정 문제와 관련된 패키지, 클래스, 인터페이스를 그룹으로 만들어 코드를 그룹화할 수 있다.
- 코드 자체를 보고 소프트웨어의 동작을 추론하긴 현실적으로 어렵다.
- 따라서 `UML 다이어그램`같은 도구를 이용하면 그룹 코드 간의 의존성을 시각적으로 보여줌으로써 소프트웨어를 추론하는데 도움을 받을 수 있다.

정보 은닉을 살펴보자. 
- 자바에서는 `public`, `protected`, `private` 등의 접근 제한자와 패키지 수준 접근 권한 등을 이용해 메서드, 필드 클래스의 접근을 제어했다.
- 하지만 이런 방식으로는 원하는 접근 제한을 달성하기 어려우며 심지어 최종 사용자에게 원하지 않는 메서드도 공개해야 하는 상황이 발생했다.
- 설계자는 자신의 클래스에서 개인적으로 사용할 용도라고 생각할 수 있으나, 결과적으로 클래스에 `public` 필드가 있다면 사용자 입장에서는 당연히 사용할 수 있다고 생각할 것이다.

모듈화의 장점을 살펴봤는데 다음으로는 자바의 모듈 지원이 어떤 변화를 가져왔는지 살펴보자. 

---

## 14.2 자바 모듈 시스템을 설계한 이유
여기서는 자바 언어와 컴파일러에 새로운 모듈 시스템이 추가된 이유를 설명한다. 먼저 자바 9 이전의 모듈화 한계를 살펴본다. 그리고 JDK 라이브러리와 관련한 배경 지식을 제공하고 모듈화가 왜 중요한지 설명한다.

### 14.2.1 모듈화의 한계
안타깝게도, 자바 9 이전까지는 모듈화된 소프트웨어 프로젝트를 만드는 데 한계가 있었다.
- 자바는 클래스, 패키지 `JAR` 세 가지 수준의 코드 그룹화를 제공한다.
- 클래스와 관련해 자바는 접근 제한자와 캡슐화를 지원했다. 
- 하지만 패키지와 `JAR` 수준에서는 캡슐화를 거의 지원하지 않았다.

#### 제한된 가시성 제어
자바는 정보를 감출 수 있는 접근자를 제공한다.
- `public`, `protected`, 패키지 수준, `private` 이렇게 네 가지 가시성 접근자가 있다.
- 한 패키지의 클래스와 인터페이스를 다른 패키지로 공겨하려면 `public`으로 이들을 선언해야 한다.
- 결과적으로 이들 클래스와 인터페이스는 모두에게 공개된다. 이런 상황에서 보통 패키지 내부의 접근자가 `public`이므로 사용자가 이 내부 구현을 마음대로 사용할 수 있다.
- 내부적으로 사용할 목적으로 만든 구현을 다른 프로그래머가 임시적으로 사용해서 정착해버릴 수 있으므로 결국 기존의 애플리케이션을 망가뜨리지 않고 라이브러리 코드를 바꾸기가 어려워진다.
- 보안 측면에서 볼 때 코드가 노출되었으므로 코드를 임의로 조작하는 위협에 더 많이 노출될 수 있다.

#### 클래스 경로
안타깝게도 애플리케이션을 번들하고 실행하는 기능과 관련해 자바는 태생적으로 약점을 갖고 있다.
- 클래스를 모두 컴파일한 다음 보통 한 개의 평범한 `JAR` 파일에 넣고 클래스 경로(`class path`)에 이 `JAR` 파일을 추가해 사용할 수 있다.
- 그러면 `JVM`이 동적으로 클래스 경로에 정의된 클래스를 필요할 때 읽는다.

안타깝게도 클래스 경로와 `JAR` 조합에는 몇 가지 약점이 존재한다.

##### 첫째. 클래스 경로에는 같은 클래스를 구분하는 버전 개념이 없다.
예를 들어 파싱 라이브러리의 `JSONParser` 클래스를 지정할 때 버전 1.0을 사용하는지 버전 2.0을 사용하는지 지정할 수가 없으므로 클래스 경로에 두 가지 버전의 같은 라이브러리가 존재할 때 어떤 일이 일어날지 예측할 수 없다.
다양한 컴포넌트가 같은 라이브러리의 다른 버전을 사용하는 상황이 발생할 수 있는 큰 애플리케이션에서 이런 문제가 두드러진다.

##### 둘째. 클래스 경로는 명시적인 의존성을 지원하지 않는다.
각각의 `JAR` 안에 있는 모든 클래스는 `classes`라는 한 주머니로 합쳐진다. 
즉 한 `JAR`가 다른 `JAR`에 포함된 클래스 집합을 사용하라고 명시적으로 의존성을 정의하는 기능을 제공하지 않는다. 
이 상황에서는 클래스 경로 때문에 어떤 일이 일어나는지 파악하기 어려우며, 다음과 같은 의문이 든다.

- 빠진 게 있는가?
- 충돌이 있는가? 

메이븐이나 그레이들(`Gradle`) 같은 빌드 도구는 이런 문제를 해결하는 데 도움을 준다. 하지만 자바 9 이전에는 자바, `JVM` 누구도 명시적인 의존성 정의를 지원하지 않았다.
결국 `JVM`이 `ClassNotFoundException` 같은 에러를 발생시키지 않고 애플리케이션을 정상적으로 실행할 때까지 클래스 경로에 클래스 파일을 더하거나 클래스 경로에서 클래스를 제거해보는 수밖에 없다.
자바 9 모듈 시스템을 이용하면 컴파일 타임에 이런 종류의 에러를 모두 검출할 수 있다.

하지만 캡슐화, 클래스 경로 지옥 문제가 소프트웨어에만 발생하는 것은 아니다. `JDK` 자체는 괜찮을까?

### 14.2.2 거대한 JDK
**`자바 개발 키트(JDK)`** 는 자바 프로그램을 만들고 실행하는 데 도움을 주는 도구의 집합이다. 
가장 익숙한 도구로 자바 프로그램을 컴파일하는 `javac`, 자바 애플리케이션을 로드하고 실행하는 `java`, 입출력을 포함해 런타임 지원을 제공하는 `JDK` 라이브러리, 컬렉션, 스트림 등이 있다.

하지만 때론 `JDK`가 애플리케이션에 불필요한 클래스를 포함하고 있는 경우가 있었고 이는 나중에 모바일에서 실행되는 애플리케이션이나 `JDK` 전부를 필요로 하지 않는 클라우드에서 문제가 되었다.
자바 8에서는 **`컴팩트 프로파일(compact profiles)`**이라는 기법을 제시했다. 관련 분야에 따라 `JDK` 라이브러리가 세 가지 프로파일로 나뉘어 각각 다른 메모리 풋프린트를 제공했다.
하지만 컴팩트 프로파일은 땜질식 처방일 뿐이다. `JDK` 라이브러리의 많은 내부 `API`는 공개되지 않아야 한다. 안타깝게도 자바 언어의 낮은 캡슐화 지원 때문에 내부 `API`가 외부에 공개되었다.
예를 들어 `스프링(Spring)`, `네티(Netty)`, `모키토(Mockito)` 등 여러 라이브러리에서 `sun.misc.Unsafe`라는 클래스를 사용했는데 이 클래스는 `JDK` 내부에서만 사용하도록 만든 클래스다.
결과적으로 호환성을 깨지않고는 관련 `API`를 바꾸기가 아주 어려운 상황이 되었다.

이런 문제들 때문에 `JDK` 자체도 모듈화할 수 있는 자바 모듈 시스템 설계의 필요성이 제기되었다. 
즉 `JDK`에서 필요한 부분만 골라 사용하고, 클래스 경로를 쉽게 유추할 수 있으며, 플랫폼을 진화시킬 수 있는 강력한 캡슐화를 제공할 새로운 건축 구조가 필요했다.

### 14.2.3 OSGi와 비교
여기서는 자바 9 모듈을 `OSGi`와 비교한다. `OSGi`를 들어본 적이 없으므로 스킵한다.

---

## 14.3 자바 모듈 : 큰 그림
자바 8은 **`모듈`**이라는 새로운 자바 프로그램 구조 단위를 제공한다. 모듈은 `module`이라는 새 키워드에 이름과 바디를 추가해서 정의한다.
**`모듈 디스크립터(module descriptor)`**는 `module-info.java`라는 특별한 파일에 저장된다.

모듈 디스크립터는 보통 패키지와 같은 폴더에 위치하며 한 개 이상의 패키지를 서술하고 캡슐화할 수 있지만 단순한 상황에서는 이들 패키지 중 한 개만 외부로 노출시킨다.

`[그림 14-2]`는 자바 모듈 디스크립터의 핵심 구조를 보여준다.

##### 그림 14-2 자바 모듈 디스크립터의 핵심 구조(module-info.java)
![](https://images.velog.io/images/banjjoknim/post/19560631-e4b8-4b17-a986-f3c5a8b2563b/14-2.png)

직소 퍼즐에 비유하자면 `exports`는 돌출부, `requires`는 패인 부분으로 생각할 수 있다.
`[그림 14-3]`은 여러 모듈의 예를 보여준다.

##### 그림 14-3 A, B, C, D 네 개의 모듈로 만든 자바 시스템의 직소 퍼즐 형식 예제
![](https://images.velog.io/images/banjjoknim/post/c062b795-de1f-497c-9909-17e379ca1d37/14-3.png)

모듈 A는 모듈 B와 C를 필요로 하며 이들은 패키지 모듈 B와 모듈 C를 이용해 각각 pkgB와 pkgC에 접근할 수 있다.
모듈 C는 비슷한 방법으로 pkgD를 사용하는데 pkgD는 모듈 C에서 필요로 하지만 모듈 B에서는 pkgD를 사용할 수 없다.

메이븐 같은 도구를 사용할 때 모듈의 많은 세부 사항을 IDE가 처리하며 사용자에게는 잘 드러나지 않는다.

---

## 14.4 자바 모듈 시스템으로 애플리케이션 개발하기
여기서는 간단한 모듈화 애플리케이션을 기초부터 만들면서 자바 9 모듈 시스템 전반을 살펴본다.

### 14.4.1 애플리케이션 셋업
다음과 같은 여러 작업을 처리해주는 애플리케이션을 구현해보자.
- 파일이나 URL에서 비용 목록을 읽는다.
- 비용의 문자열 표현을 파싱한다.
- 통계를 계산한다.
- 유용한 요약 정보를 표시한다.
- 각 태스크의 시작, 마무리 지점을 제공한다.

애플리케이션의 개념을 모델링할 여러 클래스와 인터페이스를 정의해야 한다.
먼저 `Reader` 인터페이스는 소스에서 얻어온 직렬화된 지출을 읽는 역할을 한다.
소스가 어디냐에 따라 `HttpReader`, `FileReader` 등 여러 구현을 제공해야 한다.
또한 `JSON` 객체를 자바 애플리케이션에서 사용할 수 있는 도메인 객체 `Expense`로 재구성할 `Parser` 인터페이스도 필요하다.
마지막으로 주어진 `Expense` 객체 목록으로 통계를 계산하고 `SummaryStatistics` 객체를 반환하는 `SummaryCalculator` 클래스가 필요하다.

프로젝트에는 다음처럼 분리할 수 있는 여러 기능(관심사)이 있다.
- 다양한 소스에서 데이터를 읽음(`Reader`, `HttpReader`, `FileReader`)
- 다양한 포맷으로 구성된 데이터를 파싱(`Parser`, `JSONParser`, `ExpenseJSON - Parser`)
- 도메인 객체를 구체화(`Expense`)
- 통계를 계산하고 반환(`SummaryCalculator`, `SummaryStatistics`)
- 다양한 기능을 분리 조정(`ExpensesApplication`)

교수법에 따라 아주 세부적으로 문제를 나누는 접근 방법을 이용한다. 
다음처럼 각 기능을 그룹화할 수 있다(모듈을 명명한 배경은 나중에 설명한다).
- expense.readers
- expense.readers.http
- expense.readers.file
- expense.parsers
- expense.parsers.json
- expense.model
- expense.statistics
- expense.application

이 간단한 애플리케이션에서는 모듈 시스템의 여러 부분이 두드러질 수 있도록 잘게 분해했다.
실생활에서 단순한 프로젝트를 이처럼 잘게 분해해 작은 기능까지 캡슐화한다면 장점에 비해 초기 비용이 높아지고, 논란이 생길 수 있다.
하지만 프로젝트가 점점 커지면서 많은 내부 구현이 추가되면 이때부터 캡슐화와 추론의 장점이 두드러진다.
위에서 나열한 목록을 애플리케이션 경계에 의존하는 패키지 목록으로 생각할 수 있으며, 아마 각 모듈은 다른 모듈로 노출하고 싶지 않은 내부 구현을 포함할 것이다.
예를 들어 `expenses.statistics` 모듈은 실험적인 통계 방법을 다른 방법으로 구현한 여러 패키지를 포함할 수 있다.
이들 패키지에서 어떤 것을 사용자에게 릴리스할지는 나중에 결정할 수 있다.

### 14.4.2 세부적인 모듈화와 거친 모듈화
시스템을 모듈화할 때 모듈 크기를 결정해야 한다.
세부적인 모듈화 기법 대부분은 모든 패키지가 자신의 모듈을 갖는다.
거친 모듈화 기법 대부분은 한 모듈이 시스템의 모든 패키지를 포함한다.
첫 번째 기법은 이득에 비해 설계 비용이 증가하는 반면 두 번째 기법은 모듈화의 모든 장점을 잃는다.
가장 좋은 방법은 시스템을 실용적으로 분해하면서 진화하는 소프트웨어 프로젝트가 이해하기 쉽고 고치기 쉬운 수준으로 적절하게 모듈화되어 있는지 주기적으로 확인하는 프로세스를 갖는 것이다.
요약하면 모듈화는 소프트웨어 부식의 적이다.

### 14.4.3 자바 모듈 시스템 기초
메인 애플리케이션을 지원하는 한 개의 모듈만 갖는 기본적인 모듈화 애플리케이션부터 시작하자. 다음은 디릭터리 안에 중첩된 프로젝트 디렉터리 구조를 보여준다.

```
|-- expenses.application
 |-- module-info.java
 |-- com
  |-- example
   |-- expenses
    |-- application
     |-- ExpensesApplication.java
```

정체를 알 수 없는 `module-info.java`라는 파일이 프로젝트 구조의 일부에 포함되어 있다.
이 파일은 앞에서 설명한 모듈 디스크립터로 모듈의 소스 코드 파일 루트에 위치해야 하며 모듈의 의존성 그리고 어떤 기능을 외부로 노출할지를 정의한다.
지출 애플리케이션 예제에서는 아직 다른 모듈에 의존하거나 외부로 노출하는 기능이 없으므로 최상위 수준의 `module-info.java` 파일에 이름만 정의되어 있을 뿐 내용은 비어있다.
다음은 현재 `module-info.java`의 내용이다.

```java
module expenses.application {

}
```

모듈화 애플리케이션은 어떻게 실행시킬 수 있을까? 하위 수준의 동작을 이해할 수 있는 일부 명령을 살펴보자.
보통 IDE와 빌드 시스템에서 이들 명령을 자동으로 처리하지만 이들 명령이 어떤 동작을 수행하는지 확인하는 것은 내부적으로 어떤 일이 일어나는지 이해하는 데 도움이 된다.
프로젝트의 모듈 소스 디렉터리에서 다음 명령을 실행한다.

```shell script
javac module-info.java com/example/expenses/application/ExpensesApplication.java -d target

jar cvfe expenses-application.jar com.example.expenses.application.ExpensesApplication -C target
```

그럼 어떤 폴더와 클래스 파일이 생성된 `JAR(expenses-application.jar)`에 포함되어 있는지를 보여주는 다음과 같은 결과가 출력된다.

```jvm
added manifest
added module-info: module-info.class adding: com/(in = 0) (out = 0)(stored 0%)
adding: com/example/(in = 0) (out = 0)(stored 0%)
adding: com/example/expenses/(in = 0) (out = 0)(stored 0%)
adding: com/example/expenses/application/(in = 0) (out = 0)(stored 0%)
adding: com/example/expenses/application/ExpensesApplication.class(in = 456) (out = 306)(deflated 32%)
```

마지막으로 생성된 `JAR`를 모듈화 애플리케이션으로 실행한다.

```shell script
java --module-path expenses-application.jar \
     --module expenses/com.example.expenses.application.ExpensesApplication
```

처음 두 과정은 자바 애플리케이션을 `JAR`로 패키징하는 표준 방법이다.
새로운 부분은 컴파일 과정에 새로 추가된 `module-info.java`다.

`java` 프로그램으로 자바 `.class` 파일을 실행할 때 다음과 같은 두 가지 옵션이 새로 추가되었다.
- `--module-path` : 어떤 모듈을 로드할 수 있는지 지정한다. 이 옵션은 클래스 파일을 지정하는 `--classpath` 인수와는 다르다.
- `--module` : 이 옵션은 실행할 메인 모듈과 클래스를 지정한다.

모듈 정의는 버전 문자열을 포함하지 않는다. 자바 9 모듈 시스템에서 버전 선택 문제를 크게 고려하지 않았고 따라서 버전 기능은 지원하지 않는다.
대신 버전 문제는 빌드 도구나 컨테이너 애플리케이션에서 해결해야 할 문제로 넘겼다.

---

## 14.5 여러 모듈 활용하기
모듈을 이용한 기본 애플리케이션을 실행했으므로 이제 다양한 모듈과 관련된 실용적인 예제를 살펴볼 수 있다.
비용 애플리케이션이 소스에서 비용을 읽을 수 있어야 한다. 이 기능을 캡슐화한 `expense.reader`라는 새 모듈을 만들 것이다.
`expenses.application`와 `expenses.readers` 두 모듈간의 상호 작용은 자바 9에서 지정한 `export`, `requires`를 이용해 이루어진다.

### 14.5.1 exports 구문
다음은 `expenses.readers` 모듈의 선언이다

```java
module expenses.readers { 
    exports com.example.expenses.readers; // 모듈명이 아니라 패키지명이다.
    exports com.example.expenses.readers.file; // 모듈명이 아니라 패키지명이다.
    exports com.example.expenses.readers.http; // 모듈명이 아니라 패키지명이다.
}
```

`exports`라는 구문이 새로 등장했는데 `exports`는 다른 모듈에서 사용할 수 있도록 특정 패키지를 공개 형식으로 만든다. 
기본적으로 모듈 내의 모든 것은 캡슐화된다. 모듈 시스템은 화이트 리스트 기법을 이용해 강력한 캡슐화를 제공하므로 다른 모듈에서 사용할 수 있는 기능이 무엇인지 명시적으로 결정해야 한다(이 접근법은 실수로 어떤 기능을 외부로 노출함으로 몇 년이 지난 뒤에 해커가 시스템을 남용할 여지를 방지한다).

프로젝트의 두 모듈의 디렉터리 구조는 다음과 같다.

```jvm
|-- expenses.application
 |-- module-info.java
 |-- com
  |-- example
   |-- expenses
    |-- application
     |-- ExpensesApplication.java

|-- expenses.readers
 |-- module-info.java
 |-- com
  |-- example
   |-- expenses
    |-- readers
     |-- Reader.java
    |-- file
     |-- FileReader.java
    |-- http
     |-- HttpReader.java
```

### 14.5.2 requires 구문
또는 다음처럼 `module-info.java`를 구현할 수 있다.

```java
module expenses.readers {
    requires java.base; // 패키지명이 아니라 모듈명이다.
    
    exports com.example.expenses.readers; // 모듈명이 아니라 패키지명이다.
    exports com.example.expenses.readers.file; // 모듈명이 아니라 패키지명이다.
    exports com.example.expenses.readers.http; // 모듈명이 아니라 패키지명이다.
}
```

`requires`라는 구문이 새로 등장했는데 `requires`는 의존하고 있는 모듈을 지정한다. 
기본적으로 모든 모듈은 `java.base`라는 플랫폼 모듈에 의존하는데 이 플랫폼 모듈은 `net`, `io`, `util` 등의 자바 메인 패키지를 포함한다.
항상 기본적으로 필요한 모듈이므로 `java.base`는 명시적으로 정의할 필요가 없다.
자바에서 `"class Foo extends Object { ... }"`처럼 하지 않고 `"class Foo { ... }"`처럼 클래스를 정의하는 것과 같은 원리다.

따라서 `java.base` 외의 모듈을 임포트할 때 `requires`를 사용한다.

자바 9에서는 `requires`와 `exports` 구문을 이용해 좀 더 정교하게 클래스 접근을 제어할 수 있다.

##### 표 14-2 자바 9는 클래스 가시성을 더 잘 제어할 수 있는 기능을 제공
|클래스 가시성|자바 9 이전| 자바 9 이후|
|--|--|--|
|모든 클래스가 모두에 공개됨|O|O (`exports`와 `requires` 구분 혼합)|
|제한된 클래스만 공개됨|X|O (`exports`와 `requires` 구분 혼합)|
|한 모듈의 내에서만 공개|X|O (`export` 구문 없음)|
|Protected|O|O|
|Package|O|O|
|Private|O|O|

### 14.5.3 이름 정하기
지금까지 `expenses.application`처럼 모듈과 패키지의 개념이 혼동되지 않도록 단순한 접근 방식을 사용했다(모듈은 여러 패키지를 노출시킬 수 있다).
하지만 이 방법은 권장 사항과 일치하지 않는다.

오라클은 패키지명처럼 인터넷 도메인명을 역순(예를 들어 `com.iteratrlearning.training`)으로 모듈의 이름을 정하도록 권고한다.
더욱이 모듈명은 노출된 주요 `API` 패키지와 이름이 같아야 한다는 규칙도 따라야 한다.
모듈이 패키지를 포함하지 않거나 어떤 다른 이유로 노출된 패키지 중 하나와 이름이 일치하지 않는 상황을 제외하면 모듈명은 작성자의 도메인명을 역순으로 시작해야 한다.

여러 모듈을 프로젝트에 설정하는 방법을 살펴봤으므로 이제 이들을 패키지하고 실행하는 방법을 살펴보자.

---

## 14.6 컴파일과 패키징
프로젝트를 설정하고 모듈을 정의하는 방법을 이해했으므로 메이븐 등의 빌드 도구를 이용해 프로젝트를 컴파일할 수 있다.

먼저 각 모듈에 `pom.xml`을 추가해야 한다. 사실 각 모듈은 독립적으로 컴파일되므로 자체적으로 각각이 한 개의 프로젝트다.
전체 프로젝트 빌드를 조정할 수 있도록 모든 모듈의 부모 모듈에도 `pom.xml`을 추가한다. 전체 구조는 다음과 같다.

```jvm
|-- pom.xml
|-- expenses.application
 |-- pom.xml
 |-- src
  |-- main
   |-- java
    |-- module-info.java
    |-- com
     |-- example
      |-- expenses
       |-- application
        |-- ExpensesApplication.java
|-- expenses.readers
 |-- pom.xml
 |-- src
  |-- main
   |-- java
    |-- module-info.java
    |-- com
     |-- example
      |-- expenses
       |-- readers
        |-- Reader.java
       |-- file
        |-- FileReader.java
       |--http
        |-- HttpReader.java
```

이렇게 세 개의 `pom.xml` 파일을 추가해 메이븐 디렉터리 프로젝트 구조를 완성했다.
모듈 디스크립터(`module-info.java`)는 `src/main/java` 디렉터리에 위치해야 한다.
올바른 모듈 소스 경로를 이용하도록 메이븐이 `javac`를 설정한다.

다음은 `expenses.readers` 프로젝트의 `pom.xml`이다. 

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.example</groupId>
    <artifactId>expenses.readers</artifactId>
    <version>1.0</version>
    <packaging>jar</packaging>
    <parent>
        <groupId>com.example</groupId>
        <artifactId>expenses</artifactId>
        <version>1.0</version>
    </parent>
</project>
```

순조롭게 빌드될 수 있도록 명시적으로 부모 모듈을 지정한 코드를 주목하자.
부모는 `ID expenses`를 포함하는 `부산물(artifact)`이다. 곧 살펴보겠지만 `pom.xml`에 부모를 정의해야 한다.

다음으로 `expenses.application` 모듈의 `pom.xml`을 정의한다.
이 파일을 이전 파일과 비슷하지만 `ExpenseApplication`이 필요로 하는 클래스와 인터페이스가 있으므로 `expenses.readers`를 의존성으로 추가해야 한다.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.example</groupId>
    <artifactId>expenses.application</artifactId>
    <version>1.0</version>
    <packaging>jar</packaging>

    <parent>
        <groupId>com.example</groupId>
        <artifactId>expenses</artifactId>
        <version>1.0</version>
    </parent>

    <dependencies>
        <dependency>
            <groupId>com.example</groupId>
            <artifactId>expenses.readers</artifactId>
            <version>1.0</version>
        </dependency>
    </dependencies>

</project>
```

`expenses.application`와 `expenses.readers` 두 모듈에 `pom.xml`을 추가했으므로 이제 빌드 과정을 가이드할 전역 `pom.xml`을 설정할 차례다.
메이븐은 특별한 `XML` 요소 `<module>(자식의 부산물 ID를 참조)`을 가진 여러 메이븐 모듈을 가진 프로젝트를 지원한다.
다음은 두 개의 자식 모듈 `expenses.application`와 `expenses.readers`를 참조하도록 완성한 `pom.xml` 정의다.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.example</groupId>
    <artifactId>expenses</artifactId>
    <packaging>pom</packaging>
    <version>1.0</version>
    
    <modules>
        <module>expenses.application</module>
        <module>expenses.readers</module>
    </modules>

    <build>
        <pluginManagement>
            <plugins>
                <plugin>
                    <groupId>org.apache.maven.plugins</groupId>
                    <artifactId>maven-compiler-plugin</artifactId>
                    <version>3.7.0</version>
                    <configuration>
                        <source>9</source>
                        <target>9</target>
                    </configuration>
                </plugin>
            </plugins>
        </pluginManagement>
    </build>

</project>
```

이제 `mvn clean package` 명령을 실행해서 프로젝트의 모듈을 `JAR`로 만들 수 있다. 다음과 같은 부산물이 만들어진다.

```shell script
./expenses.application/target/expenses.application-1.0.jar
./expenses.readers/target/expenses.readers-1.0.jar
```

두 `JAR`를 다음처럼 모듈 경로에 포함해서 모듈 애플리케이션을 실행할 수 있다.

```shell script
java --module-path \
./expenses.application/target/expenses.application-1.0.jar:\
./expenses.readers/target/expenses.readers-1.0.jar \
  --module \
 expenses.application/com.example.expenses.application.ExpensesApplication
```

지금까지 모듈을 만드는 방법을 배웠으며 `requires`로 `java.base`를 참조하는 방법도 살펴봤다.
하지만 실세계에서는 `java.base` 대신 외부 모듈과 라이브러리를 참조해야 한다.
이 과정은 어떻게 동작하며 기존 라이브러리가 명시적으로 `module-info.java`를 사용하도록 업데이트되지 않았을 때 어떤 일이 일어나는지 알아보자.

---

## 14.7 자동 모듈
`HttpReader`를 저수준으로 구현하지 않고 아파치 프로젝트의 `httpclient` 같은 특화 라이브러리를 사용해 구현한다고 가정하자.
이런 라이브러리는 어떻게 프로젝트에 추가할 수 있을까?
`requires` 구문을 배웠으므로 `expenses.readers` 프로젝트의 `module-info.java`에 이 구문을 추가한다.
`mvn clean package`를 다시 실행해서 어떤 일이 일어나는지 확인하자. 안타깝게도 나쁜 일이 일어난다.

`[ERROR] module not found: httpclient`

의존성을 기술하도록 `pom.xml`도 갱신해야 하므로 에러가 발생한다.
메이븐 컴파일러 플러그인은 `module-info.java`를 포함하는 프로젝트를 빌드할 때 모든 의존성 모듈을 경로에 놓아 적절한 `JAR`를 내려받고 이들이 프로젝트에 인식되도록 한다.
다음과 같은 의존성이 필요하다.

```xml
<dependencies>
    <dependency>
        <groupId>org.apache.httpcomponents</groupId>
        <artifactId>httpclient</artifactId>
        <version>4.5.3</version>
    </dependency>
</dependencies>
```

이제 `mvn clean package`를 실행하면 프로젝트가 올바로 빌드된다. 하지만 `httpclient`는 자바 모듈이 아니다.
`httpclient`는 자바 모듈로 사용하려는 외부 라이브러리인데 모듈화가 되어 있지 않은 라이브러리다.
자바는 `JAR`를 자동 모듈이라는 형태로 적절하게 변환한다. 모듈 경로상에 있으나 `module-info` 파일을 가지지 않은 모든 `JAR`는 자동 모듈이 된다.
자동 모듈은 암묵적으로 자신의 모든 패키지를 노출시킨다. 자동 모듈의 이름은 `JAR` 이름을 이용해 정해진다.
`jar` 도구의 `--describe-module` 인수를 이용해 자동으로 정해지는 이름을 바꿀 수 있다.

```shell script
jar --file=./expenses.readers/target/dependency/httpclient-4.5.3.jar \
    --describe-module httpclient@4.5.3 automatic
```

그러면 `httpclient`라는 이름으로 정의된다.

마지막으로 `httpclient JAR`를 모듈 경로에 추가한 다음 애플리케이션을 실행한다.

```shell script
java --module-path \
  ./expenses.application/target/expenses.application-1.0.jar:\
  ./expenses.readers/target/expenses.readers-1.0.jar \
  ./expenses.readers/target/dependency/httpclient-4.5.3.jar \
     --module \
  expenses.application/com.example.expenses.application.ExpensesApplication
```

>**NOTE**
>
>메이븐에는 자바 9 모듈 시스템을 더 잘 지원하는 프로젝트(https://github.com/moditect/moditect)가 있다. 이 프로젝트는 `module-info` 파일을 자동으로 생성한다.

---

## 14.8 모듈 정의와 구문들
여기서는 모듈 정의 언어에서 사용할 수 있는 몇 가지 키워드를 간단하게 소개한다.

앞에서 서술한 것처럼 `module` 지시어를 이용해 모듈을 정의할 수 있다. 다음은 `com.iteratrlearning.application`이라는 모듈명의 예제다.

```java
module com.iteratrlearning.application {

}
```

모듈 정의에는 어떤 내용을 넣을 수 있을까?
`requires`, `exports` 구문을 배웠지만 이 외에 `requires-transitive`, `exports-to`, `open`, `opens`, `uses`, `provides` 같은 다른 구문들도 있다.
지금부터 이들 구문을 하나씩 살펴본다.

### 14.8.1 requires
`requires` 구문은 컴파일 타임과 런타임에 한 모듈이 다른 모듈에 의존함을 정의한다. 예를 들어 `com.iteratrlearning.application`은 `com.iteratrlearning.ui` 모듈에 의존한다.

```java
module com.iteratrlearning.application {
    requires com.iteratrlearning.ui;
}
```

그러면 `com.iteratrlearning.ui`에서 외부로 노출한 공개 형식을 `com.iteratrlearning.application`에서 사용할 수 있다.

### 14.8.2 exports
`exports` 구문은 지정한 패키지를 다른 모듈에서 이용할 수 있도록 공개 형식으로 만든다. 아무 패키지도 공개하지 않는 것이 기본 설정이다.
어떤 패키지를 공개할 것인지를 명시적으로 지정함으로 캡슐화를 높일 수 있다. 
다음 예제에서는 `com.iteratrlearning.ui.panels`와 `com.iteratrlearning.ui.widgets`를 공개했다
(참고로 문법이 비슷함에도 불구하고 `exports`는 **패키지명**을 인수로 받지만 `requires`는 **모듈명**을 인수로 받는다는 사실에 주의하자).

```java
module com.iteratrlearning.ui {
    requires com.iteratrlearning.core;
    exports com.iteratrlearning.ui.panels;
    exports com.iteratrlearning.ui.widgets;
}
```

### 14.8.3 requires transitive
다른 모듈이 제공하는 공개 형식을 한 모듈에서 사용할 수 있다고 지정할 수 있다.
예를 들어 `com.iteratrlearning.ui` 모듈의 정의에서 `requires`를 `requires-transitive`로 바꿀 수 있다.

```java
module com.iteratrlearning.ui {
    requires transitive com.iterartlearning.core;
    
    exports com.iteratrlearning.ui.panels;
    exports com.iteratrlearning.ui.widgets;
}

module com.iteratrlearning.application {
    requires com.iteratrlearning.ui;
}
```

결과적으로 `com.iteratrlearning.application` 모듈은 `com.iteratrlearning.core`에서 노출한 공개 형식에 접근할 수 있다.
필요로 하는 모듈(`com.iteratrlearning.ui`)이 다른 모듈(`com.iteratrlearning.core`)의 형식을 변환하는 상황에서 `전이성(transitivity)` 선언을 유용하게 사용할 수 있다.
`com.iteratrlearning.application` 모듈의 내부에 `com.iteratrlearning.core` 모듈을 다시 선언하는 것은 성가신 일이기 때문이다.
따라서 이런 상황에서는 `transitive`를 이용해 문제를 해결할 수 있다.
`com.iteratrlearning.io` 모듈에 의존하는 모든 모듈은 자동으로 `com.iteratrlearning.core` 모듈을 읽을 수 있게 된다.

### 14.8.4 exports to
`exports to` 구문을 이용해 사용자에게 공개할 기능을 제한함으로 가시성을 좀 더 정교하게 제어할 수 있다.
`14.8.2`에서 살펴본 예제에서 다음처럼 `exports to`를 이용하면 `com.iteratrlearning.ui.widgets`의 접근 권한을 가진 사용자의 권한을 `com.iteratrlearning.ui.widgetuser`로 제한할 수 있다.

```java
module com.iteratrlearning.ui {
    requires com.iteratrlearning.core;
    
    exports com.iteratrlearning.ui.panels;
    exports com.iteratrlearning.ui.widgets to com.iteratrlearning.ui.widgetuser;
}
```

### 14.8.5 open과 opens
모듈 선언에 `open` 한정자를 이용하면 모든 패키지를 다른 모듈에 반사적으로 접근을 허용할 수 있다.
다음 예제에서 보여주는 것처럼 반사적인 접근 권한을 주는 것 이외에 `open` 한정자는 모듈의 가시성에 다른 영향을 미치지 않는다.

```java
open module com.iteratrlearning.ui {

}
```

자바 9 이전에는 리플렉션으로 객체의 비공개 상태를 확인할 수 있었다. 즉 진정한 캡슐화는 존재하지 않았다.
하이버네이트(`Hibernate`) 같은 객체 관계 매핑(`Object-relational mapping`-`ORM`) 도구에서는 이런 기능을 이용해 상태를 직접 고치곤 한다.
자바 9에서는 기본적으로 리플렉션이 이런 기능을 허용하지 않는다. 이제 그런 기능이 필요하면 이전 코드에서 설명한 `open` 구문을 명시적으로 사용해야 한다.

리플렉션 떄문에 전체 모듈을 개방하지 않고도 `opens` 구문을 모듈 선언에 이용해 필요한 개별 패키지만 개방할 수 있다.
`exports-to`로 노출한 패키지를 사용할 수 있는 모듈을 한정했던 것처럼, `open`에 `to`를 붙여서 반사적인 접근을 특정 모듈에만 허용할 수 있다.

### 14.8.6 uses와 provides
자바 모듈 시스템은 `provides` 구문으로 서비스 제공자를 `uses` 구문으로 서비스 소비자를 지정할 수 있는 기능을 제공하는데 서비스와 `ServiceLoader`를 알고 있다면 친숙한 내용일 것이다.
만약 모듈과 서비스 로더를 합치는 기법에 관심이 있다면 앞서 언급했던 `『The Java Module System』`을 살펴보길 추천한다.

---

## 14.9 더 큰 예제 그리고 더 배울 수 있는 방법
오라클의 자바 문서에서 가져온 다음 예제로 모듈 시스템이 어떤 것인지 더 확인할 수 있다.
아래 예제는 여기서 설명한 기능의 대부분을 모듈 선언에 사용한다.

```java
module com.example.foo {
    requires com.example.foo.http;
    requires java.logging;
    requires transitive com.example.foo.network;
   
    exports com.example.foo.bar;
    exports com.example.foo.internal to com.example.foo.probe;
    
    opens com.example.foo.quux;
    opens com.example.foo.internal to com.example.foo.network, com.example.foo.probe;

    uses com.example.foo.spi.Intf;
    provides com.example.foo.spi.Intf with com.example.foo.Impl;
}
```

이때까지 새로운 자바 모듈 시스템의 필요성을 설명하고 주요 기능을 간단하게 소개했다.
이 외에도 서비스 로더나 추가 모듈 서술자, `jeps`, `jlink` 같은 모듈 관련 도구들이 있다.
`자바 EE` 개발자라면 애플리케이션을 자바 9로 이전할 때 `EE`와 관련한 여러 패키지가 모듈화된 자바 9 가상 머신에서 기본적으로 로드되지 않는다는 사실을 기억해야 한다.
예를 들어 `JAXB API` 클래스는 이제 `자바 EE API`로 간주되므로 `자바 SE 9`의 기본 클래스 경로에 더는 포함되지 않는다.
따라서 호환성을 유지하려면 `--add-modules` 명령행을 이용해 명시적으로 필요한 모듈을 추가해야 한다.
예를 들어 `java.xml.bind`가 필요하면 `--add-modules java.xml.bind`를 지정해야 한다.

---

## 14.10 마치며
- 관심사분리와 정보 은닉은 추론하기 쉬운 소프트웨어를 만드는 중요한 두 가지 원칙이다.
- 자바 9 이전에는 각각의 기능을 담당하는 패키지, 클래스, 인터페이스로 모듈화를 구현했는데 효과적인 캡슐화를 달성하기에는 역부족이었다.
- 클래스 경로 지옥 문제는 애플리케이션의 의존성을 추론하기 더욱 어렵게 만들었다.
- 자바 9 이전의 `JDK`는 거대했으며 높은 유지 비용과 진화를 방해하는 문제가 존재했다.
- 자바 9에서는 새로운 모듈 시스템을 제공하는데 `module-info.java` 파일은 모듈의 이름을 지정하며 `필요한 의존성(requires)`과 `공개 API(exports)`를 정의한다.
- `requires` 구문으로 필요한 다른 모듈을 정의할 수 있다.
- `exports` 구문으로 특정 패키지를 다른 모듈에서 사용할 수 있는 공개 형식으로 지정할 수 있다.
- 인터넷 도메인명을 역순으로 사용하는 것이 권장 모듈 이름 규칙이다.
- 모듈 경로에 포함된 `JAR` 중에 `module-info` 파일을 포함하지 않는 모든 `JAR`는 자동 모듈이 된다.
- 자동 모듈은 암묵적으로 모든 패키지를 공개한다.
- 메이븐은 자바 9 모듈 시스템으로 구조화된 애플리케이션을 지원한다.

---


