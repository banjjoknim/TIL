# Chapter15. CompletableFuture와 리액티브 프로그래밍 컨셉의 기초
- 요즘에는 독립적으로만 동작하는 웹사이트나 네트워크 애플리케이션을 찾아보기 힘들다.
- 즉 앞으로 만들 웹 애플리케이션은 다양한 소스의 콘텐츠를 가져와서 사용자가 삶을 풍요롭게 만들도록 합치는 `매시업(mashup)` 형태가 될 가능성이 크다.

##### 그림 15-1 일반 매시업 애플리케이션
![](https://images.velog.io/images/banjjoknim/post/fb8d8b47-7cb5-4953-8234-ba3cb69cf48a/15-1.%20%EC%9D%BC%EB%B0%98%20%EB%A7%A4%EC%8B%9C%EC%97%85%20%EC%8B%9C%EB%AE%AC%EB%A0%88%EC%9D%B4%EC%85%98.png)

이런 애플리케이션을 구현하려면 인터넷으로 여러 웹 서비스에 접근해야 한다. 하지만 이들 서비스의 응답을 기다리는 동안 연산이 블록되거나 귀중한 CPU 클록 사이클 자원을 낭비하고 싶진 않다.
예를 들어 페이스북의 데이터를 기다리는 동안 트위터 데이터를 처리하지 말란 법은 없다.

이 상황은 멀티태스크 프로그래밍의 양면성을 보여준다. 이전에 설명한 포크/조인 프레임워크와 병렬 스트림은 병렬성의 귀중한 도구다. 이들은 한 태스크를
여러 하위 태스크로 나눠서 CPU의 다른 코어 또는 다른 머신에서 이들 하위 태스크를 병렬로 실행한다.

반면 병렬성이 아니라 동시성을 필요로 하는 상황 즉 조금씩 연관된 작업을 같은 CPU에서 동작하는 것 또는 애플리케이션을 생산성을 극대화할 수 있도록
코어를 바쁘게 유지하는 것이 목표라면, 원격 서비스나 데이터베이스 결과를 기다리는 스레드를 블록함으로 연산 자원을 낭비하는 일은 피해야 한다.

자바는 이런 환경에서 사용할 수 있는 두 가지 주요 도구를 제공한다.
- 첫 번째는 `Future` 인터페이스로, 자바 8의 `CompletableFuture` 구현은 간단하고 효율적인 문제 해결사다.
- 최근 자바 9에 추가된 발행 구독 프로토콜에 기반한 리액티브 프로그래밍 개념을 따르는 플로 API는 조금 더 정교한 프로그래밍 접근 방법을 제공한다.

[그림 15-2]는 동시성과 별렬성의 차이를 보여준다. 동시성은 단일 코어 머신에서 발생할 수 있는 프로그래밍 속성으로 실행이 서로 겹칠 수 있는 반면
병렬성은 실행을 하드웨어 수준에서 지원한다.

##### 그림 15-2 동시성 대 병렬성
![](https://images.velog.io/images/banjjoknim/post/65701801-fae5-436d-96d0-5b614ad609b7/15-2.%20%EB%8F%99%EC%8B%9C%EC%84%B1%20%EB%8C%80%20%EB%B3%91%EB%A0%AC%EC%84%B1.png)

여기서는 자바의 새로운 기능인 `CompletableFuture`와 `플로 API`의 기초를 구성하는 내용을 설명한다.
다양한 자바 동시성 기능을 이용해 결과를 얻는지 등의 예제를 이용해 대부분의 개념을 설명한다.

---

## 15.1 동시성을 구현하는 자바 지원의 진화
처음에 자바는 `Runnable`과 `Thread`를 동기화된 클래스와 메서드를 이용해 잠갔다.
2004년, 자바 5는 좀 더 표현력 있는 동시성을 지원하는 특히 스레드 실행과 태스크 제출을 분리하는 `ExecutorService` 인터페이스,
높은 수준의 결과 즉, `Runnable`, `Thread`의 변형을 반환하는 `Callable<T> and Future<T>`, `제네릭` 등을 지원했다.
`ExecutorServices`는 `Runnable`과 `Callable` 둘 다 실행할 수 있다. 이런 기능들 덕분에 다음 해부터 등장한 멀티코어 CPU에서
쉽게 병렬 프로그래밍을 구현할 수 있게 되었다.

멀티코어 CPU에서 효과적으로 프로그래밍을 실행할 필요성이 커지면서 이후 자바 버전에서는 개선된 동시성 지원이 추가되었다.
자바 7에서는 분할 그리고 정복 알고리즘의 포크/조인 구현을 지원하는 `java.util.concurrent.RecursiveTask`가 추가되었고
자바 8에서는 스트림과 새로 추가된 람다 지원에 기반한 병렬 프로세싱이 추가되었다.

자바는 `Future`를 조합하는 기능을 추가하면서 동시성을 강화(`Future`구현인 자바 8 `CompletableFuture`)했고,
자바 9에서는 분산 비동기 프로그래밍을 명시적으로 지원한다. 이들 API는 15장 처음 부분에서 언급했던 매쉬업 애플리케이션 즉, 다양한 웹 서비스를 이용하고
이들 정보를 실시간으로 조합해 사용자에게 제공하거나 추가 웹 서비스를 통해 제공하는 종류의 애플리케이션을 개발하는데 필수적인 기초 모델과 툴킷을 제공한다.
이 과정을 리액티브 프로그래밍이라 부르며 자바 9에서는 발행-구독 프로토콜(`java.util.concurrent.Flow` 인터페이스 추가)로 이를 지원한다.
`CompletableFuture`와 `java.util.concurrent.Flow`의 궁극적인 목표는 가능한한 동시에 실행할 수 있는 독립적인 태스크를 가능하게 만들면서
멀티코어 또는 여러 기기를 통해 제공되는 병렬성을 쉽게 이용하는 것이다.'

### 15.1.1 스레드와 높은 수준의 추상화
단일 CPU 컴퓨터도 여러 사용자를 지원할 수 있는데 이는 운영체제가 각 사용자에 프로세스 하나를 할당하기 때문이다.
운영체제는 두 사용자가 각각 자신만의 공간에 있다고 생각할 수 있도록 가상 주소 공간을 각각의 프로세스에 제공한다.
운영체제는 주기적으로 번갈아가며 각 프로세스에 CPU를 할당한다. 프로세스는 다시 운영체제에 한 개 이상의 `스레드` 즉, 본인이 가진 프로세스와 같은
주소 공간을 공유하는 프로세스를 요청함으로 태스크를 동시에 또는 협력적으로 실행할 수 있다.

멀티코어 설정(한 사용자 프로세스만 실행하는 한명의 사용자 노트북)에서는 스레드의 도움 없이 프로그램이 노트북의 컴퓨팅 파워를 모두 활용할 수 없다.
각 코어는 한 개 이상의 프로세스나 스레드에 할당될 수 있지만 프로그램이 스레드를 사용하지 않는다면 효율성을 고려해 여러 프로세서 코어 중 한 개만을 사용할 것이다.

실제로 네 개의 코어를 가진 CPU에서 이론적으로는 프로그램을 네 개의 코어에서 병렬로 실행함으로 실행 속도를 네 배까지 향상시킬 수 있다(물론 오버헤드로 인해 실제 네 배가 되긴 어렵다).

다음은 학생들이 제출한 숫자 1,000,000개를 저장한 배열을 처리하는 예제이다.

```java
long sum = 0;
for (int i = 0; i < 1_000_000; i++) {
    sum += stats[i];
}
```
 
위 코드는 한 개의 코어로 며칠 동안 작업을 수행한다. 반면 아래의 코드는 첫 스레드를 다음 처럼 실행한다.

```java
long sum0 = 0;
for (int i = 0; i < 250_000; i++) {
    sum0 += stats[i];
}
```

그리고 네 번째 스레드는 다음으로 끝난다.
```java
long sum3 = 0;
for (int i = 750_000; i < 1_000_000; i++) {
    sum3 += stats[i];
}
```

메인 프로그램은 네 개의 스레드를 완성하고 자바의 `.start()`로 실행한 다음 `.join()`으로 완료될 때까지 기다렸다가 다음을 계산한다.

```java
sum = sum0 + ... + sum3;
```

이를 각 루프로 처리하는 것은 성가시며 쉽게 에러가 발생할 수 있는 구조다. 루프가 아닌 코드라면 어떻게 처리할지도 난감해진다.

이전에, 자바 스트림으로 외부 반복(명시적 루프) 대신 내부 반복을 통해 얼마나 쉽게 병렬성을 달성할 수 있는지 설명했다.
```java
sum = Arrays.stream(stats).parallel().sum();
```

결론적으로 병렬 스트림 반복은 명시적으로 스레드를 사용하는 것에 비해 높은 수준의 개념이라는 사실을 알 수 있다.
다시 말해 스트림을 이용해 스레드 사용 패턴을 `추상화`할 수 있다.
스트림으로 추상화하는 것은 디자인 패턴을 적용하는 것과 비슷하지만 대신 쓸모 없는 코드가 라이브러리 내부로 구현되면서 복잡성도 줄어든다는 장점이 더해진다.

추가적인 스레드 추상화를 살펴보기에 앞서 추상화의 기반 개념에 해당하는 자바 5의 `ExecutorService` 개념과 스레드 풀을 살펴보자.

### 15.1.2 Executor와 스레드 풀
자바 5는 `Executor` 프레임워크와 스레드 풀을 통해 스레드의 힘을 높은 수준으로 끌어올리는 즉 자바 프로그래머가 태스크 제출과 실행을 분리할 수 있는 기능을 제공한다.

#### 스레드의 문제
자바 스레드는 직접 운영체제 스레드에 접근한다. 운영체제 스레드를 만들고 종료하려면 비싼 비용(페이지 테이블과 관련한 상호작용)을 치러야 하며
더욱이 운영체제 스레드의 숫자는 제한되어 있는 것이 문제다. 운영체제가 지원하는 스레드 수를 초과해 사용하면 자바 애플리케이션이 예상치 못한 방식으로
크래시될 수 있으므로 기존 스레드가 실행되는 상태에서 계속 새로운 스레드를 만드는 상황이 일어나지 않도록 주의해야 한다.

보통 운영체제와 자바의 스레드 개수가 하드웨어 스레드 개수보다 많으므로 일부 운영체제 스레드가 블록되거나 자고 있는 상황에서 모든 하드웨어 스레드가
코드를 실행하도록 할당된 상황에 놓일 수 있다. 다양한 기기에서 실행할 수 있는 프로그램에서는 미리 하드웨어 스레드 개수를 추측하지 않는 것이 좋다.
한편 주어진 프로그램에서 사용할 최적의 자바 스레드 개수는 사용할 수 있는 하드웨어 코어의 개수에 따라 달라진다.

#### 스레드 풀 그리고 스레드 풀이 더 좋은 이유
자바 `ExecutorService`는 태스크를 제출하고 나중에 결과를 수집할 수 있는 인터페이스를 제공한다.
프로그램은 `newFixedThreadPool` 같은 팩토리 메서드 중 하나를 이용해 스레드 풀을 만들어 사용할 수 있다.

```java
ExecutorService newFixedThreadPool(int nThreads)
```

이 메서드는 워커 스레드라 불리는 `nThreads`를 포함하는 `ExecutorService`를 만들고 이들을 스레드 풀에 저장한다.
스레드 풀에서 사용하지 않은 스레드로 제출된 태스크를 먼저 온 순서대로 실행한다. 이들 태스크 실행이 종료되면 이들 스레드를 풀로 반환한다.
이 방식의 장점은 하드웨어에 맞는 수의 태스크를 유지함과 동시에 수 천개의 태스크를 스레드 풀에 아무 오버헤드 없이 제출할 수 있다는 점이다.
큐의 크기 조정, 거부 정책, 태스크 종류에 따른 우선순위 등 다양한 설정을 할 수 있다.

프로그래머가 `태스크(Runnable이나 Callable)`를 제공하면 `스레드`가 이를 실행한다.

#### 스레드 풀 그리고 스레드 풀이 나쁜 이유
거의 모든 관점에서 스레드를 직접 사용하는 것보다 스레드 풀을 이용하는 것이 바람직하지만 두 가지 "사항"을 주의해야 한다.

- k 스레드를 가진 스레드 풀은 오직 k만큼의 스레드를 동시에 실행할 수 있다. 초과로 제출된 태스크는 큐에 저장되며 이전에 태스크 중 하나가 종료되기
전까지는 스레드에 할당하지 않는다. 불필요하게 많은 스레드를 만드는 일을 피할 수 있으므로 보통 이 상황은 아무 문제가 되지 않지만
잠을 자거나 I/O를 기다리거나 네트워크 연결을 기다리는 태스크가 있다면 주의해야 한다. I/O를 기다리는 블록 상황에서 이들 태스크가 워커 스레드에
할당된 상태를 유지하지만 아무 작업도 하지 않게 된다. [그림 15-3]에서 보여주는 것처럼 네 개의 하드웨어 스레드와 5개의 스레드를 갖는 스레드 풀에 20개의 태스크를
제출했다고 가정하자. 모든 태스크가 병렬로 실행되면서 20개의 태스크를 실행할 것이라 생각할 수 있다. 하지만 처음 제출한 세 스레드가 잠을 자거나 I/O를 기다린다고 가정하자.
그러면 나머지 15개의 태스크를 두 스레드가 실행해야 하므로 작업 효율성이 예상보다 절반으로 떨어진다. 처음 제출한 태스크나 기존 실행 중인 태스크가
나중의 태스크 제출을 기다리는 상황(`Future`의 일반적인 패턴)이라면 데드락에 걸릴 수도 있다. 핵심은 블록(자거나 이벤트를 기다리는)할 수 있는 태스크는
스레드 풀에 제출하지 말아야 한다는 것이지만 항상 이를 지킬 수 있는 것은 아니다.
- 중요한 코드를 실행하는 스레드가 죽는 일이 발생하지 않도록 보통 자바 프로그램은 `main`이 반환하기 전에 모든 스레드의 작업이 끝나길 기다린다.
따라서 프로그램을 종료하기 전에 모든 스레드 풀을 종료하는 습관을 갖는 것이 중요하다(풀의 워커 스레드가 만들어진 다음 다른 태스크 제출을 기다리면서
종료되지 않은 상태일 수 있으므로). 보통 장기간 실행하는 인터넷 서비스를 관리하도록 오래 실행되는 `ExecutorService`를 갖는 것은 흔한 일이다.
자바는 이런 상황을 다룰 수 있도록 `Thread.setDaemon` 메서드를 제공한다.

##### 그림 15-3 자는 태스크는 스레드 풀의 성능을 저하시킨다.
![](https://images.velog.io/images/banjjoknim/post/324da15c-ede3-405a-bd0c-d7746fa1b1ae/15-3.%20%EC%9E%90%EB%8A%94%20%ED%83%9C%EC%8A%A4%ED%81%AC%EB%8A%94%20%EC%8A%A4%EB%A0%88%EB%93%9C%20%ED%92%80%EC%9D%98%20%EC%84%B1%EB%8A%A5%EC%9D%84%20%EC%A0%80%ED%95%98%EC%8B%9C%ED%82%A8%EB%8B%A4..png)

### 15.1.3 스레드의 다른 추상화 : 중첩되지 않은 메서드 호출
7장(`병렬 스트림 처리와 포크/조인 프레임워크`)에서 설명한 동시성과 지금 설명하는 동시성이 어떻게 다른지 명확하게 알 수 있도록 7장에서 사용한 동시성에서는
한 개의 특별한 속성 즉, 태스크나 스레드가 메서드 호출 안에서 시작되면 그 메서드 호출은 반환하지 않고 작업이 끝나기를 기다렸다. 다시 말해 스레드 생성과
`join()`이 한 쌍처럼 중첩된 메서드 호출 내에 추가되었다. [그림 15-4]에서 보여주는 것처럼 이를 `엄격한 포크/조인`이라 부른다.

시작된 태스크를 내부 호출이 아니라 외부 호출에서 종료하도록 기다리는 좀 더 여유로운 방식의 `포크/조인`을 사용해도 비교적 안전하다. 그러면 [그림 15-5]에서
보여주는 것처럼 제공된 인터페이스를 사용자는 일반 호출로 간주할 수 있다.

##### 그림 15-4 엄격한 포크/조인. 화살표는 스레드, 원은 포크와 조인을, 사각형은 메서드 호출과 반환을 의미한다.
![](https://images.velog.io/images/banjjoknim/post/940a7b7a-16f7-4271-aa85-f902a68f8b33/15-4.%20%EC%97%84%EA%B2%A9%ED%95%9C%20%ED%8F%AC%ED%81%AC-%EC%A1%B0%EC%9D%B8.%20%ED%99%94%EC%82%B4%ED%91%9C%EB%8A%94%20%EC%8A%A4%EB%A0%88%EB%93%9C,%20%EC%9B%90%EC%9D%80%20%ED%8F%AC%ED%81%AC%EC%99%80%20%EC%A1%B0%EC%9D%B8%EC%9D%84,%20%EC%82%AC%EA%B0%81%ED%98%95%EC%9D%80%20%EB%A9%94%EC%84%9C%EB%93%9C%20%ED%98%B8%EC%B6%9C%EA%B3%BC%20%EB%B0%98%ED%99%98%EC%9D%84%20%EC%9D%98%EB%AF%B8%ED%95%9C%EB%8B%A4..png)

##### 그림 15-5 여유로운 포크/조인
![](https://images.velog.io/images/banjjoknim/post/7ad2ba59-dcbd-4c8c-94e2-3c9e0c61ad2d/15-5.%20%EC%97%AC%EC%9C%A0%EB%A1%9C%EC%9A%B4%20%ED%8F%AC%ED%81%AC-%EC%A1%B0%EC%9D%B8.png)

여기서는 [그림 15-6]처럼 사용자의 메서드 호출에 의해 스레드가 생성되고 메서드를 벗어나 계속 실행되는 동시성 형태에 초점을 둔다.

##### 그림 15-6 비동기 메서드
![](https://images.velog.io/images/banjjoknim/post/575445e1-76e0-44a7-ad54-1f7b71c1e92f/15-6.%20%EB%B9%84%EB%8F%99%EA%B8%B0%20%EB%A9%94%EC%84%9C%EB%93%9C.png)

이런 종류, 특히 메서드 호출자에 기능을 제공하도록 메서드가 반환된 후에도 만들어진 태스크 실행이 계속되는 메서드를 비동기 메서드라 한다.
이러한 메서드를 사용할 때 다음과 같은 위험성이 따를 수 있다.
- 스레드 실행은 메서드를 호출한 다음의 코드와 동시에 실행되므로 데이터 경쟁 문제를 일으키지 않도록 주의해야 한다.
- 기존 실행 중이던 스레드가 종료되지 않은 상황에서 자바의 `main()` 메서드가 반환하면 어떻게 될까? 다음과 같은 두 가지 방법이 있는데 어느 방법도 안전하지 못하다.
  - **애플리케이션을 종료하지 못하고 모든 스레드가 실행을 끝낼 떄까지 기다린다.**
  - **애플리케이션 종료를 방해하는 스레드를 강제종료시키고 애플리케이션을 종료한다.**
  
첫 번째 방법에서는 잊고서 종료를 못한 스레드에 의해 애플리케이션이 크래시될 수 있다. 또 다른 문제로 디스크에 쓰기 I/O 작업을 시도하는
일련의 작업을 중단했을 때 이로 인해 외부 데이터의 일관성이 파괴될 수 있다.
이들 문제를 피하려면 애플리케이션에서 만든 모든 스레드를 추적하고 애플리케이션을 종료하기 전에 스레드 풀을 포함한 모든 스레드를 종료하는 것이 좋다.
자바 스레드는 `setDaemon()` 메서드를 이용해 **데몬** 또는 비데몬으로 구분시킬 수 있다. 
데몬 스레드는 애플리케이션이 종료될 때 강제 종료되므로 디스크의 데이터 일관성을 파괴하지 않는 동작을 수행할 때 유용하게 활용할 수 있는 반면,
`main()` 메서드는 모든 비데몬 스레드가 종료될 때까지 프로그램을 종료하지 않고 기다린다.

### 15.1.4 스레드에 무엇을 바라는가?
일반적으로 모든 하드웨어 스레드를 활용해 병렬성의 장점을 극대화하도록 프로그램 구조를 만드는 것 즉, 프로그램을 작은 태스크 단위로 구조화하는 것이
목표다(하지만 태스크 변환 비용을 고려해 너무 작은 크기는 아니어야 한다). 7장에서는 병렬 스트림 처리와 포크/조인을 `for` 루프와 분할 그리고 정복 알고리즘을
처리하는 방법을 살펴봤는데 이 장의 나머지 부분과 16, 17장에서는 스레드를 조작하는 복잡한 코드를 구현하지 않고 메서드를 호출하는 방법을 살펴본다.

---

## 15.2 동기 API와 비동기 API
7장에서는 자바 8 스트림을 이용해 명시적으로 병렬 하드웨어를 이용할 수 있음을 설명했다. 두 가지 단계로 병렬성을 이용할 수 있다.
첫 번째로 외부 반복(명시적 for 루프)을 내부 반복(스트림 메서드 사용)으로 바꿔야 한다. 그리고 스트림에 `parallel()` 메서드를 이용하므로
자바 런타임 라이브러리가 복잡한 스레드 작업을 하지 않고 병렬로 요소가 처리되도록 할 수 있다.
루프가 실행될 때 추측에 의존해야 하는 프로그래머와 달리 런타임 시스템은 사용할 수 있는 스레드를 더 정확하게 알고 있다는 것도 내부 반복의 장점이다.

루프 기반의 계산을 제외한 다른 상황에서도 병렬성이 유용할 수 있다. 이후에 살펴볼 중요한 자바 개발의 배경에는 비동기 API가 있다.

다음과 같은 시그니처를 갖는 `f`, `g` 두 메서드의 호출을 합하는 예제를 살펴보자.

```java
int f(int x);
int g(int x);
```

참고로 이들 메서드는 물리적 결과를 반환하므로 `동기 API`라고 부른다. 다음처럼 두 메서드를 호출하고 합계를 출력하는 코드가 있다.

```java
int y = f(x);
int z = g(x);
System.out.println(y + z);
```

`f`와 `g`를 실행하는데 오랜 시간이 걸린다고 가정하자. `f`, `g`의 작업을 컴파일러가 완전하게 이해하기 어려우므로 보통 자바 컴파일러는 코드 최적화와
관련한 아무 작업도 수행하지 않을 수 있다. `f`와 `g`가 서로 상호작용하지 않는다는 사실을 알고 있거나 상호작용을 전혀 신경쓰지 않는다면 `f`와 `g`를 별도의
CPU 코어로 실행함으로 `f`와 `g`중 오래 걸리는 작업의 시간으로 합계 구하는 시간을 단축할 수 있다. 별도의 스레드로 `f`와 `g`를 실행해 이를 구현할 수 있다.
의도는 좋지만 이전의 단순했던 코드가 다음처럼 복잡하게 변한다.

```java
class ThreadExample {
    public static void main(String[] args) throws InterruptedException {
        int x = 1337;
        Result result = new Result();
        
        Thread t1 = new Thread(() -> {result.left = f(x);});
        Thread t2 = new Thread(() -> {result.right = g(x);});
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        System.out.println(result.left + result.right);
    }
    
    private static class Result {
        private int left;
        private int right;
    }
}
```

`Runnable` 대신 `Future` API 인터페이스를 이용해 코드를 더 단순화할 수 있다. 이미 `ExecutorService`로 스레드 풀을 설정했다고 가정하면
다음처럼 코드를 구현할 수 있다.

```java
public class ExecutorServiceExample {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        int x = 1337;
        
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        Future<Integer> y = executorService.submit(() -> f(x));
        Future<Integer> z = executorService.submit(() -> g(x));
        System.out.println(y.get() + z.get());

        executorService.shutdown();
    }
}
```

여전히 이 코드도 명시적인 `submit` 메서드 호출 같은 불필요한 코드로 오염되었다. 명시적 반복으로 병렬화를 수행하던 코드를 스트림을 이용해 내부 반복으로
바꾼 것처럼 비슷한 방법으로 이 문제를 해결해야 한다.

문제의 해결은 `비동기 API`라는 기능으로 API를 바꿔서 해결할 수 있다.

첫 번째 방법인 자바의 `Future`를 이용하면 이 문제를 조금 개선할 수 있다. 자바 5에서 소개된 `Future`는 자바 8의 `CompletableFuture`로 이들을
조합할 수 있게 되면서 더욱 기능이 풍부해졌다. 두 번째 방법은 발행-구독 프로토콜에 기반한 자바 9의 `java.util.concurrent.Flow` 인터페이스를
이용하는 방법이며, 이후에 소개하도록 한다.

이런 대안들을 이 문제에 적용하면 `f`, `g`의 시그니처가 어떻게 바뀔까?

### 15.2.1 Future 형식 API
대안을 이용하면 `f`, `g`의 시그니처가 다음처럼 바뀐다.

```java
Future<Integer> f(int x);
Future<Integer> g(int x);
```

그리고 다음처럼 호출이 바뀐다.

```java
Future<Integer> y = f(x);
Future<Integer> z = g(x);
System.out.println(y.get() + z.get());
```

메서드 `f`는 호출 즉시 자신의 원래 바디를 평가하는 태스크를 포함하는 `Future`를 반환한다. 마찬가지로 메서드 `g`도 `Future`를 반환하며 세 번째 코드는
`get()` 메서드를 이용해 두 `Future`가 완료되어 결과가 합쳐지기를 기다린다.

예제에서는 API는 그대로 유지하고 `g`를 그대로 호출하면서 `f`에만 `Future`를 적용할 수 있었다. 하지만 조금 더 큰 프로그램에서는
두 가지 이유로 이런 방식을 사용하지 않는다.
- 다른 상황에서는 `g`에도 `Future` 형식이 필요할 수 있으므로 API 형식을 통일하는 것이 바람직하다.
- 병렬 하드웨어로 프로그램 실행 속도를 극대화하려면 여러 개의 작지만 합리적인 크기의 태스크로 나누는 것이 좋다.

### 15.2.2 리액티브 형식 API
두 번째 대안에서 핵심은 `f`, `g`의 시그니처를 바꿔서 콜백 형식의 프로그래밍을 이용하는 것이다.

```java
void f(int x, IntConsumer dealWithResult);
```

처음에는 두 번째 대안이 이상해 보일 수 있다. `f`가 값을 반환하지 않는데 어떻게 프로그램이 동작할까? `f`에 추가 인수로 콜백(람다)을 전달해서
`f`의 바디에서는 `return` 문으로 결과를 반환하는 것이 아니라 결과가 준비되면 이를 람다로 호출하는 태스크를 만드는 것이 비결이다.
`f`는 바디를 실행하면서 태스크를 만든 다음 즉시 반환하므로 코드 형식이 다음처럼 바뀐다.

```java
public class CallbackStyleExample {
    public static void main(String[] args) {
        int x = 1337;
        Result result = new Result();

        f(x, (int y) -> {
            result.left = y;
            System.out.println((result.left + result.right));
        });
        
        g(x, (int z) -> {
            result.right = z;
            System.out.println((result.left + result.right));
        });
    }
}
```

하지만 결과가 달라졌다. `f`와 `g`의 호출 합계를 정확하게 출력하지 않고 상황에 따라 먼저 계산된 결과를 출력한다.
락을 사용하지 않으므로 값을 두 번 출력할 수 있을 뿐더러 때로는 +에 제공된 두 피연산자가 `println`이 호출되기 전에 업데이트될 수도 있다.
다음처럼 두 가지 방법으로 이 문제를 보완할 수 있다.

- `if-then-else`를 이용해 적절한 락을 이용해 두 콜백이 모두 호출되었는지 확인한 다음 `println`을 호출해 원하는 기능을 수행할 수 있다.
- 리액티브 형식의 API는 보통 한 결과가 아니라 일련의 이벤트에 반응하도록 설계되었으므로 `Future`를 이용하는 것이 더 적절하다.

리액티브 형식의 프로그래밍으로 메서드 `f`와 `g`는 `dealWithResult` 콜백을 여러 번 호출할 수 있다. 원래의 `f`, `g` 함수는 오직 한 번만 
`return`을 사용하도록 되어있다. 마찬가지로 `Future`도 한 번만 완료되며 그 결과는 `get()`으로 얻을 수 있다. 
리액티브 형식의 비동기 API는 자연스럽게 일련의 값(나중에 스트림으로 연결)을, `Future` 형식의 API는 일회성의 값을 처리하는 데 적합니다.

두 대안 모두 코드를 복잡하게 만든다고 생각할 것이다. 어느 정도는 맞는 말이다. 어떤 API를 사용할 것인지 미리 잘 생각해야 한다. 하지만 API는
명시적으로 스레드를 처리하는 코드에 비해 사용 코드를 더 단순하게 만들어주며 높은 수준의 구조를 유지할 수 있게 도와준다.
또한 (a) 계산이 오래 걸리는 메서드(수 밀리초 이상), (b) 네트워크나 사람의 입력을 기다리는 메서드에 이들 API를 잘 활용하면 애플리케이션의
효율성이 크게 향상된다. (b)의 상황에서는 리소스를 낭비하지 않고 효율적으로 하단의 시스템을 활용할 수 있다는 장점을 추가로 제공한다.

### 15.2.3 잠자기(그리고 기타 블로킹 동작)는 해로운 것으로 간주
사람과 상호작용하거나 어떤 일이 일정 속도로 제한되어 일어나는 상황의 애플리케이션을 만들 때 자연스럽게 `sleep()` 메서드를 사용할 수 있다.
하지만 스레드는 잠들어도 여전히 시스템 자원을 점유한다. 스레드를 단지 몇 개 사용하는 상황에서는 큰 문제가 아니지만 스레드가 많아지고
그 중 대부분이 잠을 잔다면 문제가 심각해진다.

스레드 풀에서 잠을 자는 태스크는 다른 태스크가 시작되지 못하게 막으므로 자원을 소비한다는 사실을 기억하자(운영 체제가 이들 태스크를 관리하므로
일단 스레드로 할당된 태스크는 중지시키지 못한다.)

물론 스레드 풀에서 잠자는 스레드만 실행을 막는것은 아니다. 모든 블록 동작도 마찬가지다. 블록 동작은 다른 태스크가 어떤 동작을 완료하기를
기다리는 동작(예를 들어, `Future`에 `get()` 호출)과 외부 상호작용(예를 들어, 네트워크, 데이터베이스 서버에서 읽기 작업을 기다리거나, 키보드 입력 같은
사람의 상호작용을 기다림)을 기다리는 동작 두 가지로 구분할 수 있다.

이상적으로는 절대 태스크에서 기다리는 일을 만들지 말거나 아니면 코드에서 예외를 일으키는 방법으로 이를 처리할 수 있따. 태스크를 앞과 뒤 부분으로 나누고
블록되지 않을 떄만 뒷부분을 자바가 스케줄링하도록 요청할 수 있다.

다음은 한 개의 작업을 갖는 코드 A다.

```java
work1();
Thread.sleep(10000); // 10초 동안 잠
work2();
```

이를 코드 B와 비교하자.

```java
public class ScheduleExecutorServiceExample {
    public static void main(String[] args) {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        
        work1();
        scheduledExecutorService.schedule(ScheduledExecutorService::work2, 10, TimeUnit.SECONDS); // work1()이 끝난 다음 10초 뒤에 work2()를 개별 태스크로 스케줄함
        
        scheduledExecutorService.shutdown();
    }

    public static void work1() {
        System.out.println("Hello from Work1!");
    }

    public static void work2() {
        System.out.println("Hello from Work2!");
    }
}
```

두 태스크 모두 스레드 풀에서 실행된다고 가정하자.

코드 A가 어떻게 실행되는지 살펴보자. 먼저 코드는 스레드 풀 큐에 추가되며 나중에 차례가 되면 실행된다. 하지만 코드가 실행되면 워커 스레드를
점유한 상태에서 아무것도 하지 않고 10초를 잔다. 그리고 깨어나서 `work2()`를 실행한 다음 작업을 종료하고 워커 스레드를 해제한다.
반면에 코드 B는 `work1()`을 실행하고 종료한다. 하지만 `work2()`가 10초 뒤에 실행될 수 있도록 큐에 추가한다.

코드 B가 더 좋은 이유는 뭘까? 코드 A나 B 모두 같은 동작을 수행한다. 두 코드의 다른 점은 A가 자는 동안 귀중한 스레드 자원을 점유하는 반면
B는 다른 작업이 실행될 수 있도록 허용한다는 점이다(스레드를 사용할 필요가 없이 메모리만 조금 더 사용했다).

태스크를 만들 떄는 이런 특징을 잘 활용해야 한다. 태스크가 실행되면 귀중한 자원을 점유하므로 태스크가 끝나서 자원을 해제하기 전까지 태스크를
계속 실행해야 한다. 태스크를 블록하는 것보다는 다음 작업을 태스크로 제출하고 현재 태스크는 종료하는 것이 바람직하다.

가능하다면 I/O 작업에도 이 원칙을 적용하는 것이 좋다. 고전적으로 읽기 작업을 기다리는 것이 아니라 블록하지 않는 '읽기 시작' 메서드를 호출하고
읽기 작업이 끝나면 이를 처리할 다음 태스크를 런타임 라이브러리에 스케줄하도록 요청하고 종료한다.

이런 디자인 패턴을 따르려면 읽기 어려운 코드가 많아지는 것처럼 보일 수 있다. 하지만 자바 `CompletableFuture` 인터페이스는 이전에 살펴본 `Future`에
`get()`을 이용해 명시적으로 블록하지 않고 콤비네이터를 사용함으로 이런 형식의 코드를 런탕미 라이브러리 내에 추상화한다.

마지막으로 스레드의 제한이 없고 저렴하다면 코드 A와 B는 사실상 같다. 하지만 스레드에는 제한이 있고 저렴하지 않으므로 잠을 자거나 블록해야 하는
여러 태스크가 있을 때 가능하면 코드 B 형식을 따르는 것이 좋다.

### 15.2.4 현실성 확인
새로운 시스템을 설계할 때 시스템을 많은 작은 동시 실행되는 태스크로 설계해서, 블록할 수 있는 모든 동작을 비동기 호출로 구현한다면 병렬 하드웨어를
최대한 활용할 수 있다. 하지만 현실적으로는 '모든 것은 비동기'라는 설계 원칙을 어겨야 한다('최상은 좋은 것의 적이다'라는 속담을 기억하자).
자바는 2002년 자바 1.4에서부터 비블록 10 기능(`java.nio`)을 제공했는데 이들은 조금 복잡하고 잘 알려지지 않았따. 실제로 자바의 개선된
동시성 API를 이용해 유익을 얻을 수 있는 상황을 찾아보고 모든 API를 비동기로 만드는 것을 따지지 말고 개선된 동시성 API를 사용해보길 권장한다.

네트워크 서버의 블록/비블록 API를 일관적으로 제공하는 `Netty(https://netty.io/)` 같은 새로운 라이브러리를 사용하는것도 도움이 된다.

### 15.2.5 비동기 API에서 예외는 어떻게 처리되는가?
`Future`나 리액티브 형식의 비동기 API에서 호출된 메서드의 실제 바디는 별도의 스레드에서 호출되며 이때 발생하는 어떤 에러는 이미 호출자의
실행 범위와는 관계가 없는 상황이 된다. 예상치못한 일이 일어나면 예외를 발생시켜 다른 동작이 실행되어야 한다. 어떻게 이를 실현할 수 있을까?
`Future`를 구현한 `CompletableFuture`에서는 런타임 `get()` 메서드에 예외를 처리할 수 있는 기능을 제공하며 예외에서 회복할 수 있도록
`exceptionally()` 같은 메서드도 제공한다.

리액티브 형식의 비동기 API에서는 `return` 대신 기존 콜백이 호출되므로 예외가 발생했을 때 실행될 추가 콜백을 만들어 인터페이스를 바꿔야 한다.
다음 예제처럼 리액티브 API에 여러 콜백을 포함해야 한다.

```java
void f(int x, Consumer<Integer> dealWithResult, Consumer<Throwable> dealWithException);
```

`f`의 바디는 다음을 수행할 수 있다.

```java
dealWithException(e);
```

콜백이 여러 개면 이를 따로 제공하는 것보다는 한 객체로 이 메서드를 감싸는 것이 좋다. 예를 들어 `자바 9 플로 API`에서는 여러 콜백을
한 객체(네 개의 콜백을 각각 대표하는 네 메서드를 포함하는 `Subcriber<T>` 클래스)로 감싼다. 다음은 그 예제다.

```java
void onComplete()
void onError(Throwable throwable)
void onNext(T item)
```

값이 있을 때(`onNext`), 도중에 에러가 발생했을 때(`onError`), 값을 다 소진했거나 에러가 발생해서 더 이상 처리할 데이터가 없을 때(`onComplete`) 각각의 콜백이 호출된다.
이전의 `f`에 이를 적용하면 다음과 같이 시그니처가 바뀐다.

```java
void f(int x, Subscriber<Integer> s);
```

`f`의 바디는 다음처럼 `Throwable`을 가리키는 `t`로 예외가 일어났음을 가리킨다.

```java
s.onError(t);
```

여러 콜백을 포함하는 API를 파일이나 키보드 장치에서 숫자를 읽는 작업과 비교해보자. 이들 장치가 수동적인 데이터 구조체가 아니라 "여기 번호 나왔어요"나
"숫자가 아니라 잘못된 형식의 아이템이 나왔어요" 같은 일련의 데이터를 만들어낸 다음 마지막으로 "더 이상 처리할 데이터가 없어요(파일의 끝)" 알림을 만든다.

보통 이런 종류의 호출을 메시지 또는 **이벤트**라 부른다. 예를 들어 파일 리더가 3, 7, 42를 읽은 다음 잘못된 형식의 숫자 이벤트를 내보내고
이어서 2, 파일의 끝 이벤트를 차례로 생성했다고 가정하자.
이런 이벤트를 API의 일부로 보자면 API는 이벤트의 순서(**채널 프로토콜**이라 불리는)에는 전혀 개의치 않는다. 실제 부속 문서에서는 "`onComplete` 이벤트 다음에는
아무 이벤트도 일어나지 않음" 같은 구문을 사용해 프로토콜을 정의한다.

---

## 15.3 박스와 채널 모델
동시성 모델을 가장 잘 설계하고 개념화하려면 그림이 필요하다. 우리는 이 기법을 **박스와 채널 모델**(box-and-channel model)이라고 부른다.
이전 예제인 `f(x) + g(x)`의 계산을 일반화해서 정수와 관련된 간단한 상황이 있다고 가정하자. `f`나 `g`를 호출하거나 `p` 함수에 인수 `x`를 이용해 호출하고
그 결과를 `q1`과 `q2`에 전달하며 다시 이 두 호출의 결과로 함수 `r`을 호출한 다음 결과를 출력한다. 편의상 클래스 `C`의 메서드와 연상 함수 `C::m`을 구분하지 않는다.
[그림 15-7]에서 보여주는 것처럼 간단한 태스크를 그림으로 표현할 수 있다.

##### 그림 15-7 간단한 박스와 채널 다이어그램
![](https://images.velog.io/images/banjjoknim/post/c8559634-168d-4607-b300-3699edfd7294/15-7.%20%EA%B0%84%EB%8B%A8%ED%95%9C%20%EB%B0%95%EC%8A%A4%EC%99%80%20%EC%B1%84%EB%84%90%20%EB%8B%A4%EC%9D%B4%EC%96%B4%EA%B7%B8%EB%9E%A8.png)

자바로 [그림 15-7]을 두 가지 방법으로 구현해 어떤 문제가 있는지 확인하자. 다음은 첫 번째 구현 방법이다.

```java
int t = p(x);
System.out.println(r(q1(t), q2(t)));
```

겉보기엔 깔끔해 보이는 코드지만 자바가 `q1`,`q2`를 차례로 호출하는데 이는 하드웨어 병렬성의 활용과 거리가 멀다.
`Future`를 이용해 `f`, `g`를 병렬로 평가하는 방법도 있다.

```java
int t = p(x);
Future<Integer> a1 = executorService.submit(() -> q1(t));
Future<Integer> a2 = executorService.submit(() -> q2(t));
System.out.println(r(a1.get(), a2.get()));
```

이 예제에서는 박스와 채널 다이어그램의 모양상 `p`와 `r`을 `Future`로 감싸지 않았다. `p`는 다른 어떤 작업보다 먼저 처리해야 하며 `r`은
모든 작업이 끝난 다음 가장 마지막으로 처리해야 한다. 아래처럼 코드를 흉내내보지만 이는 우리가 원하는 작업과 거리가 있다.

```java
System.out.println(r(q1(t), q2(t)) + s(x));
```

위 코드에서 병렬성을 극대화하려면 모든 다섯 함수(`p`, `q1`, `q2`, `r`, `s`)를 `Future`로 감싸야 하기 때문이다.

시스템에서 많은 작업이 동시에 실행되고 있지 않다면 이 방법도 잘 동작할 수 있다. 하지만 시스템이 커지고 각각의 많은 박스와 채널 다이어그램이
등장하고 각각의 박스는 내부적으로 자신만의 박스와 채널을 사용한다면 문제가 달라진다. 이런 상황에서는 앞서 설명한 것처럼 많은 태스크가 `get()`
메서드를 호출해 `Future`가 끝나기를 기다리는 상태에 놓일 수 있다. 결과적으로 하드웨어의 병렬성을 제대로 활용하지 못하거나 심지어 데드락에 걸릴 수 있다.
또한 이런 대규모 시스템 구조가 얼마나 많은 수의 `get()`을 감당할 수 있는지 이해하기 어렵다. 자바 8에서는 다음에 설명할 `CompletableFuture`와 **콤비네이터**를 이용해 문제를 해결한다.
두 `Function`이 있을 때 `compose()`, `andThen()` 등을 이용해 다른 `Function`을 얻을 수 있다는 사실을 확인했다(3장 참고).
`add1`은 정수 1을 더하고 `dble`은 정수를 두 배로 만든다고 가정하면 인수를 두 배로 만들고 결과에 2를 더하는 `Function`을 다음처럼 구현할 수 있다.

```java
Function<Integer, Integer> myfun = add1.andThen(dble);
```

하지만 박스와 채널 다이어그램은 콤비네이터로도 직접 멋지게 코딩할 수 있다. [그림 15-7]을 자바 `Function p, q1, q2, Bifunction r`로 간단하게 구현할 수 있다.

```java
p.thenBoth(q1, q2).thenCombine(r)
```

안타깝게도 `thenBoth`나 `thenCombine`은 자바 `Function`과 `BiFunction` 클래스의 일부가 아니다.

다음(15.4절)으로는 콤비네이터와 `CompletableFuture`의 개념이 얼마나 비슷하며 `get()`을 이용해 태스크가 기다리게 만드는 일을 피할 수 있는지 설명한다.

박스와 채널 모델을 이용해 생각과 코드를 구조화할 수 있으며, 박스와 채널 모델로 대규모 시스템 구현의 추상화 수준을 높일 수 있다.
박스(또는 프로그램의 콤비네이터)로 원하는 연산을 표현(계산은 나중에 이루어짐)하면 계산을 손으로 코딩한 결과보다 더 효율적일 것이다.
콤비네이터는 수학적 함수뿐 아니라 `Future`와 `리액티브 스트림 데이터`에도 적용할 수 있다. 15.5절에서는 박스와 채널 다이어그램의 각 채널을
마블 다이어그램(메시지를 가리키는 여러 마블을 포함)으로 표현하는 방법을 설명한다. 박스와 채널 모델은 병렬성을 직접 프로그래밍하는 관점을
콤비네이터를 이용해 내부적으로 작업을 처리하는 관점으로 바꿔준다. 마찬가지로 자바 8 스트림은 자료 구조를 반복해야 하는 코드를 내부적으로
작업을 처리하는 스트림 콤비네이터로 바꿔준다. 

---

## 15.4 CompletableFuture와 콤비네이터를 이용한 동시성
동시 코딩 작업을 `Future` 인터페이스로 생각하도록 유도한다는 점이 `Future` 인터페이스의 문제다. 하지만 역사적으로 주어진 연산으로 `Future`를 만들고,
이를 실행하고, 종료되길 기다리는 등 `Future`는 `FutureTask` 구현을 뛰어 넘는 몇 가지 동작을 제공했다.
이후 버전의 자바에서는 7장에서 설명한 `RecursiveTask` 같은 더 구조화된 지원을 제공했다.

자바 8에서는 `Future` 인터페이스의 구현인 `CompletableFuture`를 이용해 `Future`를 조합할 수 있는 기능을 추가했다. 그럼 `ComposableFuture`가 아니라
`CompletableFuture`라고 부르는 이유는 뭘까? 일반적으로 `Future`는 실행해서 `get()`으로 결과를 얻을 수 있는 `Callable`로 만들어진다.
하지만 `CompletableFuture`는 실행할 코드 없이 `Future`를 만들 수 있도록 허용하며 `complete()` 메서드를 이용해 나중에 어떤 값을 이용해
다른 스레드가 이를 완료할 수 있고 `get()`으로 값을 얻을 수 있도록 허용한다(그래서 `CompletableFuture`라 부른다).
`f(x)`와 `g(x)`를 동시에 실행해 합계를 구하는 코드를 다음처럼 구현할 수 있다.

```java
public class CFComplete {
    public static void main(String[] args) throws ExecutionException, InterruptedException{
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        int x = 1337;

        CompletableFuture<Integer> a = new CompletableFuture<>();
        executorService.submit(() -> a.complete(f(x)));
        int b = g(x);
        System.out.println(a.get() + b);

        executorService.shutdown();
    }
}
```

또는 다음처럼 구현할 수 있다.

```java
public class CFComplete {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        int x = 1337;

        CompletableFuture<Integer> a = new CompletableFuture<>();
        executorService.submit(() -> b.complete(g(x)));
        int a = f(x);
        System.out.println(a + b.get());
        
        executorService.shutdown();
    }
}
```

위 두 코드는 `f(x)`의 실행이 끝나지 않거나 아니면 `g(x)`의 실행이 끝나지 않는 상황에서 `get()`을 기다려야 하므로 프로세싱 자원을 낭비할 수 있다.
자바 8의 `CompletableFuture`를 이용하면 이 상황을 해결할 수 있다.

>##### 퀴즈 15-1
>위와 같은 상황에서 스레드를 완벽하게 활용할 수 있는 태스크를 어떻게 구현할 수 있을까? `f(x)`, `g(x)`를 실행하는 두 개의 활성 스레드가 있는데
한 스레드는 다른 스레드가 `return` 문을 실행해 종료될 떄까지 기다렸다가 시작한다.
>
>정답은 `f(x)`를 실행하는 한 태스크, `g(x)`를 실행하는 두 번째 태스크, 합계를 계산하는 세 번째 태스크(이전의 두 태스크를 재활용 할 수 있다)
세 개를 이용하는 것이다 .하지만 처음 두 태스크가 실행되기 전까지 세 번쨰 태스크는 실행할 수 없다. 이 문제는 `Future`를 조합해 해결할 수 있다.

`CompletableFuture<T>`에 `thenCombine` 메서드를 사용함으로 두 연산 결과를 더 효과적으로 더할 수 있다(16장에서 자세히 다룰 것이다).
`thenCombine` 메서드는 다음과 같은 시그니처(제네릭과 와일드카드와 관련된 문제를 피할 수 있게 간소화됨)를 갖고 있다.

```java
CompletableFuture<V> thenCombine(CompletableFuture<U> other, BiFunction<T, U, V> fn)
```

이 메서드는 두 개의 `CompletableFuture` 값(T, U 결과 형식)을 받아 한 개의 새 값을 만든다.
처음 두 작업이 끝나면 두 결과 모두에 fn을 적용하고 블록하지 않은 상태로 결과 `Future`를 반환한다. 이전 코드를 다음처럼 구현할 수 있다.

```java
public class CFCombine {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        int x = 1337;

        CompletableFuture<Integer> a = new CompletableFuture<>();
        CompletableFuture<Integer> b = new CompletableFuture<>();
        CompletableFuture<Integer> c = a.thenCombine(b, (y, z) -> y + z);
        executorService.submit(() -> a.complete(f(x)));
        executorService.submit(() -> b.complete(g(x)));

        System.out.println(c.get());
        executorService.shutdown();
    }
}
```

`thenCombine` 행이 핵심이다. `Future a`와 `Future b`의 결과를 알지 못한 상태에서 `thenCombine`은 두 연산이 끝났을 때 스레드 풀에서
실행된 연산을 만든다. 결과를 추가하는 세 번째 연산 c는 다른 두 작업이 끝날 때까지는 스레드에서 실행되지 않는다(먼저 시작해서 블록되지 않는 점이 특징).
따라서 기존의 두 가지 버전의 코드에서 발생했던 블록 문제가 어디서도 일어나지 않는다. `Future`의 연산이 두 번째로 종료되는 상황에서 실제 필요한
스레드는 한 개지만 스레드 풀의 두 스레드가 여전히 활성 상태다. [그림 15-8]은 이 상황을 다이어그램으로 보여준다. 이전의 두 버전에서 `y+z` 연산은
`f(x)` 또는 `g(x)`를 실행(블록될 가능성이 있는)한 같은 스레드에서 수행했다. 반면 `thenCombine`을 이용하면 `f(x)`와 `g(x)`가 끝난 다음에야
덧셈 계산이 실행된다.

##### 그림 15-8 f(x), g(x), 결과 합산 세 가지 연산의 타이밍 다이어그램
![](https://images.velog.io/images/banjjoknim/post/9747cd78-22d4-42f9-9532-8de9146f8823/15-8.%20f(x),%20g(x),%20%EA%B2%B0%EA%B3%BC%20%ED%95%A9%EC%82%B0%20%EC%84%B8%20%EA%B0%80%EC%A7%80%20%EC%97%B0%EC%82%B0%EC%9D%98%20%ED%83%80%EC%9D%B4%EB%B0%8D%20%EB%8B%A4%EC%9D%B4%EC%96%B4%EA%B7%B8%EB%9E%A8.png)

상황에 따라서는 `get()`을 기다리는 스레드가 큰 문제가 되지 않으므로 기존 자바 8의 `Future`를 이용한 방식도 해결 방법이 될 수 있다.
하지만 어떤 상황에서는 많은 수의 `Future`를 사용해야 한다(예를 들어 서비스에 여러 질의를 처리하는 상황). 이런 상황에서는 `CompletableFuture`와
콤비네이터를 이용해 `get()`에서 블록하지 않을 수 있고 그렇게 함으로 병렬 실행의 효율성은 높이고 데드락은 피하는 최상의 해결책을 구현할 수 있다.

---

## 15.5 발생-구독 그리고 리액티브 프로그래밍
`Future`와 `CompletableFuture`은 독립적 실행과 병렬성이라는 정식적 모델에 기반한다. 연산이 끝나면 `get()`으로 `Future`의 결과를 얻을 수 있다.
따라서 `Future`는 **한 번**만 실행해 결과를 제공한다.

반면 리액티브 프로그래밍은 시간이 흐르면서 여러 `Future` 같은 객체를 통해 여러 결과를 제공한다. 먼저 온도계 객체를 예로 생각해보자.
이 객체는 매 초마다 온도 값을 반복적으로 제공한다. 또 다른 예로 웹 서버 컴포넌트 응답을 기다리는 리스너 객체를 생각할 수 있다. 이 객체는
네트워크에 HTTP 요청이 발생하길 기다렸다가 이후에 결과 데이터를 생산한다. 그리고 다른 코드에서 온도 값 또는 네트워크 결과를 처리한다.
그리고 온도계와 리스너 객체는 다음 결과를 처리할 수 있도록 온도 결과나 다른 네트워크 요청을 기다린다.

눈여겨봐야 할 두 가지 사실이 있다. 이 두 예제에서 `Future` 같은 동작이 모두 사용되었지만 한 예제에서는 한 번의 결과가 아니라 여러 번의 결과가 필요하다.
두 번째 예제에서 눈여겨봐야 할 또 다른 점은 모든 결과가 똑같이 중요한 반면 온도계 예제에서는 대부분의 사람에게 가장 최근의 온도만 중요하다.
이런 종류의 프로그래밍을 리액티브라 부르는 이유는 뭘까? 이는 낮은 온도를 감지했을 때 이에 **반응-react-**(예를 들어 히터를 킴)하는 부분이 존재하기 때문이다. 

여기서 스트림을 떠올릴 수 있다. 만약 프로그램이 스트림 모델에 잘 맞는 상황이라면 가장 좋은 구현이 될 수 있다. 하지만 보통 리액티브 프로그래밍
패러다임은 비싼 편이다. 주어진 자바 스트림은 한 번의 단발 동작으로 소비될 수 있다. 15.3절에서 살펴본것처럼 스트림 패러다임은 두 개의 파이프라인으로
값을 분리(포크처럼)하기 어려우며 두 개로 분리된 스트림에서 다시 결과를 합치기도(조인처럼) 어렵다. 스트림은 선형적인 파이프라인 처리 기법에 알맞다.

자바 9에서는 `java.util.concurrent.Flow`의 인터페이스에 `발행-구독 모델(또는 줄여서 pub-sub이라 불리는 프로토콜)`을 적용해 리액티브 프로그래밍을 제공한다.
`자바 9 플로 API`는 17장에서 자세히 살펴보겠지만 여기서는 간단히 다음처럼 세 가지로 플로 API를 정리할 수 있다.

- **구독자**가 구독할 수 있는 **발행자**
- 이 연결을 **구독**(subscription)이라 한다.
- 이 연결을 이용해 **메시지**(또는 **이벤트**로 알려짐)를 전송한다.

[그림 15-9]는 구독을 채널로 발행자와 구독자를 박스로 표현한 그림을 보여준다. 여러 컴포넌트가 한 구독자로 구독할 수 있고 한 컴포넌트는
여러 개별 스트림을 발행할 수 있으며 한 컴포넌트는 여러 구독자에 가입할 수 있다.
15.5.1절에서는 이 개념이 실제 어떻게 동작하는지를 자바 9 플로 인터페이스로 한 단계씩 설명한다.

##### 그림 15-9 발행자-구독자 모델
![](https://images.velog.io/images/banjjoknim/post/60ca16dc-82e2-44c2-a4ca-74e85926b6f9/15-9.%20%EB%B0%9C%ED%96%89%EC%9E%90-%EA%B5%AC%EB%8F%85%EC%9E%90%20%EB%AA%A8%EB%8D%B8.png)

### 15.5.1 두 플로를 합치는 예제
두 정보 소스로부터 발생하는 이벤트를 합쳐서 다른 구독자가 볼 수 있도록 발행하는 예를 통해 `발행-구독`의 특징을 간단하게 확인할 수 있다.
사실 이 기능은 수식을 포함하는 스프레드시트의 셀에서 흔히 제공하는 동작이다. "=C1+C2"라는 공식을 포함하는 스프레드시트 셀 C3을 만들자.
C1이나 C2의 값이 갱신되면(사람에 의해서 또는 각 셀이 포함하는 또 다른 공식에 의해서) C3에도 새로운 값이 반영된다. 다음 코드는 셀의 값을 더할 수만 있다고 가정한다.

먼저 값을 포함하는 셀을 구현한다.

```java
private class SimpleCell {
    private int value = 0;
    private String name;
    
    public SimpleCell(String name) {
        this.name = name;
    }
}
```

아직은 코드가 단순한 편이며 다음처럼 몇 개의 셀을 초기화할 수 있다.

```java
SimpleCell c1 = new SimpleCell("C1");
SimpleCell c2 = new SimpleCell("C2");
```

c1이나 c2의 값이 바뀌었을 때 c3가 두 값을 더하도록 어떻게 지정할 수 있을까? c1과 c2에 이벤트가 발생했을 때 c3를 구독하도록 만들어야 한다.
그러려면 다음과 같은 인터페이스 `Publisher<T>`가 필요하다.

```java
interface Publisher<T> {
    void subscribe(Subscriber<? super T> subscriber);
}
```

이 인터페이스는 통신할 구독자를 인수로 받는다. `Subscriber<T>` 인터페이스는 `onNext`라는 정보를 전달할 단순 메서드를 포함하며 구현자가 필요한대로
이 메서드를 구현할 수 있다.

```java
interface Subcriber<T> {
    void onNext(T t);
}
```

이 두 개념을 어떻게 합칠 수 있을까? 사실 `Cell`은 `Publisher`(셀의 이벤트에 구독할 수 있음)이며 동시에 `Subscriber`(다른 셀의 이벤트에 반응함)임을 알 수 있다.

```java
private class SimpleCell implements Publisher<Integer>, Subscriber<Integer> {
    private int value = 0;
    private String name;
    private List<Subscriber> subscribers = new ArrayList<>();

    public SimpleCell(String name) {
        this.name = name;
    }

    @Override
    public void subscribe(Subscriber<? super Integer> subscriber) {
        subscribers.add(subscriber); 
    }

    private void notifyAllSubscribers() { // 새로운 값이 있음을 모든 구독자에게 알리는 메서드
        subscribers.forEach(subscriber -> subscriber.onNext(this.value));
    }

    @Override
    public void onNext(Integer newValue) {
        this.value = newValue; // 구독한 셀에 새 값이 생겼을 때 값을 갱신해서 반응함
        System.out.println(this.name + ":" + this.value); // 값을 콘솔로 출력하지만 실제로는 UI의 셀을 갱신할 수 있음
        notifyAllSubscribers(); // 값이 갱신되었음을 모든 구독자에게 알림
    }
} 
```

다음 간단한 예제를 시도해보자.

```java
SimpleCell c3 = new SimpleCell("C3");
SimpleCell c2 = new SimpleCell("C2");
SimpleCell c1 = new SimpleCell("C1");

c1.subscribe(c3);

c1.onNext(10); // C1의 값을 10으로 갱신
c2.onNext(20); // C2의 값을 20으로 갱신
```

C3는 직접 C1을 구독하므로 다음과 같은 결과가 출력된다.
>C1:10
>C3:10
>C2:20

'C3=C1+C2'은 어떻게 구현할까? 왼쪽과 오른쪽의 연산 결과를 저장할 수 있는 별도의 클래스가 필요하다.

```java
public class ArithmeticCell extends SimpleCell {
    private int left;
    private int right;

    public ArithmeticCell(String name) {
        super(name);
    }

    public void setLeft(int left) {
        this.left = left;
        onNext(left + this.right); // 셀 값을 갱신하고 모든 구독자에 알림
    }

    public void setRight(int right) {
        this.right = right;
        onNext(right + this.left); // 셀 값을 갱신하고 모든 구독자에 알림
    }

}
```

다음처럼 조금 더 실용적인 예제를 시도할 수도 있다.

```java
ArithmeticCell c3 = new ArithmeticCell("C3");
SimpleCell c2 = new SimpleCell("C2");
SimpleCell c1 = new SimpleCell("C1");

c1.subscribe(c3::setLeft);
c2.subscribe(c3::setRight);

c1.onNext(10); // C1의 값을 10으로 갱신
c2.onNext(20); // C2의 값을 20으로 갱신
c1.onNext(15); // C1의 값을 15으로 갱신
```

다음은 출력 결과다.
>C1:10
>C3:10
>C2:20
>C3:30
>C1:15
>C3:35

결과를 통해 C1의 값이 15로 갱신되었을 때 C3이 즉시 반응해 자신의 값을 갱신한다는 사실을 확인할 수 있다.
발행자-구독자 상호작용의 멋진 점은 발행자 구독자의 그래프를 설정할 수 있다는 점이다. 
예를 들어 "C5=C3+C4"처럼 C3과 C4에 의존하는 새로운 셀 C5를 만들 수 있다.

```java
ArithmeticCell c5 = new ArithmeticCell("C5");
ArithmeticCell c3 = new ArithmeticCell("C3");

SimpleCell c4 = new SimpleCell("C4");
SimpleCell c2 = new SimpleCell("C2");
SimpleCell c1 = new SimpleCell("C1");

c1.subscribe(c3::setLeft);
c2.subscribe(c3::setRight);

c3.subscribe(c5::setLeft);
c4.subscribe(c5::setRight);
```

이제 스프레드시트에 다음과 같은 다양한 갱신 작업을 수행할 수 있다.

```java
c1.onNext(10); // C1의 값을 10으로 갱신
c2.onNext(20); // C2의 값을 20으로 갱신
c1.onNext(15); // C2의 값을 20으로 갱신
c4.onNext(1); // C2의 값을 20으로 갱신
c4.onNext(3); // C2의 값을 20으로 갱신
```

위 동작을 수행하면 다음과 같은 결과가 출력된다.
>C1:10
>C3:10
>C5:10
>C2:20
>C3:30
>C5:30
>C1:15
>C3:35
>C5:35
>C4:1
>C5:36
>C4:3
>C5:38

최종적으로 C1은 15, C2는 20, C4는 3이므로 C5는 38의 값을 갖는다.

>데이터가 발행자(생산자)에서 구독자(소비자)로 흐름에 착안해 개발자는 이를 **업스트림(upstream)** 또는 **다운스트림(downstream)**이라 부른다.
>위 예제에서 데이터 `newValue`는 업스트림 `onNext()` 메서드로 전달되고 `notifyAllSubscribers()` 호출을 통해 다운스트림 `onNext()` 호출로 전달된다.

지금까지 `발행-구독` 핵심 개념을 확인했다. 하지만 부수적인 내용은 다루지 않았지만 `역압력(backpressure)` 같은 내용은 중요하므로 다음 절에서 따로 설명한다.

우선은 리액티브 프로그래밍과 직접적으로 관련이 있는 내용만 살펴본다. 15.2절에서 설명한것처럼 실생활에서 플로를 사용하려면
`onNext` 이벤트 외에 `onError`나 `onComplete` 같은 메서드를 통해 데이터 흐름에서 예외가 발생하거나
데이터 흐름이 종료되었음을 알 수 있어야 한다(예를 들어 온도계 샘플이 교체되어서 `onNext`로 더 이상 데이터가 발생하지 않는 상황).
`자바 9 플로 API`의 `Subcriber`에서는 실제 `onError`와 `onComplete`를 지원한다. 
기존의 옵저버 패턴에 비해 새로운 API 프로토콜이 더 강력해진 이유가 이들 바로 이런 메서드 덕분이다.

간단하지만 플로 인터페이스의 개념을 복잡하게 만든 두 가지 기능은 `압력`과 `역압력`이다. 처음에는 이 두 기능이 별로 중요해 보이지 않을 수 있지만
스레드 활용에서 이들 기능은 필수다. 기존의 온도계 예제에서 온도계가 매 초마다 온도를 보고했는데 기능이 업그레이드 되면서 매 밀리초마다 온도계를 보고한다고 가정하자.
우리 프로그램은 이렇게 빠른 속도로 발생하는 이벤트를 아무 문제없이 처리할 수 있을까? 마찬가지로 모든 SMS 메시지를 폰으로 제공하는 발행자에 가입하는 상황을 생각해보자.
처음에 약간의 SMS 메시지가 있는 새 폰에서는 가입이 잘 동작할 수 있지만 몇 년 후에는 매 초마다 수천 개의 메시지가 `onNext`로 전달된다면 어떤 일이 일어날까?
이런 상황을 `압력(pressure)`이라 부른다.

공에 담긴 메시지를 포함하는 수직 파이프를 상상해보자. 이런 상황에서는 출구로 추가될 공의 숫자를 제한하는 `역압력` 같은 기법이 필요하다.
`자바 9 플로 API`에서는 발행자가 무한의 속도로 아이템을 방출하는 대신 요청했을 때만 다음 아이템을 보내도록 하는
`request()` 메서드(`Subscription`이라는 새 인터페이스에 포함)를 제공한다(`밀어내기(push)모델`이 아니라 `당김(pull)모델`).

### 15.5.2 역압력
`Subscriber` 객체(`onNext`, `onError`, `onComplete` 메서드를 포함)를 어떻게 `Publisher`에게 전달해 발행자가 필요한 메서드를 호출할 수 있는지 살펴봤다.
이 객체는 `Publisher`에서 `Subscriber`로 정보를 전달한다. 정보의 흐름 속도를 `역압력(흐름 제어)`으로 제어,
즉 `Subscriber`에서 `Publisher`로 정보를 요청해야 할 필요가 있을 수 있다.
`Publisher`는 여러 `Subscriber`를 갖고 있으므로 역압력 요청이 한 연결에만 영향을 미쳐야 한다는 것이 문제가 될 수 있다.
`자바 9 플로 API`의 `Subcriber` 인터페이스는 네 번째 메서드를 포함한다.

```java
void onSubscribe(Subscription subscription);
```

`Publisher`와 `Subscriber` 사이에 채널이 연결되면 첫 이벤트로 이 메서드가 호출된다. `Subscription` 객체는 다음처럼
`Subscriber`와 `Publisher`와 통신할 수 있는 메서드를 포함한다.

```java
interface Subscription {
    void cancel();
    void request(long n);
}
``` 

콜백을 통한 '역방향' 소통 효과에 주목하자. `Publisher`는 `Subscription` 객체를 만들어 `Subscriber`로 전달하면 `Subscriber`는 이를 이용해
`Publisher`로 정보를 보낼 수 있다.

### 15.5.3 실제 역압력의 간단한 형태
한 번에 한 개의 이벤트를 처리하도록 `발행-구독` 연결을 구성하려면 다음과 같은 작업이 필요하다.
- `Subscriber`가 `OnSubscribe`로 전달된 `Subscription` 객체를 `subscription` 같은 필드에 로컬로 저장한다.
- `Subscriber`가 수 많은 이벤트를 받지 않도록 `onSubscribe`, `onNext`, `onError`의 마지막 동작에 `channel.request(1)`을 추가해 오직 한 이벤트만 요청한다.
- 요청을 보낸 채널에만 `onNext`, `onError` 이벤트를 보내도록 `Publisher`의 `notifyAllSubscribers` 코드를 바꾼다
(보통 여러 `Subscriber`가 자신만의 속도를 유지할 수 있도록 `Publisher`는 새 `Subscription`을 만들어 각 `Subscriber`와 연결한다).

구현이 간단해 보일 수 있지만 역압력을 구현하려면 여러 가지 장단점을 생각해야 한다.
- 여러 `Subscriber`가 있을 때 이벤트를 가장 느린 속도로 보낼 것인가? 아니면 각 `Subscriber`에게 보내지 않은 데이터를 저장할 별도의 큐를 가질 것인가?
- 큐가 너무 커지면 어떻게 해야 할까?
- `Subscriber`가 준비가 안 되었다면 큐의 데이터를 폐기할 것인가?

위 질문의 답변은 데이터의 성격에 따라 달라진다. 한 온도 데이터를 잃어버리는 것은 그리 대수로운 일이 아니지만 은행 계좌에서 크레딧이 사라지는 것은 큰 일이다.

`당김 기반 리액티브 역압력`이라는 기법에서는 `Subscriber`가 `Publisher`로부터 요청을 `당긴다(pull)`는 의미에서 **리액티브 당김 기반**(reactive pull-based)이라 불린다.
결과적으로 이런 방식으로 역압력을 구현할 수도 있다.

---

## 15.6 리액티브 시스템 vs 리액티브 프로그래밍
프로그래밍과 교육 커뮤니티에서 `리액티브 시스템`과 `리액티브 프로그래밍`이라는 말을 점점 자주 접할 수 있는데 이 둘은 상당히 다른 의미를 가지고 있다.

`리액티브 시스템(reactive system)`은 런타임 환경이 변화에 대응하도록 전체 아키텍처가 설계된 프로그램을 가리킨다.
리액티브 시스템이 가져야 할 공식적인 속성은 `Reactive Manifesto(http://www.reactivemanifesto.org)`에서 확인할 수 있다(17장 참고).
`반응성(responsive)`, `회복성(resilient)`, `탄력성(elastic)`으로 세 가지 속성을 요약할 수 있다.

`반응성`은 리액티브 시스템이 큰 작업을 처리하느라 간단한 질의의 응답을 지연하지 않고 실시간으로 입력에 반응하는 것을 의미한다.
`회복성`은 한 컴포넌트의 실패로 전체 시스템이 실패하지 않음을 의미한다. 네트워크가 고장났어도 이와 관계가 없는 질의에는 아무 영향이 없어야 하며
반응이 없는 컴포넌트를 향한 질의가 있다면 다른 대안 컴포넌트를 찾아야 한다.
`탄력성`은 시스템이 자신의 작업 부하에 맞게 적응하며 작업을 효율적으로 처리함을 의미한다.
바에서 음식과 음료를 서빙하는 직원을 동적으로 재배치 하므로 두 가지 주문의 대기줄이 일정하게 유지되도록 하듯이 각 큐가 원활하게 처리될 수 있도록
다양한 소프트웨어 서비스와 관련된 작업자 스레드를 적절하게 재배치할 수 있다.

여러 가지 방법으로 이런 속성을 구현할 수 있지만 `java.util.concurrent.Flow` 관련된 자바 인터페이스에서 제공하는 **리액티브 프로그래밍** 형식을
이용하는 것도 주요 방법 중 하나다. 이들 인터페이스 설계는 `Reactive Manifesto`의 네 번째이자 마지막 속성 즉 `메시지 주도(message-driven)` 속성을 반영한다.
`메시지 주도 시스템`은 `박스와 채널 모델`에 기반한 내부 API를 갖고 있는데 여기서 컴포넌트는 처리할 입력을 기다리고 결과를 다른 컴포넌트로 보내면서 시스템이 반응한다. 

---

## 15.7 마치며
- 자바의 동시성 지원은 계속 진화해 왔으며 앞으로도 그럴 것이다. 스레드 풀은 보통 유용하지만 블록되는 태스크가 많아지면 문제가 발생한다.
- 메서드를 `비동기(결과를 처리하기 전에 반환)`로 만들면 병렬성을 추가할 수 있으며 부수적으로 루프를 최적화한다.
- 박스와 채널 모델을 이용해 비동기 시스템을 시각화할 수 있다.
- 자바 8 `CompletableFuture` 클래스와 `자바 9 플로 API` 모두 `박스와 채널 다이어그램`으로 표현할 수 있다.
- `CompletableFuture` 클래스는 한 번의 비동기 연산을 표현한다. 콤비네이터로 비동기 연산을 조합함으로 `Future`를 이용할 때 발생했던
기존의 블로킹 문제를 해결할 수 있다.
- `플로 API`는 `발행-구독 프로토콜`, `역압력`을 이용하면 자바의 리액티브 프로그래밍의 기초를 제공한다.
- `리액티브 프로그래밍`을 이용해 `리액티브 시스템`을 구현할 수 있다.

---
