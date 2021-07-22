# 아이템 13. clone 재정의는 주의해서 진행하라

- Cloneable은 복제해도 되는 클래스임을 명시하는 용도의 믹스인 인터페이스(mixin interface)이다.
- 하지만 큰 문제가 있는데, clone 메서드가 선언된 곳이 Cloneable이 아닌 Object이고, 그마저도 protected라는 것이다.
- 그래서 Cloneable을 구현하는 것만으로는 외부에 객체에서 clone 메서드를 호출할 수 없다.
- 리플렉션을 사용하면 가능하지만, 해당 객체가 접근이 허용된 clone 메서드를 제공한다는 보장이 없기 때문에 주의해야 한다.

## Cloneable 인터페이스

- 메서드 하나 없는 Cloneable 인터페이스는 Object의 protected 메서드인 clone의 동작 방식을 결정한다.
- Cloneable을 구현한 클래스의 인스턴스에서 clone을 호출하면 그 객체의 필드들을 하나하나 복사한 객체를 반환하며, 그렇지 않은 클래스의 인스턴스에서 호출하면
  CloneNotSupportedException을 던진다.
    - 인터페이스를 이례적으로 사용한 예이다. 따라하지 말 것!
    - 인터페이스를 구현하는 것은 일반적으로 해당 클래스가 그 인터페이스에서 정의한 기능을 제공한다고 선언하는 행위다.
    - 하지만 Cloneable의 경우에는 상위 클래스에 정의된 protected 메서드의 동작 방식을 변경한 것이다.

## clone 메서드의 일반 규약

> 이 객체의 복사본을 생성해 반환한다. '복사'의 정확한 뜻은 그 객체를 구현한 클래스에 따라 다를 수 있다. 일반적인 의도는 다음과 같다. 어떤 객체 x에 대해 다음 식은 참이다.
>
> x.clone() != x
>
> 또한 다음 식도 참이다.
>
> x.clone().getClass() == x.getClass()
>
> 하지만 이상의 요구를 반드시 만족해야 하는 것은 아니다.
> 한편 다음 식도 일반적으로 참이지만, 역시 필수는 아니다.
>
> x.clone().equals(x)
>
> 관례상, 이 메서드가 반환하는 객체는 super.clone을 호출해 얻어야 한다. 이 클래스와 (Object를 제외한) 모든 상위 클래스가 이 관례를 따른다면 다음 식은 참이다.
>
> x.clone().getClass() == x.getClass()
>
> 관례상, 반환된 객체와 원본 객체는 독립적이어야 한다. 이를 만족하려면 super.clone으로 얻은 객체의 필드 중 하나 이상을 반환 전에 수정해야 할 수도 있다.

- 강제성이 없다. 따라서 clone 메서드가 super.clone이 아닌, 생성자를 호출해 얻은 인스턴스를 반환해도 컴파일러는 문제가 없다고 판단한다.
- 하지만 이 클래스의 하위 클래스에서 super.clone을 호출한다면 잘못된 클래스의 객체가 만들어져 결국 하위 클래스의 clone 메서드가 제대로 동작하지 않게 된다.
    - 클래스 B가 클래스 A를 상속할 때, 하위 클래스인 B의 clone은 B 타입 객체를 반환해야 한다. 그런데 A의 clone이 자신의 생성자, 즉 new A(...)로 생성한 객체를 반환한다면 B의
      clone도 A 타입 객체를 반환할 수 밖에 없다. 달리 말해 super.clone을 연쇄적으로 호출하도록 구현해두면 clone이 처음 호출된 하위 클래스의 객체가 만들어진다.
- clone을 재정의한 클래스가 final이라면 걱정해야 할 하위 클래스가 없으니 이 관례는 무시해도 안전하다.
- 하지만 final 클래스의 clone 메서드가 super.clone을 호출하지 않는다면 Cloneable을 구현할 이유도 없다. Object의 clone 구현의 동작 방식에 기댈 필요가 없기 때문이다.

## 가변 상태를 참조하지 않는 클래스용 clone 메서드

```java
public class PhoneNumber implements Cloneable {
    @Override
    protected PhoneNumber clone() {
        try {
            return (PhoneNumber) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(); // 일어날 수 없는 일이다.
        }
    }
}
```

#### 쓸데없는 복사를 지양한다는 관점에서 보면 불변 클래스는 굳이 clone 메서드를 제공하지 않는 것이 좋다.

