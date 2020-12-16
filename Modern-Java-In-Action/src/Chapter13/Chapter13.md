# Chapter13. 디폴트 메서드
- 전통적인 자바에서 인터페이스와 관련 메서드는 한 몸처럼 구성된다. 
- 인터페이스를 구현하는 클래스는 인터페이스에서 정의하는 모든 메서드 구현을 제공하거나 아니면 슈퍼클래스의 구현을 상속받아야 한다. 
- 평소에는 이 규칙을 지키는 데 아무 문제가 없지만 라이브러리 설계자 입장에서 인터페이스에 새로운 메서드를 추가하는 등 인터페이스를 바꾸고 싶을 때는 문제가 발생한다. 
- 인터페이스를 바꾸면 이전에 해당 인터페이스를 구현했던 모든 클래스의 구현도 고쳐야 하기 때문이다.
- 하지만 자바 8에서는 이 문제를 해결하는 새로운 기능을 제공한다.
- 자바 8에서는 기본 구현을 포함하는 인터페이스를 정의하는 두 가지 방법을 제공한다.
- 첫 번째는 인터페이스 내부에 `정적 메서드(static method)`를 사용하는 것이다.
- 두 번째는 인터페이스의 기본 구현을 제공할 수 있도록 `디폴트 메서드(default method)` 기능을 사용하는 것이다.
- 즉, 자바 8에서는 메서드 구현을 포함하는 인터페이스를 정의할 수 있다.
- 결과적으로 기존 인터페이스를 구현하는 클래스는 자동으로 인터페이스에 추가된 새로운 메서드의 디폴트 메서드를 상속받게 된다. 이렇게 하면 기존의 코드 구현을 바꾸도록 강요하지 않으면서도 인터페이스를 바꿀 수 있다.
- 이와 같은 방식으로 추가된 두 가지 예로 `List` 인터페이스의 `sort`와 `Collection` 인터페이스의 `stream` 메서드를 살펴봤다.

1장에서 살펴본 `List` 인터페이스의 `sort` 메서드는 자바 8에서 새로 추가된 메서드다. 다음은 `sort`의 구현 코드다.

```java
default void sort(Comparator<? super E> c) {
    Collections.sort(this, c);
}
```

반환형식 `void` 앞에 `default`라는 새로운 키워드가 등장했다. `default` 키워드는 해당 메서드가 디폴트 메서드임을 가리킨다. 여기서 `sort` 메서드는 `Collections.sort` 메서드를 호출한다. 이 새로운 디폴트 메서드 덕분에 리스트에 직접 `sort`를 호출할 수 있게 되었다.

```java
List<Integer> numbers = Arrays.asList(3, 5, 1, 2, 6);
numbers.sort(Comparator.naturalOrder()); // sort는 List 인터페이스의 디폴트 메서드다.
```

위 코드에서 `Comparator.naturalOrder`라는 새로운 메서드가 등장했다. `naturalOrder`는 자연순서(표준 알파벳 순서)로 요소를 정렬할 수 있도록 `Comparator` 객체를 반환하는 `Comparator` 인터페이스에 추가된 새로운 정적 메서드다. 다음은 4장에서 사용한 `Collection`의 `stream` 메서드 정의 코드다.

```java
default Stream<E> stream() {
    return StreamSupport.stream(spliterator(), false);
}
```

우리가 자주 사용했던 `stream` 메서드는 내부적으로 `StreamSupport.stream`이라는 메서드를 호출해서 스트림을 반환한다. `stream` 메서드의 내부에서는 `Collection` 인터페이스의 다른 디폴트 메서드 `spliterator`도 호출한다.

결국 인터페이스가 아니라 추상 클래스 아닌가? 인터페이스와 추상 클래스는 같은 점이 많아졌지만 여전히 다른 점도 있다. 어떤 점이 다른지는 곧 살펴볼 것이다. 디폴트 메서드를 사용하는 이유는 뭘까? 디폴트 메서드는 주로 라이브러리 설계자들이 사용한다. 디폴트 메서드를 이용하면 자바 API의 호환성을 유지하면서 라이브러리를 바꿀 수 있다.

디폴트 메서드가 없던 시절에는 인터페이스에 메서드를 추가하면서 여러 문제가 발생했다. 인터페이스에 새로 추가된 메서드를 구현하도록 인터페이스를 구현하는 기존 클래스를 고쳐야했기 때문이었다. 본인이 직접 인터페이스와 이를 구현하는 클래스를 관리할 수 있는 상황이라면 이 문제를 어렵지 않게 해결할 수 있지만 인터페이스를 대중에 공개했을 때는 상황이 다르다. 그래서 디폴트 메서드가 탄생했다. 디폴트 메서드를 이용하면 인터페이스의 기본 구현을 그대로 상속하므로 인터페이스에 자유롭게 새로운 메서드를 추가할 수 있게 된다.

만약 라이브러리 설계자라면 기존 구현을 고치지 않고도 인터페이스를 바꿀 수 있으므로 디폴트 메서드를 잘 이해하는 것이 중요하다. 또한 디폴트 메서드는 다중 상속 동작이라는 유연성을 제공하면서 프로그램 구성에도 도움을 준다(이제 클래스는 여러 디폴트 메서드를 상속받을 수 있게 되었다). 물론 라이브러리 설계자가 아닌 일반 개발자도 디폴트 메서드를 이해한다면 언젠가 도움이 될 것이다.

