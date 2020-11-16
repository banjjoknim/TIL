# Chapter7. 병렬 데이터 처리와 성능

앞선 내용에서 외부 반복을 내부 반복으로 바꾸면 네이티브 자바 라이브러리가 스트림 요소의 처리를 제어할 수 있음을 확인했다. 무엇보다도 컴퓨터의 멀티코어를 활용해서 파이프라인 연산을 실행할 수 있다는 점이 가장 중요한 특징이다. 

예를 들어 자바 7이 등장하기 전에는 데이터 컬렉션을 병렬로 처리하기가 어려웠다. 우선 데이터를 서브파트로 분할해야 한다. 그리고 분할된 서브파트를 각각의 스레드로 할당한다. 스레드로 할당한 다음에는 의도치 않은 레이스 컨디션이 발생하지 않도록 적절한 동기화를 추가해야 하며, 마지막으로 부분 결과를 합쳐야 한다. 자바 7은 더 쉽게 병렬화를 수행하면서 에러를 최소화할 수 있도록 **포크/조인 프레임워크** 기능을 제공한다.

스트림을 이용하면 순차 스트림을 병렬 스트림으로 자연스럽게 바꿀 수 있다. 어떻게 이런 마법 같은 일이 일어날 수 있는지, 더 나아가 자바 7에 추가된 포크/조인 프레임워크와 내부적인 병렬 스트림 처리는 어떤 관계가 있는지 살펴본다. 병렬 스트림이 내부적으로 어떻게 처리되는지 알아야만 스트림을 잘못 사용하는 상황을 피할 수 있다.

우선 여러 청크를 병렬로 처리하기 전에 병렬 스트림이 요소를 여러 청크로 분할하는 방법을 설명할 것이다. 이 원리를 이해하지 못하면 의도치 않은, 설명하기 어려운 결과가 발생할 수 있다. 따라서 커스텀 `Spliterator`를 직접 구현하면서 분할 과정을 우리가 원하는 방식으로 제어하는 방법도 설명한다.

---

## 7.1 병렬 스트림

컬렉션에 `parallelStream`을 호출하면 **병렬 스트림**이 생성된다. 병렬 스트림이란 각각의 스레드에서 처리할 수 있도록 스트림 요소를 여러 청크로 분할한 스트림이다. 따라서 병렬 스트림을 이용하면 모든 멀티코어 프로세서가 각각의 청크를 처리하도록 할당할 수 있다. 간단한 예제로 이를 직접 확인해보자.

숫자 `n`을 인수로 받아서 `1`부터 `n`까지의 모든 숫자의 합계를 반환하는 메서드를 구현한다고 가정하자. 조금 투박한 방식이지만 다음 코드에서 보여주는 것처럼 숫자로 이루어진 무한 스트림을 만든 다음에 인수로 주어진 크기로 스트림을 제한하고, 두 숫자를 더하는 `BinaryOperator`로 리듀싱 작업을 수행할 수 있다.

```java
public long sequentialSum(long n) {
    return Stream.iterate(1L, i -> i + 1) // 무한 자연수 스트림 생성
                 .limit(n) // n개 이하로 제한
                 .reduce(0L, Long::sum); //모든 숫자를 더하는 스트림 리듀싱 연산
}
```

전통적인 자바에서는 다음과 같이 반복문으로 이를 구현할 수 있다.

```java
public long iterativeSum(long n) {
    long result = 0;
    for (long i = 1L; i <= n; i++) {
        result += i;
    }
    return result;
}
```

특히 `n`이 커진다면 이 연산을 병렬로 처리하는 것이 좋을 것이다. 무엇부터 건드려야 할까? 결과 변수는 어떻게 동기화해야 할까? 몇 개의 스레드를 사용해야 할까? 숫자는 어떻게 생성할까? 생성된 숫자는 누가 더할까?

병렬 스트림을 이용하면 걱정, 근심 없이 모든 문제를 쉽게 해결할 수 있다.

#### 7.1.1 순차 스트림을 병렬 스트림으로 변환하기

순차 스트림에 `parallel` 메서드를 호출하면 기존의 함수형 리듀싱 연산(숫자 합계 계산)이 병렬로 처리된다.

```java
public long parallelSum(long n) {
    return Stream.iterate(1L, i -> i + 1)
                 .limit(n)
                 .parallel() // 스트림을 병렬 스트림으로 변환
                 .reduce(0L, Long::sum);
}
```

위 코드가 이전 코드와 다른 점은 스트림이 여러 청크로 분할되어 있다는 것이다. 따라서 리듀싱 연산을 여러 청크에 병렬로 수행할 수 있다. 마지막으로 리듀싱 연산으로 생성된 부분 결과를 다시 리듀싱 연산으로 합쳐서 전체 스트림의 리듀싱 결과를 도출한다.

사실 순차 스트림에 `parallel`을 호출해도 스트림 자체에는 아무 변화도 일어나지 않는다. 내부적으로는 `parallel`을 호출하면 이후 연산이 병렬로 수행해야 함을 의미하는 불리언 플래그가 설정된다. 반대로 `sequential`로 병렬 스트림을 순차 스트림으로 바꿀 수 있다. 이 두 메서드를 이용해서 어떤 연산을 병렬로 실행하고 어떤 연산을 순차로 실행할지 제어할 수 있다. 예를 들어 다음 코드를 살펴보자.