- 이 메서드가 동작하게 하려면 PhoneNumber의 클래스 선언에 Cloneable을 구현한다고 추가해야 한다.
- Object의 clone 메서드는 Object를 반환하지만 PhoneNumber의 clone 메서드는 PhoneNumber를 반환한다.
- 자바가 공변 반환 타이핑(covariant return typing)을 지원하니 이렇게 하는 것이 가능하고 권장하는 방식이다.
- 재정의한 메서드의 반환 타입은 상위 클래스의 메서드가 반환하는 타입의 하위 타입일 수 있다. 이 방식으로 클라이언트가 형변환하지 않아도 되게끔 한다.

## 가변 상태를 참조하는 클래스용 clone 메서드

**clone 메서드는 사실상 생성자와 같은 효과를 낸다. 즉, clone은 원본 객체에 아무런 해를 끼치지 않는 동시에 복제된 객체의 불변식을 보장해야 한다.**
그래서 Stack의 clone 메서드는 제대로 동작하려면 스택 내부 정보를 복사해야 한다. 이때 가장 쉬운 방법은 elements 배열의 clone을 재귀적으로 호출해주는 것이다.

```java
public class Stack implements Cloneable { // 편의상 class 내부를 간소화했다.
    private Object[] elements;

    @Override
    protected Stack clone() {
        try {
            Stack result = (Stack) super.clone();
            result.elements = elements.clone();
            return result;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
```

- elements.clone의 결과를 Object[]로 형변환할 필요는 없다.
- 배열의 clone은 런타임 타입과 컴파일타임 타입 모두가 원본 배열과 똑같은 배열을 반환한다.
- 따라서 배열을 복제할 때는 배열의 clone 메서드를 사용하라고 권장한다.
- 배열은 clone 기능을 제대로 사용하는 유일한 예라고 할 수 있다.

### elements 필드가 final일 경우

- elements 필드가 final이라면 앞선 방식은 동작하지 않는다. final 필드에는 새로운 값을 할당할 수 없기 때문이다.
- 이는 근본적인 문제로, 직렬화와 마찬가지로 **Cloneable 아키텍처는 '가변 객체를 참조하는 필드는 final로 선언하라'는 일반 용법과 충돌한다**(단, 원본과 복제된 객체가 그 가변객체를 공유해도
  안전하다면 괜찮다). 그래서 복제할 수 있는 클래스를 만들기 위해 일부 필드에서 final 한정자를 제거해야 할 수도 있다.

## 생성자에서는 재정의될 수 있는 메서드를 호출하지 않아야 한다.

- 이는 clone 메서드도 마찬가지다. 만약 clone이 하위 클래스에서 재정의한 메서드를 호출하면, 하위 클래스는 복제 과정에서 자신의 상태를 교정할 기회를 잃게 되어 원본과 복제본의 상태가 달라질 가능성이
  크다.
    - 따라서 메서드를 호출하더라도 해당 메서드는 재정의할 수 없는 final이거나 private 메서드여야 한다.
- Object의 clone 메서드는 CloneNotSupportedException을 던지지만 재정의한 메서드는 그렇지 않다. **public인 clone 메서드에서는 throws 절을 없애야 한다.** 검사
  예외를 던지지 않아야 그 메서드를 사용하기 편하기 때문이다.

## 상속해서 쓰기 위한 클래스 설계 방식 두 가지 중 어느 쪽에서든, 상속용 클래스는 Cloneable을 구현해서는 안 된다.

- Object의 방식을 모방하여, 제대로 작동하는 clone 메서드를 구현해 protected로 두고 CloneNotSupportedException도 던질 수 있다고 선언한다.
    - 이 방식은 Object를 바로 상속할 때처럼 Cloneable 구현 여부를 하위 클래스에서 선택하도록 해준다.
- 다른 방법으로는, clone을 동작하지 않게 구현해놓고 하위 클래스에서 재정의하지 못하게 할 수도 있다.

### 하위 클래스에서 Cloneable을 지원하지 못하게 하는 clone 메서드

```java
public class CloneNotSupportedExample implements Cloneable {
    @Override
    protected final Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }
}
```

## Cloneable을 구현한 스레드 안전 클래스를 작성할 때는 clone 메서드 역시 적절히 동기화해줘야 한다.

- Object의 clone 메서드는 동기화를 신경 쓰지 않았다. 따라서 super.clone 호출 외에 다른 할 일이 없더라도 clone을 재정의하고 동기화해줘야 한다.

## Cloneable을 구현하는 모든 클래스는 clone을 재정의해야 한다.

- 이때 접근 제한자는 public으로, 반환 타입은 클래스 자신으로 변경한다.
- 이 메서드는 가장 먼저 super.clone을 호출한 후 필요한 필드를 전부 적절히 수정한다.
- 일반적으로 이 말은 그 객체의 내부 '깊은 구조'에 숨어 있는 모든 가변 객체를 복사하고, 복제본이 가진 객체 참조 모두가 복사된 객체들을 가리키게 함을 뜻한다.

