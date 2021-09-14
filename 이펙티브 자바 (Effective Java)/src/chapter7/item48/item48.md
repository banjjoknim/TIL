# 아이템 48. 스트림 병렬화는 주의해서 적용하라

동시성 프로그래밍을 할 때는 안전성(safety)과 응답 가능(liveness) 상태를 유지하도록 해야 한다. 이는 병렬 스트림 파이프라인 프로그래밍에서도 마찬가지다.

## 병렬 수행 효율

### 스트림을 사용해 처음 20개의 메르센 소수를 생성하는 프로그램

```java
public class Item48 {
    public static void main(String[] args) {
        primes().map(prime -> TWO.pow(prime.intValue()).subtract(ONE))
                .filter(mersenne -> mersenne.isProbablePrime(50))
                .limit(20)
                .forEach(System.out::println);
    }

    static Stream<BigInteger> primes() {
        return Stream.iterate(TWO, BigInteger::nextProbablePrime);
    }
}
```

- 위 코드는 새로 메르센 소수를 찾을 때마다 그 전 소수를 찾을 때보다 두 배 정도 더 오래 걸린다.
    - 19번째 계산까지 마치고 마지막 20번째 계산이 수행되는 시점에 한가한 CPU 코어들이 병렬로 21번째 ... 등의 메르센 소수를 찾는 작업을 시작한다. 20번째 계산이 끝나도 이 계산들은 끝나지 않는다...
      이는 결과적으로 자동 병렬화 알고리즘을 마비시킨다.
- 환경이 아무리 좋더라도 **데이터 소스가 Stream.iterate거나 중간 연산으로 limit를 쓰면 파이프라인 병렬화로는 성능 개선을 기대할 수 없다.**
- 파이프라인 병렬화는 limit를 다룰 때 CPU 코어가 남는다면 원소를 몇 개 더 처리한 후 제한된 개수 이후의 결과를 버려도 아무런 해가 없다고 가정한다.
    - 스트림 파이프라인을 마구잡이로 병렬화하면 안 된다. 성능이 오히려 나빠질 수도 있다.
- 대체로 **스트림의 소스가 ArrayList, HashMap, HashSet, ConcurrentHashMap의 인스턴스거나 배열, int 범위, long 범위일 때 병렬화의 효과가 가장 좋다.**
    - 이 자료구조들은 모두 데이터를 원하는 크기로 정확하고 손 쉽게 나눌 수 있어서 일을 다수의 스레드에 분배하기 좋다는 특징이 있다.
    - 나누는 작업은 Spliterator가 담당하며, Spliterator 객체는 Stream이나 Iterable의 spliterator 메서드로 얻어올 수 있다.
    - 이 자료구조들은 원소들을 순차적으로 실행할 때의 참조 지역성(locality of reference)이 뛰어나다. 이웃한 원소의 참조들이 메모리에 연속해서 저장되어 있다는 뜻이다.
        - 하지만 참조들이 가리키는 실제 객체가 메모리에서 서로 떨어져 있을 수 있는데, 그러면 참조 지역성이 떨어진다.

### 참조 지역성

- 참조 지역성이 낮으면 스레드는 데이터가 주 메모리에서 캐시 메모리로 전송되어 오기를 기다리며 멍하게 있는다.
    - 따라서 참조 지역성은 다량의 데이터를 처리하는 벌크 연산을 병렬화할 때 아주 중요한 요소로 작용한다.
- 참조 지역성이 가장 뛰어난 자료구조는 기본 타입의 배열이다. 기본 타입 배열에서는 참조가 아닌 데이터 자체가 메모리에 연속해서 저장되기 때문이다.

### 스트림 파이프라인의 종단 연산

- 스트림 파이프라인의 종단 연산의 동작 방식 역시 병렬 수행 효율에 영향을 준다.
    - 종단 연산에서 수행하는 작업량이 파이프라인 전체 작업에서 상당 비중을 차지하면서 순차적인 연산이라면 파이프라인 병렬 수행의 효과는 제한될 수밖에 업다.
- 종단 연산 중에서 병렬화에 가장 적합한 것은 축소(reduction)다. 축소는 파이프라인에서 만들어진 모든 원소를 하나로 합치는 작업이다.
    - Stream의 reduce 메서드 중 하나, 혹은 min, max, count, sum 같이 완성된 형태로 제공되는 메서드 중 하나를 선택해 수행한다.
- anyMatch, allMatch, noneMatch처럼 조건에 맞으면 바로 반환되는 메서드도 병렬화에 적합하다.
- 가변 축소(mutable reduction)를 수행하는 Stream의 collect 메서드는 컬렉션들을 합치는 부담이 크기 때문에 병렬화에 적합하지 않다.
- 직접 구현한 Stream, Iterable, Collection이 병렬화의 이점을 제대로 누리게 하고 싶다면 spliterator 메서드를 반드시 재정의하고 성능을 테스트 하도록 해야 한다.

