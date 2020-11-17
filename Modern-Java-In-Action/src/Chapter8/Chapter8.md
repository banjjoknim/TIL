# Chapter8. 컬렉션 API 개선

먼저 작은 리스트, 집합, 맵을 쉽게 만들 수 있도록 자바 9에 새로 추가된 컬렉션 팩토리를 살펴본다. 다음으로 자바 8의 개선 사항으로 리스트와 집합에서 요소를 삭제하거나 바꾸는 관용 패턴을 적용하는 방법을 배운다. 마지막으로 맵 작업과 관련해 추가된 새로운 편리 기능을 살펴본다.

---

## 8.1 컬렉션 팩토리
자바 9에서는 작은 컬렉션 객체를 쉽게 만들 수 있는 몇 가지 방법을 제공한다. 우선 왜 프로그래머에게 이와 같은 기능이 필요한지 살펴본 다음 새 팩토리 메서드를 사용하는 방법을 설명한다.

자바에서는 작은 요소를 포함하는 리스트를 어떻게 만들까? 휴가를 함께 보내려는 친구 이름을 포함하는 그룹을 만들려 한다고 가정하자.

```java
List<String> friends = new ArrayList<>();
friends.add("Raphael");
friends.add("Olivia");
friends.add("Thibaut");
```

새 문자열을 저장하는데도 많은 코드가 필요하다. 다음처럼 `Arrays.asList()` 팩토리 메서드를 이용하면 코드를 간단하게 줄일 수 있다.

```java
List<String> friends = Arrays.asList("Raphael", "Olivia", "Thibaut");
```

고정 크기의 리스트를 만들었으므로 요소를 갱신할 순 있지만 새 요소를 추가하거나 요소를 삭제할 순 없다. 예를 들어 요소를 갱신하는 작업은 괜찮지만 요소를 추가하려 하면 `UnsupportedOperationException`이 발생한다.

```java
List<String> friends = Arrays.asList("Raphael", "Olivia");
friends.set(0, "Richard");
friends.add("Thibaut");
```

>**UnsupportedOperationException 예외 발생**
>
>내부적으로 고정된 크기의 변환할 수 없는 배열로 구현되었기 때문에 이와 같은 일이 일어난다.
>
>그럼 집합은 어떻까? 안타깝게도 `Arrays.asSet()`이라는 팩토리 메서드는 없으므로 다른 방법이 필요하다. 리스트를 인수로 받는 `HashSet` 생성자를 사용할 수 있다.
>
>```java
>Set<String> friends = new HashSet<>(Arrays.asList("Raphael", "Olivia", "Thibaut"));
>```
>또는 다음처럼 스트림 API를 사용할 수 있다.
>
>```java
>Set<String> friends = Stream.of("Raphael", "Olivia", "Thibaut")
>                            .collect(Collectors.toSet());
>```
>하지만 두 방법 모두 매끄럽지 못하며 내부적으로 불필요한 객체 할당을 필요로 한다. 그리고 결과는 변환할 수 없는 집합이라는 사실도 주목하자.
>
>맵은 어떨까? 작은 맵을 만들 수 있는 멋진 방법은 따로 없지만 걱정할 필요가 없다. 자바 9에서 작은 리스트, 집합, 맵을 쉽게 만들 수 있도록 팩토리 메서드를 제공하기 때문이다.
>
>먼저 리스트의 새로운 기능부터 시작해 컬렉션을 만드는 새로운 방법을 살펴본다.

#### 8.1.1 리스트 팩토리

`List.of` 팩토리 메소드를 이용해서 간단하게 리스트를 만들 수 있다.

```java
List<String> friends = List.of("Raphael", "Olivia", "Thibaut");
System.out.println(friends);
```

하지만 `friends` 리스트에 요소를 추가해보면 `java.lang.UnsupportedOperationException`이 발생한다. 사실 변경할 수 없는 리스트가 만들어졌기 때문이다. `set()` 메서드로 아이템을 바꾸려해도 비슷한 예외가 발생한다. 따라서 `set` 메서드로도 리스트를 바꿀 수 없다. 하지만 이런 제약이 꼭 나쁜 것은 아니다. 컬렉션이 의도치 않게 변하는 것을 막을 수 있기 때문이다. 하지만 요소 자체가 변하는 것을 막을 수 있는 방법은 없다. 리스트를 바꿔야 하는 상황이라면 직접 리스트를 만들면 된다. 마지막으로 `null` 요소는 금지하므로 의도치 않은 버그를 방지하고 조금 더 간결한 내부 구현을 달성했다.

