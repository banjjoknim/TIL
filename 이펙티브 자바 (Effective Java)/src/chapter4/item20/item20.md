# 아이템 20. 추상 클래스보다는 인터페이스를 우선하라

## 자바의 다중 구현 메커니즘

- 자바가 제공하는 다중 구현 메커니즘은 인터페이스와 추상 클래스, 이렇게 두 가지다.
- 자바 8부터 인터페이스도 디폴트 메서드(default method)를 제공할 수 있게 되었다.
    - 따라서, 두 메커니즘 모두 인스턴스 메서드를 구현 형태로 제공할 수 있다.

### 추상 클래스 vs 인터페이스

- 추상 클래스가 정의한 타입을 구현하는 클래스는 반드시 추상 클래스의 하위 클래스가 되어야 한다.
- 자바는 단일 상속만 지원하므로, 추상 클래스 방식은 새로운 타입을 정의하는 데 커다란 제약을 안게 된다.
- 반면, 인터페이스가 선언한 메서드를 모두 정의하고 그 일반 규약을 잘 지킨 클래스라면 다른 어떤 클래스를 상속했든 같은 타입으로 취급된다.

## 인터페이스

### 기존 클래스에도 손쉽게 새로운 인터페이스를 구현해넣을 수 있다.

- 인터페이스가 요구하는 메서드를 추가하고, 클래스 선언에 implements 구문만 추가하면 끝이다.
- 반면, 기존 클래스 위에 새로운 추상 클래스를 끼워넣기는 어려운 게 일반적이다.
    - 두 클래스가 같은 추상 클래스를 확장하길 원한다면, 그 추상 클래스는 계층구조상 두 클래스의 공통 조상이어야 한다.
    - 이 방식은 그렇게 하는 것이 적절하지 않은 상황에서도 새로 추가된 추상 클래스의 모든 자손이 이를 상속하게 된다.

### 인터페이스는 믹스인(mixin) 정의에 안성맞춤이다.

- **믹스인이란 클래스가 구현할 수 있는 타입으로, 믹스인을 구현한 클래스에 원래의 '주된 타입' 외에도 특정 선택적 행위를 제공한다고 선언하는 효과를 준다.**
- 대상 타입의 주된 기능에 선택적 기능을 '혼합(mixed in)'한다고 해서 믹스인이라 부른다.
- 추상 클래스로는 믹스인을 정의할 수 없다. 이유는 기존 클래스에 덧씌울 수 없기 때문이다. 클래스는 두 부모를 섬길 수 없고, 클래스 계층구조에는 믹스인을 삽입하기에 합리적인 위치가 없기 때문이다.

### 인터페이스로는 계층구조가 없는 타입 프레임워크를 만들 수 있다.

```java
public interface Singer {
    void sing();
}

public interface Songwriter {
    void compose();
}
```

- 타입을 계층적으로 정의하면 수많은 개념을 구조적으로 잘 표현할 수 있다. 하지만 현실에는 계층을 엄격히 구분하기 힘든 개념도 있다.
- 위 코드처럼 타입을 인터페이스로 정의하면 가수 클래스가 Singer와 Songwriter 모두를 구현해도 전혀 문제되지 않는다.
- Singer와 Songwriter 모두를 확장하고 새로운 메서드까지 추가한 제 3의 인터페이스를 정의할 수도 있다.

```java
public interface SingerSongwriter extends Singer, Songwriter {
    void strum();

    void actSensitive();
}
```

- 같은 구조를 클래스로 만들려면 가능한 조합 전부를 각각의 클래스로 정의한 고도비만 계층구조가 만들어질 것이다.
- 속성이 n개라면 지원해야 할 조합의 수는 2<sup>n</sup>개나 될 것이다.
- 이러한 현상을 조합 폭발(combinatorial explosion)이라 부른다.
- 거대한 클래스 계층구조에는 공통 기능을 정의해놓은 타입이 없으니, 자칫 매개변수 타입만 다른 메서드들을 수없이 많이 가진 거대한 클래스를 낳을 수 있다.

### 래퍼 클래스 관용구와 함께 사용하면 인터페이스는 기능을 향상시키는 안전하고 강력한 수단이 된다.

- 타입을 추상 클래스로 정의해두면 그 타입에 기능을 추가하는 방법은 상속뿐이다.
- 상속해서 만든 클래스는 래퍼 클래스보다 활용도가 떨어지고 깨지기는 더 쉽다.

### 디폴트 메서드

- 인터페이스의 메서드 중 구현 방법이 명백한 것이 있다면, 그 구현을 디폴트 메서드로 제공해줄 수 있다. 이는 프로그래머의 일을 상당히 덜어준다.
- 디폴트 메서드를 제공할 때는 상속하려는 사람을 위한 설명을 @implSpec 자바독 태그를 붙여 문서화해야 한다.