```java
stream().parallel()
        .filter(...)
        .sequential()
        .map(...)
        .parallel()
        .reduce();
```

`parallel`과 `sequential` 두 메서드 중 최종적으로 호출된 메서드가 전체 파이프라인에 영향을 미친다. 이 예제에서 파이프라인의 마지막 호출은 `parallel`이므로 파이프라인은 전체적으로 병렬로 실행된다.

#### 7.1.2 스트림 성능 측정

책의 내용을 참조할 것.
- 반복 작업은 병렬로 수행할 수 있는 독립 단위로 나누기가 어렵ㄴ다.
- 올바른 자료구조를 선택해야 병렬실행도 최적의 성능을 발휘할 수 있다.

#### 7.1.3 병렬 스트림의 올바른 사용법

병렬 스트림을 잘못 사용하면서 발생하는 많은 문제는 공유된 상태를 바꾸는 알고리즘을 사용하기 때문에 일어난다. 다음은 `n`까지의 자연수를 더하면서 공유된 누적자를 바꾸는 프로그램을 구현한 코드다.

```java
public long sideEffectSum(long n) {
    Accumulator accumulator = new Accumulator();
    LongStream.rangeClosed(1, n).forEach(accumulator::add);
    return accumulator.total;
}

public class Accumulator {
    public long total = 0;
    public void add(long value) { total += value;}
}
```

```java
public long sideEffectParallelSum(long n) {
    Accumulator accumulator = new Accumulator();
    LongStream.rangeClosed(1, n).parallel().forEach(accumulator::add);
    return accumulator.total;
}
```

위 코드는 본질적으로 순차 실행할 수 있도록 구현되어 있으므로 병렬로 실행하면 참사가 일어난다. 특히 `total`을 접근할 때마다 (다수의 스레드에서 동시에 데이터에 접근하는) 데이터 레이스 문제가 일어난다. 동기화로 문제를 해결하다보면 결국 병렬화라는 특성이 없어져 버릴 것이다. 또한 메서드의 성능은 둘째 치고, 올바른 결과값이 나오지 않는다. 여러 스레드에서 동시에 누적자, 즉 `total += value`를 실행하면서 이런 문제가 발생한다. 얼핏 보면 아토믹 연산 같지만 `total += value`는 아토믹 연산이 아니다. 결국 여러 스레드에서 공유하는 객체의 상태를 바꾸는 `forEach` 블록 내부에서 `add` 메서드를 호출하면서 이 같은 문제가 발생한다. 이 예제처럼 병렬 스트림을 사용했을 때 이상한 결과에 당황하지 않으려면 상태 공유에 따른 부작용을 피해야 한다. 병렬 스트림이 올바로 동작하려면 공유된 가변 상태를 피해야 한다는 사실을 기억하자.

#### 7.1.4 병렬 스트림 효과적으로 사용하기

- 확신이 서지 않으면 직접 측정하라. 순차 스트림을 병렬 스트림으로 쉽게 바꿀 수 있다. 하지만 무조건 병렬 스트림으로 바꾸는 것이 능사는 아니다. 더욱이 병렬 스트림의 수행 과정은 투명하지 않을 때가 많다. 따라서 순차 스트림과 병렬 스트림 중 어떤 것이 좋을지 모르겠다면 적절한 벤치마크로 직접 성능을 측정하는 것이 바람직하다.
- 박싱을 주의하라. 자동 박싱과 언박싱은 성능을 크게 저하시킬 수 있는 요소다. 자바 8은 박싱 동작을 피할 수 있도록 기본형 특화 스트림(`IntStream`, `LongStream`, `DoubleStream`)을 제공한다. 따라서 되도록이면 기본형 특화 스트림을 사용하는 것이 좋다.
- 순차 스트림보다 병렬 스트림에서 성능이 떨어지는 연산이 있다. 특히 `limit`나 `findFirst`처럼 요소의 순서에 의존하는 연산을 병렬 스트림에서 수행하려면 비싼 비용을 치러야한다. 예를 들어 `findAny`는 요소의 순서와 상관없이 연산하므로 `findFirst`보다 성능이 좋다. 정렬된 스트림에 `unordered`를 호출하면 비정렬된 스트림을 얻을 수 있다. 스트림에 `N`개 요소가 있을 때 요소의 순서가 상관없다면(예를 들어 소스가 리스트라면) 비정렬된 스트림에 `limit`를 호출하는 것이 더 효율적이다.
- 스트림에서 수행하는 전체 파이프라인 연산 비용을 고려하라. 처리해야 할 요소 수가 `N`이고 하나의 요소를 처리하는 데 드는 비용을 `Q`라 하면 전체 스트림 파이프라인 처리 비용을 `N*Q`로 예상할 수 있다. `Q`가 높아진다는 것은 병렬 스트림으로 성능을 개선할 수 있는 가능성이 있음을 의미한다.
- 소량의 데이터에서는 병렬 스트림이 도움 되지 않는다. 소량의 데이터를 처리하는 상황에서는 병렬화 과정에서 생기는 부가 비용을 상홰할 수 있을 만큼의 이득을 얻지 못하기 때문이다.
- 스트림을 구성하는 자료구조가 적절한지 확인하라. 예를 들어 `ArrayList`를 `LinkedList`보다 효율적으로 분할할 수 있다. `LinkedList`를 분할하려면 모든 요소를 탐색해야 하지만 `ArrayList`는 요소를 탐색하지 않고도 리스트를 분할할 수 있기 때문이다. 또한 `range` 팩토리 메서드로 만든 기본형 스트림도 쉽게 분해할 수 있다. 마지막으로 커스텀 `Spliterator`를 구현해서 분해 과정을 완벽하게 제어할 수 있다.
- 스트림의 특성과 파이프라인의 중간 연산이 스트림의 특성을 어떻게 바꾸는지에 따라 분해 과정의 성능이 달라질 수 있다. 예를 들어 `SIZED` 스트림은 정확히 같은 크기의 두 스트림으로 분할할 수 있으므로 효과적으로 스트림을 병렬 처리할 수 있다. 반면 필터 연산이 있으면 스트림의 길이를 예측할 수 없으므로 효과적으로 스트림을 병렬 처리할 수 있을지 알 수 없게 된다.
- 최종 연산의 병합 과정(예를 들면 `Collector`의 `combiner` 메서드) 비용을 살펴보라. 병합 과정의 비용이 비싸다면 병렬 스트림으로 얻은 성능의 이익이 서브스트림의 부분 결과를 합치는 과정에서 상쇄될 수 있다.