>**정적 메서드와 인터페이스**
>
>- 보통 자바에서는 인터페이스 그리고 인터페이스의 인스턴스를 활용할 수 있는 다양한 정적 메서드를 정의하는 유틸리티 클래스를 활용한다.
>- 예를 들어 `Collections`는 `Collection` 객체를 활용할 수 있는 유틸리티 클래스다. 자바 8에서는 인터페이스에 직접 정적 메서드를 선언할 수 있으므로 유틸리티 클래스를 없애고 직접 인터페이스 내부에 정적 메서드를 구현할 수 있다.
>- 그럼에도 불구하고 과거 버전과의 호환성을 유지할 수 있도록 자바 API에는 유틸리티 클래스가 남아있다.

우선 API가 바뀌면서 어떤 문제가 생기는지 확인한다. 또한 디폴트 메서드란 무엇이며 API가 바뀌면서 발생한 문제를 디폴트 메서드로 어떻게 해결할 수 있는지 설명한다. 그리고 디폴트 메서드를 만들어 다중 상속을 달성하는 방법을 보여준다. 마지막으로 같은 시그니처를 갖는 여러 디폴트 메서드를 상속받으면서 발생하는 모호성 문제를 자바 컴파일러가 어떻게 해결하는지 살펴본다.

---

## 13.1 변화하는 API
API를 바꾸는 것이 왜 어려운지 예제를 통해 살펴보자. 우리가 인기 있는 자바 그리기 라이브러리 설계자가 되었다고 가정하자. 우리가 만든 라이브러리에는 모양의 크기를 조절하는 데 필요한 `setHeight`, `setWidth`, `getHeight`, `getWidth`, `setAbsoluteSize` 등의 메서드를 정의하는 `Resizable` 인터페이스가 있다. 그뿐만 아니라 `Rectangle`이나 `Square`처럼 `Resizable`을 구현하는 클래스도 제공한다. 라이브러리가 인기를 얻으면서 일부 사용자는 직접 `Resizable` 인터페이스를 구현하는 `Ellipse`라는 클래스를 구현하기도 했다.

API를 릴리스한 지 몇 개월이 지나면서 `Resizable`에 몇 가지 기능이 부족하다는 사실을 알게되었다. 예를 들어 `Resizable` 인터페이스에 크기 조절 인수로 모양의 크기를 조절할 수 있는 `setRelativeSize`라는 메서드가 있으면 좋을 것 같다. 그래서 `Resizable`에 `setRelativeSize`를 추가한 다음에 `Square`와 `Rectangle` 구현도 고쳤다. 이제 모든 문제가 해결된 걸까? 이전에 우리의 `Resizable` 인터페이스를 구현한 사용자는 어떻게 되는 걸까? 안타깝게도 라이브러리 사용자가 만든 클래스를 우리가 어떻게 할 수는 없다. 바로 자바 라이브러리 설계자가 라이브러리를 바꾸고 싶을 때 같은 문제가 발생한다. 이미 릴리스된 인터페이스를 고치면 어떤 문제가 발생하는지 더 자세히 알아보자.

### 13.1.1 API 버전 1
`Resizable` 인터페이스 초기 버전은 다음과 같은 메서드를 포함한다.

```java
public interface Resizable extends Drawable {
    int getWidth();
    int getHeight();
    void setWidth();
    void setHeight();
    void setAbsoluteSize(int width, int height);
}
```

#### 사용자 구현
우리 라이브러리를 즐겨 사용하는 사용자 중 한 명은 직접 `Resizable`을 구현하는 `Ellipse` 클래스를 만들었다.

```java
public class Ellipse implements Resizable {
    ...
}
```

이 사용자는 다양한 `Resizable` 모양(자신이 만든 `Ellipse`를 포함해서)을 처리하는 게임을 만들었다.

```java
public class Game {
    public static void main(String... args) {
        List<Resizable> resizableShapes = 
            Arrays.asList(new Square(), new Rectangle(), new Ellipse()); // 크기를 조절할 수 있는 모양 리스트
        Utils.paint(resizableShapes);
    }
}

public class Utils {
    public static void paint(List<Resizable> l) {
        l.forEach(r -> {
            r.setAbsoluteSize(42, 42); // 각 모양에 setAbsoluteSize 호출
            r.draw();
        });
    }
}
```

### 13.1.2 API 버전 2
몇 개월이 지나자 `Resizable`을 구현하는 `Square`와 `Rectangle` 구현을 개선해달라는 많은 요청을 받았다. 그래서 다음 코드에서 보여주는 것처럼 API 버전 2를 만들었다.

```java
public interface Resizable extends Drawable {
    int getWidth();
    int getHeight();
    void setWidth();
    void setHeight();
    void setAbsoluteSize(int width, int height);
    void setRelativeSize(int wFactor, int hFactor); // API 버전 2에 추가된 새로운 메서드
}
```

- `Resizable`에 메서드를 추가하면서 API가 바뀌었다. 따라서 인터페이스를 바꾼 다음에 애플리케이션을 재컴파일하면 에러가 발생한다.