#### 디폴트 메서드의 제약

- 많은 인터페이스가 equals와 hashCode 같은 Object의 메서드를 정의하고 있지만, 이들은 디폴트 메서드로 제공해서는 안 된다.
- 또한 인터페이스는 인스턴스 필드를 가질 수 없고 public이 아닌 정적 멤버도 가질 수 없다(단, private 정적 메서드는 예외다).

## 골격 구현(skeletal implementation)

### 추상 골격 구현(skeletal implementation) 클래스

- 인터페이스와 추상 골격 구현(skeletal implementation) 클래스를 함께 제공하는 식으로 인터페이스와 추상 클래스의 장점을 모두 취하는 방법도 있다.
    - 인터페이스로는 타입을 정의하고, 필요하면 디폴트 메서드 몇 개도 함께 제공한다.
    - 골격 구현 클래스는 나머지 메서드들까지 구현한다.
    - 이렇게하면 단순히 골격 구현을 확장하는 것만으로 이 인터페이스를 구현하는 데 필요한 일이 대부분 완료된다.
    - 이를 **템플릿 메서드 패턴**이라 한다.
    - 관례상 인터페이스 이름이 Interface라면 그 골격 구현 클래스의 이름은 AbstractInterface로 짓는다.
        - ex. AbstractCollection, AbstractSet, AbstractList, AbstractMap ...

#### 골격 구현을 사용해 완성한 구체 클래스

```java
public class AbstractSkeletalConcreteClass {
    static List<Integer> intArrayAsList(int[] a) {
        Objects.requireNonNull(a);

        // 다이아몬드 연산자를 이렇게 사용하는 건 자바 9부터 가능하다.
        // 더 낮은 버전을 사용한다면 <Integer>로 추정하자.
        return new AbstractList<>() {
            @Override
            public Integer get(int index) {
                return a[index]; // 오토박싱
            }

            @Override
            public Integer set(int index, Integer element) {
                int oldElement = a[index];
                a[index] = element;
                return oldElement;
            }

            @Override
            public int size() {
                return a.length;
            }
        };
    }
}
```

- 골격 구현 클래스는 추상 클래스처럼 구현을 도와주는 동시에, 추상 클래스로 타입을 정의할 때 따라오는 심각한 제약에서는 자유롭다.
- 골격 구현을 확장하는 것으로 인터페이스 구현이 거의 끝나지만, 반드시 이렇게 해야하는 것은 아니다.
- 구조상 골격 구현을 확장하지 못한다면 인터페이스를 직접 구현해야 한다. 그래도 여전히 디폴트 메서드의 이점을 누릴 수 있다.
- 골격 구현 클래스를 우회적으로 이용할 수도 있다.
    - 인터페이스를 구현한 클래스에서 해당 골격 구현을 확장한 private 내부 클래스를 정의하고, 각 메서드 호출을 내부 클래스의 인스턴스에 전달하면 된다.
    - 래퍼 클래스와 비슷한 이 방식을 **시뮬레이트한 다중 상속(simulated multiple inheritance)**이라 하며, 다중 상속의 많은 장점을 제공하면서 단점은 피하게 해준다.

#### 골격 구현 클래스

```java
public abstract class AbstractMapEntry<K, V> implements Map.Entry<K, V> {

    // 변경 가능한 엔트리는 이 메서드를 반드시 재정의해야 한다.
    @Override
    public V setValue(V value) {
        throw new UnsupportedOperationException();
    }

    // Map.Entry.equals의 일반 규약을 구현한다.
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Map.Entry)) {
            return false;
        }
        Map.Entry<?, ?> e = (Map.Entry) obj;
        return Objects.equals(e.getKey(), getKey()) && Objects.equals(e.getValue(), getValue());
    }

    // Map.Entry.hashCode의 일반 규약을 구현한다.
    @Override
    public int hashCode() {
        return Objects.hashCode(getKey()) ^ Objects.hashCode(getValue());
    }

    @Override
    public String toString() {
        return getKey() + "=" + getValue();
    }
}
```

- Map.Entry 인터페이스나 그 하위 인터페이스로는 이 골격 구현을 제공할 수 없다.
    - **디폴트 메서드는 equals, hashCode, toString 같은 Object 메서드를 재정의할 수 없기 때문이다.**
- 골격 구현은 기본적으로 상속해서 사용하는 걸 가정한다. 따라서 설계 및 문서화 지침을 모두 따라야 한다.
- 인터페이스에 정의한 디폴트 메서드든 별도의 추상 클래스든, 골격 구현은 반드시 그 동작 방식을 잘 정리해 문서로 남겨야 한다.

### 단순 구현(simple implementation)

