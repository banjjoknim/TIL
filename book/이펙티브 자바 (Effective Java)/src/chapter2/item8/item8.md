# 아이템 8. finalizer와 cleaner 사용을 피하라

## 자바의 두 가지 객체 소멸자

- 자바는 두 가지 객체 소멸자를 제공한다.
- **그중 finalizer는 예측할 수 없고, 상황에 따라 위험할 수 있어 일반적으로 불필요하다.**
- **cleaner는 finalizer보다는 덜 위험하지만, 여전히 예측할 수 없고, 느리고, 일반적으로 불필요하다.**
- 자바에서는 접근할 수 없게 된 객체를 회수하는 역할을 가비지 컬렉터가 담당하고, 프로그래머에게는 아무것도 요구하지 않는다.
- 자바에서는 try-with-resources와 try-finally를 사용해 자원 회수를 해결한다.

## finalizer & cleaner

- finalizer와 cleaner는 즉시 수행된다는 보장이 없다.
- **즉, finalizer와 cleaner로는 제때 실행되어야 하는 작업은 절대 할 수 없다.**
- 자바 언어 명세는 어떤 스레드가 finalizer를 수행할지 명시하지 않으니 이 문제를 해결할 보편적인 해법은 없다. 딱 하나, finalizer를 사용하지 않는 방법 뿐이다.
- 한편, cleaner는 자신을 수행할 스레드를 제어할 수 있다는 면에서 조금 낫다. 하지만 여전히 백그라운드에서 수행되며 가비지 컬렉터의 통제하에 있으니 즉각 수행되리라는 보장은 없다.
- 자바 언어 명세는 finalizer나 cleaner의 수행 시점뿐 아니라 수행 여부조차 보장하지 않는다.
- 따라서 프로그램 생애주기와 상관없는, **상태를 영구적으로 수정하는 작업에서는 절대 finalizer나 cleaner에 의존해서는 안 된다.**

### finalizer & cleaner의 성능 문제

- finalizer는 가비지 컬렉터의 성능을 떨어뜨린다.
- cleaner도 클래스의 모든 인스턴스를 수거하는 형태로 사용하면 성능은 finalizer와 비슷하다.
- 하지만 안전망의 형태로만 사용한다면 훨씬 빨라진다. 그럼에도 불구하고 AutoCloseable 객체를 사용하는 것보다 느리다.

### finalizer의 보안 문제

- **finalizer를 사용한 클래스는 finalizer 공격에 노출되어 심각한 보안 문제를 일으킬 수도 있다.**
- finalizer의 공격 원리는 간단하다. 생성자나 직렬화 과정에서 예외가 발생하면, 이 생성되다 만 객체에서 악의적인 하위 클래스의 finalizer가 수행될 수 있게 된다.
- 이 finalizer는 정적 필드에 자신의 참조를 할당하여 가비지 컬렉터가 수집하지 못하게 막을 수 있다.
- 이렇게 일그러진 객체가 만들어지고 나면, 이 객체의 메서드를 호출해 애초에는 허용되지 않았을 작업을 수행할 수 있게 된다.
- **객체 생성을 막으려면 생성자에서 예외를 던지는 것만으로 충분하지만, finalizer가 있으면 그렇지도 않다.**
- final 클래스들은 하위 클래스를 만들 수 없으니 이 공격에서 안전하다.
- **final이 아닌 클래스들을 finalizer 공격으로부터 방어하려면 아무 일도 하지 않는 finalize 메서드를 만들고 final로 선언하면 된다.**

## finalizer, cleaner를 대신할 묘안. AutoCloseable

- **AutoCloseable**을 구현해주고, 클라이언트에서 인스턴스를 다 쓰고 나면 close 메서드를 호출하면 된다.
- 이때, 일반적으로 예외가 발생해도 제대로 종료되도록 try-with-resources를 사용해야 한다.
- 구체적인 구현에 대하여, 각 인스턴스는 자신이 닫혔는지를 추적하는 것이 좋다.
- 즉, close 메서드에서 이 객체는 더 이상 유효하지 않음을 필드에 기록하고, 다른 메서드는 이 필드를 검사해서 객체가 닫힌 후에 불렸다면 IllegalStateException을 던지도록 한다.

## cleaner를 안전망으로 활용하는 AutoCloseable 클래스

```java
public class Room implements AutoCloseable {
    private static final Cleaner cleaner = Cleaner.create();

    // 청소가 필요한 자원. 절대 Room을 참조해서는 안 된다!
    private static class State implements Runnable {
        int numJunkPiles;

        State(int numJunkPiles) {
            this.numJunkPiles = numJunkPiles;
        }

        // close 메서드나 cleaner가 호출한다.
        @Override
        public void run() {
            System.out.println("방 청소");
            numJunkPiles = 0;
        }
    }

    // 방의 상태. cleanable과 공유한다.
    private final State state;

    // cleanable 객체. 수거 대상이 되면 방을 청소한다.
    private final Cleaner.Cleanable cleanable;

    public Room(int numJunkPiles) {
        this.state = new State(numJunkPiles);
        this.cleanable = cleaner.register(this, state);
    }


    @Override
    public void close() throws Exception {
        cleanable.clean();
    }
}
```

- State 인스턴스는 '절대로' Room 인스턴스를 참조해서는 안 된다. Room 인스턴스를 참조할 경우 순환참조가 생겨 가비지 컬렉터가 Room 인스턴스를 회수해갈 기회가 오지 않는다.
- State가 정적 중첩 클래스인 이유가 여기에 있다. 정적이 아닌 중첩 클래스는 자동으로 바깥 객체의 참조를 갖게 되기 때문이다.
- 이와 비슷하게 람다 역시 바깥 객체의 참조를 갖기 쉬우니 사용하지 않는 것이 좋다.

## 잘 짜인 클라이언트 코드

```java
public class Adult {
    public static void main(String[] args) {
        try (Room room = new Room(7)) {
            System.out.println("안녕~");
        }
    }
}
```

## 잘못된(결코 방 청소를 하지 않는) 클라이언트 코드

```java
import chapter2.item8.Room;

public class Adult {
    public static void main(String[] args) {
        Room room = new Room(99);
        System.out.println("아무렴");
    }
}
```

- cleaner의 명세에는 아래와 같이 쓰여 있으며, 이는 일반적인 프로그램 종료에서도 마찬가지다.
  > System.exit를 호출할 때의 cleaner 동작은 구현하기 나름이다. 청소가 이뤄질지는 보장하지 않는다.

## 핵심 정리
cleaner(자바 8까지는 finalizer)는 안전망 역할이나 중요하지 않은 네이티브 자원 회수용으로만 사용하자. 물론 이런 경우라도 불확실성과 성능 저하에 주의해야 한다.