데이터 처리 형식을 설정하거나 데이터를 변환할 필요가 없다면 사용하기 간편한 팩토리 메서드를 이용할 것을 권장한다. 팩토리 메서드 구현이 더 단순하고 목적을 달성하는데 충분하기 때문이다.

#### 8.1.2 집합 팩토리

`List.of`와 비슷한 방법으로 바꿀 수 없는 집합을 만들 수 있다.

```java
Set<String> friends = Set.of("Raphael", "Olivia", "Thibaut");
System.out.println(friends);
```

중복된 요소를 제공해 집합을 만들려고 하면 `Olivia`라는 요소가 중복되어 있다는 설명과 함께 `IllegalArgumentException`이 발생한다. 집합은 오직 고유의 요소만 포함할 수 있다는 원칙을 상기시킨다.

```java
Set<String> friends = Set.of("Raphael", "Olivia", "Olivia");
```

#### 8.1.3 맵 팩토리

맵을 만드는 것은 리스트나 집합을 만드는 것에 비해 조금 복잡한데 맵을 만들려면 키와 값이 있어야 하기 때문이다. 자바 9에서는 두 가지 방법으로 바꿀 수 없는 맵을 초기화할 수 있다. `Map.of` 팩토리 메서드에 키와 값을 번갈아 제공하는 방법으로 맵을 만들 수 있다.

```java
Map<String, Integer> ageOfFriends = Map.of("Raphael", 30, "Olivia", 25, "Thibaut", 26);
System.out.println(ageOfFriends);
```

열 개 이하의 키와 값 쌍을 가진 작은 맵을 만들 때는 이 메소드가 유용하다. 그 이상의 맵에서는 `Map.Entry<K, V>` 객체를 인수로 받으며 가변 인수로 구현된 `Map.ofEntries` 팩토리 메서드를 이용하는 것이 좋다. 이 메서드는 키와 값을 감쌀 추가 객체 할당을 필요로한다.

```java
import static java.util.Map.entry;

Map<String, Integer> ageOfFriends = Map.ofEntries(entry("Raphael", 30),
                                                  entry("Olivia", 25),
                                                  entry("Thibaut", 26));
System.out.println(ageOfFriends);
```

`Map.entry`는 `Map.Entry` 객체를 만드는 새로운 팩토리 메서드다.

---

## 8.2 리스트와 집합 처리
자바 8에서는 `List`, `Set` 인터페이스에 다음과 같은 메서드를 추가했다.

- `removeIf` : 프레디케이트를 만족하는 요소를 제거한다. `List`나 `Set`을 구현하거나 그 구현을 상속받은 모든 클래스에서 이용할 수 있다.
- `replaceAll` : 리스트에서 이용할 수 있는 기능으로 `UnaryOperator` 함수를 이용해 요소를 바꾼다.
- `sort` : `List` 인터페이스에서 제공하는 기능으로 리스트를 정렬한다.

이들 메서드는 호출한 컬렉션 자체를 바꾼다. 새로운 결과를 만드는 스트림 동작과 달리 이들 메서드는 기존 컬렉션을 바꾼다. 왜 이런 메서드가 추가되었을까? 컬렉션을 바꾸는 동작은 에러를 유발하며 복잡함을 더한다. 자바 8에 `removeIf`와 `replaceAll`를 추가한 이유가 바로 이때문이다.

#### 8.2.1 removeIf 메서드
다음은 숫자로 시작되는 참조 코드를 가진 트랜잭션을 삭제하는 코드다.

```java
for (Transaction transaction : transactions) {
    if(Character.isDigit(transaction.getReferenceCode().charAt(0))) {
        transactions.remove(transaction);
    }
}
```

안타깝게도 위 코드는 `ConcurrentModificationException`을 일으킨다. 내부적으로 `for-each` 루프는 `Iterator` 객체를 사용하므로 위 코드는 다음과 같이 해석된다.