- **단순 구현(simple implementation)은 골격 구현의 작은 변종**으로, AbstractMap.SimpleEntry가 좋은 예다.

```java
public static class SimpleEntry<K, V> implements Entry<K, V>, java.io.Serializable {
    private static final long serialVersionUID = -8499721149061103585L;

    private final K key;
    private V value;

    /**
     * Creates an entry representing a mapping from the specified
     * key to the specified value.
     *
     * @param key the key represented by this entry
     * @param value the value represented by this entry
     */
    public SimpleEntry(K key, V value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Creates an entry representing the same mapping as the
     * specified entry.
     *
     * @param entry the entry to copy
     */
    public SimpleEntry(Entry<? extends K, ? extends V> entry) {
        this.key = entry.getKey();
        this.value = entry.getValue();
    }

    /**
     * Returns the key corresponding to this entry.
     *
     * @return the key corresponding to this entry
     */
    public K getKey() {
        return key;
    }

    /**
     * Returns the value corresponding to this entry.
     *
     * @return the value corresponding to this entry
     */
    public V getValue() {
        return value;
    }

    /**
     * Replaces the value corresponding to this entry with the specified
     * value.
     *
     * @param value new value to be stored in this entry
     * @return the old value corresponding to the entry
     */
    public V setValue(V value) {
        V oldValue = this.value;
        this.value = value;
        return oldValue;
    }

    /**
     * Compares the specified object with this entry for equality.
     * Returns {@code true} if the given object is also a map entry and
     * the two entries represent the same mapping.  More formally, two
     * entries {@code e1} and {@code e2} represent the same mapping
     * if<pre>
     *   (e1.getKey()==null ?
     *    e2.getKey()==null :
     *    e1.getKey().equals(e2.getKey()))
     *   &amp;&amp;
     *   (e1.getValue()==null ?
     *    e2.getValue()==null :
     *    e1.getValue().equals(e2.getValue()))</pre>
     * This ensures that the {@code equals} method works properly across
     * different implementations of the {@code Map.Entry} interface.
     *
     * @param o object to be compared for equality with this map entry
     * @return {@code true} if the specified object is equal to this map
     *         entry
     * @see    #hashCode
     */
    public boolean equals(Object o) {
        if (!(o instanceof Map.Entry))
            return false;
        Map.Entry<?, ?> e = (Map.Entry<?, ?>) o;
        return eq(key, e.getKey()) && eq(value, e.getValue());
    }

    /**
     * Returns the hash code value for this map entry.  The hash code
     * of a map entry {@code e} is defined to be: <pre>
     *   (e.getKey()==null   ? 0 : e.getKey().hashCode()) ^
     *   (e.getValue()==null ? 0 : e.getValue().hashCode())</pre>
     * This ensures that {@code e1.equals(e2)} implies that
     * {@code e1.hashCode()==e2.hashCode()} for any two Entries
     * {@code e1} and {@code e2}, as required by the general
     * contract of {@link Object#hashCode}.
     *
     * @return the hash code value for this map entry
     * @see    #equals
     */
    public int hashCode() {
        return (key == null ? 0 : key.hashCode()) ^
                (value == null ? 0 : value.hashCode());
    }

    /**
     * Returns a String representation of this map entry.  This
     * implementation returns the string representation of this
     * entry's key followed by the equals character ("{@code =}")
     * followed by the string representation of this entry's value.
     *
     * @return a String representation of this map entry
     */
    public String toString() {
        return key + "=" + value;
    }

}
```

- 단순 구현도 골격 구현과 같이 상속을 위해 인터페이스를 구현한 것이지만, 추상 클래스가 아니란 점이 다르다.
- 쉽게 말하면, 동작하는 가장 단순한 구현이다. 이러한 단순 구현은 그대로 써도 되고 필요에 맞게 확장해도 된다.

## 핵심 정리

- 일반적으로 다중 구현용 타입으로는 인터페이스가 가장 적합하다.
- 복잡한 인터페이스라면 구현하는 수고를 덜어주는 골격 구현을 함께 제공하는 방법을 꼭 고려해본다.
- 골격 구현은 '가능한 한' 인터페이스의 디폴트 메서드로 제공하여 그 인터페이스를 구현한 모든 곳에서 활용하도록 하는 것이 좋다.
- 골격 구현은 인터페이스에 걸려 있는 구현상의 제약 때문에 추상 클래스로 제공하는 경우가 더 흔하다.

## 참고자료

- [[JAVA] 추상클래스 VS 인터페이스 왜 사용할까? 차이점, 예제로 확인 :: 마이자몽](https://myjamong.tistory.com/150)
- [[Java] 인터페이스와 추상클래스](https://kingofbackend.tistory.com/128)
