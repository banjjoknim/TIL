# 아이템 47. 반환 타입으로는 스트림보다 컬렉션이 낫다

- 자바 7까지는 일련의 원소들을 반환하는 메서드의 반환 타입으로 Collection, Set, List 같은 컬렉션 인터페이스, 혹은 Iterable이나 배열을 썼다.
- 기본은 컬렉션 인터페이스이며, for-each 문에서만 쓰이거나 반환된 원소 시퀀스가 일부 Collection 메서드를 구현할 수 없을 때(주로 contains(Object) 같은)는 Iterable 인터페이스를
  썼다.
- 반환 원소들이 기본 타입이거나 성능에 민감한 상황이라면 배열을 썼다.

## 반복

- 스트림은 반복(iteration)을 지원하지 않는다. 따라서 스트림과 반복을 알맞게 조합해야 좋은 코드가 나온다.
- Stream 인터페이스는 Iterable 인터페이스가 정의한 추상 메서드를 전부 포함하며, Iterable 인터페이스가 정의한 방식대로 동작한다.
    - 다만, for-each로 반복할 수 없는 이유는 Stream이 Iterable을 extends하지 않았기 때문이다.

### 자바 타입 추론의 한계로 컴파일되지 않는다.

```java
public class Item47 {
    public static void main(String[] args) {
        for (ProcessHandle ph : ProcessHandle.allProcesses()::iterator) { // 컴파일 에러가 난다.
            // 프로세스를 처리한다.
        }
    }
}
```

- Stream의 iterator 메서드에 메서드 참조를 건네면 될 것 같지만 컴파일 오류가 난다.

### 스트림을 반복하기 위한 '끔찍한' 우회 방법

```java
public class Item47 {
    public static void main(String[] args) {
        for (ProcessHandle ph : (Iterable<ProcessHandle>) ProcessHandle.allProcesses()::iterator) {
            // 프로세스를 처리한다.
        }
    }
}
```

- 작동은 하지만 난잡하고 직관성이 떨어진다.

### 어댑터 메서드를 사용한 스트림 반복

```java
public class Item47 {
    public static void main(String[] args) {
        for (ProcessHandle p : iterableOf(ProcessHandle.allProcesses())) {
            // 프로세스를 처리한다.
        }
    }

    public static <E> Iterable<E> iterableOf(Stream<E> stream) {
        return stream::iterator;
    }
}
```

- 어댑터 메서드를 사용하면 자바의 타입 추론이 문맥을 잘 파악하여 어댑터 메서드 안에서 따로 형변환하지 않아도 된다.
- 어댑터를 사용하면 어떤 스트림도 for-each 문으로 반복할 수 있다.

### `Iterable<E>`를 `Stream<E>`로 중개해주는 어댑터

```java
public class Item47 {
    public static <E> Stream<E> streamOf(Iterable<E> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false);
    }
}
```

- 만약 API가 Iterable만 반환하면 스트림 파이프라인에서 이를 처리할 수 없게 된다. 이 경우, 위와 같이 어댑터를 구현해서 사용할 수 있다.
- 만약 이 메서드가 오직 스트림 파이프라인에서만 쓰일 걸 안다면 마음 놓고 스트림을 반환해도 된다.
- 반대로 반환된 객체들이 반복문에서만 쓰일 걸 안다면 Iterable을 반환하도록 한다.
- 공개 API를 작성할 때는 스트림 파이프라인을 사용하는 사람과 반복문에서 쓰려는 사람 모두를 고려해야 한다.
- Collection 인터페이스는 Iterable의 하위 타입이고 stream 메서드도 제공하기 때문에 반복과 스트림을 동시에 지원한다.
    - **원소 시퀀스를 반환하는 공개 API의 반환 타입에는 Collection이나 그 하위 타입을 쓰는 게 일반적으로 최선이다.**
- 반환하는 시퀀스의 크기가 메모리에 올려도 안전할 만큼 작다면 ArrayList나 HashSet 같은 표준 컬렉션 구현체를 반환하는 게 최선일 수도 있다.
    - **하지만 단지 컬렉션을 반환한다는 이유로 덩치 큰 시퀀스를 메모리에 올려서는 안 된다.**
    - 반환할 시퀀스가 크지만 표현을 간결하게 할 수 있다면 전용 컬렉션의 구현을 고려해보도록 한다. 이때 AbstractList를 이용하면 전용 컬렉션을 손쉽게 구현할 수 있다.
    - AbstractCollection을 활용해서 Collection 구현체를 작성할 때는 Iterable용 메서드 외에 contains와 size만 더 구현하면 된다.
    - 만약 반복이 시작되기 전에 시퀀스의 내용을 확정할 수 없는 등의 이유로 contains와 size를 구현하는 게 불가능할 때는 컬렉션보다는 스트림이나 Iterable을 반환하는 편이 낫다.
    - 별도의 메서드를 두어 두 방식을 모두 제공할 수도 있다.

참고 : Collection의 size 메서드는 int 값을 반환하는데, 따라서 이 경우 시퀀스의 최대 길이는 `Integer.MAX_VALUE` 혹은 `2^31 - 1` 로 제한된다.

## 스트림을 반환하는 방법

### 입력 리스트의 모든 부분리스트를 스트림으로 반환한다 - 1

```java
public class SubLists {
    public static <E> Stream<List<E>> of(List<E> list) {
        return Stream.concat(Stream.of(Collections.emptyList()), prefixes(list).flatMap(SubLists::suffixes));
    }

    private static <E> Stream<List<E>> prefixes(List<E> list) {
        return IntStream.rangeClosed(1, list.size())
                .mapToObj(end -> list.subList(0, end));
    }

    private static <E> Stream<List<E>> suffixes(List<E> list) {
        return IntStream.range(0, list.size())
                .mapToObj(start -> list.subList(start, list.size()));
    }
}
```

- 위 관용구는 정수 인덱스를 사용한 표준 for 반복문의 스트림 버전이라 할 수 있다. 아래의 중첩 for 반복문과 취지가 비슷하다.

```java
import java.util.ArrayList;

public class Temp {
    public static void main(String[] args) {
        List<String> src = new ArrayList<>(); // 편의상 String 타입으로 작성하였음.
        for (int start = 0; start < src.size(); start++) {
            for (int end = start + 1; end <= src.size(); end++) {
                System.out.println(src.subList(start, end));
            }
        }
    }
}
```

### 입력 리스트의 모든 부분리스트를 스트림으로 반환한다 - 2

```java
public class SubLists {
    public static <E> Stream<List<E>> of(List<E> list) {
        return IntStream.range(0, list.size())
                .mapToObj(start -> IntStream.rangeClosed(start + 1, list.size())
                        .mapToObj(end -> list.subList(start, end)))
                .flatMap(x -> x);

    }
}
```

- 스트림의 flatMap을 이용하였다.

## 핵심 정리

- 원소 시퀀스를 반환하는 메서드를 작성할 때는, 스트림과 반복에서의 사용을 모두 고려해야 한다.
- 컬렉션을 반환할 수 있다면 컬렉션을 반환하도록 한다.
- 반환 전부터 이미 원소들을 컬렉션에 담아 관리하고 있거나 컬렉션을 하나 더 만들어도 될 정도로 원소 개수가 적다면 ArrayList 같은 표준 컬렉션에 담아 반환하도록 한다.
    - 만약 그렇지 않다면 전용 컬렉션을 구현할지 고민해보도록 한다.