```java
for (Iterator<Transaction> iterator = transactions.iterator(); iterator.hasNext();) {
    Transaction transaction = iterator.next();
    if(Character.isDigit(transaction.getReferenceCode().charAt(0))) {
        transactions.remove(transaction); // 반복하면서 별도의 두 객체를 통해 컬렉션을 바꾸고 있는 문제
    }
}
```

두 개의 개별 객체가 컬렉션을 관리한다는 사실을 주목하자.
- `Iterator` 객체, `next()`, `hasNext()`를 이용해 소스를 질의한다.
- `Collection` 객체 자체, `remove()`를 호출해 요소를 삭제한다.

결과적으로 반복자의 상태는 컬렉션의 상태와 서로 동기화되지 않는다.`Iterator` 객체를 명시적으로 사용하고 그 객체의 `remove()` 메서드를 호출함으로 이 문제를 해결할 수 있다.

```java
for (Iterator<Transaction> iterator = transactions.iterator(); iterator.hasNext();) {
    Transaction transaction = iterator.next();
    if(Character.isDigit(transaction.getReferenceCode().charAt(0))) {
        iterator.remove();
    }
}
```
코드가 조금 복잡해졌다. 이 코드 패턴은 자바 8의 `removeIf` 메서드로 바꿀 수 있다. 그러면 코드가 단순해질 뿐 아니라 버그도 예방할 수 있다. `removeIf` 메서드는 삭제할 요소를 가리키는 프레디케이트를 인수로 받는다.

```java
transactions.removeIf(transaction -> Character.isDigit(transaction.getReferenceCode().charAt(0)));
```
하지만 때로는 요소를 제거하는 게 아니라 바꿔야 하는 상황이 있다. 이런 상황에 사용할 수 있도록 자바 8에서는 `replaceAll`을 제공한다.

---

#### 8.2.2 replaceAll 메서드
`List` 인터페이스의 `replaceAll` 메서드를 이용해 리스트의 각 요소를 새로운 요소로 바꿀 수 있다. 스트림 API를 이용하면 다음처럼 문제를 해결할 수 있었다.

```java
referenceCodes.stream().map(code -> Character.toUpperCase(code.charAt(0)) + code.substring(1))
                       .collect(Collectors.toList())
                       .forEach(System.out::println);
```
하지만 이 코드는 새 문자열 컬렉션을 만든다. 우리가 원하는 것은 기존 컬렉션을 바꾸는 것이다. 다음처럼 `ListIterator` 객체(요소를 바꾸는 `set()` 메서드를 지원)를 이용할 수 있다.

```java
for (ListIterator<String> iterator = referenceCodes.listIterator(); iterator.hasNext();) {
    String code = iterator.next();
    iterator.set(Character.toUpperCase(code.charAt(0)) + code.substring(1));
}
```
코드가 조금 복잡해졌다. 이전에 설명한 것처럼 컬렉션 객체를 `Iterator` 객체와 혼용하면 반복과 컬렉션 변경이 동시에 이루어지면서 쉽게 문제를 일으킨다. 자바 8의 기능을 이용하면 다음처럼 간단하게 구현할 수 있다.

```java
referenceCodes.replaceAll(code -> Character.toUpperCase(code.charAt(0)) + code.substring(1));
```

---

## 8.3 맵 처리
자바 8에서는 `Map` 인터페이스에 몇 가지 디폴트 메서드를 추가했다. 여기서 디폴트 메서드는 기본적인 구현을 인터페이스에 제공하는 기능이라고 생각해두자. 자주 사용되는 패턴을 개발자가 직접 구현할 필요가 없도록 이들 메서드를 추가한 것이다.

#### 8.3.1 forEach 메서드
맵에서 키와 값을 반복하면서 확인하는 작업은 잘 알려진 귀찮은 작업 중 하나다. 실제로는 `Map.Entry<K, V>`의 반복자를 이용해 맵의 항목 집합을 반복할 수 있다.

