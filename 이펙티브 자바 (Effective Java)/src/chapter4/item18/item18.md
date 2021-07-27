# 아이템 18. 상속보다는 컴포지션을 사용하라

상속은 코드를 재사용하는 강력한 수단이지만, 항상 최선은 아니다. 일반적인 구체 클래스를 패키지 경계를 넘어 다른 패키지의 구체 클래스를 상속하는 일은 위험하다. 이때의 '상속'은 다른 패키지의 구체 클래스를
확장하는 '구현 상속'을 말하며, 인터페이스의 상속과는 무관하다.

## 메서드 호출과는 달리 상속은 캡슐화를 깨뜨린다.

- 상위 클래스가 어떻게 구현되느냐에 따라 하위 클래스의 동작에 이상이 생길 수 있다.
- 상위 클래스는 릴리스마다 내부 구현이 달라질 수 있으며, 그 여파로 코드 한 줄 건드리지 않은 하위 클래스가 오동작할 수 있다.

### 잘못된 예 - 상속을 잘못 사용했다!

```java
public class InstrumentedHashSetUseExtends<E> extends HashSet {
    private int addCount = 0; // 추가된 원소의 수

    public InstrumentedHashSetUseExtends() {
    }

    public InstrumentedHashSetUseExtends(int initCap, float loadFactor) {
        super(initCap, loadFactor);
    }

    @Override
    public boolean add(Object o) {
        return super.add(o);
    }

    @Override
    public boolean addAll(Collection c) {
        return super.addAll(c);
    }

    public int getAddCount() {
        return addCount;
    }

}
```

- 위 클래스의 인스턴스에 addAll 메서드를 사용하면 제대로 작동하지 않는다.

```java
public class HashSet<E> extends AbstractSet<E> implements Set<E>, Cloneable, java.io.Serializable {
    // ...

    public HashSet(Collection<? extends E> c) {
        map = new HashMap<>(Math.max((int) (c.size() / .75f) + 1, 16));
        addAll(c); // super의 addAll 메서드를 호출한다.
    }

    // ...
}
```

```java
public abstract class AbstractCollection<E> implements Collection<E> {
    // ...

    public boolean addAll(Collection<? extends E> c) {
        boolean modified = false;
        for (E e : c)
            if (add(e))
                modified = true;
        return modified;
    }

    // ...
}
```

- HashSet의 addAll 메서드는 add 메서드를 사용해서 구현되어 있다.
- InstrumentedHashSetUseExtends의 addAll은 addCount를 더한 후, HashSet의 addAll을 호출한다.
- HashSet의 addAll은 각 원소를 add 메서드를 호출해 추가하는데, 이때 불리는 add는 InstrumentedHashSetUseExtends에서 재정의한 메서드다.
- 따라서 addCount의 값이 중복으로 더해지게 된다.

이 경우 하위 클래스에서 addAll 메서드를 재정의하지 않으면 문제를 고칠 수 있다. 하지만 HashSet의 addAll이 add 메서드를 이용해 구현했음을 가정한 해법이라는 한계를 가진다. 이처럼 자신의 다른
부분을 사용하는 '자기 사용(self-use)' 여부는 해당 클래스의 내부 구현 방식에 해당한다. 따라서 이런 가정에 기댄 InstrumentedHashSetUseExtends도 깨지기 쉽다.

addAll 메서드를 다른 식으로 재정의할 수도 있다. 하지만 상위 클래스의 메서드 동작을 다시 구현하는 것은 어렵고, 시간도 더 들고, 오류를 내거나 성능을 떨어뜨릴 수도 있다. 또한 하위 클래스에서는 접근할 수
없는 private 필드를 써야 하는 상황이라면 이 방식으로는 구현자체가 불가능하다.

### 다음 릴리스에서 상위 클래스에 새로운 메서드를 추가한다면?