**스트림 소스와 분해성 표**

소스|분해성
----|----
ArrayList|훌륭함
LinkedList|나쁨
IntStream.range|훌륭함
Stream.iterate|나쁨
HashSet|좋음
TreeSet|좋음

마지막으로 병렬 스트림이 수행되는 내부 인프라구조도 살펴봐야 한다. 자바 7에서 추가된 포크/조인 프레임워크로 병렬 스트림이 처리된다. 병렬 스트림을 제대로 사용하려면 병렬 스트림의 내부 구조를 잘 알아야 한다.

---

## 7.2 포크/조인 프레임워크

포크/조인 프레임워크는 병렬화할 수 있는 작업을 재귀적으로 작은 작업으로 분할한 다음에 서브태스크 각각의 결과를 합쳐서 전체 결과를 만들도록 설계되었다. 포크/조인 프레임워크에서는 서브태스크를 스레드 풀(`ForkJoinPool`)의 작업자 스레드에 분산 할당하는 `ExecutorService` 인터페이스를 구현한다.

#### 7.2.1 RecursiveTask 활용

스레드 풀을 이용하려면 `RecursiveTask<R>`의 서브클래스를 만들어야 한다. 여기서 `R`은 병렬화된 태스크가 생성하는 결과 형식 또는 결과가 없을 때(결과가 없더라도 다른 비지역 구조를 바꿀 수 있다)는 `RecursiveAction` 형식이다. `RecursiveTask`를 정의하려면 추상 메서드 `compute`를 구현해야 한다.

```java
protected abstract R compute();
```

`compute` 메서드는 태스크를 서브태스크로 분할하는 로직과 더 이상 분할할 수 없을 때 개별 서브태스크의 결과를 생산할 알고리즘을 정의한다. 따라서 대부분의 `compute` 메서드 구현은 다음과 같은 의사코드 형식을 유지한다.

```java
if (태스크가 충분히 작거나 더 이상 분할할 수 없으면) {
    순차적으로 태스크 계산
} else {
    태스크를 두 서브태스크로 분할
    태스크가 다시 서브태스크로 분할되도록 이 메서드를 재귀적으로 호출함
    모든 서브태스크의 연산이 완료될 때까지 기다림
    각 서브태스크의 결과를 합침
}
```

태스크를 더 분할할 것인지 말 것인지 정해진 기준은 없지만 몇 가지 경험적으로 얻은 좋은 데이터가 있다. 이 알고리즘은 분할 후 정복 알고리즘의 병렬화 버전이다. 포크/조인 프레임워크를 이용해서 범위의 숫자를 더하는 문제(예제에서는 long[]으로 이루어진 숫자 배열 사용)를 구현하면서 포크/조인 프레임워크를 사용하는 방법을 확인하자. 다음의 `ForkJoinSumCalculator` 코드에서 보여주는 것처럼 먼저 `RecursiveTask`를 구현해야 한다.

```java
// 포크/조인 프레임워크를 이용해서 병렬 합계 수행
public class ForkJoinSumCalculator extends java.util.concurrent.RecursiveTask<Long> { // RecursiveTask를 상속받아 포크/조인 프레임워크에서 사용할 태스크를 생성한다.
    private final long[] numbers; // 더할 숫자 배열
    private final int start; // 이 서브태스크에서 처리할 배열의 초기 위치와 최종 위치
    private final int end;
    public static final long THRESHOLD = 10_000; // 이 값 이하의 서브태스크는 더 이상 분할할 수 없다.

    public ForkJoinSumCalculator(long[] numbers) {  // 메인 태스크를 생성할 때 사용할 공개 생성자
        this(numbers, 0, numbers.length);
    }

    // 메인 태스크의 서브태스크를 재귀적으로 만들 때 사용할 비공개 생성자
    private ForkJoinSumCalculator(long[] numbers, int start, int end) { 
        this.numbers = numbers;
        this.start = start;
        this.end = end;
    }

    @Override
    protected Long compute() { // RecursiveTask의 추상 메서드 오버라이드
        int length = end - start; // 이 태스크에서 더할 배열의 길이
        if (length <= THRESHOLD) {
            return computeSequentially(); // 기준값과 같거나 작으면 순차적으로 결과를 계산한다.
        }
        ForkJoinSumCalculator leftTask = new ForkJoinSumCalculator(numbers, start + length / 2, end); // 배열의 첫 번째 절반을 더하도록 서브태스크를 생성한다.
        leftTask.fork(); // ForkJoinPool의 다른 스레드로 새로 생성한 태스크를 비동기로 실행한다.
        ForkJoinSumCalculator rightTask = new ForkJoinSumCalculator(numbers, start + length / 2, end);
        Long rightResult = rightTask.compute(); // 두 번째 서브태스크를 동기 실행한다. 이때 추가로 분할이 일어날 수 있다.
        Long leftResult = leftTask.join(); // 첫 번째 서브태스크의 결과를 읽거나 아직 결과가 없으면 기다린다.
        return leftResult + rightResult; // 두 서브태스크의 결과를 조합한 값이 이 태스크의 결과다.
    }

    private long computeSequentially() { // 더 분할할 수 없을 때 서브태스크의 결과를 계산하는 단순한 알고리즘
        long sum = 0;
        for (int i = start; i < end; i++) {
            sum += numbers[i];
        }
        return sum;
    }
}
```