```java
for(Map.Entry<String, Integer> entry : ageOfFriends.entrySet()) {
    String friend = entry.getKey();
    Integer age = entry.getValue();
    System.out.println(friend + " is " + age + " years old");
}
```
자바 8에서부터 `Map` 인터페이스는 `BiConsumer`(키와 값을 인수로 받음)를 인수로 받는 `forEach` 메서드를 지원하므로 코드를 조금 더 간단하게 구현할 수 있다.

```java
ageOfFriends.forEach((friend, age) -> System.out.println(friend + " is " + age + " years old"));
```
정렬은 반복과 관련한 오래된 고민거리다. 자바 8에서는 맵의 항목을 쉽게 비교할 수 있는 몇가지 방법을 제공한다.

#### 8.3.2 정렬 메서드
다음 두 개의 새로운 유틸리티를 이용하면 맵의 항목을 값 또는 키를 기준으로 정렬할 수 있다.

- `Entry.comparingByValue`
- `Entry.comparingByKey`

코드를 살펴보자.

```java
Map<String, String> favouriteMovies = Map.ofEntries(entry("Raphael", "Star Wars"),
                                                    entry("Cristina", "Matrix"),
                                                    entry("Olivia", "James Bond"));

favouriteMovies.entrySet()
               .stream()
               .sorted(Entry.comparingByKey())
               .forEachOrdered(System.out::println); // 사람의 이름을 알파벳 순으로 스트림 요소를 처리한다.
```

위 코드는 다음과 같은 결과를 출력한다.
`Cristina=Matrix, Olivia=James Bond, Raphael=Star Wars`

요청한 키가 맵에 존재하지 않을 때 이를 어떻게 처리하느냐도 흔히 발생하는 문제다. 새로 추가된 `getOrDefault` 메서드를 이용하면 이를 쉽게 해결할 수 있다.

#### 8.3.3 getOrDefault 메서드
기존에는 찾으려는 키가 존재하지 않으면 `null`이 반환되므로 `NullPointerException`을 방지하려면 요청 결과가 `null`인지 확인해야 한다. 기본값을 반환하는 형식으로 이 문제를 해결할 수 있다. `getOrDefault` 메서드를 이용하면 쉽게 이 문제를 해결할 수 있다. 이 메서드는 첫 번째 인수로 키룰, 두 번째 인수로 기본값을 받으며 맵에 키가 존재하지 않으면 두 번째 인수로 받은 기본값을 반환한다.

```java
Map<String, String> favouriteMovies = Map.ofEntries(entry("Raphael", "Star Wars"),
                                                    entry("Cristina", "Matrix"),
                                                    entry("Olivia", "James Bond"));
System.out.println(favouriteMovies.getOrDefault("Olivia", "Matrix")); // James Bond 출력
System.out.println(favouriteMovies.getOrDefault("Thibaut", "Matrix")); // Matrix 출력
```
키가 존재하더라도 값이 `null`인 상황에서는 `getOrDefault`가 `null`을 반환할 수 있다는 사실을 주목하자. 즉 키가 존재하느냐의 여부에 따라서 두 번째 인수가 반환될지 결정된다.

자바 8에서는 키의 값이 존재하는지 여부를 확인할 수 있는 더 복잡한 몇 개의 패턴도 제공한다.

#### 8.3.4 계산 패턴
맵에 키가 존재하는지 여부에 따라 어떤 동작을 실행하고 결과를 저장해야 하는 상황이 필요한 때가 있다. 예를 들어 키를 이용해 값비싼 동작을 실행해서 얻은 결과를 캐시하려 한다. 키가 존재하면 결과를 다시 계산할 필요가 없다. 다음의 세 가지 연산이 이런 상황에서 도움을 준다.

- `computeIfAbsent` : 제공된 키에 해당하는 값이 없으면(값이 없거나 `null`), 키를 이용해 새 값을 계산하고 맵에 추가한다.
- `computeIfPresent` : 제공된 키가 존재하면 새 값을 계산하고 맵에 추가한다.
- `compute` : 제공된 키로 새 값을 계산하고 맵에 저장한다.

정보를 캐시할 때 `computeIfAbsent`를 활용할 수 있다. 파일 집합의 각 행을 파싱해 `SHA-256`을 계산한다고 가정하자. 기존에 이미 데이터를 처리했다면 이 값을 다시 계산할 필요가 없다.