#### 사용자가 겪는 문제
`Resizable`을 고치면 몇 가지 문제가 발생한다. 첫 번째로 `Resizable`을 구현하는 모든 클래스는 `setRelativeSize` 메서드를 구현해야 한다. 하지만 라이브러리 사용자가 직접 구현한 `Ellipse`는 `setRelativeSize` 메서드를 구현하지 않는다. 인터페이스에 새로운 메서드를 추가하면 `바이너리 호환성`은 유지된다. `바이너리 호환성`이란 새로 추가된 메서드를 호출하지만 않으면 새로운 메서드 구현이 없이도 기존 클래스 파일 구현이 잘 동작한다는 의미다. 하지만 언젠가는 누군가가 `Resizable`을 인수로 받는 `Utils.paint`에서 `setRelativeSize`를 사용하도록 코드를 바꿀 수 있다. 이때 `Ellipse` 객체가 인수로 전달되면 `Ellipse`는 `setRelativeSize` 메서드를 정의하지 않았으므로 런타임에 다음과 같은 에러가 발생할 것이다.

```
Exception in thread "main" java.lang.AbstractMethodError: 
lambdasinaction.chap9.Ellipse.setRelativeSize(II)V
```

두 번째로 사용자가 `Ellipse`를 포함하는 전체 애플리케이션을 재빌드할 때 다음과 같은 컴파일 에러가 발생한다.

```
lambdasinaction/chap9/Ellipse.java:6: error: 
Ellipse is not abstract and does not override abstract method setRelativeSize(int, int) in Resizable
```

공개된 API를 고치면 기존 버전과의 호환성 문제가 발생한다. 이런 이유 때문에 공식 자바 컬렉션 API 같은 기존의 API는 고치기 어렵다. 물론 API를 바꿀 수 있는 몇 가지 대안이 있지만 완벽한 해결책은 될 수 없다. 예를 들어 자신만의 API를 별도로 만든 다음에 예전 버전과 새로운 버전을 직접 관리하는 방법도 있다. 하지만 이는 여러 가지로 불편하다. 첫째, 라이브러리를 관리하기가 복잡하다. 둘째, 사용자는 같은 코드에 예전 버전과 새로운 버전 두 가지 라이브러리를 모두 사용해야 하는 상황이 생긴다. 결국 프로젝트에서 로딩해야 할 클래스 파일이 많아지면서 메모리 사용과 로딩 시간 문제가 발생한다.

디폴트 메서드로 이 모든 문제를 해결할 수 있다. 디폴트 메서드를 이용해서 API를 바꾸면 새롭게 바뀐 인터페이스에서 자동으로 기본 구현을 제공하므로 기존 코드를 고치지 않아도 된다.