위 메서드는 `n`까지의 자연수 덧셈 작업을 병렬로 수행하는 방법을 더 직관적으로 보여준다. 다음 코드처럼 `ForkJoinSumCalculator`의 생성자로 원하는 수의 배열을 넘겨줄 수 있다.

```java
public static long forkJoinSum(long n) {
    long[] numbers = LongStream.rangeClosed(1, n).toArray();
    ForkJoinTask<Long> task = new ForkJoinSumCalculator(numbers);
    return new ForkJoinPool().invoke(task);
}
```

`LongStream`으로 `n`까지의 자연수를 포함하는 배열을 생성했다. 그리고 생성된 배열을 `ForkJoinSumCalculator`의 생성자로 전달해서 `ForkJoinTask`를 만들었다. 마지막으로 생성한 태스크를 새로운 `ForkJoinPool`의 `invoke` 메서드로 전달했다. `ForkJoinPool`에서 실행되는 마지막 `invoke` 메서드의 반환값은 `ForkJoinSumCalculator`에서 정의한 태스크의 결과가 된다.

일반적으로 애플리케이션에서는 둘 이상의 `ForkJoinPool`을 사용하지 않는다. 즉, 소프트웨어의 필요한 곳에서 언제든 가져다 쓸 수 있도록 `ForkJoinPool`을 한 번만 인스턴스화해서 정적 필드에 싱글턴으로 저장한다. `ForkJoinPool`을 만들면서 인수가 없는 디폴트 생성자를 이용했는데, 이는 `JVM`에서 이용할 수 있는 모든 프로세서가 자유롭게 풀에 접근할 수 있음을 의미한다. 더 정확하게는 `Runtime.availableProcessors`의 반환값으로 풀에 사용할 스레드 수를 결정한다. `availableProcessors`, 즉 '사용할 수 있는 프로세서'라는 이름과는 달리 실제 프로세서외에 하이퍼스레딩과 관련된 가상 프로세서도 개수에 포함된다.

>**ForkJoinSumCalculator 실행**
>
>`ForkJoinSumCalculator`를 `ForkJoinPool`로 전달하면 풀의 스레드가 `ForkJoinSumCalculator`의 `compute` 메서드를 실행하면서 작업을 수행한다. `compute` 메서드는 병렬로 실행할 수 있을만큼 태스크의 크기가 충분히 작아졌는지 확인하며, 아직 태스크의 크기가 크다고 판단되면 숫자 배열을 반으로 분할해서 두 개의 새로운 `ForkJoinSumCalculator`로 할당한다. 그러면 다시 `ForkJoinPool`이 새로 생성된 `ForkJoinSumCalculator`를 실행한다. 결국 이 과정이 재귀적으로 반복되면서 주어진 조건(예제에서는 덧셈을 수행할 항목이 만 개 이하여야 함)을 만족할 때까지 태스크 분할을 반복한다. 이제 각 서브태스크는 순차적으로 처리되며 포킹 프로세스로 만들어진 이진트리의 태스크를 루트에서 역순으로 방문한다. 즉, 각 서브태스크의 부분 결과를 합쳐서 태스크의 최종 결과를 계산한다.

#### 7.2.2 포크/조인 프레임워크를 제대로 사용하는 방법

포크/조인 프레임워크는 쉽게 사용할 수 있는 편이지만 항상 주의를 기울여야 한다. 다음은 포크/조인 프레임워크를 효과적으로 사용하는 방법이다.