맵을 이용해 캐시를 구현했다고 가정하면 다음처럼 `MessageDigest` 인스턴스로 `SHA-256` 해시를 계산할 수 있다.

```java
Map<String, byte[]> dataToHash = new HashMap<>();
MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
```

이제 데이터를 반복하면서 결과를 캐시한다.

```java
lines.forEach(line -> dataToHash.computeIfAbsent(line, // line은 맵에서 찾을 키
                                                 this::calculateDigest)); // 키가 존재하지 않으면 동작을 실행

private byte[] calculateDigest(String key) { // 헬퍼가 제공된 키의 해시를 계산할 것이다.
    return messageDigest.digest(key.getBytes(StandardCharsets.UTF_8));
}
```
여러 값을 저장하는 맵을 처리할 때도 이 패턴을 유용하게 활용할 수 있다. `Map<K, List<V>>`에 요소를 추가하려면 항목이 초기화되어 있는지 확인해야 한다. `Raphael`에게 줄 영화 목록을 만든다고 가정하자.

```java
String friend = "Raphael";
List<String> movies = friendsToMovies.get(friend);
if(movies == null) { // 리스트가 초기화되었는지 확인
    movies = new ArrayList<>();
    friendsToMovies.put(friend, movies);
}
movies.add("Star Wars"); // 영화를 추가

System.out.println(friendsToMovies);
```

`computeIfAbsent`는 키가 존재하지 않으면 값을 계산해 맵에 추가하고 키가 존재하면 기존 값을 반환한다. 이를 이용해 다음처럼 코드를 구현할 수 있다.

```java
friendsToMovies.computeIfAbsent("Raphael", name -> new ArrayList<>()).add("Star Wars");
```
`computeIfPresent` 메서드는 현재 키와 관련된 값이 맵에 존재하며 `null`이 아닐 때만 새 값을 계산한다. 값을 만드는 함수가 `null`을 반환하면 현재 매핑을 맵에서 제거한다. 하지만 매핑을 제거할 때는 `remove` 메서드를 오버라이드하는 것이 더 적합하다.

#### 8.3.5 삭제 패턴
제공된 키에 해당하는 맵 항목을 제거하는 `remove` 메서드는 이미 알고 있다. 자바 8에서는 키가 특정한 값과 연관되었을 때만 항목을 제거하는 오버로드 버전 메서드를 제공한다. 기존에는 다음처럼 코드를 구현했다.

```java
String key = "Raphael";
String value = "Jack Reacher 2";
if(favouriteMovies.containsKey(key) && Objects.equals(favouriteMovies.get(key), value)) {
    favouriteMovies.remove(key);
    return true;
} else {
    return false;
}
```
이견이 없이 다음처럼 코드를 간결하게 구현할 수 있다.

```java
favouriteMovies.remove(key, value);
```

#### 8.3.6 교체 패턴
맵의 항목을 바꾸는 데 사용할 수 있는 두 개의 메서드가 맵에 추가되었다.

- `replaceAll` : `BiFunction`을 적용한 결과로 각 항목의 값을 교체한다. 이 메서드는 이전에 살펴본 `List`의 `replaceAll`과 비슷한 동작을 수행한다.
- `replace` : 키가 존재하면 맵의 값을 바꾼다. 키가 특정 값으로 매핑되었을 때만 값을 교체하는 오버로드 버전도 있다.

다음과 같은 방법으로 맵의 모든 값의 형식을 바꿀 수 있다.

```java
Map<String, String> favouriteMovies = new HashMap<>(); // replaceAll을 적용할 것이므로 바꿀 수 있는 맵을 사용해야 한다.
favouriteMovies.put("Raphael", "Star Wars");
favouriteMovies.put("Olivia", "james bond");
favouriteMovies.replaceAll((friend, movie) -> movie.toUpperCase());
System.out.println(favouriteMovies);
```

두 개의 맵에서 값을 합치거나 바꿔야 할때는 새로운 `merge` 메서드를 이용하면 이 문제를 해결할 수 있다.

#### 8.3.7 합침
두 그룹의 연락처를 포함하는 두 개의 맵을 합친다고 가정하자. 다음처럼 `putAll`을 사용할 수 있다.

