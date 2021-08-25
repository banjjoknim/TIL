# 아이템 39. 명명 패턴보다 애너테이션을 사용하라

전통적으로 도구나 프레임워크나 특별히 다뤄야 할 프로그램 요소에는 딱 구분되는 명명 패턴을 적용해왔다.

## 명명 패턴의 단점

- 첫 번째, 오타가 나면 안 된다.
- 두 번째, 올바른 프로그램 요소에서만 사용되리라 보증할 방법이 없다.
- 세 번째, 프로그램 요소를 매개변수로 전달할 마땅한 방법이 없다.

추가로, 컴파일러는 문자열이 무엇을 가리키는지 알 방법이 없다. 애너테이션은 이 모든 문제를 해결해준다. JUnit도 버전 4부터 전면 도입하였다.

## 애너테이션

### 마커(marker) 애너테이션 타입 선언

```java
/**
 * 테스트 메서드임을 선언하는 애너테이션이다.
 * 매개변수 없는 정적 메서드 전용이다.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Test {
}
```

- @Retention과 @Target과 같이 애너테이션 선언에 다는 애너테이션을 메타애너테이션(meta-annotation)이라 한다.
- @Retention(RetentionPolicy.RUNTIME) 메타애너테이션은 @Test가 런타임에도 유지되어야 한다는 표시다.
    - 만약 이 메타애너테이션을 생략하면 테스트 도구는 @Test를 인식할 수 없다.
- @Target(ElementType.METHOD) 메타애너테이션은 @Test가 반드시 메서드 선언에서만 사용돼야 한다고 알려준다. 따라서 클래스 선언, 필드 선언 등 다른 프로그램 요소에는 달 수 없다.
- 앞의 Test 애너테이션을 적절한 애너테이션 처리기 없이 인스턴스 메서드나 매개변수가 있는 메서드에 달면 컴파일은 잘 되겠지만, 테스트 도구를 실행할 때 문제가 된다.
- 이와 같은 애너테이션을 "아무 매개변수 없이 단순히 대상에 마킹(marking)한다"는 뜻에서 마커(marker) 애너테이션이라 한다.

### 마커 애너테이션을 사용한 프로그램 예

```java
public class Sample {
    @Test
    public static void m1() { // 설공해야 한다.
    }

    public static void m2() {
    }

    @Test
    public static void m3() { // 실패해야 한다.
        throw new RuntimeException("실패");
    }

    public static void m4() {
    }

    @Test
    public void m5() { // 잘못 사용한 예: 정적 메서드가 아니다.
    }

    public static void m6() {
    }

    @Test
    public static void m7() { // 실패해야 한다.
        throw new RuntimeException("실패");
    }

    public static void m8() {
    }
}
```

- @Test 애너테이션이 Sample 클래스의 의미에 직접적인 영향을 주지는 않는다.
- 단지 이 애너테이션에 관심 있는 프로그램에게 추가 정보를 제공할 뿐이다.
- 대상 코드의 의미는 그대로 둔 채 그 애너테이션에 관심 있는 도구에서 특별한 처리를 할 기회를 준다.

### 마커 애너테이션을 처리하는 프로그램

```java
public class RunTests {
    public static void main(String[] args) throws ClassNotFoundException, InvocationTargetException, IllegalAccessException {
        int tests = 0;
        int passed = 0;
        Class<?> testClass = Class.forName(args[0]);
        for (Method m : testClass.getDeclaredMethods()) {
            if (m.isAnnotationPresent(Test.class)) {
                tests++;
                try {
                    m.invoke(null);
                    passed++;
                } catch (InvocationTargetException wrappedExc) {
                    Throwable exc = wrappedExc.getCause();
                    System.out.println(m + "실패: " + exc);
                } catch (Exception exception) {
                    System.out.println("잘못 사용한 @Test: " + m);
                }
            }
        }
        System.out.printf("성공: %d, 실패: %d%n", passed, tests - passed);
    }
}
```

- 이 테스트 러너는 명령줄로부터 완전 정규화된 클래스 이름을 받아, 그 클래스에서 @Test 애너테이션이 달린 메서드를 차례로 호출한다.
- 여기서 isAnnotationPresent가 실행할 메서드를 찾아주는 메서드다.
- 테스트 메서드가 예외를 던지면 리플렉션 메커니즘이 InvocationTargetException으로 감싸서 다시 던진다.
    - 따라서 이 프로그램은 InvocationTargetException을 잡아 원래 예외에 담긴 실패 정보를 추출해(getCause) 출력한다.
