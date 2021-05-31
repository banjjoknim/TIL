# Chapter16. CompletableFuture : 안정적 비동기 프로그래밍
- 병렬 스트림과 포크/조인 기법을 이용해 컬렉션을 반복하거나 분할 그리고 정복 알고리즘을 활용하는 프로그램에서 높은 수준의 병렬을 적용할 수 있다.
- 자바 8, 자바 9에서는 `CompletableFuture`와 리액티브 프로그래밍 패러다임 두 가지 API를 제공한다.
- 자바 8에서 제공하는 `Future`의 구현 `CompletableFuture`은 비동기 프로그램에 큰 도움을 준다.

---

## 16.1 Future의 단순 활용
- 자바 5부터는 미래의 어느 시점에 결과를 얻는 모델에 활용할 수 있도록 `Future` 인터페이스를 제공하고 있다.
- 비동기 계산을 모델링하는 데 `Future`를 이용할 수 있으며, `Future`는 계산이 끝났을 때 결과에 접근할 수 있는 참조를 제공한다.
- 시간이 걸릴 수 있는 작업을 `Future` 내부로 설정하면 호출자 스레드가 결과를 기다리는 동안 다른 유용한 작업을 수행할 수 있다.
- `Future`는 저수준의 스레드에 비해 직관적으로 이해하기 쉽다는 장점이 있다.
- `Future`를 이용하려면 시간이 오래 걸리는 작업을 `Callable` 객체 내부로 감싼 다음에 `ExecutorService`에 제출해야 한다.

다음은 자바 8 이전의 예제 코드다.

##### 예제 16-1 Future로 오래 걸리는 작업을 비동기적으로 실행하기
```java
        ExecutorService executor = Executors.newCachedThreadPool(); // 스레드 풀에 태스크를 제출하려면 ExecutorService를 만들어야 한다.
        Future<Double> future = executor.submit(new Callable<Double>() { // Callable을 ExecutorService로 제출한다.
            @Override
            public Double call() throws Exception {
                return doSomeLongComputation(); // 시간이 오래 걸리는 작업은 다른 스레드에서 비동기적으로 실행한다.
            }
        });
        doSomethingElse(); // 비동기 작업을 수행하는 동안 다른 작업을 한다.
        try {
            Double result = future.get(1, TimeUnit.SECONDS); // 비동기 작업의 결과를 가져온다. 결과가 준비되어 있지 않으면 호출 스레드가 블록된다. 하지만 최대 1초까지만 기다린다.
        } catch (InterruptedException e) {
            // 계산 중 예외 발생
        } catch (ExecutionException e) {
            // 현재 스레드에서 대기 중 인터럽트 발생
        } catch (TimeoutException e) {
            // Future가 완료되기전에 타임아웃 발생
        }
```