```java
Map<String, String> family = Map.ofEntries(
    entry("Teo", "Star Wars"), entry("Cristina", "James Bond"));
Map<String, String> friends = Map.ofEntries(
    entry("Raphael", "Star Wars"));
Map<String, String> everyone = new HashMap<>(family);
everyone.putAll(friends); // friends의 모든 항목을 everyone으로 복사
System.out.println(everyone);
```
중복된 키가 없다면 위 코드는 잘 동작한다. 값을 좀 더 유연하게 합쳐야 한다면 새로운 `merge` 메서드를 이용할 수 있다. 이 메서드는 중복된 키를 어떻게 합칠지 결정하는 `BiFunction`을 인수로 받는다. `family`와 `friends` 두 맵 모두에 `Cristina`가 다른 영화 값으로 존재한다고 가정하자.

```java
Map<String, String> family = Map.ofEntries(
    entry("Teo", "Star Wars"), entry("Cristina", "James Bond"));
Map<String, String> friends = Map.ofEntries(
    entry("Raphael", "Star Wars"), entry("Cristina", "Matrix"));
```
`forEach`와 `merge` 메서드를 이용해 충돌을 해결할 수 있다. 다음 코드는 두 영화의 문자열을 합치는 방법으로 문제를 해결한다.

```java
Map<String, String> everyone = new HashMap<>(family);
friends.forEach((k, v) -> everyone.merge(k, v, (movie1, movie2) -> movie1 + " & " + movie2)); // 중복된 키가 있으면 두 값을 연결
System.out.println(everyone);
```
자바독에서 설명하는 것처럼 `merge` 메서드는 널값과 관련된 복잡한 상황도 처리한다.

>"지정된 키와 연관된 값이 `null`이면 `[merge]`는 키를 `null`이 아닌 값과 연결한다. 아니면 `[merge]`는 연결된 값을 주어진 매핑 함수의 `[결과]` 값으로 대치하거나 결과가 `null`이면 `[항목]`을 제거한다."

`merge`를 이용해 초기화 검사를 구현할 수도 있다. 영화를 몇 회 시청했는지 기록하는 맵이 있다고 가정하자. 해당 값을 증가시키기 전에 관련 영화가 이미 맵에 존재하는지 확인해야 한다.

```java
Map<String, Long> moviesToCount = new HashMap<>();
String movieName = "JamesBond";
long count = moviesToCount.get(movieName);
if(count == null) {
    moviesToCount.put(movieName, 1L);
} else {
    moviesToCount.put(movieName, count + 1L);
}
```
위 코드를 다음처럼 구현할 수 있다.

```java
moviesToCount.merge(movieName, 1L, (key, count) -> count + 1L);
```
위 코드에서 `merge`의 두 번째 인수는 `1L`이다. 자바독에 따르면 이 인수는 "키와 연관된 기존 값에 합쳐질 `null`이 아닌 값 또는 값이 없거나 키에 `null` 값이 연관되어 있다면 이 값을 키와 연결"하는데 사용된다. 키의 반환값이 `null`이므로 처음에는 `1`이 사용된다. 그 다음부터는 값이 `1`로 초기화되어 있으므로 `BiFunction`을 적용해 값이 증가된다.

지금까지 `Map` 인터페이스에 추가된 기능을 확인했다. 맵의 사촌인 `ConcurrentHashMap`의 기능도 개선되었다. 이제 어떤 기능이 개선되었는지 살펴본다.

---

## 8.4 개선된 ConcurrentHashMap
`ConcurrnetHashMap` 클래스는 동시성 친화적이며 최신 기술을 반영한 `HashMap` 버전이다. `ConcurrentHashMap`은 내부 자료구조의 특정 부분만 잠궈 동시 추가, 갱신 작업을 허용한다. 따라서 동기화된 `Hashtable` 버전에 비해 읽기 쓰기 연산 성능이 월등하다(참고로, 표준 `HashMap`은 비동기로 동작함).

#### 8.4.1 리듀스와 검색
`ConcurrentHashMap`은 스트림에서 봤던 것과 비슷한 종류의 세 가지 새로운 연산을 지원한다.