- 새롭게 추가된 메서드를 통해서 허용되지 않은 동작을 수행할 수 있게 될 수도 있다.
- 만약 하위 클래스에 추가한 메서드와 상위 클래스에 새롭게 추가된 메서드의 시그니처가 같고 반환 타입이 다르다면 컴파일조차 되지 않는다.
    - 하위 클래스에 메서드를 작성할 때는 상위 클래스의 메서드는 존재하지도 않았으니, 하위 클래스의 메서드는 상위 클래스에 새롭게 추가된 메서드가 요구하는 규약을 지키지 못할 가능성이 크다.

## 이러한 문제들을 해결할 좋은 방법이 있다!

- 기존 클래스를 확장하는 대신, 새로운 클래스를 만들고 private 필드로 기존 클래스의 인스턴스를 참조하게 하면 된다.
    - 기존 클래스가 새로운 클래스의 구성요소로 쓰인다는 뜻에서 이러한 설계를 컴포지션(composition; 구성)이라 한다.
- 새 클래스의 인스턴스 메서드들은 (private 필드로 참조하는) 기존 클래스의 대응하는 메서드를 호출해 그 결과를 반환한다.
    - 이 방식을 전달(forwarding)이라 하며, 새 클래스의 메서드들을 전달 메서드(forwarding method)라 부른다.
    - 그 결과 새로운 클래스는 기존 클래스의 내부 구현 방식의 영향에서 벗어나며, 심지어 기존 클래스에 새로운 메서드가 추가되더라도 전혀 영향받지 않는다.

### 래퍼 클래스 - 상속 대신 컴포지션을 사용했다.

```java
public class InstrumentedHashSetUseComposition<E> extends ForwardingSet<E> {
    private int addCount = 0;

    public InstrumentedHashSetUseComposition(Set<E> s) {
        super(s);
    }

    @Override
    public boolean add(E e) {
        addCount++;
        return super.add(e);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        addCount += c.size();
        return super.addAll(c);
    }

    public int getAddCount() {
        return addCount;
    }
}
```

아래는 재사용할 수 있는 전달클래스인 ForwardingSet이다.

```java
public class ForwardingSet<E> implements Set<E> {
    private final Set<E> s;

    public ForwardingSet(Set<E> s) {
        this.s = s;
    }

    public int size() {
        return 0;
    }

    public boolean isEmpty() {
        return s.isEmpty();
    }

    public boolean contains(Object o) {
        return s.contains(o);
    }

    public Iterator<E> iterator() {
        return s.iterator();
    }

    public Object[] toArray() {
        return s.toArray();
    }

    public <T> T[] toArray(T[] a) {
        return s.toArray(a);
    }

    public boolean add(E e) {
        return s.add(e);
    }

    public boolean remove(Object o) {
        return s.remove(o);
    }

    public boolean containsAll(Collection<?> c) {
        return s.containsAll(c);
    }

    public boolean addAll(Collection<? extends E> c) {
        return s.addAll(c);
    }

    public boolean retainAll(Collection<?> c) {
        return s.retainAll(c);
    }

    public boolean removeAll(Collection<?> c) {
        return s.removeAll(c);
    }

    public void clear() {
        s.clear();
    }

    @Override
    public boolean equals(Object o) {
        return s.equals(o);
    }

    @Override
    public int hashCode() {
        return s.hashCode();
    }

    @Override
    public String toString() {
        return s.toString();
    }
}
```

- InstrumentedHashSetUseComposition은 HashSet의 모든 기능을 정의한 Set 인터페이스를 활용해 설계되어 견고하고 아주 유연하다.
- 임의의 Set에 계측 기능을 덧씌워 새로운 Set으로 만드는 것이 이 클래스의 핵심이다.

상속 방식은 구체 클래스 각각을 따로 확장해야 하며, 지원하고 싶은 상위 클래스의 생성자 각각에 대응하는 생성자를 별도로 정의해줘야 한다. 하지만 컴포지션 방식은 한 번만 구현해두면 어떠한 Set 구현체라도 계측할
수 있으며, 기존 생성자들과도 함께 사용할 수 있다.