##### 그림 16-1 Future로 시간이 오래 걸리는 작업을 비동기적으로 실행하기
![](https://images.velog.io/images/banjjoknim/post/58fae8ce-afcf-412c-b01a-ed54f6292457/16-1.%20Future%EB%A1%9C%20%EC%8B%9C%EA%B0%84%EC%9D%B4%20%EC%98%A4%EB%9E%98%20%EA%B1%B8%EB%A6%AC%EB%8A%94%20%EC%9E%91%EC%97%85%EC%9D%84%20%EB%B9%84%EB%8F%99%EA%B8%B0%EC%A0%81%EC%9C%BC%EB%A1%9C%20%EC%8B%A4%ED%96%89%ED%95%98%EA%B8%B0.png)

만약 오래 걸리는 작업이 영원히 끝나지 않으면 작업이 끝나지 않는 문제가 있을 수 있으므로 `[예제 16-1]`이 보여주는 것처럼 `get` 메서드를 오버라이딩해서
우리 스레드가 대기할 최대 타임아웃 시간을 설정하는 것이 좋다.

### 16.1.1 Future 제한
- `Future` 인터페이스가 비동기 계산이 끝났는지 알 수 있는 `isDone` 메서드, 계산이 끝나길 기다리는 메서드, 결과 회수 메서드 등이 있다.
- 하지만 이들만으로는 복잡한 비동기 프로그램을 구현하기 어렵다(A라는 계산이 끝나면 그 결과를 다른 계산 B에 전달하고, B의 결과가 나오면 다른 질의의 결과와 B의 결과를 조합하시오와 같은).
- 다음과 같은 선언형 기능이 있다면 유용할 것이다.
  - 두 개의 비동기 계산 결과를 하나로 합친다. 두 가지 계산 결과는 서로 독립적일 수 있으며 또는 두 번째 결과가 첫 번째 결과에 의존하는 상황일 수 있다.
  - `Future` 집합이 실행하는 모든 태스크의 완료를 기다린다.
  - `Future` 집합에서 가장 빨리 완료되는 태스크를 기다렸다가 결과를 얻는다(예를 들어 여러 태스크가 다양한 방식으로 같은 결과를 구하는 상황).
  - 프로그램적으로 `Future`를 완료시킨다(즉, 비동기 동작에 수동으로 결과 제공).
  - `Future` 완료 동작에 반응한다(즉, 결과를 기다리면서 블록되지 않고 결과가 준비되었다는 알림을 받은 다음에 `Future`의 결과로 원하는 추가 동작을 수행할 수 있음).
- 이러한 기능들을 선언형으로 이용할 수 있도록 자바 8에서는 `CompletableFuture` 클래스(`Future` 인터페이스를 구현한 클래스)를 제공한다.
- `Stream`과 `CompletableFuture`는 비슷한 패턴, 즉 람다 표현식과 파이프라이닝을 활용한다. 따라서 `Future`와 `CompletableFuture`의 관계를 `Collection`과 `Stream`의 관계에 비유할 수 있다.

### 16.1.2 CompletableFuture로 비동기 애플리케이션 만들기
- 어떤 제품이나 서비스를 이용해야 하는 상황이라 가정한다.
- 예산을 줄일 수 있도록 여러 온라인상점 중 가장 저렴한 가격을 제시하는 상점을 찾는 애플리케이션을 완성해가는 예제를 이용해서 `CompletableFuture`의 기능을 살펴본다.
- 이 예제를 통해서 다음과 같은 기술을 배울 수 있다.
  - 첫째, 고객에게 비동기 API를 제공하는 방법(온라인상점을 운영하고 있는 독자에게 특히 유용한 기술).
  - 둘째, 동기 API를 사용해야 할 때 코드를 비블록으로 만드는 방법, 그리고 두 개의 비동기 동작을 파이프라인으로 만드는 방법과 두 개의 동작 결과를 하나의 비동기 계산으로 합치는 방법.
    - 예를 들어 온라인상점에서 우리가 사려는 물건에 대응하는 할인 코드를 반환한다고 가정하면, 우리는 다른 원격 할인 서비스에 접근해서 할인 코드에 해당하는 할인율을 찾아야 한다. 그래야 원래 가격에 할인율을 적용해서 최종 결과를 계산할 수 있다.
  - 셋째, 비동기 동작의 완료에 대응하는 방법
    - 모든 상점에서 가격 정보를 얻을 때까지 기다리는 것이 아니라 각 상점에서 가격 정보를 얻을 때마다 즉시 최저가격을 찾는 애플리케이션을 갱신하는 방법(그렇지 않으면 서버가 다운되는 등 문제가 발생했을 때 사용자에게 검은 화면만 보여주게 될 수 있다).

>#### 동기 API와 비동기 API
>
>전통적인 **동기 API**에서는 메서드를 호출한 다음에 메서드가 계산을 완료할 때까지 기다렸다가 메서드가 반환되면 호출자는 반환된 값으로 계속 다른 동작을 수행한다.
>호출자와 피호출자가 각각 다른 스레드에서 실행되는 상황이었더라도 호출자는 피호출자의 동작 완료를 기다렸을 것이다.
>이처럼 동기 API를 사용하는 상황을 **블록 호출(blocking call)**이라고 한다. 
>
>반면 **비동기 API**에서는 메서드가 즉시 반환되며 끝내지 못한 나머지 작업을 호출자 스레드와 동기적으로 실행될 수 있도록 다른 스레드에 할당한다.
>이와 같은 비동기 API를 사용하는 상황을 **비블록 호출(non-blocking call)**이라고 한다.
>다른 스레드에 할당된 나머지 계산 결과는 콜백 메서드를 호출해서 전달하거나 호출자가 '계산 결과가 끝날 때까지 기다림' 메서드를 추가로 호출하면서 전달된다.
>주로 I/O 시스템 프로그래밍에서 이와 같은 방식으로 동작을 수행한다. 즉, 계산 동작을 수행하는 동안 비동기적으로 디스크 접근을 수행한다.
>그리고 더 이상 수행할 동작이 없으면 디스크 블록이 메모리로 로딩될 때까지 기다린다.

---

## 16.2 비동기 API 구현
최저가격 검색 애플리케이션을 구현하기 위해 먼저 각각의 상점에서 제공해야 하는 API부터 정의한다.
다음은 제품명에 해당하는 가격을 반환하는 메서드 정의 코드다.

```java
public class Shop {
    public double getPrice(String product) {
        // 구현해야 함
    }
}
```

`getPrice` 메서드는 상점의 데이터베이스를 이용해서 가격 정보를 얻는 동시에 다른 외부 서비스에도 접근할 것이다.
여기서는 실제 호출할 서비스까지 구현할 수 없으므로 이처럼 오래 걸리는 작업을 다음처럼 `delay`라는 메서드로 대체할 것이다.
`delay`는 인위적으로 1초를 지연시키는 메서드다.

##### 예제 16-2
```java
    public static void delay() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
```

위에서 구현한 `delay`를 이용해서 지연을 흉내 낸 다음에 임의의 계산값을 반환하도록 `getPrice`를 구현할 수 있다.
아래 코드에서 볼 수 있는 것처럼 제품명에 `charAt`을 적용해서 임의의 계산값을 반환한다.

##### 예제 16-3 getPrice 메서드의 지연 흉내 내기
```java
    public double getPrice(String product) {
        return calculatePrice(product);
    }

    private double calculatePrice(String product) {
        delay();
        return random.nextDouble() * product.charAt(0) + product.charAt(1);
    }
```

사용자가 이 API(최저가격 검색 애플리케이션)를 호출하면 비동기 동작이 완료될 때까지 1초동안 블록된다.
최저가격 검색 애플리케이션에서 위 메서드를 사용해서 네트워크상의 모든 온라인상점의 가격을 검색해야 하므로 블록 동작은 바람직하지 않다.
물론 동기 API를 비동기적으로 소비하는 방법도 있다. 예제에서는 편의상 사용자가 편리하게 이용할 수 있도록 비동기 API를 만들기로 결정했다고 가정한다.

### 16.2.1 동기 메서드를 비동기 메서드로 전환
동기 메서드 `getPrice`를 비동기 메서드로 변환하려면 다음 코드처럼 먼저 이름(`getPriceAsync`)과 반환값을 바꿔야 한다.

```java
public Future<Double> getPriceAsync(String product) { ... }
```

- 자바 5부터 비동기 계산의 결과를 표현할 수 있는 `java.util.concurrent.Future` 인터페이스를 제공한다(즉, 호출자 스레드가 블록되지 않고 다른 작업을 실행할 수 있다).
- 간단히 말해, `Future`는 결과값의 핸들일 뿐이며 계산이 완료되면 `get` 메서드로 결과를 얻을 수 있다.
- `getPriceAsync` 메서드는 즉시 반환되므로 호출자 스레드는 다른 작업을 수행할 수 있다.
- 자바 8의 새로운 `CompletableFuture` 클래스는 다음 예제에서 보여주는 것처럼 `getPriceAsync`를 쉽게 구현하는 데 도움이 되는 기능을 제공한다.

##### 예제 16-4 getPriceAsync 메서드 구현
```java
    public Future<Double> getPriceAsync(String product) {
        CompletableFuture<Double> futurePrice = new CompletableFuture<>(); // 계산 결과를 포함할 CompletableFuture을 생성한다.
        new Thread(() -> {
            double price = calculatePrice(product); // 다른 스레드에서 비동기적으로 계산을 수행한다.
            futurePrice.complete(price); // 오랜 시간이 걸리는 계산이 완료되면 Future에 값을 설정한다.
        }).start();
        return futurePrice; // 계산 결과가 완료되길 기다리지 않고 Future를 반환한다.
    }
```

위 코드에서 비동기 계산과 완료 결과를 포함하는 `CompletableFuture` 인스턴스를 만들었다.
그리고 실제 가격을 계산할 다른 스레드를 만든 다음에 오래 걸리는 계산 결과를 기다리지 않고 결과를 포함할 `Future` 인스턴스를 바로 반환했다.
요청한 제품의 가격 정보가 도착하면 `complete` 메서드를 이용해서 `CompletableFuture`를 종료할 수 있다.
다음 코드에서 보여주는것처럼 클라이언트는 `getPriceAsync`를 활용할 수 있다.

##### 예제 16-5 비동기 API 사용
```java
        Shop shop = new Shop("bestShop");
        long start = System.nanoTime();
        Future<Double> futurePrice = shop.getPriceAsync("my favorite product"); // 상점에 제품가격 정보 요청
        long invocationTime = ((System.nanoTime() - start) / 1_000_000);
        System.out.println("Invocation returned after " + invocationTime + " msecs");

        // 제품의 가격을 계산하는 동안
        doSomethingElse();

        // 다른 상점 검색 등 다른 작업 수행
        try {
            double price = futurePrice.get();// 가격 정보가 있으면 Future에서 가격 정보를 읽고, 가격 정보가 없으면 가격 정보를 받을 때까지 블록한다.
            System.out.printf("Price is %.2f%n", price);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        long retrievalTime = ((System.nanoTime() - start) / 1_000_000);
        System.out.println("Price returned after " + retrievalTime + " msecs");
```

- 클라이언트는 특정 제품의 가격 정보를 상점에 요청한다.
- 상점은 비동기 API를 제공하므로 즉시 `Future`를 반환한다.
- 클라이언트는 반환된 `Future`를 이용해서 나중에 결과를 얻을 수 있다.
- 그 사이 클라이언트는 다른 상점에 가격 정보를 요청하는등 첫 번째 상점의 결과를 기다리면서 대기하지 않고 다른 작업을 처리할 수 있다.
- 나중에 클라이언트가 특별히 할 일이 없으면 `Future`의 `get` 메서드를 호출한다. 이때 `Future`가 결과값을 가지고 있다면 `Future`에 포함된 값을 읽거나 아니면 값이 계산될 때까지 블록한다.

다음은 `[예제 16-5]`의 결과다.

```java
Invocation returned after 3 msecs
Price is 224.93
Price returned after 1032 msecs
```

가격 계산이 끝나기전에 `getPriceAsync`가 반환된다는 사실을 확인할 수 있다.
16.4절에서는 클라이언트가 블록되는 상황을 거의 완벽하게 회피하는 방법을 설명한다.
즉, 블록하지 않고 `Future`의 작업이 끝났을 때만 이를 통지받으면서 람다 표현식이나 메서드 참조로 정의된 콜백 메서드를 실행한다.
우선은 비동기 태스크를 실행하면서 발생하는 에러를 올바로 관리하는 방법을 살펴본다.

### 16.2.2 에러 처리 방법
- 예외가 발생하면 해당 스레드에만 영향을 미친다.
- 즉, 에러가 발생해도 가격 계산은 계속 진행되며 일의 순서가 꼬인다.
- 결과적으로 클라이언트는 `get` 메서드가 반환될 떄까지 영원히 기다리게 될 수도 있다.
- 클라이언트는 타임아웃값을 받는 `get` 메서드의 오버로드 버전을 만들어 이 문제를 해결할 수 있다.
- 이처럼 블록 문제가 발생할 수 있는 상황에서는 타임아웃을 활용하는 것이 좋다.
- 그래야 문제가 발생했을 때 클라이언트가 영원히 블록되지 않고 타임아웃 시간이 지나면 `TimeoutException`을 받을 수 있다.
- 이때 제품가격 계산에 왜 에러가 발생했는지 알 수 있는 방법이 없다.
- 따라서 `completeExceptionally` 메서드를 이용해서 `CompletableFuture` 내부에서 발생한 예외를 클라이언트로 전달해야 한다.

다음은 `[예제 16-4]`의 문제점을 개선한 코드다.

##### 예제 16-6 CompletableFuture 내부에서 발생한 에러 전파
```java
    public Future<Double> getPriceAsync(String product) {
        CompletableFuture<Double> futurePrice = new CompletableFuture<>();
        new Thread(() -> {
            try {
                double price = calculatePrice(product);
                futurePrice.complete(price); // 계산이 정상적으로 종료되면 Future에 가격 정보를 저장한채로 Future를 종료한다.
            } catch (Exception e) {
                futurePrice.completeExceptionally(e); // 도중에 문제가 발생하면 발생한 에러를 포함시켜 Future를 종료한다.
            }
        }).start();
        return futurePrice;
    }
```

이제 클라이언트는 가격 계산 메서드에서 발생한 예외 파라미터를 포함하는 `ExecutionException`을 받게 된다.

#### 팩토리 메서드 supplyAsync로 CompletableFuture 만들기
직접 `CompletableFuture`를 만들지 않고 간단하게 `CompletableFuture`를 만드는 방법도 있다. 예를 들어 `[예제 16-4]`의 `getPriceAsync` 메서드를 다음처럼 간단하게 한 행으로 구현할 수 있다.

##### 예제 16-7 팩토리 메서드 supplyAsync로 CompletableFuture 만들기
```java
    public Future<Double> getPriceAsync(String product) {
        return CompletableFuture.supplyAsync(() -> calculatePrice(product));
    }
```

- `supplyAsync` 메서드는 `Supplier`를 인수로 받아서 `CompletableFuture`를 반환한다.
- `CompletableFuture`는 `Supplier`를 실행해서 비동기적으로 결과를 생성한다.
- `ForkJoinPool`의 `Executor` 중 하나가 `Supplier`를 실행할 것이다.
- 두 번째 인수를 받는 오버로드 버전의 `supplyAsync` 메서드를 이용해서 다른 `Executor`를 지정할 수 있다.
- 결국 모든 다른 `CompletableFuture`의 팩토리 메서드에 `Executor`를 선택적으로 전달할 수 있다.

`[예제 16-7]`의 `getPriceAsync` 메서드가 반환하는 `CompletableFuture`는 `[예제 16-6]`에서 만든 `CompletableFuture`와 똑같다. 즉, 둘 다 같은 방법으로 에러를 관리한다.

---

## 16.3 비블록 코드 만들기
우리는 동기 API를 이용해서 최저가격 검색 애플리케이션을 개발해야 한다. 다음과 같은 상점 리스트가 있다고 가정하자.
```java
    List<Shop> shops = Arrays.asList(
            new Shop("BestPrice"),
            new Shop("LetsSaveBig"),
            new Shop("MyFavoriteShop"),
            new Shop("BuyItAll")
    );
```

그리고 다음처럼 제품명을 입력하면 상점 이름과 제품가격 문자열 정보를 포함하는 `List`를 반환하는 메서드를 구현해야 한다.
```java
public List<String> findPrices(String product);
```

스트림 기능을 이용하여 아래와 같이 원하는 동작을 구현할 수 있다.

##### 예제 16-8 모든 상점에 순차적으로 정보를 요청하는 findPrices
```java
    public List<String> findPrices(String product) {
        return shops.stream()
                .map(shop -> String.format("%s price is %.2f", shop.getName(), shop.getPrice(product)))
                .collect(toList());
    }
```

##### 예제 16-9 findPrices의 결과와 성능 확인
```java
        Shops shops = new Shops();
        long start = System.nanoTime();
        System.out.println(shops.findPrices("myPhone27S"));
        long duration = ((System.nanoTime() - start) / 1_000_000);
        System.out.println("Done in " + duration + " msecs");
```

다음은 예제 실행 결과다.
```
[BestPrice price is 171.56, LetsSaveBig price is 162.60, MyFavoriteShop price is 220.16, BuyItAll price is 204.03]
Done in 4044 msecs
```

네 개의 상점에서 가격을 검색하는 동안 각각 1초의 대기시간이 있으므로 전체 가격 검색 결과는 4초보다 조금 더 걸린다. 어떻게 성능을 개선할 수 있을까?

### 16.3.1 병렬 스트림으로 요청 병렬화하기
병렬 스트림을 이용해서 순차 계산을 병렬로 처리해서 성능을 개선할 수 있다.

##### 예제 16-10 findPrices 메서드 병렬화
```java
    public List<String> findPrices(String product) {
        return shops.parallelStream()
                .map(shop -> String.format("%s price is %.2f", shop.getName(), shop.getPrice(product)))
                .collect(toList());
    }
```

4개의 상점에서 병렬로 검색이 진행되므로 아래와 같이 성능이 향상된 것을 볼 수 있다(4초 -> 1초).
```
[BestPrice price is 201.28, LetsSaveBig price is 195.92, MyFavoriteShop price is 225.75, BuyItAll price is 198.68]
Done in 1035 msecs
```

여기서 `CompletableFuture` 기능을 활용해서 `findPrices` 메서드의 동기호출을 비동기 호출로 바꾸면 이를 더 개선할 수 있다.

### 16.3.2 CompletableFuture로 비동기 호출 구현하기
팩토리 메서드 `supplyAsync`로 `CompletableFuture`를 만들 수 있다.

```java
        List<CompletableFuture<String>> priceFutures = shops.stream()
                .map(shop -> CompletableFuture.supplyAsync(() -> String.format("%s price is %.2f", shop.getName(), shop.getPrice(product))))
                .collect(toList());
```

- 위 코드로 `CompletableFuture`를 포함하는 리스트 `List<CompletableFuture<String>>`를 얻을 수 있다.
- 리스트의 `CompletableFuture`는 각각 계산 결과가 끝난 상점의 이름 문자열을 포함한다.
- 하지만 우리가 재구현하는 `findPrices` 메서드의 반환 형식은 `List<String>`이므로 모든 `CompletableFuture`의 동작이 완료되고 결과를 추출한 다음에 리스트를 반환해야 한다.
- 두번째 `map` 연산을 `List<CompletableFuture<String>>`에 적용할 수 있다. 즉, 리스트의 모든 `CompletableFuture`에 `join`을 호출해서 모든 동작이 끝나기를 기다린다.
- `CompletableFuture`의 `join` 메서드는 `Future` 인터페이스의 `get` 메서드와 같은 의미를 갖는다. 다만 `join`은 아무 예외도 발생시키지 않는다는 점이 다르다.
- 따라서 두 번째 `map`의 람다 표현식을 `try/catch`로 감쌀 필요가 없다.

##### 예제 16-11 CompletableFuture로 findPrices 구현하기
```java
    public List<String> findPrices(String product) {
        List<CompletableFuture<String>> priceFutures = shops.stream()
                .map(shop -> CompletableFuture.supplyAsync( // CompletableFuture로 각각의 가격을 비동기적으로 계산한다.
                        () -> String.format("%s price is %.2f", shop.getName(), shop.getPrice(product))))
                .collect(toList());
        return priceFutures.stream()
                .map(CompletableFuture::join) // 모든 비동기 동작이 끝나길 기다린다.
                .collect(toList());
    }
```

- 두 `map` 연산을 하나의 스트림 처리 파이프라인으로 처리하지 않고 두 개의 스트림 파이프라인으로 처리했다.
- 스트림 연산은 게으른 특성이 있으므로 하나의 파이프라인으로 연산을 처리했다면 모든 가격 정보 요청 동작이 동기적, 순차적으로 이루어지는 결과가 된다.
- `CompletableFuture`로 각 상점의 정보를 요청할 때 기존 요청 작업이 완료되어야 `join`이 결과를 반환하면서 다음 상점으로 정보를 요청할 수 있기 때문이다.

##### 그림 16-2 스트림의 게으름 때문에 순차 계산이 일어나는 이유와 순차 계산을 회피하는 방법
![](https://images.velog.io/images/banjjoknim/post/088e5d9b-dba3-4723-bff8-9aa400a43781/16-2.%20%EC%8A%A4%ED%8A%B8%EB%A6%BC%EC%9D%98%20%EA%B2%8C%EC%9C%BC%EB%A6%84%20%EB%95%8C%EB%AC%B8%EC%97%90%20%EC%88%9C%EC%B0%A8%20%EA%B3%84%EC%82%B0%EC%9D%B4%20%EC%9D%BC%EC%96%B4%EB%82%98%EB%8A%94%20%EC%9D%B4%EC%9C%A0%EC%99%80%20%EC%88%9C%EC%B0%A8%20%EA%B3%84%EC%82%B0%EC%9D%84%20%ED%94%BC%ED%95%98%EB%8A%94%20%EB%B0%A9%EB%B2%95.png)

- `[그림 16-2]`의 윗부분은 순차적으로 평가를 진행하는 단일 파이프라인 스트림 처리 과정을 보여준다(점선으로 표시된 부분).
- 즉, 이전 요청의 처리가 완전히 끝난 다음에 새로 만든 `CompletableFuture`가 처리된다.
- 반면 아래쪽은 우선 `CompletableFuture`를 리스트로 모은 다음에 다른 작업과는 독립적으로 각자의 작업을 수행하는 모습을 보여준다.

`[예제 16-11]`의 코드를 이용해서 세 번째 버전의 `findPrices` 성능을 확인하면 다음과 같은 결과를 얻을 수 있다.
```
[BestPrice price is 158.79, LetsSaveBig price is 138.62, MyFavoriteShop price is 185.18, BuyItAll price is 180.33]
Done in 2059 msecs
```

`[예제 16-8]`의 순차적인, 블록 방식의 구현에 비해서는 빨라졌지만 병렬 스트림을 사용한 구현보다는 두 배나 느리다.
순차 스트림 버전의 코드를 조금만 고쳐서 병렬 스트림을 만들 수 있다는 사실을 생각해야 한다.
코드를 실행하는 기기가 네 개의 스레드를 병렬로 실행할 수 있는 기기라는 점에 착안해서 이 문제를 좀 더 고민해본다
(만약 네 개 이상의 스레드를 병렬로 수행할 수 있는 기기를 사용한다면 더 많은 상점을 처리하는 상황이어야 여기서 보여주는 것과 비슷한 결과를 얻을 수 있다).

### 16.3.3 더 확장성이 좋은 해결 방법
- 병렬 스트림 버전의 코드는 정확히 네 개의 상점에 하나의 스레드를 할당해서 네 개의 작업을 병렬로 수행하면서 검색 시간을 최소화할 수 있었다.
- 만약 검색해야하는 다섯 번째 상점이 추가된다면?

다음 출력 결과에서 보여주는 것처럼 순차 버전에서는 시간이 1초 정도 늘어난다.
```
[BestPrice price is 176.08, LetsSaveBig price is 136.19, MyFavoriteShop price is 202.81, BuyItAll price is 143.47, easyShop price is 121.02]
Done in 5048 msecs -> 순차 스트림을 이용한 프로그램 출력 결과
```

병렬 스트림 버전에서는 네 개의 상점을 검색하느라 네 개의 모든 스레드(일반적으로 스레드 풀에서 제공하는 스레드 수는 4개)가 사용된 상황이므로
다섯 번째 상점을 처리하는 데 추가로 1초 이상 소요된다. 즉, 네 개의 스레드 중 누군가가 작업을 완료해야 다섯 번째 질의를 수행할 수 있다.
```
[BestPrice price is 198.88, LetsSaveBig price is 215.64, MyFavoriteShop price is 207.88, BuyItAll price is 228.21, BuyItAll price is 158.84, BuyItAll price is 165.08, BuyItAll price is 130.34, BuyItAll price is 215.70, easyShop price is 155.05]
Done in 2177 msecs -> 병렬 스트림을 이용한 프로그램 출력 결과
```

`CompletableFuture` 버전에서는 어떤 일이 일어날까? 다음은 `CompletableFuture` 버전의 출력 결과다.
```
[BestPrice price is 198.88, LetsSaveBig price is 215.64, MyFavoriteShop price is 207.88, BuyItAll price is 228.21, BuyItAll price is 158.84, BuyItAll price is 165.08, BuyItAll price is 130.34, BuyItAll price is 215.70, easyShop price is 155.05]
Done in 2006 msecs -> CompletableFuture를 이용한 프로그램 출력 결과
```

- `CompletableFuture` 버전이 병렬 스트림 버전보다 아주 조금 빠르다.
- 두 가지 버전 모두 내부적으로 `Runtime.getRuntime().availableProcessors()`가 반환하는 스레드 수를 사용하면서 비슷한 결과가 된다.
- 결과적으로는 비슷하지만 `CompletableFuture`는 병렬 스트림 버전에 비해 작업에 이용할 수 있는 다양한 `Executor`를 지정할 수 있다는 장점이 있다.
- 따라서 `Executor`로 스레드 풀의 크기를 조절하는 등 애플리케이션에 맞는 최적화된 설정을 만들 수 있다.

### 16.3.4 커스텀 Executor 사용하기
애플리케이션에 실제로 필요한 작업량을 고려한 풀에서 관리하는 스레드 수에 맞게 `Executor`를 만들 수 있으면 좋을 것이다.
풀에서 관리하는 스레드 수를 어떻게 결정할 수 있을까?

>#### 스레드 풀 크기 조절
>
>『자바 병렬 프로그래밍(Java Concurrency in Practice』(브라이언 게츠 공저)에서는 스레드 풀의 최적값을 찾는 방법을 제안한다.
>스레드 풀이 너무 크면 CPU와 메모리 자원을 서로 경쟁하느라 시간을 낭비할 수 있다.
>반면 스레드 풀이 너무 작으면 CPU의 일부 코어는 활용되지 않을 수 있다.
>게츠는 다음 공식으로 대략적인 CPU 활용 비율을 계산할 수 있다고 제안한다.
>
>N<sub>threads</sub> = N<sub>CPU</sub> * U<sub>CPU</sub> * (1 + W/C)
>
>공식에서 N<sub>CPU</sub>, U<sub>CPU</sub>, W/C는 각각 다음을 의미한다.
>
>- N<sub>CPU</sub>는 `Runtime.getRuntime().availableProcessors()`가 반환하는 코어 수
>- U<sub>CPU</sub>는 0과 1 사이의 값을 갖는 CPU 활용 비율
>- W/C는 대기시간과 계산시간의 비율

- 우리 애플리케이션은 상점의 응답을 대략 99퍼센트의 시간만큼 기다리므로 W/C의 비율을 100으로 간주할 수 있다.
- 즉, 대상 CPU 활용률이 100퍼센트라면 400스레드를 갖는 풀을 만들어야 함을 의미한다.
- 하지만 상점 수보다 많은 스레드를 가지고 있어봐야 사용할 가능성이 전혀 없으므로 이는 낭비일 뿐이다.
- 따라서 한 상점에 하나의 스레드가 할당될 수 있도록, 즉 가격 정보를 검색하려는 상점 수만큼 스레드를 갖도록 `Executor`를 설정한다.
- 스레드 수가 너무 많으면 오히려 서버가 크래시될 수 있으므로 하나의 `Executor`에서 사용할 스레드의 최대 개수는 100 이하로 설정하는 것이 바람직하다.

##### 예제 16-12 우리의 최저가격 검색 애플리케이션에 맞는 커스텀 Executor
```java
    private final Executor executor = Executors.newFixedThreadPool(Math.min(shops.size(), 100), // 상점 수만큼의 스레드를 갖는 풀을 생성한다(스레드 수의 범위는 0과 100 사이).
            new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread();
            t.setDaemon(true); // 프로그램 종료를 방해하지 않는 데몬 스레드를 사용한다.
            return t;
        }
    });
```

- 우리가 만드는 풀은 **데몬 스레드(daemon thread)**를 포함한다.
- 자바에서 일반 스레드가 실행 중이면 자바 프로그램은 종료되지 않는다.
- 따라서 어떤 이벤트를 한없이 기다리면서 종료되지 않는 일반 스레드가 있으면 문제가 될 수 있다.
- 반면 데몬 스레드는 자바 프로그램이 종료될 때 강제로 실행이 종료될 수 있다. 두 스레드의 성능은 같다.
- 이제 새로운 `Executor`를 팩토리 메서드 `supplyAsync`의 두 번째 인수로 전달할 수 있다.

예를 들어 다음 코드처럼 제품가격 정보를 얻는 `CompletableFuture`를 만들 수 있다.
```java
CompletableFuture.supplyAsync(() -> shop.getName() + " price is " + shop.getPrice(product), executor));
```

이렇게 만든 `CompletableFuture` 버전의 코드 성능은 400개의 상점까지 1초 가량의 성능을 유지할 수 있다.
결국 애플리케이션의 특성에 맞는 `Executor`를 만들어 `CompletableFuture`를 활용하는 것이 바람직하다는 사실을 확인할 수 있다.
비동기 동작을 많이 사용하는 상황에서는 지금 살펴본 기법이 가장 효과적일 수 있다.

>#### 스트림 병렬화와 CompletableFuture 병렬화
>
>`CompletableFuture`를 이용하면 전체적인 계산이 블록되지 않도록 스레드 풀의 크기를 조절할 수 있다.
>
>다음을 참고하면 어떤 병렬화 기법을 사용할 것인지 선택하는 데 도움이 된다.
>
>- I/O가 포함되지 않은 계산 중심의 동작을 실행할 때는 스트림 인터페이스가 가장 구현하기 간단하며 효율적일 수 있다(모든 스레드가 계산 작업을 수행하는 상황에서는 프로세서 코어 수 이상의 스레드를 가질 필요가 없다).
>- 반면 작업이 I/O를 기다리는 작업을 병렬로 실행할 때는 `CompletableFuture`가 더 많은 유연성을 제공하며 대기/계산(W/C)의 비율에 적합한 스레드 수를 설정할 수 있다. 특히 스트림의 게으른 특성 때문에 스트림에서 I/O를 실제로 언제 처리할지 예측하기 어려운 문제도 있다. 

지금까지는 `Future` 내부에서 수행하는 작업이 모두 일회성 작업이었다. 다음으로는 선언형으로 여러 비동기 연산을 `CompletableFuture`로 파이프라인화하는 방법을 살펴본다.

---

## 16.4 비동기 작업 파이프라인 만들기
우리와 계약을 맺은 모든 상점이 하나의 할인 서비스를 사용하기로 했다고 가정한다. 이때, 할인 서비스에서는 서로 다른 할인율을 제공하는 다섯 가지 코드를 제공한다.

##### 예제 16-13 enum으로 할인 코드 정의하기
```java
public class Discount {
    public enum Code {
        NONE(0),
        SILVER(5),
        GOLD(10),
        PLATINUM(15),
        DIAMOND(20);

        private final int percentage;

        Code(int percentage) {
            this.percentage = percentage;
        }
    }
    // 생략된 Discount 클래스 구현은 [예제 16-14] 참조
}
```

또한 상점에서 `getPrice` 메서드의 결과 형식도 바꾸기로 했다. 이제 `getPrice`는 `ShopName:price:DiscountCode` 형식의 문자열을 반환한다.
예제에서는 미리 계산된 임의의 가격과 임의의 `Discount.Code`를 반환할 것이다.

```java
    public String getPrice(String product) {
        double price = calculatePrice(product);
        Discount.Code code = Discount.Code.values()[random.nextInt(Discount.Code.values().length)];
        return String.format("%s:%.2f:%s", name, price, code);
    }

    private double calculatePrice(String product) {
        delay();
        return random.nextDouble() * product.charAt(0) + product.charAt(1);
    }
```

### 16.4.1 할인 서비스 구현
상점에서 제공한 문자열 파싱은 다음처럼 `Quote` 클래스로 캡슐화할 수 있다.

```java
public class Quote {
    private final String shopName;
    private final double price;
    private final Discount.Code discountCode;

    public Quote(String shopName, double price, Discount.Code discountCode) {
        this.shopName = shopName;
        this.price = price;
        this.discountCode = discountCode;
    }

    public static Quote parse(String s) {
        String[] split = s.split(":");
        String shopName = split[0];
        double price = Double.parseDouble(split[1]);
        Discount.Code discountCode = Discount.Code.valueOf(split[2]);
        return new Quote(shopName, price, discountCode);
    }

    public String getShopName() {
        return shopName;
    }

    public double getPrice() {
        return price;
    }

    public Discount.Code getDiscountCode() {
        return discountCode;
    }
}
```

상점에서 얻은 문자열을 정적 팩토리 메서드 `parse`로 넘겨주면 상점 이름, 할인전 가격, 할인된 가격 정보를 포함하는 `Quote` 클래스 인스턴스가 생성된다.
`Discount` 서비스에서는 `Quote` 객체를 인수로 받아 할인된 가격 문자열을 반환하는 `applyDiscount` 메서드도 제공한다.

##### 예제 16-14 Discount 서비스
```java
public class Discount {
    public enum Code {
        // 소스 생략
    }

    public static String applyDiscount(Quote quote) {
        return quote.getShopName() + " price is " +
                Discount.apply(quote.getPrice(), // 기존 가격에 할인 코드를 적용한다.
                        quote.getDiscountCode());
    }

    private static double apply(double price, Code code) {
        delay(); // Discount 서비스의 응답 지연을 흉내 낸다.
        return price * (100 - code.percentage) / 100;
    }
}
```

### 16.4.2 할인 서비스 사용
`Discount`는 원격 서비스이므로 다음 코드에서 보여주는 것처럼 1초의 지연을 추가한다.

##### 예제 16-15 Discount 서비스를 이용하는 가장 간단한 findPrices 구현
```java
    public List<String> findPrices(String product) {
        return shops.stream()
                .map(shop -> shop.getPrice(product)) // 각 상점에서 할인된 가격 얻기
                .map(Quote::parse) // 상점에서 반환한 문자열을 Quote 객체로 변환한다.
                .map(Discount::applyDiscount) // Discount 서비스를 이용해서 각 Quote에 할인을 적용한다.
                .collect(toList());
    }
```

세 개의 `map` 연산을 상점 스트림에 파이프라인으로 연결해서 원하는 결과를 얻었다.

- 첫 번째 연산에서는 각 상점을 요청한 제품의 가격과 할인 코드로 변환한다.
- 두 번째 연산에서는 이들 문자열을 파싱해서 `Quote` 객체를 만든다.
- 세 번째 연산에서는 원격 `Discount` 서비스에 접근해서 최종 할인가격을 계산하고 가격에 대응하는 상점 이름을 포함하는 문자열을 반환한다.

다음은 결과다.
```
[BestPrice price is 112.36149999999999, LetsSaveBig price is 136.8665, MyFavoriteShop price is 130.93, BuyItAll price is 137.731, easyShop price is 206.70100000000002]
Done in 10095 msecs
```

- 순차적으로 다섯 상점에 가격 정보를 요청하느라 5초가 소요되었고, 다섯 상점에서 반환한 가격 정보에 할인 코드를 적용할 수 있도록 할인 서비스에 5초가 소요되었다.
- 병렬 스트림을 이용하면 성능을 쉽게 개선할 수 있다. 하지만 병렬 스트림에서는 스트림이 사용하는 스레드 풀의 크기가 고정되어 있어 상점 수가 늘어났을 때처럼 검색 대상이 확장되었을 때 유연하게 대응할 수 없다.
- 따라서 `CompletableFuture`에서 수행하는 태스크를 설정할 수 있는 커스텀 `Executor`를 정의함으로써 CPU 사용을 극대화할 수 있다.

### 16.4.3 동기 작업과 비동기 작업 조합하기
다음은 비동기적으로 재구현한 코드다.

```java
    public List<String> findPrices(String product) {
        List<CompletableFuture<String>> priceFutures = shops.stream()
                .map(shop -> CompletableFuture.supplyAsync(
                        () -> shop.getPrice(product), executor))
                .map(future -> future.thenApply(Quote::parse))
                .map(future -> future.thenCompose(quote ->
                        CompletableFuture.supplyAsync(
                                () -> Discount.applyDiscount(quote), executor)))
                .collect(toList());

        return priceFutures.stream()
                .map(CompletableFuture::join)
                .collect(toList());
    }
```

`[그림 16-3]`는 세 가지 변환 과정을 보여준다. 이번에도 `[예제 16-15]`에서 사용한 세 개의 `map` 연산을 적용한다.
다만 이번에는 `CompletableFuture` 클래스의 기능을 이용해서 이들 동작을 비동기로 만들어야 한다.

##### 그림 16-3 동기 작업과 비동기 작업 조합하기
![](https://images.velog.io/images/banjjoknim/post/c5a462bd-d28a-4aa8-9778-d4734250dc47/16-3.%20%EB%8F%99%EA%B8%B0%20%EC%9E%91%EC%97%85%EA%B3%BC%20%EB%B9%84%EB%8F%99%EA%B8%B0%20%EC%9E%91%EC%97%85%20%EC%A1%B0%ED%95%A9%ED%95%98%EA%B8%B0.png)

#### 가격 정보 얻기
- 팩토리 메서드 `supplyAsync`에 람다 표현식을 전달해서 비동기적으로 상점에서 정보를 조회한다.
- 첫 번째 변환의 결과는 `Stream<CompletableFuture<String>>`이다.
- 각 `CompletableFuture`는 작업이 끝났을 때 해당 상점에서 반환하는 문자열 정보를 포함한다.

#### Quote 파싱하기
- 첫 번째 결과 문자열을 `Quote`로 변환한다. 파싱 동작에서는 원격 서비스나 I/O가 없으므로 원하는 즉시 지연 없이 동작을 수행할 수 있다.
- 따라서 첫 번째 과정에서 생성된 `CompletableFuture`에 `thenApply` 메서드를 호출한 다음에 문자열을 `Quote` 인스턴스로 변환하는 `Function`으로 전달한다.
- `thenApply` 메서드는 `CompletableFuture`가 끝날 때까지 블록하지 않는다는 점을 주의해야 한다.
- 즉, `CompletableFuture`가 동작을 완전히 완료한 다음에 `thenApply` 메서드로 전달된 람다 표현식을 적용할 수 있다.
- 따라서 `CompletableFuture<String>`을 `CompletableFuture<Quote>`로 변환할 것이다.

#### CompletableFuture를 조합해서 할인된 가격 계산하기
- 세 번째 `map` 연산에서는 상점에서 받은 할인전 가격에 원격 `Discount` 서비스에서 제공하는 할인율을 적용해야 한다.
- 이번에는 원격 실행이 포함되므로 이전 두 변환과 다르며 동기적으로 작업을 수행해야 한다(1초의 지연).
- 람다 표현식으로 이 동작을 팩토리 메서드 `supplyAsync`에 전달할 수 있다. 그러면 다른 `CompletableFuture`가 반환된다.
- 결국 두 가지 `CompletableFuture`로 이루어진 연쇄적으로 수행되는 두 개의 비동기 동작을 만들 수 있다.
  - 상점에서 가격 정보를 얻어 와서 Quote로 변환하기
  - 변환된 Quote를 Discount 서비스로 전달해서 할인된 최종가격 획득하기
- 자바 8의 `CompletableFuture API`는 이와 같이 두 비동기 연산을 파이프라인으로 만들 수 있도록 `thenCompose` 메서드를 제공한다.
- `thenCompose` 메서드는 첫 번째 연산의 결과를 두 번째 연산으로 전달한다.
- 즉, 첫 번째 `CompletableFuture`에 `thenCompose` 메서드를 호출하고 `Function`에 넘겨주는 식으로 두 `CompletableFuture`를 조합할 수 있다.
- `Function`은 첫 번째 `CompletableFuture` 반환 결과를 인수로 받고 두 번째 `CompletableFuture`를 반환하는데, 두 번째 `CompletableFuture`는
첫 번째 `CompletableFuture`의 결과를 계산의 입력으로 사용한다.
- 따라서 `Future`가 여러 상점에서 `Quote`를 얻는 동안 메인 스레드는 UI 이벤트에 반응하는 등 유용한 작업을 수행할 수 있다.
- 세 개의 `map` 연산 결과 스트림의 요소를 리스트로 수집하면 `List<CompletableFuture<String>>` 형식의 자료를 얻을 수 있다.
- 마지막으로 `CompletableFuture`가 완료되기를 기다렸다가 `[예제 16-11]`에서 그랬듯이 `join`으로 값을 추출할 수 있다.
- `CompletableFuture` 클래스의 다른 메서드처럼 `[예제 16-16]`에서 사용한 `thenCompose` 메서드도 `Async`로 끝나는 버전이 존재한다.
- `Async`로 끝나지 않는 메서드는 이전 작업을 수행한 스레드와 같은 스레드에서 작업을 실행함을 의미한다.
- `Async`로 끝나는 메서드는 다음 작업이 다른 스레드에서 실행되도록 스레드 풀로 작업을 제출한다.
- 여기서 두 번째 `CompletableFuture`의 결과는 첫 번째 `CompletableFuture`에 의존하므로 두 `CompletableFuture`를 하나로 조합하든 `Async` 버전의
메서드를 사용하든 최종 결과나 개괄적인 실행시간에는 영향을 미치지 않는다.
- 따라서 스레드 전환 오버헤드가 적게 발생하면서 효율성이 좀 더 좋은 `thenCompose`를 사용했다.

### 16.4.4 독립 CompletableFuture와 비독립 CompletableFuture 합치기
- 실전에서는 독립적으로 실행된 두 개의 `CompletableFuture` 결과를 합쳐야 하는 상황이 종종 발생한다.
- 물론 첫 번째 `CompletableFuture`의 동작 완료와 관계없이 두 번째 `CompletableFuture`를 실행할 수 있어야 한다.
- 이런 상황에서는 `thenCombine` 메서드를 사용한다. `thenCombine` 메서드는 `BiFunction`을 두 번째 인수로 받는다.
- `BiFunction`은 두 개의 `CompletablFuture` 결과를 어떻게 합칠지 정의한다.
- `thenCompose`와 마찬가지로 `thenCombine` 메서드에도 `Async` 버전이 존재한다.
- `thenCombineAsync` 메서드에서는 `BiFunction`이 정의하는 조합 동작이 스레드 풀로 제출되면서 별도의 태스크에서 비동기적으로 수행된다.

예제에서는 한 온라인상점이 유로(EUR) 가격 정보를 제공하는데, 고객에게는 항상 달러(USD) 가격을 보여줘야 한다.
우리는 주어진 상품의 가격을 상점에 요청하는 한편 원격 환율 교환 서비스를 이용해서 유로와 달러의 현재 환율을 비동기적으로 요청해야 한다.
두 가지 데이터를 얻었으면 가격에 환율을 곱해서 결과를 합칠 수 있다.
이렇게 해서 두 `CompletableFuture`의 결과가 생성되고 `BiFunction`으로 합쳐진 다음에 세 번째 `CompletableFuture`를 얻을 수 있다.

##### 예제 16-17 독립적인 두 개의 CompletableFuture 합치기
```java
        Future<Double> futurePriceInUSD = CompletableFuture.supplyAsync(() -> this.getPrice(product)) // 제품가격 정보를 요청하는 첫 번째 태스크를 생성한다.
                .thenCombine(
                        CompletableFuture.supplyAsync(
                                () -> exchangeService.getRate(Money.EUR, Money.USD)), // USD, EUR의 환율 정보를 요청하는 독립적인 두 번째 태스크를 생성한다.
                        (price, rate) -> price * rate // 두 결과를 곱해서 가격과 환율 정보를 합친다.
                );
```

여기서 합치는 연산은 단순한 곱셈이므로 별도의 태스크에서 수행하여 자원을 낭비할 필요가 없으므로 `thenCombineAsync` 대신 `thenCombine` 메서드 사용한다.
`그림 [16-4]`는 `[예제 16-17]`에서 생성된 태스크가 풀의 스레드에서 어떻게 실행되고 결과가 합쳐지는지 보여준다.

##### 그림 16-4 독립적인 두 개의 비동기 태스크 합치기
![](https://images.velog.io/images/banjjoknim/post/852dfaf6-e426-4313-ace5-f9691f69836c/16-4.%20%EB%8F%85%EB%A6%BD%EC%A0%81%EC%9D%B8%20%EB%91%90%20%EA%B0%9C%EC%9D%98%20%EB%B9%84%EB%8F%99%EA%B8%B0%20%ED%83%9C%EC%8A%A4%ED%81%AC%20%ED%95%A9%EC%B9%98%EA%B8%B0.png)

### 16.4.5 Future의 리플렉션과 CompletableFuture의 리플렉션
- `CompletableFuture`는 람다 표현식을 사용한다.
- 이미 살펴본 것처럼 람다 덕분에 다양한 동기 태스크, 비동기 태스크를 활용해서 복잡한 연산 수행 방법을 효과적으로 쉽게 정의할 수 있는 선언형 API를 만들 수 있다.
- `[예제 16-17]`을 자바 7로 구현하면서 실질적으로 `CompletableFuture`를 이용했을 때 얻을 수 있는 코드 가독성의 이점을 확인할 수 있다. `[예제 16-18]`은 자바 7 코드다.

##### 예제 16-18 자바 7로 두 Future 합치기
```java
        ExecutorService executor = Executors.newCachedThreadPool(); // 태스크를 스레드 풀에 제출할 수 있도록 ExecutorService를 생성한다.
        final Future<Double> futureRate = executor.submit(new Callable<Double>() {
            @Override
            public Double call() throws Exception {
                return exchangeService.getRate(Money.EUR, Money.USD); // EUR, USD 환율 정보를 가져올 Future를 생성한다.
            }
        });
        Future<Double> futurePriceInUSD = executor.submit(new Callable<Double>() {
            @Override
            public Double call() throws Exception {
                double priceInEUR = shop.getPrice(product); // 두 번째 Future로 상점에서 요청 제품의 가격을 검색한다.
                return priceInEUR * futureRate.get(); // 가격을 검색한 Future을 이용해서 가격과 환율을 곱한다. 
            }
        });
```

- `[예제 16-18]`에서는 `Executor`에 EUR과 USD 간의 환율 검색 외부 서비스를 이용하는 `Callable`을 `submit`의 인수로 전달해서 첫 번째 `Future`를 만들었다.
- 그리고 상점에서 해당 제품의 가격을 EUR로 반환하는 두 번째 `Future`를 만들었다.
- 마지막으로 `[예제 16-17]`에서처럼 EUR 가격 정보에 환율을 곱한다.
- `[예제 16-17]`에서 `thenCombine` 대신 `thenCombineAsync`를 사용하는 것은 `[예제 16-18]`에서 가격과 환율을 곱하는 세 번째 `Future`를 만드는 것과 같다.

### 16.4.6 타임아웃 효과적으로 사용하기
- `Future`의 계산 결과를 읽을 때는 무한정 기다리는 상황이 발생할 수 있으므로 블록을 하지 않는 것이 좋다.
- 자바 9에서는 `CompletableFuture`에서 제공하는 몇 가지 기능을 이용해 이러한 문제를 해결할 수 있다.
- `orTimeout` 메서드는 지정된 시간이 지난 후에 `CompletableFuture`를 `TimeoutException`으로 완료하면서 또 다른 `CompletableFuture`를 반환할 수 있도록 내부적으로 `ScheduledThreadExecutor`를 활용한다.
- 이 메서드를 이용하면 계산 파이프라인을 연결하고 여기서 `TimeoutException`이 발생했을 때 사용자가 쉽게 이해할 수 있는 메시지를 제공할 수 있다.
- `Future`가 3초 후에 작업을 끝내지 못할 경우 `TimeoutException`이 발생하도록 메서드 체인의 끝에 `orTimeout` 메서드를 추가할 수 있다.
- 물론 타임아웃 길이는 필요에 따라 조절할 수 있다.

##### 예제 16-19 CompletableFuture에 타임아웃 추가
```java
        CompletableFuture.supplyAsync(() -> shop.getPrice(product))
                .thenCombine(
                        CompletableFuture.supplyAsync(
                            () -> exchangeService.getRate(Money.EUR, Money.USD)),
                        (price, rate) -> price * rate
                )
                .orTimeout(3, TimeUnit.SECONDS); // 3초 뒤에 작업이 완료되지 않으면 Future가 TimeoutException을 발생시키도록 설정. 자바 9에서는 비동기 타임아웃 관리 기능이 추가됨
```

- 일시적으로 서비스를 이용할 수 없는 상황에서는 꼭 서버에서 얻은 값이 아닌 미리 지정된 값을 사용할 수 있는 상황도 있다.
- 이런 상황에서는 미리 정의한 값을 이용해 연산을 이어갈 수 있다.
- 자바 9에 추가된 `completeOnTimeout` 메서드를 이용하면 이 기능을 쉽게 구현할 수 있다.

##### 예제 16-20 CompletableFuture에 타임아웃이 발생하면 기본값으로 처리
```java
        CompletableFuture.supplyAsync(() -> shop.getPrice(product))
                .thenCombine(
                        CompletableFuture.supplyAsync(
                            () -> exchangeService.getRate(Money.EUR, Money.USD))
                            .completeOnTimeout(DEFAULT_RATE, 1, TimeUnit.SECONDS), // 환전 서비스가 일 초 안에 결과를 제공하지 않으면 기본 환율값을 사용
                        (price, rate) -> price * rate
                )
                .orTimeout(3, TimeUnit.SECONDS);
```

- `orTimeout` 메서드처럼 `completeOnTimeout` 메서드는 `CompletableFuture`를 반환하므로 이 결과를 다른 `CompletableFuture` 메서드와 연결할 수 있다.
- 한 개는 3초 이내에 연산을 마치지 못하는 상황에서 발생하는 타임아웃이고, 다른 타임아웃은 1초 이후에 환율을 얻지 못했을 때 발생한다.
- 하지만 두 번째 타임아웃이 발생하면 미리 지정된 값을 사용한다.

모든 검색 결과가 완료될 때까지 사용자를 기다리게 만들지 말고, 이용할 수 있는 가격 정보는 즉시 사용자에게 보여줄 수 있어야 한다.
따라서 다음으로는 `get`이나 `join`으로 `CompletableFuture`가 완료될 때까지 블록하지 않고 다른 방식으로 `CompletableFuture`의 종료에 대응하는 방법을 설명한다.

---

## 16.5 CompletableFuture의 종료에 대응하는 방법
- 실전에서 사용하는 다양한 원격 서비스는 얼마나 지연될지 예측하기 어렵다. 다양한 지연 요소가 있기 때문이다.
- 따라서 0.5초에서 2.5초 사이의 임의의 지연으로 이를 시뮬레이션한다.

##### 예제 16-21 0.5초에서 2.5초 사이의 임의의 지연을 흉내내는 메서드
```java
    public static void randomDelay() {
        int delay = 500 + random.nextInt(2000);
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
```

이제 모든 상점에서 가격 정보를 제공할 때까지 기다리지 않고 각 상점에서 가격 정보를 제공할 때마다 즉시 보여줄 수 있는 최저가격 검색 애플리케이션을 만들어본다.

### 16.5.1 최저가격 검색 애플리케이션 리팩터링
- 먼저 모든 가격 정보를 포함할 때까지 리스트 생성을 기다리지 않도록 프로그램을 고쳐야 한다.
- 따라서, 상점에 필요한 일련의 연산 실행 정보를 포함하는 `CompletableFuture`의 스트림을 직접 제어해야 한다.

##### 예제 16-22 Future 스트림을 반환하도록 findPrices 메서드 리팩터링하기
```java
    public Stream<CompletableFuture<String>> findPricesStream(String product) {
        return shops.stream()
                .map(shop -> CompletableFuture.supplyAsync(() -> shop.getPrice(product), executor))
                .map(future -> future.thenApply(Quote::parse))
                .map(future -> future.thenCompose(quote -> CompletableFuture.supplyAsync(() -> Discount.applyDiscount(quote), executor)));
    }
```

- 이제 `findPricesStream` 메서드 내부에서 세 가지 `map` 연산을 적용하고 반환하는 스트림에 네 번째 `map` 연산을 적용하도록 한다.
- 새로 추가한 연산은 단순하게 각 `CompletableFuture`에 동작을 **등록**한다.
- `CompletableFuture`에 등록된 동작은 `CompletableFuture`의 계산이 끝나면 값을 소비한다.
- 자바 8의 `CompletableFuture API`는 `thenAccept`라는 메서드로 이 기능을 제공한다.
- `thenAccept` 메서드는 연산 결과르르 소비하는 `Consumer`를 인수로 받는다.

이 예제에서는 할인 서비스에서 반환하는 문자열이 값이다.
이 문자열은 상점 이름과 할인율을 적용한 제품의 가격을 포함한다.
우리가 원하는 동작은 이 값을 출력하는 것이다.

```java
findPricesStream("myPhone").map(f -> f.thenAccept(System.out::println));
```

- `thenCompose`, `thenCombine` 메서드와 마찬가지로 `thenAccept`에도 `thenAcceptAsync`라는 `Async` 버전이 존재한다.
- `thenAcceptAsync` 메서드는 `CompletableFuture`가 완료된 스레드가 아니라 새로운 스레드를 이용해서 `Consumer`를 실행한다.
- 불필요한 콘텍스트 변경은 피하는 동시에 `CompletableFuture`가 완료되는 즉시 응답하는 것이 좋으므로 `thenAcceptAsync`를 사용하지 않는다(오히려 `thenAcceptAsync`를 사용하면 새로운 스레드를 이용할 수 있을 때까지 기다려야 하는 상황이 일어날 수 있다).
- `thenAccept` 메서드는 `CompletableFuture`가 생성한 결과를 어떻게 소비할지 미리 지정했으므로 `CompletableFuture<Void>`를 반환한다.
- 따라서 네 번째 `map` 연산은 `<CompletableFuture<Void>>`를 반환한다. 이제 `CompletableFuture<Void>`가 동작을 끝낼 때까지 딱히 할 수 있는 일이 없으며, 이렇게 우리가 원하는 동작을 구현했다.

만약 가장 느린 상점에서 응답을 받아서 반환된 가격을 출력할 기회를 제공하고 싶다고 가정한다면, 다음 코드에서 보여주는 것처럼 스트림의 모든 `CompletableFuture<Void>`를 배열로 추가하고 실행 결과를 기다려야 한다.

##### 예제 16-23 CompletableFuture 종료에 반응하기
```java
        CompletableFuture[] futures = shops.findPricesStream("myPhone")
                .map(f -> f.thenAccept(System.out::println))
                .toArray(size -> new CompletableFuture[size]);
        CompletableFuture.allOf(futures).join();
```

- 팩토리 메서드 `allOf`는 `CompletableFuture` 배열을 입력으로 받아 `CompletableFuture<Void>`를 반환한다.
- 전달된 모든 `CompletableFuture`가 완료되어야 `CompletableFuture<Void>`가 완료된다.
- 따라서 `allOf` 메서드가 반환하는 `CompletableFuture`에 `join`을 호출하면 원래 스트림의 모든 `CompletableFuture`의 실행 완료를 기다릴 수 있다.
- 이를 이용해서 최저가격 검색 애플리케이션은 '모든 상점이 결과를 반환했거나 타임아웃되었음' 같은 메시지를 사용자에게 보여줌으로써 사용자는 추가로 가격 정보를 기다리지 않아도 된다는 사실을 보여줄 수 있다.

반면 배열의 `CompletableFuture` 중 하나의 작업이 끝나길 기다리는 상황도 있을 수 있다(예를 들어 두 개의 환율 정보 서버에 동시 접근했을 때 한 서버의 응답만 있으면 충분하다).

- 이때는 팩토리 메서드 `anyOf`를 사용한다.
- `anyOf` 메서드는 `CompletableFuture` 배열을 입력으로 받아서 `CompletableFuture<Object>`를 반환한다.
- `CompletableFuture<Object>`는 처음으로 완료한 `CompletableFuture`의 값으로 동작을 완료한다.

### 16.5.2 응용
- 여기서는 `[예제 16-21]`에서 구현한 `randomDelay`로 0.5초에서 2.5초 사이에 임의의 지연을 발생시켜 원격 서비스 호출을 흉내낸다.
- 이제 `[예제 16-23]` 코드를 실행시키면 예전처럼 가격 정보가 지정된 시간에 나타나지 않을 뿐 아니라 상점 가격 정보가 들어오는 대로 결과가 출력된다.

어떤 부분이 달라졌는지 좀 더 명확하게 확인할 수 있도록 각각의 계산에 소요된 시간을 출력하는 부분을 코드에 추가한다.

```java
        long start = System.nanoTime();
        CompletableFuture[] futures = shops.findPricesStream("myPhone27S")
                .map(f -> f.thenAccept(
                        s -> System.out.println(s + " (done in " + ((System.nanoTime() - start) / 1_000_000) + " msecs")))
                .toArray(size -> new CompletableFuture[size]);
        CompletableFuture.allOf(futures).join();
        System.out.println("All shops have now responded in " + ((System.nanoTime() - start) / 1_000_000 + " msecs"));
```

다음은 코드 수행 결과다.

```
BuyItAll price is 184.74 (done in 2005 msecs)
MyFavoriteShop price is 192.72 (done in 2157 msecs)
LetsSaveBig price is 135.58 (done in 3301 msecs)
ShopEasy price is 167.28 (done in 3869 msecs)
BestPrice price is 110.93 (done in 4188 msecs)
All shops have now responded in 4188 msecs
```

임의의 지연이 추가되면 마지막 가격 정보에 비해 처음 가격 정보를 두 배 빨리 얻는다는 것을 출력 결과에서 확인할 수 있다.

---

## 16.6 로드맵
17장에서는 `CompletableFuture(연산 또는 값으로 종료하는 일회성 기법)`의 기능이 한 번에 종료되지 않고 일련의 값을 생산하도록 일반화하는 `자바 9 플로 API`를 살펴본다.

---

## 16.7 마치며
- 한 개 이상의 원격 외부 서비스를 사용하는 긴 동작을 실행할 때는 비동기 방식으로 애플리케이션의 성능과 반응성을 향상시킬 수 있다.
- 우리 고객에게 비동기 API를 제공하는 것을 고려해야 한다. `CompletableFuture`의 기능을 이용하면 쉽게 비동기 API를 구현할 수 있다.
- `CompletableFuture`를 이용할 때 비동기 태스크에서 발생한 에러를 관리하고 전달할 수 있다.
- 동기 API를 `CompletableFuture`로 감싸서 비동기적으로 소비할 수 있다.
- 서로 독립적인 비동기 동작이든 아니면 하나의 비동기 동작이 다른 비동기 동작의 결과에 의존하는 상황이든 여러 비동기 동작을 조립하고 조합할 수 있다.
- `CompletableFuture`에 콜백을 등록해서 `Future`가 동작을 끝내고 결과를 생산했을 때 어떤 코드를 실행하도록 지정할 수 있다.
- `CompletableFuture` 리스트의 모든 값이 완료될 때까지 기다릴지 아니면 첫 값만 완료되길 기다릴지 선택할 수 있다.
- 자바 9에서는 `orTimeout`, `completeOnTimeout` 메서드로 `CompletableFuture`에 비동기 타임아웃 기능을 추가했다.

---