### 성능 저하 및 안전 실패

- **스트림을 잘못 병렬화하면 응답 불가 상태에 빠지거나 성능이 나빠질 수 있고, 결과 자체가 잘못 되거나 예상치 못한 동작이 발생할 수 있다.**
- 결과가 잘못되거나 오동작하는 것은 안전 실패(safety failure)라고 한다.
    - 안전 실패는 병렬화한 파이프라인이 사용하는 mappers, filters, 혹은 프로그래머가 제공한 다른 함수 객체가 명세대로 동작하지 않을 때 벌어질 수 있다.
    - Stream 명세는 이때 사용되는 함수 객체에 관해 엄격한 규약을 정의해놨다.
        - ex. Stream의 reduce 연산에 건네지는 accumulator(누적기)와 combiner(결합기) 함수는 반드시 결합법칙을 만족(associative)하고, 간섭받지 않고(
          non-interfering), 상태를 갖지 않아야(stateless) 한다.
- 데이터 소스 스트림이 효율적으로 나눠지고, 병렬화하거나 빨리 끝나는 종단 연산을 사용하고, 함수 객체들도 간섭하지 않더라도, 파이프라인이 수행하는 진짜 작업이 병렬화에 드는 추가 비용을 상쇄하지 못한다면 성능
  향상은 미미할 수 있다.

**forEach의 순서를 순차 버전처럼 정렬하고 싶다면 종단 연산 forEach를 forEachOrdered로 바꿔주면 된다.**

## 최적화

- 스트림 병렬화는 오직 성능 최적화의 수단일 뿐이다. 다른 최적화와 마찬가지로 변경 전후로 반드시 성능을 테스트하여 병렬화를 사용할 가치가 있는지 확인해야 한다.
- 보통은 병렬 스트림 파이프라인도 공통의 포크 조인 풀에서 수행되므로(같은 스레드 풀을 사용하므로), 잘못된 파이프라인 하나가 시스템의 다른 부분의 성능에까지 악영향을 줄 수 있다.
- 스트림 병렬화가 효과를 보는 경우는 많지 않으나, **조건이 잘 갖춰지면 parallel 메서드 호출 하나로 거의 프로세서 코어 수에 비례하는 성능 향상을 얻을 수 있다.**

### 소수 계산 스트림 파이프라인 - 병렬화에 적합하다.

```java
public class Item48 {
    static long pi(long n) {
        return LongStream.rangeClosed(2, n)
                .mapToObj(BigInteger::valueOf)
                .filter(i -> i.isProbablePrime(50))
                .count();
    }
}
```

### 소수 계산 스트림 파이프라인 - 병렬화 버전

```java
public class Item48 {
    static long pi(long n) {
        return LongStream.rangeClosed(2, n)
                .parallel() // 기존 로직에 추가된 부분
                .mapToObj(BigInteger::valueOf)
                .filter(i -> i.isProbablePrime(50))
                .count();
    }
}
```

만약 n이 크다면 π(n)을 이 방식으로 계산하는 건 좋지 않다. 이에 대해서는 레머의 공식(Lehmer's Formula)이라는 훨씬 효율적인 알고리즘을 참고하도록 한다.

- 무작위 수들로 이뤄진 스트림을 병렬화하려거든 ThreadLocalRandom(혹은 구식인 Random)보다는 SplittableRandom 인스턴스를 이용하도록 한다.
    - SplittableRandom은 정확히 이럴 때 쓰고자 설계된 것으로, 병렬화하면 성능이 선형으로 증가한다.
- ThreadLocalRandom은 단일 스레드에서 쓰고자 만들어졌다.
    - 병렬 스트림용 데이터 소스로도 사용할 수는 있지만 SplittableRandom만큼 빠르지는 않을 것이다.
- Random은 모든 연산을 동기화하기 때문에 병렬 처리하면 최악의 성능을 보일 것이다.

## 핵심 정리

- 정확한 계산을 하고 성능도 빨리질 거라는 확신 없이는 스트림 파이프라인 병렬화는 시도조차 하지 말도록 한다.
- 스트림을 잘못 병렬화하면 프로그램을 오동작하게 하거나 성능을 급격히 떨어뜨린다.
- 병렬화 이전과 이후를 면밀히 비교한 다음, 계산도 정확하고 성능도 좋아졌음이 확실해졌을 때, 오직 그럴 때만 병렬화 버전 코드를 사용하도록 한다.

## 참고 자료

- [참조 지역성](https://itwiki.kr/w/%EC%B0%B8%EC%A1%B0_%EC%A7%80%EC%97%AD%EC%84%B1)
- [Java Spliterator interface](https://howtodoinjava.com/java/collections/java-spliterator/)
- [13. Parallel Data Processing](https://java-8-tips.readthedocs.io/en/stable/parallelization.html)