>**바이너리 호환성, 소스 호환성, 동작 호환성**
>
>- 자바 프로그램을 바꾸는 것과 관련된 호환성 문제는 크게 바이너리 호환성, 소스 호환성, 동작 호환성 세 가지로 분류할 수 있다(더 자세한 사항은 http://goo.gl/JNn4vm 함고)
>- 인터페이스에 메서드를 추가했을 때는 바이너리 호환성을 유지하지만 인터페이스를 구현하는 클래스를 재컴파일하면 에러가 발생한다.
>- 즉, 다양한 호환성이 있다는 사실을 이해해야한다.
>- 뭔가를 바꾼 이후에도 에러 없이 기존 바이너리가 실행될 수 있는 상황을 `바이너리 호환성`이라고 한다(바이너리 실행에는 인증, 준비, 해석 등의 과정이 포함된다). 예를 들어 인터페이스에 메서드를 추가했을 때 추가된 메서드를 호출하지 않는 한 문제가 일어나지 않는데 이를 바이너리 호환성이라고 한다.
>- 간단히 말해, `소스 호환성`이란 코드를 고쳐도 기존 프로그램을 성공적으로 재컴파일할 수 있음을 의미한다. 예를 들어 인터페이스에 메서드를 추가하면 소스 호환성이 아니다. 추가한 메서드를 구현하도록 클래스를 고쳐야 하기 때문이다.
>- 마지막으로 `동작 호환성`이란 코드를 바꾼 다음에도 같은 입력값이 주어지면 프로그램이 같은 동작을 실행한다는 의미다. 예를 들어 인터페이스에 메서드를 추가하더라도 프로그램에서 추가된 메서드를 호출할 일은 없으므로(혹은 우연히 구현 클래스가 이를 오버라이드했을 수도 있다) 동작 호환성은 유지된다.

---

## 13.2 디폴트 메서드란 무엇인가?
공개된 API에 새로운 메서드를 추가하면 기존 구현에 어떤 문제가 생기는지 살펴봤다. 자바 8에서는 호환성을 유지하면서 API를 바꿀 수 있도록 새로운 기능인 **디폴트 메서드**(default method)를 제공한다. 이제 인터페이스는 자신을 구현하는 클래스에서 메서드를 구현하지 않을 수 있는 새로운 메서드 시그니처를 제공한다. 그럼 디폴트 메서드는 누가 구현할까? 인터페이스를 구현하는 클래스에서 구현하지 않은 메서드는 인터페이스 자체에서 기본으로 제공한다(그래서 이를 디폴트 메서드라고 부른다).

디폴트 메서드인지 어떻게 알 수 있을까? 디폴트 메서드는 간단하게 알아볼 수 있다. 우선 디폴트 메서드는 `default`라는 키워드로 시작하며 다른 클래스에 선언된 메서드처럼 메서드 바디를 포함한다. 예를 들어 컬렉션 라이브러리에 `Sized`라는 인터페이스를 정의했다고 가정하자. 다음 코드에서 보여주는 것처럼 `Sized` 인터페이스는 추상 메서드 `size`와 디폴트 메서드 `isEmpty`를 포함한다.

```java
public interface Sized {
    int size();
    default boolean isEmpty() { // 디폴트 메서드
        return size() == 0;
    }
}
```

이제 `Sized` 인터페이스를 구현하는 모든 클래스는 `isEmpty`의 구현도 상속받는다. 즉, 인터페이스에 디폴트 메서드를 추가하면 소스 호환성이 유지된다.

자바 그리기 라이브러리와 게임 예제로 돌아가자. 결론적으로 말해서 디폴트 메서드를 이용해서 `setRelativeSize`의 디폴트 구현을 제공한다면 호환성을 유지하면서 라이브러리를 고칠 수 있다(즉, 우리 라이브러리 사용자는 `Resizable` 인터페이스를 구현하는 클래스를 고칠 필요가 없다).

```java
default void setRelativeSize(int wFactor, int hFactor) {
    setAbsoluteSize(getWidth() / wFactor, getHeight() / hFactor);
}
```

인터페이스가 구현을 가질 수 있고 클래스는 여러 인터페이스를 동시에 구현할 수 있으므로 결국 자바도 다중 상속을 지원하는 걸까? 인터페이스를 구현하는 클래스가 디폴트 메서드와 같은 메서드 시그니처를 정의하거나 아니면 디폴트 메서드를 오버라이드한다면 어떻게 될까? 이런 문제는 아직 걱정하지 않아도 된다. 이 문제를 해결할 수 있는 몇 가지 규칙이 있는데 이후에 살펴본다.

자바 8 API에서 디폴트 메서드가 상당히 많이 활용되었을 것임을 추측할 수 있다. 예를 들어 `Collection` 인터페이스의 `stream` 메서드처럼 부지불식간에 많은 디폴트 메서드를 사용했다. `List` 인터페이스의 `sort` 메서드도 디폴트 메서드다. `Predicate`, `Function`, `Comparator` 등 많은 함수형 인터페이스도 `Predicate.and` 또는 `Function.andThen` 같은 다양한 디폴트 메서드를 포함한다(함수형 인터페이스는 오직 하나의 추상 메서드를 포함한다. 디폴트 메서드는 추상 메서드에 해당하지 않는다는 점을 기억하자).

>**추상 클래스와 자바 8의 인터페이스**
>
>추상 클래스와 인터페이스는 뭐가 다를까? 둘 다 추상 메서드와 바디를 포함하는 메서드를 정의할 수 있다.
>첫째, 클래스는 하나의 추상 클래스만 상속받을 수 있지만 인터페이스를 여러 개 구현할 수 있다.
>둘째, 추상 클래스는 인스턴스 변수(필드)로 공통 상태를 가질 수 있다. 하지만 인터페이스는 인스턴스 변수를 가질 수 없다.

##### ----- 퀴즈 13-1. removeIf ------
여러분이 자바 언어와 API의 달인이라고 가정하자. 어느 날 다수의 사용자로부터 `ArrayList`, `TreeSet`, `LinkedList` 및 다른 모든 컬렉션에서 사용할 수 있는 `removeIf` 메서드를 추가해달라는 요청을 받았다. `removeIf` 메서드는 주어진 프레디케이트와 일치하는 모든 요소를 컬렉션에서 제거하는 기능을 수행한다. 새로운 `removeIf`를 기존 컬렉션 API에 가장 적절하게 추가하는 방법은 무엇일까?

`정답`
모든 컬렉션 클래스는 `java.util.Collection` 인터페이스를 구현한다. 그러면 `Collection` 인터페이스에 메서드를 추가할 수 있을까? 지금까지 확인한 것처럼 디폴트 메서드를 인터페이스에 추가함으로써 소스 호환성을 유지할 수 있다. 그러면 `Collection`을 구현하는 모든 클래스(물론 컬렉션 라이브러리의 클래스뿐 아니라 `Collection` 인터페이스를 직접 구현한 모든 사용자의 클래스도 포함)는 자동으로 `removeIf`를 사용할 수 있게 된다.

```java
default boolean removeIf(Predicate<? super E> filter) {
    boolean removed = false;
    Iterator<E> each = iterator();
    while(each.hasNext()) {
        if(filter.test(each.next())) {
            each.remove();
            removed = true;
        }
    }
    return removed;
}
```

##### -----------------------------------------

---

## 13.3 디폴트 메서드 활용 패턴
여러분은 이미 디폴트 메서드를 이용하면 라이브러리를 바꿔도 호환성을 유지할 수 있음을 확인했다. 디폴트 메서드를 다른 방식으로도 활용할 수 있을까? 우리가 만드는 인터페이스에도 디폴트 메서드를 추가할 수 있다. 이 절에서는 디폴트 메서드를 이용하는 두 가지 방식, 즉 `선택형 메서드(optional method)`와 `동작 다중 상속(multiple inheritance of behavior)`을 설명한다.

### 13.3.1 선택형 메서드
여러분은 아마 인터페이스를 구현하는 클래스에서 메서드의 내용이 비어있는 상황을 본 적이 있을 것이다. 예를 들어 `Iterator` 인터페이스를 보자. `Iterator`는 `hasNext`와 `next`뿐 아니라 `remove` 메서드도 정의한다. 사용자들이 `remove` 기능은 잘 사용하지 않으므로 자바 8 이전에는 `remove` 기능을 무시했다. 결과적으로 `Iterator`를 구현하는 많은 클래스에서는 `remove`에 빈 구현을 제공했다.

디폴트 메서드를 이용하면 `remove` 같은 메서드에 기본 구현을 제공할 수 있으므로 인터페이스를 구현하는 클래스에서 빈 구현을 제공할 필요가 없다. 예를 들어 자바 8의 `Iterator` 인터페이스는 다음처럼 `remove` 메서드를 정의한다.

```java
interface Iterator<T> {
    boolean hasNext();
    T next();
    default void remove() {
        throw new UnsupportedOperationException();
    }
}
```

기본 구현이 제공되므로 `Iterator` 인터페이스를 구현하는 클래스는 빈 `remove` 메서드를 구현할 필요가 없어졌고, 불필요한 코드를 줄일 수 있다.

### 13.3.2 동작 다중 상속
디폴트 메서드를 이용하면 기존에는 불가능했던 동작 다중 상속 기능도 구현할 수 있다. 클래스는 다중 상속을 이용해서 기존 코드를 재사용할 수 있다.

자바에서 클래스는 한 개의 다른 클래스만 상속할 수 있지만 인터페이스는 여러 개 구현할 수 있다. 다음은 자바 API에 정의된 `ArrayList` 클래스다.

```java
public class ArrayList<E> extends AbstractList<E> // 한 개의 클래스를 상속받는다.
implements List<E>, RandomAccess, Cloneable, Serializable { // 네 개의 인터페이스를 구현한다.
    ...
}
```

#### 다중 상속 형식
여기서 `ArrayList`는 한 개의 클래스를 상속받고, 여섯 개의 인터페이스를 구현한다. 결과적으로 `ArrayList`는 `AbstractList`, `List`, `RandomAccess`, `Cloneable`, `Serializable`, `Iterable`, `Collection`의 **서브형식**(subtype)이 된다. 따라서 디플트 메서드를 사용하지 않아도 다중 상속을 활용할 수 있다.

자바 8에서는 인터페이스가 구현을 포함할 수 있으므로 클래스는 여러 인터페이스에서 동작(구현 코드)을 상속받을 수 있따. 다중 동작 상속이 어떤 장점을 제공하는지 예제로 살펴보자. 중복되지 않는 최소한의 인터페이스를 유지한다면 우리 코드에서 동작을 쉽게 재사용하고 조합할 수 있다.

#### 기능이 중복되지 않는 최소의 인터페이스
우리가 만든느 게임에 다양한 특성을 갖는 여러 모양을 정의한다고 가정하자. 어떤 모양은 회전할 수 없지만 크기는 조절할 수 있다. 어떤 모양은 회전할 수 있으며 움직일 수 있지만 크기는 조절할 수 없다. 최대한 기존 코드를 재사용해서 이 기능을 구현하려면 어떻게 해야 할까?

먼저 `setRotationAngle`과 `getRatationAngle` 두 개의 추상 메서드를 포함하는 `Rotatable` 인터페이스를 정의한다. 인터페이스는 다음 코드에서 보여주는 것처럼 `setRotationAngle`과 `getRotationAngle` 메서드를 이용해서 디폴트 메서드 `rotateBy`도 구현한다.

```java
public interface Rotatable {
    void setRotationAngle(int angleInDegrees);
    int getRotationAngle();
    default void rotateBy(int angleInDegrees) { // rotateBy 메서드의 기본 구현
        setRotationAngle((getRotationAngle() + angleInDegrees) % 360);
    }
}
```

위 인터페이스는 구현해야 할 다른 메서드에 따라 뼈대 알고리즘이 결정되는 템플릿 디자인 패턴과 비슷해 보인다.

`Rotatable`을 구현하는 모든 클래스는 `setRotationAngle`과 `getRotationAngle`의 구현을 제공해야 한다. 하지만 `rotateBy`는 기본 구현이 제공되므로 따로 구현을 제공하지 않아도 된다.

마찬가지로 이전에 살펴본 두 가지 인터페이스 `Moveable`과 `Resizable`을 정의해야 한다. 두 인터페이스 모두 디폴트 구현을 제공한다. 다음은 `Moveable` 코드다.

```java
public interface Moveable {
    int getX();
    int getY();
    void setX(int x);
    void setY(int y);

    default void moveHorizontally(int distance) {
        setX(getX() + distance);
    }

    default void moveVertically(int distance) {
        setY(getY() + distance);
    }
}
```

다음은 `Resizable` 코드다.

```java
public interface Resizable {
    int getWidth();
    int getHeight();
    void setWidth(int width);
    void setHeight(int height);
    void setAbsoluteSize(int width, int height);

    default void setRelativeSize(int wFactor, int hFactor) {
        setAbsoluteSize(getWidth() / wFactor, getHeight() / hFactor);
    }
}
```

#### 인터페이스 조합
이제 이들 인터페이스를 조합해서 게임에 필요한 다양한 클래스를 구현할 수 있다.예를 들어 다음 코드처럼 움직일 수 있고(moveable), 회전할 수 있으며(rotatable), 크기를 조절할 수 있는(resizable) 괴물(Monster) 클래스를 구현할 수 있다.

```java
public class Monster implements Rotatable, Moveable, Resizable{
    ... // 모든 추상 메서드의 구현은 제공해야 하지만 디폴트 메서드의 구현은 제공할 필요가 없다.
}
```

`Monster` 클래스는 `Rotatable`, `Moveable`, `Resizable` 인터페이스의 디폴트 메서드를 자동으로 상속받는다. 즉, `Monster` 클래스는 `rotateBy`, `moveHorizontally`, `moveVertically`, `setRelativeSize` 구현을 상속받는다.

상속받은 다양한 메서드를 직접 호출할 수 있다.

```java
Monster m = new Monster(); // 생성자는 내부적으로 좌표, 높이, 기본 각도를 설정한다.
m.rotateBy(180); // Rotatable의 rotateBy 호출
m.moveVertically(10); // Moveable의 moveVertically 호출
```

이번에는 움직일 수 있으며 회전할 수 있지만, 크기는 조절할 수 없는 `Sun` 클래스를 정의한다. 이때 코드를 복사&붙여넣기할 필요가 전혀 없다. `Moveable`과 `Rotatable`을 구현할 때 자동으로 디폴트 메서드를 재사용할 수 있기 때문이다.

```java
public class Sun implements Moveable, Rotatable{
    ... // 모든 추상 메서드의 구현은 제공해야 하지만 디폴트 메서드의 구현은 제공할 필요가 없다.
}
```

인터페이스에 디폴트 구현을 포함시키면 또 다른 장점이 생긴다. 예를 들어 `moveVertically`의 구현을 더 효율적으로 고쳐야 한다고 가정하자. 디폴트 메서드 덕분에 `Moveable` 인터페이스를 직접 고칠 수 있고 따라서 `Moveable`을 구현하는 모든 클래스도 자동으로 변경한 코드를 상속받는다(물론 구현 클래스에서 메서드를 정의하지 않은 상황에 한해서다).

>**옳지 못한 상속**
>
>상속으로 코드 재사용 문제를 모두 해결할 수 있는 것은 아니다. 예를 들면 한 개의 메서드를 재사용하려고 100개의 메서드와 필드가 정의되어 있는 클래스를 상속받는 것은 좋은 생각이 아니다. 이럴 때는 **델리게이션**(delegation), 즉 멤버 변수를 이용해서 클래스에서 필요한 메서드를 직접 호출하는 메서드를 작성하는 것이 좋다. 종종 `final`로 선언된 클래스를 볼 수 있다. 다른 클래스가 이 클래스를 상속받지 못하게 함으로써 원래 동작이 바뀌지 않길 원하기 때문이다. 예를 들어 `String` 클래스도 `final`로 선언되어 있다. 이렇게 해서 다른 누군가가 `String`의 핵심 기능을 바꾸지 못하도록 제한할 수 있다.
>
>우리의 디폴트 메서드에도 이 규칙을 적용할 수 있다. 필요한 기능만 포함하도록 인터페이스를 최소한으로 유지한다면 필요한 기능만 선택할 수 있으므로 쉽게 기능을 조립할 수 있다.

지금까지 다양한 방법으로 디폴트 메서드를 활용할 수 있음을 살펴봤다. 만약 어떤 클래스가 같은 디폴트 메서드 시그니처를 포함하는 두 인터페이스를 구현하는 상황이라면 어떻게 될까? 클래스는 어떤 인터페이스의 디폴트 메서드를 사용하게 될까? 다음으로는 이 문제를 자세히 살펴본다.

---

## 13.4 해석 규칙
이미 살펴봤듯이, 자바의 클래스는 하나의 부모 클래스만 상속받을 수 있지만 여러 인터페이스를 동시에 구현할 수 있다. 자바 8에는 디폴트 메서드가 추가되었으므로 같은 시그니처를 갖는 디폴트 메서드를 상속받는 상황이 생길 수 있다. 이런 상황에서는 어떤 인터페이스의 디폴트 메서드를 사용하게 될까? 실전에서 자주 일어나는 일은 아니지만 이를 해결할 수 있는 규칙이 필요하다. 이 절에서는 자바 컴파일러가 이러한 충돌을 어떻게 해결하는지 설명한다. 이 절을 통해 '다음 예제에서 클래스 C는 누구의 hello를 호출할까?'라는 질문에 대한 답을 찾을 수 있을 것이다. 

다음 코드는 의도적으로 문제를 보여주려고 만든 예제일 뿐 실제로는 자주 일어나지 않는다.

```java
public interface A {
    default void hello() {
        System.out.println("Hello from A");
    }
}

public interface B extends A {
    default void hello() {
        System.out.println("Hello from B");
    }
}

public class C implements B, A {
    public static void main(String... args) {
        new C().hello(); // 무엇이 출력될까?
    }
}
```

`C++`의 다이아몬드 문제, 즉 같은 시그니처를 갖는 두 메서드를 상속받는 클래스를 들어본 독자도 있을 것이다. 이때 어떤 메서드가 사용될까? 자바 8은 이러한 문제에 대한 해결 규칙을 제공한다. 다음 절에서 대답을 찾을 수 있다.

### 13.4.1 알아야 할 세 가지 해결 규칙
다른 클래스나 인터페이스로부터 같은 시그니처를 갖는 메서드를 상속받을 때는 세 가지 규칙을 따라야 한다.

1. 클래스가 항상 이긴다. 클래스나 슈퍼클래스에서 정의한 메서드가 디폴트 메서드보다 우선권을 갖는다.
2. 1번 규칙 이외의 상황에서는 서브인터페이스가 이긴다. 상속관계를 갖는 인터페이스에서 같은 시그니처를 갖는 메서드를 정의할 때는 서브인터페이스가 이긴다. 즉, B가 A를 상속받는다면 B가 A를 이긴다.
3. 여전히 디폴트 메서드의 우선순위가 결정되지 않았다면 여러 인터페이스를 상속받는 클래스가 명시적으로 디폴트 메서드를 오버라이드하고 호출해야 한다.

이 세 가지 규칙만 알면 모든 디폴트 메서드 해석 문제가 해결된다. 이제 예제로 더 자세히 알아보자.

### 13.4.2 디폴트 메서드를 제공하는 서브인터페이스가 이긴다.
13.4절의 시작 부분에서 B와 A를 구현하는 클래스 C가 등장했던 예제를 살펴보자. B와 A는 `hello`라는 디폴트 메서드를 정의한다. 또한 B는 A를 상속받는다.

- 디폴트 메서드를 제공하는 가장 하위의 서브인터페이스가 이긴다.

컴파일러는 누구의 `hello` 메서드 정의를 사용할까? 2번 규칙에서는 서브인터페이스가 이긴다고 설명한다. 즉, B가 A를 상속받았으므로 컴파일러는 B의 `hello`를 선택한다. 따라서 프로그램은 'Hello from B'를 출력한다.

이번에는 C가 D를 상속받는다면 어떤 일이 일어날지 생각해보자.

```java
public class D implements A{ }
public class C extends D implements B, A {
    public static void main(String... args) {
        new C().hello(); // 무엇이 출력될까?
    }
}
```

1번 규칙은 클래스의 메서드 구현이 이긴다고 설명한다. D는 `hello`를 오버라이드하지 않았고 단순히 인터페이스 A를 구현했다. 따라서 D는 인터페이스 A의 디폴트 메서드 구현을 상속받는다. 2번 규칙에서는 클래스나 슈퍼클래스에 메서드 정의가 없을 때는 디폴트 메서드를 정의하는 서브인터페이스가 선택된다. 따라서 컴파일러는 인터페이스 A의 `hello`나 인터페이스 B의 `hello` 둘 중 하나를 선택해야 한다. 여기서 B가 A를 상속받는 관계이므로 이번에도 'Hello from B'가 출력된다.

##### -----퀴즈 13-2. 해석 규칙을 기억하라-----
이전 예제를 그대로 활용하자. 다만 퀴즈에서는 D가 명시적으로 A의 `hello` 메서드를 오버라이드한다. 프로그램의 실행 결과는 무엇일까?

```java
public class D implements A {
    void hello() {
        System.out.println("Hello from D");
    }
}

public class C extends D implements B, A {
    public static void main(String... args) {
        new C().hello();
    }
}
```

`정답`
프로그램의 실행 결과는 'Hello from D'다. 규칙 1에 의해 슈퍼클래스의 메서드 정의가 우선권을 갖기 때문이다.

D가 다음처럼 구현되었다고 가정하자.

```java
public abstract class D implements A {
    public abstract void hello();
}
```

그러면 A에서 디폴트 메서드를 제공함에도 불구하고 C는 `hello`를 구현해야 한다.

##### ------------------------------------------------------

### 13.4.3 충돌 그리고 명시적인 문제해결
지금까지는 1번과 2번 규칙으로 문제를 해결할 수 있었다. 이번에는 B가 A를 상속받지 않는 상황이라고 가정하자.

```java
public interface A {
    default void hello() {
        System.out.println("Hello from A");
    }
}

public interface B {
    default void hello() {
        System.out.println("Hello from B");
    }
}

public class C implements B, A { }
```

이번에는 인터페이스 간에 상속관계가 없으므로 2번 규칙을 적용할 수 없다. 그러므로 A와 B의 `hello` 메서드를 구별할 기준이 없다. 따라서 자바 컴파일러는 어떤 메서드를 호출해야 할지 알 수 없으므로 `"Error: class C imherits unrelated defaults for hello() from types B and A."` 같은 에러가 발생한다.

#### 충돌 해결
클래스와 메서드 관계로 디폴트 메서드를 선택할 수 없는 상황에서는 선택할 수 있는 방법이 없다. 개발자가 직접 클래스 C에서 사용하려는 메서드를 명시적으로 선택해야 한다. 즉, 클래스 C에서 `hello` 메서드를 오버라이드한 다음에 호출하려는 메서드를 명시적으로 선택해야 한다. 자바 8에서는 `X.super.m(...)` 형태의 새로운 문법을 제공한다. 여기서 `X`는 호출하려는 메서드 `m`의 슈퍼인터페이스다. 예를 들어 다음처럼 C에서 B의 인터페이스를 호출할 수 있다.

```java
public class C implements B, A {
    void hello() {
        B.super.hello(); // 명시적으로 인터페이스 B의 메서드를 선택한다.
    }
}
```

##### -----퀴즈 13-3. 거의 비슷한 시그니처-----
이 퀴즈에서는 인터페이스 A와 B가 다음처럼 정의되어 있다고 가정하자.

```java
public interface A {
    default Number getNumber() {
        return 10;
    }
}

public interface B {
    default Number getNumber() {
        return 42;
    }
}
```

다음은 클래스 C의 정의다.

```java
public class C implements B, A {
    public static void main(String... args) {
        System.out.println(new C().getNumber());
    }
}
```

프로그램 출력 결과는?

`정답`
C는 A와 B의 메서드를 구분할 수 없다. 따라서 클래스 C에서 컴파일 에러가 발생한다.

##### ------------------------------------------------------

### 13.4.4 다이아몬드 문제
`C++` 커뮤니티를 긴장시킬 만한 마지막 시나리오를 살펴보자.

```java
public interface A {
    default void hello() {
        System.out.println("Hello from A");
    }
}
public interface B extends A { }
public interface C extends A { }
public class D implements B, C {
    public static void main(String... args) {
        new D().hello(); // 무엇이 출력될까?
    }
}
```

이 시나리오의 다이어그램은 모양이 다이아몬드를 닮았으므로 이를 **다이아몬드 문제**(diamond problem)라고 부른다. D는 B와 C 중 누구의 디폴트 메서드 정의를 상속받을까? 실제로 선택할 수 있는 메서드 선언은 하나뿐이다. A만 디폴트 메서드를 정의하고 있다. 따라서 결국 프로그램 출력 결과는 'Hello from A'가 된다.

B에도 같은 시그니처의 디폴트 메서드 `hello`가 있다면 어떻게 될까? 2번 규칙은 디폴트 메서드를 제공하는 가장 하위의 인터페이스가 선택된다고 했다. B는 A를 상속받으므로 B가 선택된다. B와 C가 모두 디폴트 메서드 `hello` 메서드를 정의한다면 충돌이 발생하므로 이전에 설명한 것처럼 둘 중 하나의 메서드를 명시적으로 호출해야 한다.

다음처럼 인터페이스 C에 추상 메서드 `hello`(디폴트 메서드가 아님!)를 추가하면 어떤 일이 벌어질까(A와 B에는 아무 메서드도 정의하지 않는다)?

```java
public interface C extends A {
    void hello();
}
```

C는 A를 상속받으므로 C의 추상 메서드 `hello`가 A의 디폴트 메서드 `hello`보다 우선권을 갖는다. 따라서 컴파일 에러가 발생하며, 클래스 D가 어떤 `hello`를 사용할지 명시적으로 선택해서 에러를 해결해야 한다.

>**C++ 다이아몬드 문제**
>
>`C++`의 다이아몬드 문제는 이보다 더 복잡하다. 우선 `C++`는 클래스의 다중 상속을 지원한다. 클래스 D가 클래스 B와 C를 상속받고 B와 C는 클래스 A를 상속받는다고 가정하자. 그러면 클래스 D는 B 객체와 C 객체의 복사본에 접근할 수 있다. 결과적으로 A의 메서드를 사용할 때 B의 메서드인지 C의 메서드인지 명시적으로 해결해야 한다. 또한 클래스는 상태를 가질 수 있으므로 B의 멤버 변수를 고쳐도 C 객체의 복사본에 반영되지 않는다.

같은 디폴트 메서드 시그니처를 갖는 여러 메서드를 상속받는 문제를 쉽게 해결할 수 있음을 살펴봤다. 다음과 같은 세 가지 규칙만 적용하면 모든 충돌 문제를 해결할 수 있다.

1. 클래스가 항상 이긴다. 클래스나 슈퍼클래스에서 정의한 메서드가 디폴트 메서드보다 우선권을 갖는다.
2. 1번 규칙 이외의 상황에서는 서브인터페이스가 이긴다. 상속관계를 갖는 인터페이스에서 같은 시그니처를 갖는 메서드를 정의할 때는 서브인터페이스가 이긴다. 즉, B가 A를 상속받는다면 B가 A를 이긴다.
3. 여전히 디폴트 메서드의 우선순위가 결정되지 않았다면 여러 인터페이스를 상속받는 클래스가 명시적으로 디폴트 메서드를 오버라이드하고 호출해야 한다.

---

## 13.5 마치며
- 자바 8의 인터페이스는 구현 코드를 포함하는 디폴트 메서드, 정적 메서드를 정의할 수 있다.
- 디폴트 메서드의 정의는 `default` 키워드로 시작하며 일반 클래스 메서드처럼 바디를 갖는다.
- 공개된 인터페이스에 추상 메서드를 추가하면 소스 호환성이 깨진다.
- 디폴트 메서드 덕분에 라이브러리 설계자가 API를 바꿔도 기존 버전과 호환성을 유지할 수 있다.
- 선택형 메서드와 동작 다중 상속에도 디폴트 메서드를 사용할 수 있다.
- 클래스가 같은 시그니처를 갖는 여러 디폴트 메서드를 상속하면서 생기는 충돌 문제를 해결하는 규칙이 있다.
- 클래스나 슈퍼클래스에 정의된 메서드가 다른 디폴트 메서드 정의보다 우선한다. 이 외의 상황에서는 서브인터페이스에서 제공하는 디폴트 메서드가 선택된다.
- 두 메서드의 시그니처가 같고, 상속관계로도 충돌 문제를 해결할 수 없을 때는 디폴트 메서드를 사용하는 클래스에서 메서드를 오버라이드해서 어떤 디폴트 메서드를 호출할지 명시적으로 결정해야 한다.

---