- 다른 Set 인스턴스를 감싸고(wrap) 있다는 뜻에서 InstrumentedHashSetUseComposition 같은 클래스를 래퍼 클래스라고 한다.
- 다른 Set에 계측 기능을 덧씌운다는 뜻에서 데코레이터 패턴(Decorator pattern)이라고 부른다. 단, 엄밀히 따지면 래퍼 객체가 내부 객체에 자기 자신의 참조를 넘기는 경우만 위임에 해당한다.
- 래퍼 클래스는 단점이 거의 없다. 래퍼 클래스가 콜백(callback) 프레임워크와는 어울리지 않는다는 점만 주의하도록 하자.

#### 래퍼 클래스와 SELF 문제

- 콜백 프레임워크에서는 자기 자신의 참조를 다른 객체에 넘겨서 다음 호출(콜백) 때 사용하도록 한다.
- 내부 객체는 자신을 감싸고 있는 래퍼의 존재를 모르니 대신 자신(this)의 참조를 넘기고, 콜백 때는 래퍼가 아닌 내부 객체를 호출하게 되는데, 이를 SELF 문제라고 한다.

## 상속과 is-a

- 상속은 반드시 하위 클래스가 상위 클래스의 '진짜' 하위 타입인 상황에서만 쓰여야 한다.
- 즉, 클래스 B가 클래스 A와 is-a 관계일 때만 클래스 A를 상속해야 한다.
- 만약 is-a 관계가 아니라면 A는 B의 필수 구성요소가 아니라 구현하는 방법 중 하나일 뿐이다.

## 상속을 사용할 때는 주의해야 한다

- 컴포지션을 써야 할 상황에서 상속을 사용하는 건 내부 구현을 불필요하게 노출하는 것과 같다.
    - 클라이언트에서 노출된 내부에 직접 접근할 수 있게 된다.
    - 최악의 경우, 클라이언트에서 상위 클래스를 직접 수정하여 하위 클래스의 불변식을 해칠 수도 있다.
- 상속은 상위 클래스의 API를 '그 결함까지도' 승계하므로 주의해야 한다.

## 추가 정리

래퍼 클래스와 관련하여 알아두면 좋을 것들이 있다.

- [일급 컬렉션](https://jojoldu.tistory.com/412) 이라는 것이 있다.
- 소트웍스 앤솔러지에는 [모든 원시값과 문자열을 포장하라](https://developerfarm.wordpress.com/2012/01/27/object_calisthenics_4/) 한다.

## 핵심 정리

- 상속은 강력하지만 캡슐화를 해친다는 문제가 있다.
- 상속은 상위 클래스와 하위 클래스가 순수한 is-a 관계일 때만 써야 한다.
    - is-a 관계일 때도 안심할 수만은 없다. 하위 클래스의 패키지가 상위 클래스와 다르고, 상위 클래스가 확장을 고려해 설계되지 않았을 수도 있기 때문이다.
- 상속의 취약점을 피하려면 상속 대신 컴포지션과 전달을 사용한다. 특히 래퍼 클래스로 구현할 적당한 인터페이스가 있다면 더욱 그렇다. 래퍼 클래스는 하위 클래스보다 견고하고 강력하다.

## 참고자료

- [일급 컬렉션 (First Class Collection)의 소개와 써야할 이유](https://jojoldu.tistory.com/412)
- [규칙 3: 모든 원시값과 문자열을 포장한다.](https://developerfarm.wordpress.com/2012/01/27/object_calisthenics_4/)
- [데코레이터 패턴 (Decorator Pattern) - 객체에 동적으로 새로운 책임을 추가한다](https://johngrib.github.io/wiki/decorator-pattern/)
- [SELF 문제 - Wrapper Classes are not suited for callback frameworks](https://stackoverflow.com/questions/28254116/wrapper-classes-are-not-suited-for-callback-frameworks)
