# 아이템 3. private 생성자나 열거 타입으로 싱글턴임을 보증하라

- 싱글턴(singleton)이란 인스턴스를 오직 하나만 생성할 수 있는 클래스를 말한다.
- 싱글턴의 전형적인 예로는 함수와 같은 무상태(stateless) 객체나 설계상 유일해야 하는 시스템 컴포넌트를 들 수 있다.
- **클래스를 싱글턴으로 만들면 이를 사용하는 클라이언트를 테스트하기가 어려워질 수 있다.** 타입을 인터페이스로 정의한 다음 그 인터페이스를 구현해서 만든 싱글턴이 아니라면 싱글턴 인스턴스를 가짜(mock)
  구현으로 대체할 수 없기 때문이다.

## 싱글턴을 만드는 방식

- 싱글턴을 만드는 방식은 보통 둘 중 하나다. 두 방식 모두 생성자는 private으로 감춰두고, 유일한 인스턴스에 접근할 수 있는 수단으로 public static 멤버를 하나 마련해둔다.

### public static final 필드 방식의 싱글턴

```java
public class Elvis {
    public static final Elvis INSTANCE = new Elvis();

    private Elvis() {
    }

    public void doSomeThing() {
        // ...
    }
}
```

- 해당 클래스가 싱글턴임이 API에 명백히 드러난다.
- public static 필드가 final이니 절대로 다른 객체를 참조할 수 없다.
- 간결하다.

### 정적 팩터리 방식의 싱글턴

```java
public class Elvis {
    private static final Elvis INSTANCE = new Elvis();

    private Elvis() {

    }

    public static Elvis getInstance() {
        return INSTANCE;
    }
}
```

- API를 바꾸지 않고도 싱글턴이 아니게 변경할 수 있다(변화에 대해 유연하다).
- 원한다면 정적 팩터리를 제네릭 싱글턴 팩터리로 만들 수 있다.
- 정적 팩터리의 메서드 참조를 공급자(supplier)로 사용할 수 있다.

### 열거 타입 방식의 싱글턴 - 바람직한 방법

```java
public enum Elvis {
    INSTANCE;

    public void doSomeThing() {
        // ...
    }
}
```

- public 필드 방식과 비슷하지만, 더 간결하고, 추가 노력 없이 직렬화할 수 있다.
- 아주 복잡한 직렬화 상황이나 리플렉션 공격에서도 제2의 인스턴스가 생기는 일을 완벽히 막아준다.
- **대부분 상황에서는 원소가 하나뿐인 열거 타입이 싱글턴을 만드는 가장 좋은 방법이다.**
- 단, 만들려는 싱글턴이 Enum 외의 클래스를 상속해야 한다면 이 방법은 사용할 수 없다(열거 타입이 다른 인터페이스를 구현하도록 선언할 수는 있다).
