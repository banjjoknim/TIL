# 아이템 1. 생성자 대신 정적 팩터리 메서드를 고려하라

클라이언트가 클래스의 인스턴스를 얻는 전통적인 수단은 public 생성자다. 하지만 모든 프로그래머가 꼭 알아둬야 할 기법이 하나 더 있다. 클래스는 생성자와 별도로 정적 팩터리 메서드(static factory
method)를 제공할 수 있다.

클래스는 클라이언트에 public 생성자 대신 (혹은 생성자와 함께) 정적 팩터리 메서드를 제공할 수 있다. 이 방식에는 장점과 단점이 모두 존재한다.

## 정적 팩터리 메서드 - 장점

### 1. 이름을 가질 수 있다.

- 정적 팩터리 메서드는 생성자보다 반환될 객체의 특성을 잘 설명할 수 있다.

```java
public class Client {
    public static void main(String[] args) {
        Person person = new Person("Hello", 24);
        Person.fromNameAndAge("Hello", 24);
    }
}

```

### 2. 호출될 때마다 인스턴스를 새로 생성하지 않아도 된다.

- 이 덕분에 불변 클래스는 인스턴스를 미리 만들어 놓거나 새로 생성한 인스턴스를 캐싱하여 재활용하는 식으로 불필요한 객체 생성을 피할 수 있다.
- 따라서 (특히 생성 비용이 큰) 같은 객체가 자주 요청되는 상황이라면 성능을 상당히 끌어올려 준다.
- 이를 이용해 인스턴스를 통제할 수 있고, 인스턴스를 통제하면 클래스를 싱글턴으로 만들 수도, 인스턴스화 불가로 만들 수도 있다.
- 이와 비슷한 플라이웨이트 패턴(Flyweight pattern)이라는 기법도 있다.

```java
public class Person {
    private static final Person COLT = new Person("Colt", 28);

    private String name;
    private int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public static Person getColt() {
        return COLT;
    }
}

public class Client {
    public static void main(String[] args) {
        Person colt = Person.getColt(); // Person 객체 내부의 상수 객체인 COLT 반환
    }
}
```

### 3. 반환 타입의 하위 타입 객체를 반환할 수 있는 능력이 있다.

- 반환할 객체의 클래스를 자유롭게 선택할 수 있게 해주는 유연성을 제공한다.

```java
public class Person {

    private String name;
    private int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public static Colt createColtWithNameAndAge(String name, int age) {
        return new Colt(name, age);
    }
}

public class Client {
    public static void main(String[] args) {
        Colt person1 = Person.createColtWithNameAndAge("Colt", 28);

        Colt person2 = new Person("Colt", 28); // 컴파일 에러가 발생한다.
    }
}
```

### 4. 입력 매개변수에 따라 매번 다른 클래스의 객체를 반환할 수 있다.

- 반환 타입의 하위 타입이기만 하면 어떤 클래스의 객체를 반환하든 상관없다.
- 대표적인 예로 EnumSet이 있다.
- 클라이언트는 팩터리가 건네주는 객체가 어느 클래스의 인스턴스인지 알 수도 없고 알 필요도 없다.
- 단지 EnumSet의 하위 클래스이기만 하면 된다.

```java
public abstract class EnumSet<E extends Enum<E>> extends AbstractSet<E>
        implements Cloneable, java.io.Serializable {

    // ...

    public static <E extends Enum<E>> EnumSet<E> noneOf(Class<E> elementType) {
        Enum<?>[] universe = getUniverse(elementType);
        if (universe == null)
            throw new ClassCastException(elementType + " not an enum");

        if (universe.length <= 64) // 클라이언트는 RegularEnumSet이 반환되는지 JumboEnumSet이 반환되는지 몰라도 된다. 그냥 알아서 상황에 맞게 반환될 뿐이다.
            return new RegularEnumSet<>(elementType, universe);
        else
            return new JumboEnumSet<>(elementType, universe);
    }
}
```

### 5. 정적 팩터리 메서드를 작성하는 시점에는 반환할 객체의 클래스가 존재하지 않아도 된다.

- 이런 유연함은 서비스 제공자 프레임워크를 만드는 근간이 된다.
- 대표적인 서비스 제공자 프레임워크로는 JDBC(Java Database Connectivity)가 있다.
- 서비스 제공자 프레임워크에서의 제공자는(provider)는 서비스의 구현체다.
- 그리고 이 구현체들을 클라이언트에 제공하는 역할을 프레임워크가 통제하여, 클라이언트를 구현체로부터 분리해준다.

#### 5-1. 서비스 제공자 프레임워크의 핵심 컴포넌트들

