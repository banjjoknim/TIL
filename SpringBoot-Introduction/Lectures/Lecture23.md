# 스프링 데이터 JPA
- `스프링 부트`와 `JPA`만 사용해도 개발 생산성이 정말 많이 증가하고, 개발해야할 코드도 확연히 줄어든다.
-여기에 `스프링 데이터 JPA`를 사용하면 기존의 한계를 넘어 마치 마법처럼, 리포지토리에 구현 클래스 없이 인터페이스 만으로 개발을 완료할 수 있다.
- 그리고 반복 개발해온 기본 CRUD 기능도 스프링 데이터 JPA가 모두 제공합니다.
- 스프링 부트와 JPA라는 기반 위에, 스프링 데이터 JPA라는 환상적인 프레임워크를 더하면 지금까지 조금이라도 단순하고 반복이라 생각했던 개발 코드들이 확연하게 줄어든다.
- 따라서 개발자는 핵심 비즈니스 로직을 개발하는데, 집중할 수 있다.
- 실무에서 관계형 데이터베이스를 사용한다면 스프링 데이터 JPA는 이제 선택이 아니라 필수이다.

>**주의**
>
>스프링 데이터 JPA는 JPA를 편리하게 사용하도록 도와주는 기술이다. 따라서 JPA를 먼저 학습한 후에 스프링 데이터 JPA를 학습해야 한다.

---

## SpringDataJpaRepository 인터페이스 생성
```java
public interface SpringDataJpaMemberRepository extends JpaRepository<Member, Long>, MemberRepository {

    @Override
    Optional<Member> findByName(String name);
}
```

- `org.springframework.data.jpa.repository.JpaRepository`를 상속받는다.
- 인터페이스는 인터페이스를 `상속(extends)` 받는다(`구현(implements)`-말고).
- `JpaRepository<Entity, ID>`의 제네릭 타입은 `Entity`와 `ID`로 지정해야 하며, 각각 `Entity`가 선언된 클래스 타입과 해당 클래스의 `PK` 타입을 지정한다.
- 인터페이스는 다중 상속이 된다.
- `스프링 데이터 JPA`는 `JpaRepository<Entity, ID>`를 상속받는 인터페이스의 구현체를 `프록시`라는 기능을 이용해서 자동으로 만들어 스프링 빈에 자동으로 등록해준다. 우리는 그걸 그냥 가져다가 쓰는 것이다.

---

## 스프링 데이터 JPA 회원 리포지토리를 사용하도록 스프링 설정 변경

```java
@Configuration
public class SpringConfig {

    private final MemberRepository memberRepository;

    @Autowired
    public SpringConfig(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Bean
    public MemberService memberService() {
        return new MemberService(memberRepository);
    }
}
```

- 위의 `SpringConfig` 생성자에 `MemberRepository`는 다음과 같이 주입된다. 
- 스프링 컨테이너에서 `MemberRepository`를 찾는다. 이때 `MemberRepository`는 `스프링 데이터 JPA`가 만들고 빈으로 등록해줬던 구현체가(를) 주입된다(주입 받을 수 있다).
- `스프링 데이터 JPA`는 `JPA`의 기술을 그대로 가져다 쓰기 때문에 로그가 동일하게 나온다.

---

## 스프링 데이터 JPA 제공 클래스
![스프링 데이터 JPA 제공 클래스](https://user-images.githubusercontent.com/68052095/103027431-310a3f00-4599-11eb-8cc5-1e2d7a02b86f.PNG)

- 기본적으로 `JpaRepository` 인터페이스에서 공통적인 메서드를 제공해준다.
- 하지만 예를 들어, 이름으로 찾는다던지 이메일로 찾는다던지 공통적이지 않은 경우 메서드를 정의해줘야 한다.
    - ex. 메서드명이 `findByName`일 경우, `스프링 데이터 JPA`가 규칙에 따라 되어있는 `select m from Member m where m.name = ?`라는 `JPQL` 쿼리를 짜준다.
    - 이와 같이 인터페이스 이름만으로 개발을 끝낼 수 있다.
    - 메서드명과 반환타입, 파라미터 등을 `리플렉션` 기술로 읽어들여서 쿼리로 풀어낸다.

---

## 스프링 데이터 JPA 제공 기능
- 인터페이스를 통한 기본적인 `CRUD` 기능 제공
- `findByName()` , `findByEmail()` 처럼 `메서드 이름 만으로 조회 기능` 제공
- `페이징 기능` 자동 제공

---

## 참고
- 실무에서는 `JPA`와 `스프링 데이터 JPA`를 기본으로 사용하고, `복잡한 동적 쿼리`는 `Querydsl`이라는 라이브러리를 사용하면 된다. 
- `Querydsl`을 사용하면 쿼리도 자바 코드로 안전하게 작성할 수 있고, 동적
쿼리도 편리하게 작성할 수 있다. 
- 이 조합으로 해결하기 어려운 쿼리는 `JPA`가 제공하는 `네이티브 쿼리(직접 쿼리를 짜는 것)`를 사용하거나, 앞서 학습한 스프링 `JdbcTemplate`를 사용하면 된다.

---