- `forEach` : 각 (키, 값) 쌍에 주어진 액션을 실행
- `reduce` : 모든 (키, 값) 쌍을 제공된 리듀스 함수를 이용해 결과로 합침
- `search` : `null`이 아닌 값을 반환할 때까지 각 (키, 값) 쌍에 함수를 적용

다음처럼 키에 함수 받기, 값, `Map.Entry`, (키, 값) 인수를 이용한 네 가지 연산 형태를 지원한다.

- 키, 값으로 연산(`forEach`, `reduce`, `search`)
- 키로 연산(`forEachKey`, `redeceKeys`, `searchKeys`)
- 값으로 연산(`forEachValue`, `reduceValues`, `searchValues`)
- `Map.Entry` 객체로 연산(`forEachEntry`, `reduceEntries`, `searchEntries`)

이들 연산은 `ConcurrentHashMap`의 상태를 잠그지 않고 연산을 수행한다는 점을 주목하자. 따라서 이들 연산에 제공한 함수는 계산이 진행되는 동안 바뀔 수 있는 객체, 값, 순서 등에 의존하지 않아야 한다.

또한 이들 연산에 병렬성 기준값^threshold^을 지정해야 한다. 맵의 크기가 주어진 기준값보다 작으면 순차적으로 연산을 실행한다. 기준값을 `1`로 지정하면 공통 스레드 풀을 이용해 병렬성을 극대화한다. `Long.MAX_VALUE`를 기준값으로 설정하면 한 개의 스레드로 연산을 실행한다. 여러분의 소프트웨어 아키렉처가 고급 수준의 자원 활용 최적화를 사용하고 있지 않다면 기준값 규칙을 따르는 것이 좋다.

이 예제에서는 `reduceValues` 메서드를 이용해 맵의 최댓값을 찾는다.

```java
ConcurrentHashMap<String, Long> map = new ConcurrentHashMap<>();
long parallelismThreshold = 1;
Optional<Long> maxValue = Optional.ofNullable(map.reduceValues(parallelismThreshold, Long::max));
```

`int`, `long`, `double` 등의 기본값에는 전용 `each reduce` 연산이 제공되므로 `reduceValuesToInt`, `reduceValuesToLong` 등을 이용하면 박싱 작업을 할 필요가 없고 효율적으로 작업을 처리할 수 있다.

#### 8.4.2 계수
`ConcurrentHashMap` 클래스는 맵의 매핑 개수를 반환하는 `mappingCount` 메서드를 제공한다. 기존의 `size` 메서드 대신 새 코드에서는 `long`를 반환하는 `mappingCount` 메서드를 사용하는 것이 좋다. 그래야 매핑의 개수가 `int`의 범위를 넘어서는 이후의 상황을 대처할 수 있기 때문이다.

#### 8.4.3 집합뷰
`ConcurrentHashMap` 클래스는 `ConcurrentHashMap`을 집합 뷰로 반환하는 `keySet`이라는 새 메서드를 제공한다. 맵을 바꾸면 집합도 바뀌고 반대로 집합을 바꾸면 맵도 영향을 받는다. `newKeySet`이라는 새 메서드를 이용해 `ConcurrentHashMap`으로 유지되는 집합을 만들 수도 있다.

---

## 8.5 마치며

- 자바 9는 적은 원소를 포함하며 바꿀 수 없는 리스트, 집합, 맵을 쉽게 만들 수 있도록 `List.of`, `Set.of`, `Map.of`, `Map.ofEntries` 등의 컬렉션 팩토리를 지원한다.
- 이들 컬렉션 팩토리가 반환한 객체는 만들어진 다음 바꿀 수 없다.
- `List` 인터페이스는 `removeIf`, `replaceAll`, `sort` 세 가지 디폴트 메서드를 지원한다.
- `Set` 인터페이스는 `removeIf` 디폴트 메서드를 지원한다.
- `Map` 인터페이스는 자주 사용하는 패턴과 버그를 방지할 수 있도록 다양한 디폴트 메서드를 지원한다.
- `ConcurrentHashMap`은 `Map`에서 상속받은 새 디폴트 메서드를 지원함과 동시에 스레드 안전성도 제공한다.

---