## 복사 생성자와 복사 팩터리

- Cloneable을 이미 구현한 클래스를 확장한다면 어쩔 수 없이 clone을 잘 작동하도록 구현해야 한다.
- 그렇지 않은 상황에서는 **복사 생성자와 복사 팩터리라는 더 나은 객체 복사 방식을 제공할 수 있다.**

### 복사 생성자

```java
public class Yum {

    public Yum(Yum yum) {
        // ...
    }
}
```

- 복사 생성자란 단순히 자신과 같은 클래스의 인스턴스를 인수로 받는 생성자를 말한다.

### 복사 팩터리

```java
public class Yum {
    public static Yum newInstance(Yum yum) {
        // ...
        return new Yum(); // 컴파일을 위해 편의상 작성했다.
    }
}
```

- 복사 팩터리는 복사 생성자를 모방한 정적 팩터리다.

### 복사 생성자, 복사 팩터리 vs Cloneable/clone

복사 생성자와 그 변형인 복사 팩터리는 Cloneable/clone 방식보다 나은 면이 많다.

- 언어 모순적이고 위험천만한 객체 생성 메커니즘(생성자를 쓰지 않는 방식)을 사용하지 않는다.
- 엉성하게 문서화된 규약에 기대지 않는다.
- 정상적인 final 필드 용법과도 충돌하지 않는다.
- 불필요한 검사 예외를 던지지도 않는다.
- 형변환이 필요하지 않다.

복사 생성자와 복사 팩터리는 해당 클래스가 구현한 '인터페이스' 타입의 인스턴스를 인수로 받을 수 있다.

- 모든 범용 컬렉션 구현체는 Collection이나 Map 타입을 받는 생성자를 제공한다.
- 인터페이스 기반 복사 생성자와 복사 팩터리의 더 정확한 이름은 '변환 생성자(conversion constructor)'와 '변환 팩터리(conversion factory)'다.
- 이들을 이용하면 클라이언트는 원본의 구현 타입에 얽매이지 않고 복제본의 타입을 직접 선택할 수 있다.

## 추가 정리 - 믹스인

- 믹스인은 다른 클래스의 부모클래스가 되지 않으면서 다른 클래스에서 사용할 수 있는 메서드를 포함하는 클래스이다.
- 믹스인이란 클래스가 구현할 수 있는 타입으로, 믹스인을 구현한 클래스에 원래의 '주된 타입' 외에도 특정 선택적 행위를 제공한다고 선언하는 효과를 준다.
- 대상 타입의 주된 기능에 선택적 기능을 '혼합(mixed in)'한다고 해서 믹스인이라 부른다.
- 쉽게 말하자면, 부모로써 자식에게 동작과 기능, 그리고 정체성까지 모두 물려주는 게 아니라 총을 손에 쥐면 단순히 총을 쏠 수 있게 되는거처럼 동작, 기능만을 제공한다.
- 자바는 다중 상속을 지원하지 않기 때문에, 인터페이스의 구현을 통해서 이러한 개념을 사용하고 있다.
- 즉, 클래스가 인터페이스를 구현함으로써 인터페이스에 작성된 기능을 해당 클래스에 '혼합'하는 것이다.

## 핵심 정리

Cloneable이 몰고 온 모든 문제를 되짚어봤을 때, 새로운 인터페이스를 만들 때는 절대 Cloneable을 확장해서는 안 되며, 새로운 클래스도 이를 구현해서는 안 된다. final 클래스라면
Cloneable을 구현해도 위험이 크지 않지만, 성능 최적화 관점에서 검토한 후 별다른 문제가 없을 때만 드물게 허용해야 한다. 기본 원칙은 '복제 기능은 생성자와 팩터리를 이용하는 게 최고'라는 것이다. 단,
배열만은 clone 메서드 방식이 가장 깔끔한, 이 규칙의 합당한 예외라 할 수 있다.

## 참고자료

- [An example of a mixin in Java? [duplicate]](https://stackoverflow.com/questions/17987704/an-example-of-a-mixin-in-java)
- [Implement Mixin In Java? [closed]](https://stackoverflow.com/questions/587458/implement-mixin-in-java)
- [믹스인(mixin)](https://ko.wikipedia.org/wiki/%EB%AF%B9%EC%8A%A4%EC%9D%B8)
- [mixin이란](https://applefarm.tistory.com/133)
- [Covariant Return Type](https://joochang.tistory.com/75)
- [공변반환 타이핑](https://bperhaps.tistory.com/entry/%EA%B3%B5%EB%B3%80%EB%B0%98%ED%99%98-%ED%83%80%EC%9D%B4%ED%95%91?category=724216)