# 아이템 5. 자원을 직접 명시하지 말고 의존 객체 주입을 사용하라

## 정적 유틸리티 클래스를 잘못 사용한 예 - 유연하지 않고 테스트하기 어렵다.

```java
public class SpellChecker {
    private static final Lexicon DICTIONARY = null; // 편의상 null로 초기화했다.

    private SpellChecker() {

    } // 객체 생성 방지

    public static boolean isValid(String word) {
        // someThing
        return true; // 편의상 이렇게 작성했다.
    }

    public static List<String> suggestions(String typo) {
        // someThing
        return Collections.emptyList(); // 편의상 이렇게 작성했다.
    }
}
```

## 싱글턴을 잘못 사용한 예 - 유연하지 않고 테스트하기 어렵다.

```java
public class SpellChecker {
    private final Lexicon dictionary = null;

    private SpellChecker() {

    }

    public static SpellChecker INSTANCE = new SpellChecker();

    public boolean isValid(String word) {
        return true; // 편의상 이렇게 작성했다.
    }

    public List<String> suggestions(String typo) {
        return Collections.emptyList(); // 편의상 이렇게 작성했다.
    }
}
```

## 만약 SpellChecker가 여러 사전을 사용할 수 있도록 하려면?

- dictionary 필드에서 final 한정자를 제거하고 다른 사전으로 교체하는 메서드를 추가할 수 있지만, 이 방식은 어색하고 오류를 내기 쉬우며 멀티스레드 환경에서는 쓸 수 없다.
- **사용하는 자원에 따라 동작이 달라지는 클래스에는 정적 유틸리티 클래스나 싱글턴 방식이 적합하지 않다.**
- **인스턴스를 생성할 때 생성자에 필요한 자원을 넘겨주는 방식**을 사용하면 클라이언트가 원하는 자원을 사용할 수 있게 할 수 있다.

## 의존 객체 주입은 유연성과 테스트 용이성을 높여준다.

```java
public class SpellChecker {
    private final Lexicon dictionary;

    public SpellChecker(Lexicon dictionary) {
        this.dictionary = Objects.requireNonNull(dictionary);
    }

    public boolean isValid(String word) {
        return true; // 편의상 이렇게 작성했다.
    }

    public List<String> suggestions(String typo) {
        return Collections.emptyList(); // 편의상 이렇게 작성했다.
    }
}
```

- 의존 객체 주입 패턴은 자원이 몇 개든 의존 관계가 어떻든 상관없이 잘 작동한다.
- 불변을 보장하여 (같은 자원을 사용하려는) 여러 클라이언트가 의존 객체들을 안심하고 공유할 수 있기도 하다.
- 의존 객체 주입은 생성자, 정적 팩터리, 빌더 모두에 똑같이 응용할 수 있다.
- 이 패턴의 변형으로, 생성자에 자원 팩터리를 넘겨주는 방식이 있다. 즉, 팩터리 메서드 패턴(Factory Method Pattern)을 구현한 것이다.
- 팩터리란 호출할 때마다 특정 타입의 인스턴스를 반복해서 만들어주는 객체를 말한다.
- 의존 객체 주입이 유연성과 테스트 용이성을 개선해주긴 하지만, 의존성이 수천 개나 되는 큰 프로젝트에서는 코드를 어지럽게 만들 수도 있다.
- 스프링(Spring) 같은 의존 객체 주입 프레임워크를 사용하면 이런 어질러짐을 해소할 수 있다.

### Objects.requireNonNull(T obj)

```java
public final class Objects {
    // ...
    public static <T> T requireNonNull(T obj) {
        if (obj == null)
            throw new NullPointerException();
        return obj;
    }
    // ...
}
```

- 매개변수로 입력된 값이 null이면 NullPointerException을 던지고, 그게 아니라면 그대로 반환한다.

## 핵심 정리

클래스가 내부적으로 하나 이상의 자원에 의존하고, 그 자원이 클래스 동작에 영향을 준다면 싱글턴과 정적 유틸리티 클래스는 사용하지 않는 것이 좋다. 이 자원들을 클래스가 직접 만들게 해서도 안 된다. 대신 필요한
자원을 (혹은 그 자원을 만들어주는 팩터리를) 생성자에 (혹은 정적 팩터리나 빌더에) 넘겨주자. 의존 객체 주입이라 하는 이 기법은 클래스의 유연성, 재사용성, 테스트 용이성을 기막히게 개선해준다.