- `join` 메서드를 태스크에 호출하면 태스크가 생산하는 결과가 준비될 때까지 호출자를 블록시킨다. 따라서 두 서브태스크가 모두 시작된 다음에 `join`을 호출해야 한다. 그렇지 않으면 각각의 서브태스크가 다른 태스크가 끝나길 기다리는 일이 발생하며 원래 순차 알고리즘보다 느리고 복잡한 프로그램이 되어버릴 수 있다.
- `RecursiveTask` 내에서는 `ForkJoinPool`의 `invoke` 메서드를 사용하지 말아야 한다. 대신 `compute`나 `fork` 메서드를 직접 호출할 수 있다. 순차 코드에서 병렬 계산을 시작할 때만 `invoke`를 사용한다.
- 서브태스크에 `fork` 메서드를 호출해서 `ForkJoinPool`의 일정을 조절할 수 있다. 왼쪽 작업과 오른쪽 작업 모두에 `fork` 메서드를 호출하는 것이 자연스러울 것 같지만 한쪽 잡업에는 `fork`를 호출하는 것보다는 `compute`를 호출하는 것이 효율적이다. 그러면 두 서브태스크의 한 태스크에는 같은 스레드를 재사용할 수 있으므로 풀에서 불필요한 태스크를 할당하는 오버헤드를 피할 수 있다.
- 포크/조인 프레임워크를 이용하는 병렬 계산은 디버깅하기 어렵다. 보통 IDE로 디버깅할때 스택 트레이스로 문제가 일어난 과정을 쉽게 확인할 수 있는데, 포크/조인 프레임워크에서는 `fork`라 불리는 다른 스레드에서 `compute`를 호출하므로 스택 트레이스가 도움이 되지 않는다.
- 병렬 스트림에서 살펴본 것처럼 멀티코어에 포크/조인 프레임워크를 사용하는 것이 순차 처리보다 무조건 빠를 거라는 생각은 버려야 한다. 병렬 처리로 성능을 개선하려면 태스크를 여러 독립적인 서브태스크로 분할할 수 있어야 한다. 각 서브태스크의 실행시간은 새로운 태스크를 포킹하는 데 드는 시간보다 길어야 한다. 예를 들어 I/O를 한 서브태스크에 할당하고 다른 서브태스크에서는 계산을 실행, 즉 I/O와 계산을 병렬로 실행할 수있다. 또한 순차 버전과 병렬 버전의 성능을 비교할 때는 다른 요소도 고려해야 한다. 다른 자바 코드와 마찬가지로 `JIT` 컴파일러에 의해 최적화되려면 몇 차례의 '준비 과정' 또는 실행 과정을 거쳐야 한다. 따라서 성능을 측정할 때는 여러 번 프로그램을 실행한 결과를 측정해야 한다. 또한 컴파일러 최적화는 병렬 버전보다는 순차 버전에 집중될 수 있다는 사실도 기억하자(예를 들어 순차 버전에서는 죽은 코드를 분석해서 사용되지 않는 계산은 아예 삭제하는 등의 최적화를 달성하기 쉽다).

포크/조인 분할 전략에서는 주어진 서브태스크를 더 분할할 것인지 결정할 기준을 정해야 한다.

#### 7.2.3 작업 훔치기

작업 훔치기 기법에서는 `ForkJoinPool`의 모든 스레드를 거의 공정하게 분할한다. 각각의 스레드는 자신에게 할당된 태스크를 포함하는 이중 연결 리스트를 참조하면서 작업이 끝날 때마다 큐의 헤드에서 다른 태스크를 가져와서 작업을 처리한다. 이때 한 스레드는 다른 스레드보다 자신에게 할당된 태스크를 더 빨리 처리할 수 있다. 즉, 다른 스레드는 바쁘게 일하고 있는데 한 스레드는 할일이 다 떨어진 상황이다. 이때 할일이 없어진 스레드는 유휴 상태로 바뀌는 것이 아니라 다른 스레드 큐의 꼬리에서 작업을 훔쳐온다. 모든 태스크가 작업을 끝낼 때까지, 즉 모든 큐가 빌 때까지 이 과정을 반복한다. 따라서 태스크의 크기를 작게 나누어야 작업자 스레드 간의 작업부하를 비슷한 수준으로 유지할 수 있다.

풀에 있는 작업자 스레드의 태스크를 재분배하고 균형을 맞출 때 작업 훔치기 알고리즘을 사용한다. 작업자의 큐에 있는 태스크를 두 개의 서브 태스크로 분할했을 때 둘 중 하나의 태스크를 다른 유휴 작업자가 훔쳐갈 수 있다. 그리고 주어진 태스크를 순차 실행할 단계가 될 때까지 이 과정을 재귀적으로 반복한다. 포크/조인 프레임워크가 어떻게 작업을 병렬로 처리하는지 살펴봤는데, 여기서는 숫자 배열을 여러 태스크로 분할하는 로직을 개발하며 예제를 살펴봤다. 그러나 분할 로직을 개발하지 않고도 병렬 스트림을 이용할 수 있었다. 즉, 스트림을 자동으로 분할해주는 기능이 있다는 사실을 이미 확인했다. 다음으로는 자동으로 스트림을 분할하는 기법인 `Spliterator`를 설명한다.

---

## 7.3 Spliterator 인터페이스

자바 8은 `Spliterator`라는 새로운 인터페이스를 제공한다. `Spliterator`는 '분할할 수 있는 반복자'라는 의미다. `Iterator`처럼 `Spliterator`는 소스의 요소 탐색 기능을 제공한다는 점은 같지만 `Spliterator`는 병렬 작업에 특화되어 있다. 커스텀 `Spliterator`를 꼭 직접 구현해야 하는 것은 아니지만 `Spliterator`가 어떻게 동작하는지 이해한다면 병렬 스트림 동작과 관련한 통찰력을 얻을 수 있다. 자바 8은 컬렉션 프레임워크에 포함된 모든 자료구조에 사용할 수 있는 디폴트 `Spliterator` 인터페이스 구현을 제공한다. `Spliterator` 인터페이스는 여러 메서드를 정의한다.

