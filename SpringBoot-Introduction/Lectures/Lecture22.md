# JPA
- JPA는 기존의 반복 코드는 물론이고, 기본적인 SQL도 JPA가 직접 만들어서 실행해준다.
- JPA를 사용하면, SQL과 데이터 중심의 설계에서 객체 중심의 설계로 패러다임을 전환을 할 수 있다.
- JPA를 사용하면 개발 생산성을 크게 높일 수 있다.
- 객체를 JPA에 넣으면 JPA가 중간에서 DB에 SQL을 날리고 데이터를 가져오는 등의 처리를 JPA가 다 해준다.

---

## build.gradle 파일에 JPA, h2 데이터베이스 관련 라이브러리 추가
```java
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-thymeleaf'
    implementation 'org.springframework.boot:spring-boot-starter-web'
    //implementation 'org.springframework.boot:spring-boot-starter-jdbc'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    runtimeOnly 'com.h2database:h2'
    testImplementation('org.springframework.boot:spring-boot-starter-test') {
    exclude group: 'org.junit.vintage', module: 'junit-vintage-engine'
    }
}
```

- `spring-boot-starter-data-jpa` 는 내부에 `jdbc 관련 라이브러리를 포함`한다. 따라서 jdbc는 제거해도 된다.

---

## 스프링 부트에 JPA 설정 추가
`resources/application.properties`
```java
spring.datasource.url=jdbc:h2:tcp://localhost/~/test
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.jpa.show-sql=true
spring.jpa.hibernate.ddl-auto=none
```

>**주의**
>
>스프링부트 2.4부터는 spring.datasource.username=sa 를 꼭 추가해주어야 한다. 그렇지 않으면 오류가 발생한다.

- `show-sql` : JPA가 생성하는 SQL을 출력한다.
- `ddl-auto` : JPA는 테이블을 자동으로 생성하는 기능을 제공하는데 `none` 를 사용하면 해당 기능을 끈다.
    - `create` 를 사용하면 엔티티 정보를 바탕으로 테이블도 직접 생성해준다.

---

## JPA 엔티티 매핑
- `JPA`를 사용하려면 먼저 `Entity`를 매핑해야한다.
- `JPA`는 인터페이스만 제공한다. 그리고 구현 기술들이 여러개가 있는데, 우리는 `Hibernate`만 사용한다고 생각하면 된다.
- `JPA`는 자바진영의 표준 인터페이스이고 구현은 여러 업체들이 하는 것이라 생각하면 된다. 각 업체별로 차이점이 조금씩 있다.
- `ORM(Object Relational Mapping)` : 객체와 관계형 데이터베이스의 테이블을 매핑한다는 뜻이다.
- `@Entity(javax.persistence.Entity)`를 클래스에 선언하면 해당 클래스는 `JPA`가 관리하는 엔티티가 된다.
- 그 다음으로는 `PK`를 매핑해줘야 한다(`@Id` 사용). 또, 현재 우리는 데이터베이스에서 자동으로 `ID`를 생성해주고 있는데 이를 `IDENTITY`전략이라고 부르며 `@GeneratedValue(strategy = GenerationType.IDENTITY)`로 선언한다.
- 특정 필드를 DB에 있는 특정 컬럼명과 매핑할 때, 해당 필드에 `@Column(name = "컬럼명")`을 선언해주면 된다.
- 이렇게 어노테이션들을 통해서 데이터베이스의 테이블과 매핑하면 `JPA`가 해당 정보를 가지고 각종 쿼리들을 처리해준다.

---

## JPA 회원 리포지토리
```java
@Transactional
public class JpaMemberRepository implements MemberRepository {

    private final EntityManager entityManager;

    public JpaMemberRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Member save(Member member) {
        entityManager.persist(member);
        return member;
    }

    @Override
    public Optional<Member> findById(Long id) {
        Member member = entityManager.find(Member.class, id);
        return Optional.ofNullable(member);
    }

    @Override
    public Optional<Member> findByName(String name) {
        List<Member> result = entityManager.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name", name)
                .getResultList();

        return result.stream().findAny();
    }

    @Override
    public List<Member> findAll() {
        return entityManager.createQuery("select m from Member m", Member.class)
                .getResultList();
    }
}
```

- `'org.springframework.boot:spring-boot-starter-data-jpa'` 라이브러리를 받으면 스프링 부트가 자동으로 설정 정보와 데이터베이스 커넥션 정보등을 이용해서 `EntityManager`라는 것을 생성해준다. 그럼 우리는 이걸 그대로 주입받으면 된다.
- `JPA`는 `EntityManager`라는 것으로 모든게 동작한다. 결론적으로 `JPA`를 사용하려면 `EntityManager`를 주입받아야 한다.
- `EntityManager`는 내부적으로 `DataSource`를 가지고 있어서 DB와 통신하는 등의 행위를 알아서 처리한다.
- `persist` 는 영구저장(영속화)하다 라는 뜻이다. 이렇게 하면`JPA`가 알아서 `insert` 쿼리를 만들어 DB에 집어넣고 `setId`까지 모든걸 다 해준다.
- `inline` 단축키 : `Ctrl + Alt + N`
- `PK`의 경우는 그냥 조회할 수 있지만(`EntityManager.find()` 이용) 그 외에 `PK` 기반이 아닌 것들은 `JPQL`이라는 객체지향 쿼리언어를 사용해야 한다(거의 SQL과 똑같다).
    - 객체를 대상으로 쿼리를 날리면 SQL로 번역이 된다.
    - `select`의 대상이 객체(`Entity`) 그 자체이다.
- `JPA`를 사용하려면 항상 `@Transactional`이 선언되어 있어야 한다.
- `org.springframework.transaction.annotation.Transactional` 를 사용하자.
- 스프링은 해당 클래스의 메서드를 실행할 때 트랜잭션을 시작하고, 메서드가 정상 종료되면 트랜잭션을 커밋한다. 만약 런타임 예외가 발생하면 롤백한다.
- JPA를 통한 모든 데이터 변경은 트랜잭션 안에서 실행해야 한다.

---

## JPA를 사용하도록 스프링 설정 변경

```java
@Configuration
public class SpringConfig {

    private EntityManager entityManager;

    @Autowired
    public SpringConfig(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Bean
    public MemberService memberService() {
        return new MemberService(memberRepository());
    }

    @Bean
    public MemberRepository memberRepository() {
//        return new MemoryMemberRepository();
//        return new JdbcMemberRepository(dataSource);
//        return new JdbcTemplateMemberRepository(dataSource);
        return new JpaMemberRepository(entityManager);
    }
}
```

---

## 추가 설명
- `spring-data-jpa`를 세팅하면 기본적으로 `Hibernate`라는 오픈소스 구현체가 사용된다.
