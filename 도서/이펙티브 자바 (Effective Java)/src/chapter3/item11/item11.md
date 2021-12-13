# 아이템 11. equals를 재정의하려거든 hashCode도 재정의하라

- **equals**를 재정의한 클래스 모두에서 **hashCode**도 재정의해야 한다.
- 그렇지 않으면 hashCode 일반 규약을 어기게 되어 해당 클래스의 인스턴스를 HashMap이나 HashSet 같은 컬렉션의 원소로 사용할 때 문제를 일으킬 것이다.

다음은 Object 명세에서 발췌한 규약이다.

> - equals 비교에 사용되는 정보가 변경되지 않았다면, 애플리케이션이 실행되는 동안 그 객체의 hashCode 메서드는 몇 번을 호출해도 일관되게 항상 같은 값을 반환해야 한다. 단, 애플리케이션을 다시 실행한다면 이 값이 달라져도 상관없다.
> - equals(Object)가 두 객체를 같다고 판단했다면, 두 객체의 hashCode는 똑같은 값을 반환해야 한다.
> - equals(Object)가 두 객체를 다르다고 판단했더라도, 두 객체의 hashCode가 서로 다른 값을 반환할 필요는 없다. 단, 다른 객체에 대해서는 다른 값을 반환해야 해시테이블의 성능이 좋아진다.

- **hashCode 재정의를 잘못했을 때 크게 문제가 되는 조항은 두 번째다. 즉, 논리적으로 같은 객체는 같은 해시코드를 반환해야 한다.**
- equals는 물리적으로 다른 두 객체를 논리적으로 같다고 할 수 있다.
- 하지만 Object의 기본 hashCode 메서드는 이 둘이 전혀 다르다고 판단하여, 규약과 달리 (무작위처럼 보이는) 서로 다른 값을 반환한다.
- hashCode를 재정의하지 않으면 논리적 동치인 두 객체가 서로 다른 해시코드를 반환하여 두 번째 규약을 지키지 못한다.

## HashMap과 hashCode

- HashMap에서 hashCode를 재정의하지 않을 경우, hashCode가 다르기 때문에 논리적으로 동치일지라도 서로 다른 해시 버킷에 인스턴스가 담기게 되어 문제가 생기게 된다.
- 설사 인스턴스를 같은 버킷에 담았더라도 HashMap에서 get 메서드를 호출하면 여전히 null을 반환한다.
- 이는, HashMap이 해시코드가 다른 엔트리끼리는 동치성 비교를 시도조차 하지 않도록 최적화되어 있기 때문이다.

### 최악의 (하지만 적법한) hashCode 구현 - 사용 금지!

```java
public class Item11 {
    @Override
    public int hashCode() {
        return 42;
    }
}
```

- 이 코드는 동치인 모든 객체에서 똑같은 해시코드를 반환하니 적법하다.
- 하지만 모든 객체에게 똑같은 값만 내어주므로 모든 객체가 해시테이블의 버킷 하나에 담겨 마치 연결 리스트(linked list)처럼 동작한다.
- 그 결과 평균 수행 시간이 O(1)인 해시테이블이 O(n)으로 느려져서, 객체가 많아지면 도저히 쓸 수 없게 된다.

### 전형적인 hashCode 메서드

```java
public class Item11 {
    @Override
    public int hashCode() {
        int result = Short.hashCode(value1);
        result = 31 * result + Short.hashCode(value2);
        result = 31 * result + Short.hashCode(value3);
        return result;
    }
}
```

### 좋은 해시 함수

- 좋은 해시 함수라면 서로 다른 인스턴스에 다른 해시코드를 반환해야 한다. 이는 hashCode의 세 번째 규약이 요구하는 속성이다.
- 이상적인 해시 함수는 주어진 (서로 다른) 인스턴스들을 32비트 정수 범위에 균일하게 분배해야 한다.

## 한 줄짜리 hashCode 메서드 - 성능이 살짝 아쉽다.

```java
public class Item11 {
    @Override
    public int hashCode() {
        return Objects.hash();
    }
}
```

- Objects 클래스는 임의의 개수만큼 객체를 받아 해시코드를 계산해주는 정적 메서드인 hash를 제공한다.
- 이 메서드를 이용하면 hashCode 함수를 단 한 줄로 작성할 수 있다.
- 하지만 입력 인수를 담기 위한 배열이 만들어지고, 입력 중 기본 타입이 있다면 박싱과 언박싱도 거쳐야하므로 속도는 조금 느리다.
- 따라서, hash 메서드는 성능에 민감하지 않은 상황에서만 사용해야 한다.

## 해시코드를 지연 초기화하는 hashCode 메서드 - 스레드 안정성까지 고려해야 한다.

```java
public class Item11 {
    //  ======================================== 해시코드를 지연 초기화하는 hashCode 메서드 - 스레드 안정성까지 고려해야 한다.
    private int hashCode; // 자동으로 0으로 초기화된다.

    @Override
    public int hashCode() {
        int result = hashCode;
        if (result == 0) {
            // doSomething ...
            hashCode = result;
        }
        return result;
    }
}
```

- 클래스가 불변이고 해시코드를 계산하는 비용이 크다면, 매번 새로 계산하기 보다는 캐싱하는 방식을 고려해야 한다.
- 이 타입의 객체가 주로 해시의 키로 사용될 것 같다면 인스턴스가 만들어질 때 해시코드를 계산해둬야 한다.
- 해시의 키로 사용되지 않는 경우라면 hashCode가 처음 불릴 때 계산하는 지연 초기화(lazy initialization) 전략을 고려해본다.
- 이때, 필드를 지연 초기화하려면 그 클래스를 스레드 안전하게 만들도록 신경 써야 한다.
- 또한, hashCode 필드의 초깃값은 흔히 생성되는 객체의 해시코드와는 달라야 한다.

## 주의사항

- **성능을 높이기위해 해시코드를 계산할 때 핵심 필드를 생략해서는 안 된다.**
- 속도는 빨라지겠지만, 해시 품질이 나빠져 해시테이블의 성능을 심각하게 떨어뜨릴 수도 있다.
- **hashCode가 반환하는 값의 생성 규칙을 API 사용자에게 자세히 공표하지 말아야 한다. 그래야 클라이언트가 이 값에 의지하지 않게 된다.**

## 핵심 정리

equals를 재정의할 때는 hashCode도 반드시 재정의해야 한다. 그렇지 않으면 프로그램이 제대로 동작하지 않을 것이다. 재정의한 hashCode는 Object의 API 문서에 기술된 일반 규약을 따라야 하며,
서로 다른 인스턴스라면 되도록 해시코드도 서로 다르게 구현해야 한다.

## 참고자료
- [[java] hashcode()와 equals() 메서드는 언제 사용하고 왜 사용할까?](https://jisooo.tistory.com/entry/java-hashcode%EC%99%80-equals-%EB%A9%94%EC%84%9C%EB%93%9C%EB%8A%94-%EC%96%B8%EC%A0%9C-%EC%82%AC%EC%9A%A9%ED%95%98%EA%B3%A0-%EC%99%9C-%EC%82%AC%EC%9A%A9%ED%95%A0%EA%B9%8C)