```java
// Spliterator 인터페이스
public interface Spliterator<T> {
    boolean tryAdvance(Consumer<? super T> action);
    Spliterator<T> trySplit();
    long estimateSize();
    int characteristics();
}
```

여기서 `T`는 `Spliterator`에서 탐색하는 요소의 형식을 가리킨다. `tryAdvance` 메서드는 `Spliterator`의 요소를 하나씩 순차적으로 소비하면서 탐색해야 할 요소가 남아있으면 참을 반환한다(즉, 일반적인 `Iterator` 동작과 같다). 반면 `trySplit` 메서드는 `Spliterator`의 일부 요소(자신이 반환한 요소)를 분할해서 두 번째 `Spliterator`를 생성하는 메서드다. `Spliterator`에서는 `estimateSize` 메서드로 탐색해야 할 요소 수 정보를 제공할 수 있다. 특히 탐색해야 할 요소 수가 정확하진 않더라도 제공된 값을 이용해서 더 쉽고 공평하게 `Spliterator`를 분할할 수 있다.

#### 7.3.1 분할 과정

스트림을 여러 스트림으로 분할하는 과정은 재귀적으로 일어난다. 1단계에서 첫 번째 `Spliterator`에 `trySplit`을 호출하면 두 번째 `Spliterator`가 생성된다. 2단계에서 두 개의 `Spliterator`에 `trySplit`를 다시 호출하면 네 개의 `Spliterator`가 생성된다. 이처럼 `trySplit`의 결과가 `null`이 될 때까지 이 과정을 반복한다. 만약 `trySplit`이 `null`을 반환했다는 것은 더 이상 자료구조를 분할할 수 없음을 의미한다. 4단계에서 `Spliterator`에 호출한 모든 `trySplit`의 결과가 `null`이면 재귀 분할 과정이 종료된다. 이 분할 과정은 `characteristics` 메서드로 정의하는 `Spliterator`의 특성에 영향을 받는다.

>**Spliterator 특성**
>
>`Spliterator`는 `characteristics`라는 추상 메서드도 정의한다. `Characteristics` 메서드는 `Spliterator` 자체의 특성 집합을 포함하는 `int`를 반환한다. `Spliterator`를 이용하는 프로그램은 이들 특성을 참고해서 `Spliterator`를 더 잘 제어하고 최적화할 수 있다.
>
>`Spliterator`의 특성 정보 표
>특성|의미
>--|--
>`ORDERED`|리스트처럼 요소에 정해진 순서가 있으므로 `Spliterator`는 요소를 탐색하고 분할할 때 이 순서에 유의해야 한다.
>`DISTINCT`|`x`,`y` 두 요소를 방문했을 때 `x.equals(y)`는 항상 `false`를 반환한다.
>`SORTED`|탐색된 요소는 미리 정의된 정렬 순서를 따른다.
>`SIZED`|크기가 알려진 소스(예를 들면 `Set`)로 `Spliterator`를 생성했으므로 `estimateSize()`는 정확한 값을 반환한다.
>`NON-NULL`|탐색하는 모든 요소는 `null`이 아니다.
>`IMMUTABLE`|이 Spliterator의 소스는 불변이다. 즉, 요소를 탐색하는 동안 요소를 추가하거나, 삭제하거나, 고칠 수 없다.
>`CONCURRENT`|동기화 없이 `Spliterator`의 소스를 여러 스레드에서 동시에 고칠 수 있다.
>`SUBSIZED`|이 `Spliterator` 그리고 분할되는 모든 `Spliterator`는 `SIZED` 특성을 갖는다.

#### 7.3.2 커스텀 Spliterator 구현하기

`Spliterator`를 구현하는 예제를 살펴보자. 문자열의 단어 수를 계산하는 단순한 메서드를 구현할 것이다. 다음은 반복 버전으로 메서드를 구현한 예제다.

```java
// 반복형으로 단어 수를 세는 메서드
public int countWordsIteratively(String s) {
    int counter = 0;
    boolean lastSpace = true;
    for (char c : s.toCharArray()) { // 문자열의 모든 문자를 하나씩 탐색한다.
        if (Character.isWhitespace(c)) {
            lastSpace = true;
        } else {
            if (lastSpace) counter++; // 문자를 하나씩 탐색하다 공백 문자를 만나면 지금까지 탐색한 문자를 단어로 간주하여(공백 문자는 제외) 단어 수를 증가시킨다.
            lastSpace = false;
        }
    }
    return counter;
}
```

```java
final String SENTENCE = "Nel    mezzo del cammin di nostra vita " + "mi ritrovai in una  selva oscura" + "ch   la dritta via era smarrita ";
System.out.println("Found " + countWordsIteratively(SENTENCE) + " words");
```

단어 사이에 공백이 여러 개일 때도 반복 구현이 제대로 작동된다는 것을 보이고자(즉, 여러 개의 공백을 공백 하나로 간주함) 문장에 임의로 공백을 추가했다. 다음은 프로그램 실행 결과다.

`Found 19 words`

반복형 대신 함수형을 이용하면 직접 스레드를 동기화하지 않고도 병렬 스트림으로 작업을 병렬화할 수 있다.