- 만약 InvocationTargetException 외의 예외가 발생한다면 @Test 애너테이션을 잘못 사용했다는 뜻이다.

### 매개변수 하나를 받는 애너테이션 타입

```java
/**
 * 명시한 예외를 던저야만 성공하는 테스트 메서드 애너테이션
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ExceptionTest {
    Class<? extends Throwable> value();
}
```

- 이 애너테이션의 매개변수 타입은 `Class<? extend Throwable>`이다.
- 여기서 와일드카드 타입은 "Throwable을 확장한 클래스의 Class 객체"라는 뜻이며, 따라서 모든 예외 타입을 다 수용한다.

### 배열 매개변수를 받는 애너테이션 타입

```java

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ExceptionTest {
    Class<? extends Throwable>[] value();
}
```

- 배열 매개변수를 받는 애너테이션용 문법은 아주 유연하다.
- 단일 원소 배열에 최적화했지만, 앞서의 @ExceptionTest들도 모두 수정 없이 수용한다.
- 원소가 여럿인 배열을 지정할 때는 다음과 같이 원소들을 중괄호로 감싸고 쉼표로 구분해주기만 하면 된다.

### 배열 매개변수를 받는 애너테이션을 사용하는 코드

```java
public class RunTests {
    @ExceptionTest({IndexOutOfBoundsException.class, NullPointerException.class})
    public static void doublyBad() { // 성공해야 한다.
        List<String> list = new ArrayList<>();

        // 자바 API 명세에 따르면 다음 메서드는 IndexOutOfBoundsException이나 NullPointerException을 던질 수 있다.
        list.addAll(5, null);
    }
}
```

### 반복 가능한 애너테이션 타입

- 자바 8에서는 여러 개의 값을 받는 애너테이션을 다른 방식으로도 만들 수 있다.
- 배열 매개변수를 사용하는 대신 애너테이션에 @Repeatable 메타애너테이션을 다는 방식이다.
- @Repeatable을 단 애너테이션은 하나의 프로그램 요소에 여러 번 달 수 있다.

**주의사항**

- 첫 번째, @Repeatable을 단 애너테이션을 반환하는 '컨테이너 애너테이션'을 하나 더 정의하고, @Repeatable에 이 컨테이너 애너테이션의 class 객체를 매개변수로 전달해야 한다.
- 두 번째, 컨테이너 애너테이션은 내부 애너테이션 타입의 배열을 반환하는 value 메서드를 정의해야 한다.
- 마지막으로 컨테이너 애너테이션 타입에는 적절한 보존 정책(@Retention)과 적용 대상(@Target)을 명시해야 한다. 그렇지 않으면 컴파일되지 않을 것이다.

```java
// 반복 가능한 애너테이션
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(ExceptionTestContainer.class)
public @interface ExceptionTest {
    Class<? extends Throwable> value();
}

// 컨테이너 애너테이션
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ExceptionTestContainer {
    ExceptionTest[] value();
}
```

- 반복 가능 애너테이션을 여러 개 달면 하나만 달았을 때와 구분하기 위해 해당 '컨테이너' 애너테이션 타입이 적용된다.
- getAnnotationByType 메서드는 이 둘을 구분하지 않아서 반복 가능 애너테이션과 그 컨테이너 애너테이션을 모두 가져오지만, isAnnotationPresent 메서드는 둘을 명확히 구분한다.
- 따라서 반복 가능 애너테이션을 여러 번 단 다음 isAnnotationPresent로 반복 가능 애너테이션이 달렸는지 검사한다면 "그렇지 않다"고 알려준다(컨테이너가 달렸기 때문에).
    - 그 결과 애너테이션을 여러 번 단 메서드들을 모두 무시하고 지나친다.
    - 같은 이유로, isAnnotationPresent로 컨테이너 애너테이션이 달렸는지 검사한다면 반복 가능 애너테이션을 한 번만 단 메서드를 무시하고 지나친다.
    - 결론적으로, 애너테이션이 달려 있는 수와 상관없이 모두 검사하려면 둘을 따로따로 확인해야 한다.

## 핵심 정리

- **애너테이션으로 할 수 있는 일을 명명 패턴으로 처리할 이유는 없다.**
- **자바 프로그래머라면 예외 없이 자바가 제공하는 애너테이션 타입들은 사용해야 한다.**
    - IDE나 정적 분석 도구가 제공하는 애너테이션을 사용하면 해당 도구가 제공하는 진단 정보의 품질을 높여줄 것이다.
    - 단, 그 애너테이션들은 표준이 아니니 도구를 바꾸거나 표준이 만들어지면 수정 작업이 필요할 수 있다.