- 구현체의 동작을 정의하는 서비스 인터페이스(service interface)
- 제공자가 구현체를 등록할 때 사용하는 제공자 등록 API(provider registration API)
- 클라이언트가 서비스의 인스턴스를 얻을 때 사용하는 서비스 접근 API(service access API) -> 유연한 정적 팩터리의 실체.
- 서비스 제공자 인터페이스(service provider interface)

서비스 제공자 인터페이스가 없다면 각 구현체를 인스턴스로 만들 때 리플렉션을 사용해야 한다.

```
ex. JDBC에서 각각의 컴포넌트 및 역할

- Connection -> 서비스 인터페이스 역할
- DriverManager.registerDriver -> 제공자 등록 API 역할
- DriverManager.getConnection -> 서비스 접근 API 역할
- Driver -> 서비스 제공자 인터페이스 역할
```

```java
public class Client {
    public static void main(String[] args) {
        Class.forName("oracle.jdbc.driver.OracleDriver"); // Class.forName()만 호출하면 DriverManager에 인수로 명시해준 Driver가 등록된다.
        Connection conn = null;
        conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:ORA92", "scott", "tiger");
        // Statement..
    }
}
```

```java
public final class Class<T> implements java.io.Serializable,
        GenericDeclaration,
        Type,
        AnnotatedElement {
    // ...

    @CallerSensitive
    public static Class<?> forName(String className)
            throws ClassNotFoundException {
        Class<?> caller = Reflection.getCallerClass();
        return forName0(className, true, ClassLoader.getClassLoader(caller), caller);
    }
}
```

> Service Provider Interface는 Driver, Connection 인터페이스와 실제 그 인터페이스를 구현하는 구현체 클래스가 완전히 분리 되어 제공된다는 것이 포인트이다.
> 인터페이스를 사용해 틀을 만들어 놓고, 그 틀에 맞춰서 각각의 서비스 제공자들이 자신의 서비스에 맞춰서 구현 클래스를 제공하도록 하는 것이다.
>
>*- [[Java] Class.forName(String className) 그리고 Service Provider Interface](https://devyongsik.tistory.com/294)*

## 정적 팩터리 메서드 - 단점

### 1. 상속을 하려면 public이나 protected 생성자가 필요하니 정적 팩터리 메서드만 제공하면 하위 클래스를 만들 수 없다.

- 앞서 이야기한 컬렉션 프레임워크의 유틸리티 구현 클래스들은 상속할 수 없다.
- 상속을 하려면 public, protected 생성자가 필요한데, private 생성자로 변경하고 정적 팩터리만 제공할 때 해당된다.

### 2. 정적 팩터리 메서드는 프로그래머가 찾기 어렵다.

- 생성자처럼 API 설명에 명확히 드러나지 않으니 사용자는 정적 팩터리 메서드 방식 클래스를 인스턴스화할 방법을 알아내야 한다.

## 정적 팩터리 메서드에 흔히 사용하는 명명 방식

- from : 매개변수를 하나 받아서 해당 타입의 인스턴스를 반환하는 형변환 메서드
- of : 여러 매개변수를 받아 적합한 타입의 인스턴스를 반환하는 집계 메서드
- valueOf : from과 of의 더 자세한 버전
- instance 혹은 getInstance : (매개변수를 받는다면) 매개변수로 명시한 인스턴스를 반환하지만, 같은 인스턴스임을 보장하지는 않는다.
- create 혹은 newInstance : instance 혹은 getInstance와 같지만, 매번 새로운 인스턴스를 생성해 반환함을 보장한다.
- getType : getInstacne와 같으나, 생성할 클래스가 아닌 다른 클래스에 팩터리 메서드를 정의할 때 쓴다. "Type"은 팩터리 메서드가 반환할 객체의 타입이다.
- newType : newInstance와 같으나, 생성할 클래스가 아닌 다른 클래스에 팩터리 메서드를 정의할 때 쓴다. "Type"은 팩터리 메서드가 반환할 객체의 타입이다.
- type : getType과 newType의 간결한 버전

## 핵심 정리

정적 팩터리 메서드와 public 생성자는 각자의 쓰임새가 있으니 상대적인 장단점을 이해하고 사용하는 것이 좋다. 그렇다고 하더라도 정적 팩터리를 사용하는 게 유리한 경우가 더 많으므로 무작정 public 생성자를
제공하던 습관이 있다면 고치자.

---

# 참고자료

- [[Java] Class.forName(String className) 그리고 Service Provider Interface](https://devyongsik.tistory.com/294)*
- [Java Database Connectivity](https://en.wikipedia.org/wiki/Java_Database_Connectivity)
- [[구조 패턴] 플라이웨이트 패턴(Flyweight Pattern) 이해 및 예제](https://readystory.tistory.com/137)
- [[디자인패턴/Design Pattern] Flyweight Pattern / 플라이웨이트 패턴](https://lee1535.tistory.com/106)