>**함수형으로 단어 수를 세는 메서드 재구현하기**
>
>우선 `String`을 스트림으로 변환해야 한다. 안타깝게도 스트림은 `int`, `long`, `double` 기본형만 제공하므로 `Stream<Character>`를 사용해야 한다.
>
>```java
>Stream<Character> stream = IntStream.range(0, SENTENCE.length())
>                                    .mapToObj(SENTENCE::charAt);
>```
>
>스트림에 리듀싱 연산을 실행하면서 단어 수를 계산할 수 있다. 이때 지금까지 발견한 단어 수를 계산하는 `int` 변수와 마지막 문자가 공백이었는지 여부를 기억하는 `Boolean` 변수 등 두 가지 변수가 필요하다. 자바에는 튜플(래퍼 객체 없이 다형 요소의 정렬 리스트를 표현할 수 있는 구조체)이 없으므로 이들 변수 상태를 캡슐화하는 새로운 클래스 `WordCounter`를 만들어야 한다.
>
>```java
>// 문자열 스트림을 탐색하면서 단어 수를 세는 클래스
>public class WordCounter {
>    private final int counter;
>    private final boolean lastSpace;
>
>    public WordCounter(int counter, boolean lastSpace) {
>        this.counter = counter;
>        this.lastSpace = lastSpace;
>    }
>
>    public WordCounter accumulate(Character c) { // 반복 알고리즘처럼 accumulate 메서드는 문자열의 문자를 하나씩 탐색한다.
>        if (Character.isWhitespace(c)) {
>            return lastSpace ? this : new WordCounter(counter, true);
>        } else {
>            // 문자를 하나씩 탐색하다 공백 문자를 만나면 지금까지 탐색한 문자를 단어로 간주하여(공백 문자는 제외) 단어 수를 증가시킨다.
>            return lastSpace ? new WordCounter(counter + 1, false) : this;
>        }
>    }
>
>    public WordCounter combine(WordCounter wordCounter) {
>        return new WordCounter(counter + wordCounter.counter, // 두 WordCounter의 counter 값을 더한다.
>                wordCounter.lastSpace); // counter 값만 더할 것이므로 마지막 공백은 신경 쓰지 않는다.
>    }
>
>    public int getCounter() {
>        return counter;
>    }
>}
>```
>
>`accumulate` 메서드는 `WordCounter`의 상태를 어떻게 바꿀 것인지, 또는 엄밀히 `WordCounter`는(속성을 바꿀 수 없는) 불변 클래스이므로 새로운 `WordCounter` 클래스를 어떤 상태로 생성할 것인지 정의한다. 스트림을 탐색하면서 새로운 문자를 찾을 때마다 `accumulate` 메서드를 호출한다. 새로운 비공백 문자를 탐색한 다음에 마지막 문자가 공백이면 `counter`를 증가시킨다.
>
>두 번째 메서드 `combine`은 문자열 서브 스트림을 처리한 `WordCounter`의 결과를 합친다. 즉, `combine`은 `WordCounter`의 내부 `counter`값을 서로 합친다.
>
>`WordCounter`가 어떻게 문자의 개수를 누적하고 합치는지 살펴봤다. 이제 다음 코드처럼 문자스트림의 리듀싱 연산을 직관적으로 구현할 수 있다.
>
>```java
>private int countWords(Stream<Character> stream) {
>    WordCounter wordCounter = stream.reduce(new WordCounter(0, true),
>                                                WordCounter::accumulate,
>                                                WordCounter::combine);
>    return wordCounter.getCounter();
>}
>```
>
>```java
>Stream<Character> stream = IntStream.range(0, SENTENCE.length())
>                                    .mapToObj(SENTENCE::charAt);
>System.out.println("Found " + countWordsIteratively(stream) + " words");
>```
>
>다음처럼 반복 버전과 같은 결과가 출력된다.
>
>`Found 19 words`

