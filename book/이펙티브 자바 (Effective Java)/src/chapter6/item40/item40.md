# 아이템 40. @Override 애너테이션을 일관되게 사용하라

@Override는 메서드 선언에만 달 수 있으며, 이 애너테이션이 달렸다는 것은 상위 타입의 메서드를 재정의했음을 뜻한다. 이 애너테이션을 일관되게 사용하면 여러 가지 악명 높은 버그들을 예방해준다.

## 영어 알파벳 2개로 구겅된 문자열을 표현하는 클래스 - 버그를 찾아보자.

```java
public class Bigram {
    private final char first;
    private final char second;

    public Bigram(char first, char second) {
        this.first = first;
        this.second = second;
    }

    public boolean equals(Bigram bigram) {
        return bigram.first == first && bigram.second == second;
    }

    public int hashCode() {
        return 31 * first + second;
    }

    public static void main(String[] args) {
        Set<Bigram> s = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            for (char ch = 'a'; ch <= 'z'; ch++) {
                s.add(new Bigram(ch, ch));
            }
        }
        System.out.println(s.size());
    }
}
```

- Set은 중복을 허용하지 않으니 26이 출력될 거 같지만, 실제로는 260이 출력된다!
- equals를 '재정의(overriding)'한 게 아니라 '다중정의(overloading)'해버렸다.
    - Object를 재정의하려면 매개변수 타입을 Object로 해야만 한다.
    - 따라서 Object에서 상속한 equals와는 별개인 equals를 새로 정의한 꼴이 되었다.
    - Object의 equals는 == 연산자와 똑같이 객체 식별성(identity)만을 확인한다.

## @Override를 일관성 있게 사용하자

- 위 오류는 컴파일러가 찾아낼 수 있지만, 그러려면 Object.equals를 재정의한다는 의도를 명시해야 한다.
- 즉, @Override 애너테이션을 사용하면 컴파일 오류를 통해 사고를 방지할 수 있다.
- 따라서, **상위 클래스의 메서드를 재정의하려는 모든 메서드에 @Override 애너테이션을 달도록 해야한다.**
    - 예외는 한 가지뿐이다. 구체 클래스에서 상위 클래스의 추상 메서드를 재정의할 때는 굳이 @Override를 달지 않아도 된다.
    - 구체 클래스인데 아직 구현하지 않은 추상 메서드가 남아 있다면 컴파일러가 그 사실을 바로 알려주기 때문이다.
    - 물론 재정의 메서드 모두에 @Override를 일괄로 붙여두어도 상관없다.
- @Override를 일관되게 사용한다면 실수로 재정의했을 때 경고해줄 것이다. 이는 재정의할 의도였으나 실수로 새로운 메서드를 추가했을 때 알려주는 컴파일 오류의 보완재 역할로 보면 된다.
- @Override는 클래스뿐 아니라 인터페이스의 메서드를 재정의할 때도 사용할 수 있다.
    - 디폴트 메서드를 지원하기 시작하면서, 인터페이스 메서드를 구현한 메서드에도 @Override를 다는 습관을 들이면 시그니처가 올바른지 재차 확신할 수 있다.
    - 구현하려는 인터페이스에 디폴트 메서드가 없음을 안다면 이를 구현한 메서드에서는 @Override를 생략해 코드를 조금 더 깔끔히 유지해도 좋다.
    - 다만, 추상 클래스나 인터페이스에서는 상위 클래스나 상위 인터페이스의 메서드를 재정의하는 모든 메서드에 @Override를 다는 것이 좋다. 상위 클래스가 구체 클래스든 추상 클래스든 마찬가지다.
        - ex. Set 인터페이스는 Collection 인터페이스를 확장했지만 새로 추가한 메서드는 없다. 따라서 모든 메서드 선언에 @Override를 달아 실수로 추가한 메서드가 없음을 보장했다.

## 핵심 정리

- 재정의한 모든 메서드에 @Override 애너테이션을 의식적으로 달면 실수했을 때 컴파일러가 알려줄 것이다.
- 예외는 한 가지뿐이다. 구체 클래스에서 상위 클래스의 추상 메서드를 재정의한 경우엔 이 애너테이션을 달지 않아도 괜찮다.
- 그래도 의식적으로 달려고 해보자. 일관성이 있으면.. 좋지 않을까?