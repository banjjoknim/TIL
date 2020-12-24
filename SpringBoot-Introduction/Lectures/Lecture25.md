# AOP 적용
- `AOP`: `Aspect Oriented Programming(관점지향 프로그래밍)`
- `공통 관심 사항(cross-cutting concern)` vs `핵심 관심 사항(core concern)` 분리

![공통 관심 사항](https://user-images.githubusercontent.com/68052095/103096266-b861bc00-4646-11eb-887a-61f9bc0468f4.PNG)

- `AOP`를 이용하면 `공통 관심 사항`에 대한 로직을 한 군데에 다 모아두고 원하는 곳에만 지정하여 적용할 수 있다.

```java
@Aspect
@Component
public class TimeTraceAop {

    @Around("execution(* hello.springintroduction..*(..))")
    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        System.out.println("START: " + joinPoint.toString());
        try {
            return joinPoint.proceed();
        } finally {
            long finish = System.currentTimeMillis();
            long timeMs = finish - start;
            System.out.println("END: " + joinPoint.toString() + " " + timeMs + "ms");
        }
    }
}
```

- `AOP`는 `@Aspect` 어노테이션을 선언해야 사용할 수 있다.
- `ProceedingJoinPoint`????
- `joinPoint.proceed()`를 호출하면 다음 메서드로 진행할 수 있다.
- `joinPoint.toString()`를 통해서 어떤 메서드를 호출했는지 이름을 다 얻을 수 있다.
- `AOP`는 설정 파일을 이용해 스프링의 빈으로 등록해서 사용하는 것을 선호한다. 평범한 서비스, 리포지토리와 같은 것들은 정형화해서 사용할 수 있지만, `AOP`의 경우에는 `AOP`임을 인지할 수 있도록 빈으로 직접 등록하는 것이 낫다(강의에서는 간단하게 컴포넌트스캔 사용).
- 그리고 `AOP`를 사용하기 위해서는 `@Around()`를 선언해서 괄호 안에 공통 관심사를 어디에 적용할 것인지를 지정해줘야 한다.
- `@Around("execution(* hello.springintroduction..*(..))")` 에서 `"execution(* hello.springintroduction..*(..))"`는 `hello.springintroduction` 패키지의 하위에는 전부 적용하라 라는 뜻이다.
- `joinPoint`는 메서드가 호출될 때마다 중간에서 인터셉트(?)가 걸려서 메서드 `execute`가 호출된다. 조건을 걸어서 메서드 호출 등을 조작할 수도 있다.

---

## 해결
- 회원가입, 회원 조회등 핵심 관심사항과 시간을 측정하는 공통 관심 사항을 분리한다.
- 시간을 측정하는 로직을 별도의 공통 로직으로 만들었다.
- 핵심 관심 사항을 깔끔하게 유지할 수 있다.
- 변경이 필요하면 이 로직만 변경하면 된다.
- 원하는 적용 대상을 선택할 수 있다(보통 패키지레벨로 많이 한다).

---

## 스프링의 AOP 동작 방식

### AOP 적용 전 의존관계
![AOP 적용 전 의존관계](https://user-images.githubusercontent.com/68052095/103100515-41342400-4656-11eb-9aa1-961ab06a3432.PNG)
- 컨트롤러에서 서비스를 호출할 때 의존관계를 이용해서 호출한다.

### AOP 적용 후 의존관계
![AOP 적용 후 의존관계](https://user-images.githubusercontent.com/68052095/103100513-409b8d80-4656-11eb-9da4-23fb85a71e78.PNG)
- `AOP`를 적용하고 어디에 적용할지 지정을 하면 의존관계가 있는 서비스가 지정된다.
- 스프링은 `AOP`가 있으면 가짜 서비스를 만들어낸다(프록시라는 기술을 사용한다).
- `AOP`를 적용하면 스프링 컨테이너는 스프링이 올라올 때(스프링 빈을 등록할 때) 진짜 스프링 빈 말고 가짜 스프링 빈을 앞에 세워둔다.
- 그리고 가짜 스프링 빈이 끝나고(?), `joinPoint.proceed()`를 호출하면 내부적인 처리를 거쳐 진짜 스프링 빈을 호출한다. 즉, 컨트롤러가 호출하는 건 진짜 서비스가 아닌 프록시라는 기술로 발생하는 가짜 서비스이다.
- 실제로 서비스가 주입될 때 프록시(가짜 서비스)를 콘솔에서 확인할 수도 있다.
- 콘솔 로그를 보면 `memberService = class hello.springintroduction.service.MemberService$$EnhancerBySpringCGLIB$$6974282c` 라고 해서, `EnhancerBySpringCGLIB`을 확인할 수 있다. 여기서는 `MemberService`를 복제해서 코드를 조작하는 기술이다.
- 스프링 컨테이너가 `AOP`가 적용되면 프록시(가짜)를 통해서 `AOP`가 실행이 되고 다음으로 `joinPoint.proceed()`가 호출되면 진짜 서비스가 호출된다.
- 컨테이너에서 스프링 빈을 관리하면 가짜를 만들어서 `DI`를 해줄 수 있다. 그렇기 때문에 `DI`함으로써(이때 프록시가 주입된다) `AOP`가 가능해진다.
- 스프링에서는 이를 `프록시 방식의 AOP`라고 하며, 아예 자바에서 컴파일할 때 코드를 생성해서 코드를 위아래로 박아서 넣어주는 기술들도 있다(?).

### AOP 적용 전 전체그림
![AOP 적용 전 전체그림](https://user-images.githubusercontent.com/68052095/103100512-4002f700-4656-11eb-9d0c-e4070ab70785.PNG)

### AOP 적용 후 전체그림
![AOP 적용 후 전체그림](https://user-images.githubusercontent.com/68052095/103100508-3ed1ca00-4656-11eb-9bbc-7907abbbeb65.PNG)

---