>**WordCounter 병렬로 수행하기**
>
>다음처럼 단어 수를 계산하는 연산을 병렬 스트림으로 처리하자.
>
>```java
>System.out.println("Found " + countWords(stream.parallel())) + " words");
>```
>
>안타깝게도 원하는 결과가 나오지 않는다.
>
>`Found 25 words`
>
>뭐가 잘못되었을까? 원래 문자열을 임의의 위치에서 둘로 나누다보니 예상치 못하게 하나의 단어를 둘로 계산하는 상황이 발생할 수 있다. 즉, 순차 스트림을 병렬 스트림으로 바꿀 때 스트림 분할 위치에 따라 잘못된 결과가 나올 수 있다.
>
>어떻게 이 문제를 해결할 수 있을까? 문자열을 임의의 위치에서 분할하지 말고 단어가 끝나는 위치에서만 분할하는 방법으로 이 문제를 해결할 수 있다. 그러려면 단어 끝에서 문자열을 분할하는 문자 `Spliterator`가 필요하다.
>
>```java
>public class WordCounterSpliterator implements Spliterator<Character> {
>    private final String string;
>    private int currentChar = 0;
>
>    public WordCounterSpliterator(String string) {
>        this.string = string;
>    }
>
>    @Override
>    public boolean tryAdvance(Consumer<? super Character> action) {
>        action.accept(string.charAt(currentChar++)); // 현재 문자를 소비한다.
>        return currentChar < string.length(); // 소비할 문자가 남아있으면 true를 반환한다.
>    }
>
>    @Override
>    public Spliterator<Character> trySplit() {
>        int currentSize = string.length() - currentChar;
>        if (currentSize < 10) {
>            return null; // 파싱할 문자열을 순차 처리할 수 있을 만큼 충분히 작아졌음을 알리는 null을 반환한다.
>        }
>        for (int splitPos = currentSize / 2 + currentChar;
>             splitPos < string.length(); splitPos++) { // 파싱할 문자열의 중간을 분할 위치로 설정한다.
>            if(Character.isWhitespace(string.charAt(splitPos))) { // 다음 공백이 나올 때까지 분할 위치를 뒤로 이동시킨다.
>                Spliterator<Character> spliterator = // 처음부터 분할 위치까지 문자열을 파싱할 새로운 WordCounterSpliterator를 생성한다.
>                        new WordCounterSpliterator(string.substring(currentChar,
>                                splitPos));
>                currentChar = splitPos; // 시작 위치를 분할 위치로 설정한다.
>                return spliterator; // 공백을 찾았고 문자열을 분리했으므로 루프를 종료한다.
>            }
>        }
>        return null;
>    }
>
>    @Override
>    public long estimateSize() {
>        return string.length() - currentChar;
>    }
>
>    @Override
>    public int characteristics() {
>        return ORDERED + SIZED + SUBSIZED + NONNULL + IMMUTABLE;
>    }
>}
>```
>
>분석 대상 문자열로 `Spliterator`를 생성한 다음에 현재 탐색 중인 문자를 가리키는 인덱스를 이용해서 모든 문자를 반복 탐색한다. `Spliterator`를 구현하는 `WordCounterSpliterator`의 메서드를 더 자세히 살펴보자.
> - `tryAdvance` 메서드는 문자열에서 현재 인덱스에 해당하는 문자를 `Consumer`에 제공한 다음에 인덱스를 증가시킨다. 인수로 전달된 `Consumer`는 스트림을 탐색하면서 적용해야 하는 함수 집합이 작업을 처리할 수 있도록 소비한 문자를 전달하는 자바 내부 클래스다. 예제에서는 스트림을 탐색하면서 하나의 리듀싱 함수, 즉 `WordCounter`의 `accumulate` 메서드만 적용한다. `tryAdvance` 메서드는 새로운 커서 위치가 전체 문자열 길이보다 작으면 참을 반환하며 이는 반복 탐색해야 할 문자가 남아있음을 의미한다.
> - `trySplit`은 반복될 자료구조를 분할하는 로직을 포함하므로 `Spliterator`에서 가장 중요한 메서드다. 우선 분할 동작을 중단할 한계를 설정해야 한다. 여기서는 아주 작은 한계값(10개의 문자)을 사용했지만 실전의 애플리케이션에서는 너무 많은 태스크를 만들지 않도록 더 높은 한계값을 설정해야 한다. 분할 과정에서 남은 문자 수가 한계값 이하면 `null`을 반환, 즉 분할을 중지하도록 지시한다. 반대로 분할이 필요한 상황에는 파싱해야 할 문자열 청크의 중간 위치를 기준으로 분할하도록 지시한다. 이때 단어 중간을 분할하지 않도록 빈 문자가 나올때까지 분할 위치를 이동시킨다. 분할할 위치를 찾았으면 새로운 `Spliterator`를 만든다. 새로 만든 `Spliterator`는 현재 위치(`currentChar`)부터 분할된 위치까지의 문자를 탐색한다.
> - 탐색해야 할 요소의 개수(`estimateSize`)는 `Spliterator`가 파싱할 문자열 전체 길이(`string.length()`)와 현재 반복 중인 위치(`currentChar`)의 차다.
> - 마지막으로 `characteristic` 메서드는 프레임워크에 `Spliterator`가 `ORDERED`(문자열의 문자 등장 순서가 유의미함), `SIZED`(`estimateSize` 메서드의 반환값이 정확함), `SUBSIZED`(`trySplit`으로 생성된 `Spliterator`도 정확한 크기를 가짐). `NONNULL`(문자열에는 `null` 문자가 존재하지 않음), `IMMUTABLE`(문자열 자체가 불변 클래스이므로 문자열을 파싱하면서 속성이 추가되지 않음) 등의 특성임을 알려준다.

>**WordCounterSpliterator 활용**
>
>이제 새로운 `WordCounterSpliterator`를 병렬 스트림에 사용할 수 있다.
>
>```java
>Spliterator<Character> spliterator = new WordCounterSpliterator(SENTENCE);
>Stream<Character> stream = StreamSupport.stream(spliterator, true);
>```
>
>`StreamSupport.stream` 팩토리 메서드로 전달한 두 번째 불리언 인수는 병렬 스트림 생성 여부를 지시한다. 이제 병렬 스트림을 `countWords` 메서드로 전달한다.
>
>```java
>System.out.println("Found " + countWords(stream) + " words");
>```
>
>예상대로 다음과 같은 출력 결과가 나온다.
>
>`Found 19 words`

지금까지 `Spliterator`에서 어떻게 자료구조 분할 과정을 제어할 수 있는지 살펴봤다. 특히 `Spliterator`는 첫 번째 탐색 시점, 첫 번째 분할 시점, 또는 첫 번째 예상 크기(`estimateSize`) 요청 시점에 요소의 소스를 바인딩할 수 있다. 이와 같은 동작을 늦은 바인딩 `Spliterator`라고 부른다.

---