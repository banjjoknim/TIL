# Chapter3. 스프링 부트에서 JPA로 데이터베이스 다뤄보자

---

## 3.1 JPA 소개

현대의 웹 애플리케이션에서 관계형 데이터베이스(`RDB`, `Relational Database`)는 빠질 수 없는 요소입니다. `Oracle`, `MySQL`, `MSSQL` 등을 쓰지 않는 웹 애플리케이션은 거의 없습니다. 그러다 보니 **객체를 관계형 데이터베이스에서 관리**하는 것이 무엇보다 중요합니다. 그러나 개발자가 아무리 자바 클래스를 아릅답게 설계해도, SQL을 통해야만 데이터베이스에 저장하고 조회할 수 있습니다. 결국, 관계형 데이터베이스를 사용해야만 하는 상황에서 **SQL은 피할 수 없습니다.** 

이 반복적인 SQL을 단순하게 반복해야만 하는 문제 외에도 **패러다임 불일치** 문제도 있습니다. 관계형 데이터베이스는 **어떻게 데이터를 저장**할지에 초점이 맞춰진 기술입니다. 반대로 객체지향 프로그래밍 언어는 메시지를 기반으로 **기능과 속성을 한 곳에서 관리**하는 기술입니다. 이 둘은 이미 사상부터 다른 시작점에서 출발했습니다. 관계형 데이터베이스와 객체지향 프로그래밍 언어의 패러다임이 서로 다른데, 객체를 데이터베이스에 저장하려고 하니 여러 문제가 발생합니다. 이를 **패러다임 불일치**라고 합니다.

`상속`, `1 : N` 등 다양한 객체 모델링을 데이터베이스로는 구현할 수 없습니다. 그러다 보니 웹 어플리케이션 개발은 점점 **데이터베이스 모델링**에만 집중하게 됩니다. `JPA`는 이런 문제점을 해결하기 위해 등장하게 됩니다.

서로 지향하는 바가 다른 2개 영역(객체지향 프로그래밍 언어와 관계형 데이터베이스)을 **중간에서 패러다임 일치**를 시켜주기 위한 기술입니다. 즉, 개발자는 **객체지향적으로 프로그래밍을 하고**, `JAP`가 이를 관계형 데이터베이스에 맞게 SQL을 대신 생성해서 실행합니다. 개발자는 항상 객체 지향적으로 코드를 표현할 수 있으니 더는 **SQL에 종속적인 개발을 하지 않아도 됩니다.**

#### Spring Data JPA

`JPA`는 인터페이스로서 자바 표준명세서입니다.인터페이스인 `JPA`를 사용하기 위해서는 구현체가 필요합니다. 대표적으로 `Hibernate`, `EclipseLink` 등이 있습니다. 하지만 `Spring`에서 `JPA`를 사용할 때는 이 구현체들을 직접 다루진 않습니다. 구현체들을 좀 더 쉽게 사용하고자 추상화시킨 `Spring Data JPA`라는 모듈을 이용하여 `JPA` 기술을 다룹니다. 이들의 관계를 보면 다음과 같습니다.

- `JPA <- Hibernate <- Spring Data JPA`

`Hibernate`를 쓰는 것과 `Spring Data JPA`를 쓰는 것 사이에는 큰 차이가 없습니다. 그럼에도 스프링 진영에서는 `Spring Data JPA`를 개발했고, 이를 권장하고 있습니다. 이렇게 한 단계 더 감싸놓은 `Spring Data JPA`가 등장한 이유는 크게 두 가지가 있습니다.

- **구현체 교체의 용이성**
- **저장소 교체의 용이성**

먼저 '구현체 교체의 용이성'이란 **`Hibernate` 외에 다른 구현체로 쉽게 교체하기 위함**입니다. `Hibernate`가 언젠간 수명을 다해서 새로운 `JPA` 구현체가 대세로 떠오를 때, `Spring Data JPA`를 쓰는 중이라면 아주 쉽게 교체할 수 있습니다. `Spring Data JPA` 내부에서 구현체 매핑을 지원해주기 때문입니다. 
다음으로 '저장소 교체의 용이성'이란 **관계형 데이터베이스 외에 다른 저장소로 쉽게 교체하기 위함**입니다. `Spring Data`의 하위 프로젝트들은 기본적인 **CRUD의 인터페이스가 같기**때문에, 간단하게 **의존성만 교체**함으로써 저장소의 교체가 가능합니다. 즉, `Spring Data JPA`, `Spring Data Redis`, `Spring Data MongoDB` 등등 `Spring Data`의 하위 프로젝트들은 `save()`, `findAll()`, `findOne()` 등을 인터페이스로 갖고 있습니다. 그러다 보니 저장소가 교체되어도 기본적인 기능은 변경할 것이 없습니다. 이런 장점들로 인해 `Spring Data` 프로젝트를 권장하고 있습니다.

#### 실무에서 JPA

실무에서 `JPA`를 사용하지 못하는 가장 큰 이유로 **높은 러닝 커브**를 이야기합니다. `JPA`를 잘 쓰려면 **객체지향 프로그래밍과 관계형 데이터베이스**를 둘 다 이해해야 합니다. 하지만 그만큼 `JPA`를 사용해서 얻는 보상은 큽니다. 가장 먼저 `CRUD` 쿼리를 직접 작성할 필요가 없습니다. 또한, 부모-자식 관계 표현, `1 : N` 관계 표현, 상태와 행위를 한 곳에서 관리하는 등 객체지향 프로그래밍을 쉽게 할 수 있습니다.

#### 요구사항 분석
앞으로 만들 게시판의 요구사항입니다.

- 게시판 기능
  - 게시글 조회
  - 게시글 등록
  - 게시글 수정
  - 게시글 삭제

- 회원기능
  - 구글 / 네이버 로그인
  - 로그인한 사용자 글 작성 권한
  - 본인 작성 글에 대한 권한 관리

---

## 3.2 프로젝트에 Spring Data JPA 적용하기
먼저 `build.gradle`에 다음과 같이 `org.springframework.boot:spring-boot-starter-data-jpa`와 `com.h2database:h2` 의존성들을 등록합니다.

```java
dependencies {
    compile('org.springframework.boot:spring-boot-starter-web')
    compile('org.projectlombok:lombok')
    compile('org.springframework.boot:spring-boot-starter-data-jpa') // 1.
    compile('com.h2database:h2') // 2.
    testCompile('org.springframework.boot:spring-boot-starter-test')
}
```
**1. spring-boot-starter-data-jpa**
- 스프링 부트용 `Spring Data JPA` 추상화 라이브러리입니다.
- 스프링 부트 버전에 맞춰 자동으로 `JPA`관련 라이브러리들의 버전을 관리해 줍니다.

**2. h2**
- 인메모리 관계형 데이터베이스입니다.
- 별도의 설치가 필요 없이 프로젝트 의존성만으로 관리할 수 있습니다.
- 메모리에서 실행되기 때문에 애플리케이션을 재시작할 때마다 초기화된다는 점을 이용하여 테스트 용도로 많이 사용됩니다.

도메인이란 게시글, 댓글, 회원, 정산, 결제 등 소프트웨어에 대한 요구사항 혹은 문제영역이라고 생각하면 됩니다. 그간 `xml`에 쿼리를 담고, 클래스는 오로지 쿼리의 결과만 담던 일들이 모두 도메인 클래스라고 불리는 곳에서 해결됩니다.

`domain` 패키지에 **posts 패키지와 Posts 클래스**를 만듭니다.

`Posts` 클래스의 코드는 다음과 같습니다.

```java
package com.banjjoknim.book.springboot.domain.posts;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter // 6.
@NoArgsConstructor // 5.
@Entity // 1.
public class Posts {

    @Id // 2.
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 3.
    private Long id;

    @Column(length = 500, nullable = false) // 4.
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    private String author;

    @Builder // 7.
    public Posts(String title, String content, String author) {
        this.title = title;
        this.content = content;
        this.author = author;
    }
}
```
여기서 `Posts` 클래스는 실제 `DB`의 테이블과 매칭될 클래스이며 보통 `Entity` 클래스라고도 합니다.`JPA`를 사용하시면 `DB` 데이터에 작업할 경우 실제 쿼리를 날리기보다는, 이 `Entity` 클래스의 수정을 통해 작업합니다.

**`Posts` 클래스에는 `JPA`에서 제공하는 어노테이션들이 몇 개 있습니다.**

**1. @Entity**
- 테이블과 링크될 클래스임을 나타냅니다.
- 기본값으로 클래스의 카멜케이스 이름을 언더스코어 네이밍(_)으로 테이블 이름을 칭합니다.
- ex)SalesManager.java -> sales_manager table

**2. @Id**
- 해당 테이블의 `PK` 필드를 나타냅니다.

**3. @GeneratedValue**
- `PK`의 생성 규칙을 나타냅니다.
- 스프링 부트 2.0 에서는 `GenerationType.IDENTITY` 옵션을 추가해야만 `auto_increment`가 됩니다.

**4. @Column**
- 테이블의 칼럼을 나타내며 굳이 선언하지 않더라도 해당 클래스의 필드는 모두 럼이 됩니다.
- 사용하는 이유는, 기본값 외에 추가로 변경이 필요한 옵션이 있으면 사용합니다.
- 문자열의 경우 `VARCHAR(255)`가 기본값인데, 사이즈를 500으로 늘리고 싶거나(`ex: title`), 타입을 `TEXT`로 변경하고 싶거나(`ex: content`) 등의 경우에 사용됩니다.

**5. @NoArgsConstructor**
- 기본 생성자 자동 추가
- `public Posts(){}`와 같은 효과

**6. @Getter**
- 클래스 내 모든 필드의 `Getter` 메소드를 자동생성

**7. @Builder**
- 해당 클래스의 빌더 패턴 클래스를 생성
- 생성자 상단에 선언 시 생성자에 포함된 필드만 빌더에 포함

이 `Posts` 클래스에는 **Setter 메소드가 없습니다.** 자바빈 규약을 생각하면서 **`getter/setter`를 무작정 생성**하는 경우가 있습니다. 이렇게 되면 해당 클래스의 인스턴스 값들이 언제 어디서 변해야하는지 코드상으로 명확하게 구분할 수가 없어, 차후 기능 변경 시 정말 복잡해집니다. 그래서 **`Entity` 클래스에서는 절대 `Setter` 메소드를 만들지 않습니다.** 대신, 해당 필드의 값 변경이 필요하면 명확히 그 목적과 의도를 나타낼 수 있는 메소드를 추가해야만 합니다. 예를 들어 주문 취소 메소드를 만든다고 가정하면 다음 코드로 비교해보면 됩니다.

**잘못된 사용 예**
```java
public class Order{
    public void setStatus(boolean status) {
        this.status = status;
    }
}

public void 주문서비스의_취소이벤트() {
    order.setStatus(false);
}
```
**올바른 사용 예**
```java
public class Order{
    public void cancelOrder() {
        this.status = false;
    }
}

public void 주문서비스의_취소이벤트() {
    order.cancelOrder();
}
```

그렇다면 **Setter가 없는 이 상황에서 어떻게 값을 채워 DB에 삽입**^insert^해야 할까요?

기본적인 구조는 **생성자를 통해** 최종값을 채운 후 `DB`에 삽입^insert^하는 것이며, 값 변경이 필요한 경우 **해당 이벤트에 맞는 public 메소드를 호출**하여 변경하는 것을 전제로 합니다.
여기서는 생성자 대신에 **@Builder를 통해 제공되는 빌더 클래스**를 사용합니다. 생성자나 빌더나 생성 시점에 값을 채워주는 역할은 똑같습니다. 다만, 생성자의 경우 지금 채워야 할 필드가 무엇인지 명확히 지정할 수가 없습니다. 예를 들어 다음과 같은 생성자가 있다면 개발자가 `new Example(b, a)`처럼 **a와 b의 위치를 변경해도 코드를 실행하기 전까지는 문제를 찾을 수가 없습니다.**

```java
public Example(String a, String b) {
    this.a = a;
    this.b = b;
}
```
하지만 빌더를 사용하게 되면 다음과 같이 **어느 필드에 어떤 값을 채워야 할지** 명확하게 인지할 수 있습니다.

```java
Example.builder()
       .a(a)
       .b(b)
       .build();
```

`Posts` 클래스 생성이 끝났다면, `Posts` 클래스로 `Database`를 접근하게 해줄 `JpaRepository`를 생성합니다.

```java
package com.banjjoknim.book.springboot.domain.posts;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PostsRepository extends JpaRepository<Posts, Long> {
    
}
```
보통 `ibatis`나 `Mybatis` 등에서 `Dao`라고 불리는 `DB Layer` 접근자입니다. `JPA`에선 `Repository`라고 부르며 **인터페이스**로 생성합니다. 단순히 인터페이스를 생성 후, `JpaRepository<Entity 클래스, PK 타입>`를 상속하면 기본적인 `CRUD` 메소드가 자동으로 생성됩니다.
**@Repository를 추가할 필요도 없습니다.** 여기서 주의하실 점은 **Entity 클래스와 기본 Entity Repository는 함께 위치**해야 한다는 점입니다. 둘은 아주 밀접한 관계이고, `Entity` 클래스는 **기본 Repository 없이는 제대로 역할을 할 수가 없습니다.**

나중에 프로젝트 규모가 커져 도메인별로 프로젝트를 분리해야 한다면 이때 `Entity` 클래스와 기본 `Repository`는 함께 움직여야 하므로 **도메인 패키지에서 함께 관리**합니다.

---

## 3.3 Spring Data JPA 테스트 코드 작성하기

`test` 디렉토리에 `domain.posts` 패키지를 생성하고, 테스트 클래스는 `PostsRepositoryTest`란 이름으로 생성합니다. `PostsRepositoryTest`에서는 다음과 같이 `save`, `findAll` 기능을 테스트합니다.

```java
package com.banjjoknim.book.springboot.domain.posts;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PostsRepositoryTest {

    @Autowired
    PostsRepository postsRepository;

    @After // 1.
    public void cleanup() {
        postsRepository.deleteAll();
    }

    @Test
    public void 게시글저장_불러오기() {
        // given
        String title = "테스트 게시글";
        String content = "테스트 본문";

        postsRepository.save(Posts.builder() // 2.
                .title(title)
                .content(content)
                .author("banjjoknim")
                .build());

        // when
        List<Posts> postsList = postsRepository.findAll(); // 3.

        // then
        Posts posts = postsList.get(0);
        assertThat(posts.getTitle()).isEqualTo(title);
        assertThat(posts.getContent()).isEqualTo(content);
    }
}
```
**1. @After**
- `JUnit`에서 단위 테스트가 끝날 때마다 수행되는 메소드를 지정
- 보통은 배포 전 전체 테스트를 수행할 때 테스트간 데이터 침범을 막기 위해 사용합니다.
- 여러 테스트가 동시에 수행되면 테스트용 데이터베이스인 `H2`에 데이터가 그대로 남아 있어 다음 테스트 실행 시 테스트가 실패할 수 있습니다.

**2. postsRepository.save**
- 테이블 `posts`에 `insert/update` 쿼리를 실행합니다.
- `id` 값이 있다면 `update`가, 없다면 `insert` 쿼리가 실행됩니다.

**3. postsRepository.findAll**
- 테이블 `posts`에 있는 모든 데이터를 조회해오는 메소드입니다.

별다른 설정 없이 `@SpringBootTest`를 사용할 경우 **H2 데이터베이스**를 자동으로 실행해 줍니다. 이 테스트 역시 실행할 경우 `H2`가 자동으로 실행됩니다.

여기서 **실제로 실행된 쿼리는 어떤 형태일까?** 라는 의문이 생길 수 있습니다. 쿼리 로그를 `ON/OFF`할 수 있는 설정이 있습니다. 다만, 이런 설정들을 `Java`클래스로 구현할 수 있으나 스프링 부트에서는 `application.properties`, `application.yml` 등의 파일로 **한 줄의 코드로 설정**할 수 있도록 지원하고 권장하니 이를 사용합니다.

`src/main/resources` 디렉토리 아래에 `application.properties` 파일을 생성합니다.

옵션은 다음과 같습니다. 옵션이 추가되었다면 다시 테스트를 수행해봅니다.

`spring.jpa.show_sql=true`

로그를 살펴보면 `create table` 쿼리를 보면 `id bigint generated by default as identity`라는 옵션으로 생성됩니다. 이는 `H2` 쿼리 문법이 적용되었기 때문입니다. `H2`는`MySQL`의 쿼리를 수행해도 정상적으로 작동하기 때문에 이후 디버깅을 위해서 **출력되는 쿼리 로그를 MySQL 버전**으로 변경해 보겠습니다.

이 옵션 역시 `application.properties`에서 설정이 가능합니다. 다음 코드를 추가합니다.

`spring.jap.properties.hibernate.dialect=org.hibernate.dialect.MySQL5InnoDBDialect`

추가했다면 다시 테스트 코드를 수행해 봅니다.

---

## 3.4 등록/수정/조회 API 만들기

API를 만들기 위해 총 3개의 클래스가 필요합니다.
- **`Request` 데이터를 받을 `Dto`**
- **API 요청을 받을 `Controller`**
- **트랜잭션, 도메인 기능 간의 순서를 보장하는 `Service`**

여기서 많은 분들이 오해하고 계신 것이, **`Service`에서 비지니스 로직을 처리**해야 한다는 것입니다. 하지만, 전혀 그렇지 않습니다. `Service`는 **트랜잭션, 도메인 간 순서 보장**의 역할만 합니다.

**Spring 웹 계층**
![Spring 웹 계층](https://blog.kakaocdn.net/dn/bFruEV/btqAUv4HJLQ/H5TVBjqkKc5KBgD4Vdyvkk/img.png)

- **Web Layer**
  - 흔히 사용하는 컨트롤러(`@Controller`)와 `JSP/Freemarker` 등의 뷰 템플릿 영역입니다.
  - 이외에도 필터(`@Filter`), 인터셉터, 컨트롤러 어드바이스(`@ControllerAdvice`) 등 **외부 요청과 응답**에 대한 전반적인 영역을 이야기합니다.

- **Service Layer**
  - `@Service`에 사용되는 서비스 영역입니다.
  - 일반적으로 `Controller`와 `Dao`의 중간 영역에서 사용됩니다.
  - `@Transactional`이 사용되어야 하는 영역이기도 합니다.

- **Repository Layer**
  - **Database**와 같이 데이터 저장소에 접근하는 영역입니다.
  - 기존에 개발하셨던 분들이라면 `Dao(Data Access Object)` 영역으로 이해하시면 쉬울 것입니다.

- **Dtos**
  - `Dto(Data Transfer Object)`는 **계층 간에 데이터 교환을 위한 객체**를 이야기하며 `Dtos`는 이들의 영역을 이야기합니다.
  - 예를 들어 뷰 템플릿 엔진에서 사용될 객체나 `Repository Layer`에서 결과로 넘겨준 객체 등이 이들을 이야기합니다.

- **Domain Model**
  - 도메인이라 불리는 개발 대상을 모든 사람이 동일한 관점에서 이해할 수 있고 공유할 수 있도록 단순화시킨 것을 도메인 모델이라고 합니다.
  - 이를테면 택시 앱이라고 하면 배차, 탑승, 요금 등이 모두 도메인이 될 수 있습니다.
  - `@Entity`를 사용해보신 분들은 `@Entity`가 사용된 영역 역시 도메인 모델이라고 이해해주시면 됩니다.
  - 다만, 무조건 데이터베이스의 테이블과 관계가 있어야 하는 것은 아닙니다.
  - `VO`처럼 값 객체들도 이 영역에 해당하기 때문입니다.

`Web`^Controller^, `Service`, `Repository`, `Dto`, `Domain` 이 5가지 레이어에서 비지니스 처리를 담당해야 할 곳은 바로 **Domain** 입니다. 기존에 서비스로 처리하던 방식을 **트랜잭션 스크립트**라고 합니다. 주문 취소 로직을 작성한다면 다음과 같습니다.

**슈도 코드**
```java
@Transactional
public Order cancelOrder(int orderId) {

    1) 데이터베이스로부터 주문정보 (Orders), 결제정보 (Billing), 배송정보 (Delivery) 조회

    2) 배송 취소를 해야 하는지 확인

    3) if(배송 중이라면) {
        배송 취소로 변경
    }

    4) 각 테이블에 취소 상태 Update
}
```
**실제 코드**
```java
@Transactional
public Order cancelOrder(int orderId) {
    
    // 1)
    OrdersDto order = ordersDao.selectOrders(orderId);
    BillingDto billing = billingDao.selectBilling(orderId);
    DeliveryDto delivery = deliveryDao.selectDelivery(orderId);

    // 2)
    String deliveryStatus = delivery.getStatus();

    // 3)
    if("IN_PROGRESS".equals(deliveryStatus)) {
        delivery.setStatus("CANCEL");
        deliveryDao.update(delivery);
    }

    // 4)
    order.setStatus("CANCEL");
    ordersDao.update(order);

    billing.setStatus("CANCEL");
    deliveryDao.update(billing);

    return order;
}
```
모든 로직이 **서비스 클래스 내부에서 처리됩니다.** 그러다 보니 **서비스 계층이 무의미하며, 객체란 단순히 데이터 덩어리** 역할만 하게 됩니다. 반면 도메인 모델에서 처리할 경우 다음과 같은 코드가 될 수 있습니다.

```java
@Transactional
public Order cancelOrder(int orderId) {

    // 1)
    Orders order = ordersRepository.findById(orderId);
    Billing billing = billingRepository.findByOrderId(orderId);
    Delivery delivery = deliveryRepository.findByOrderId(orderId);

    // 2 - 3)
    delivery.cancel();

    // 4)
    order.cancel();
    billing.cancel();

    return order;
}
```
`order`, `billing`, `delivery`가 각자 본인의 취소 이벤트 처리를 하며, 서비스 메소드는 **트랜잭션과 도메인 간의 순서만 보장**해 줍니다. 여기서는 계속 이렇게 **도메인 모델을** 다루고 코드를 작성합니다.

그럼 등록, 수정, 삭제 기능을 만들어 보겠습니다. `PostsApiController`를 `web` 패키지에, `PostsSaveRequestDto`를 `web.dto` 패키지에, `PostsService`를 `service.posts` 패키지에 생성합니다.

```java
package com.banjjoknim.book.springboot.web;

import com.banjjoknim.book.springboot.service.PostsService;
import com.banjjoknim.book.springboot.web.dto.PostsSaveRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class PostsApiController {

    private final PostsService postsService;

    @PostMapping("/api/v1/posts")
    public Long save(@RequestBody PostsSaveRequestDto requestDto){
        return postsService.save(requestDto);
    }
}
```

```java
package com.banjjoknim.book.springboot.service;

import com.banjjoknim.book.springboot.domain.posts.PostsRepository;
import com.banjjoknim.book.springboot.web.dto.PostsSaveRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PostsService {
    private final PostsRepository postsRepository;

    @Transactional
    public Long save(PostsSaveRequestDto requestDto) {
        return postsRepository.save(requestDto.toEntity()).getId();
    }
}
```
스프링에선 `Bean`을 주입받는 방식들이 다음과 같습니다.
- **@Autowired**
- **setter**
- **생성자**

이 중 가장 권장하는 방식이 **생성자로 주입**받는 방식입니다(**`@Autowired`는 권장하지 않습니다**). 즉 **생성자로** `Bean` 객체를 받도록 하면 `@Autowired`와 동일한 효과를 볼 수 있다는 것입니다. 여기서 생성자는 `@RequiredArgsConstructor`에서 해결해 줍니다. **final이 선언된 모든 필드**를 인자값으로 하는 생성자를 롬복의 `@RequiredArgsConstructor`가 대신 생성해 준 것입니다. 생성자를 직접 안 쓰고 롬복 어노테이션을 사용한 이유는 해당 클래스의 의존성 관계가 변경될 때마다 생성자 코드를 계속해서 수정하는 번거로움을 해결하기 위함입니다.

`Controller`와 `Service`에서 사용할 `Dto` 클래스를 생성합니다.

```java
package com.banjjoknim.book.springboot.web.dto;

import com.banjjoknim.book.springboot.domain.posts.Posts;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostsSaveRequestDto {
    private String title;
    private String content;
    private String author;

    @Builder
    public PostsSaveRequestDto(String title, String content, String author) {
        this.title = title;
        this.content = content;
        this.author = author;
    }

    public Posts toEntity() {
        return Posts.builder()
                .title(title)
                .content(content)
                .author(author)
                .build();
    }
}
```

여기서 `Entity` 클래스와 거의 유사한 형태임에도 `Dto` 클래스를 추가로 생성했습니다. 하지만, 절대로 **Entity 클래스를 Request/Response 클래스로 사용해서는 안 됩니다.** `Entity` 클래스는 **떼이터베이스와 맞닿은 핵심 클래스**입니다. `Entity` 클래스를 기준으로 테이블이 생성되고, 스키마가 변경됩니다. 화면 변경은 아주 사소한 기능 변경인데, 이를 위해 테이블과 연결된 `Entity` 클래스를 변경하는 것은 너무 큰 변경입니다.

수 많은 서비스 클래스나 비즈니스 로직들이 `Entity` 클래스를 기준으로 동작합니다. `Entity` 클래스가 변경되면 여러 클래스에 영향을 끼치지만, `Request`와 `Response`용 `Dto`는 `View`를 위한 클래스라 정말 자주 변경이 필요합니다.

`View Layer`와 `DB Layer`의 역할 분리를 철저하게 하는 게 좋습니다. 실제로 `Controller`에서 **결괏값으로 여러 테이블을 조인해서 줘야 할 경우**가 빈번하므로 `Entity` 클래스만으로 표현하기 어려운 경우가 많습니다.

꼭 `Entity` 클래스와 `Controller`에서 쓸 `Dto`는 분리해서 사용해야 합니다. 등록 기능의 코드가 완성되었으니, 테스트 코드로 검증해 보겠습니다. 테스트 패키지 중 `web` 패키지에 `PostApiControllerTest`를 생성합니다.

```java
package com.banjjoknim.book.springboot.web;

import com.banjjoknim.book.springboot.domain.posts.Posts;
import com.banjjoknim.book.springboot.domain.posts.PostsRepository;
import com.banjjoknim.book.springboot.web.dto.PostsSaveRequestDto;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PostsApiControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private PostsRepository postsRepository;

    @After
    public void tearDown() {
        postsRepository.deleteAll();
    }

    @Test
    public void Posts_등록된다() {
        // given
        String title = "title";
        String content = "content";
        PostsSaveRequestDto requestDto = PostsSaveRequestDto.builder()
                .title(title)
                .content(content)
                .author("author")
                .build();

        String url = "http://localhost:" + port + "/api/v1/posts";

        // when
        ResponseEntity<Long> responseEntity = restTemplate.postForEntity(url, requestDto, Long.class);

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isGreaterThan(0L);

        List<Posts> all = postsRepository.findAll();
        assertThat(all.get(0).getTitle()).isEqualTo(title);
        assertThat(all.get(0).getContent()).isEqualTo(content);
    }
}
```
`Api Controller`를 테스트하는데 `HelloController`와 달리 `@WebMvcTest`를 사용하지 않았습니다. **@WebMvcTest의 경우 JPA 기능이 작동하지 않기** 때문인데, `Controller`와 `ControllerAdvice` 등 **외부 연동과 관련된 부분만** 활성화되니 지금 같이 `JPA` 기능까지 한번에 테스트할 때는 `@SpringBootTest`와 `TestRestTemplate`을 사용하면 됩니다. 테스트를 수행하보면 `WebEnvironment.RANDOM_PORT`로 인한 랜덤 포트 실행과 `insert` 쿼리가 실행된 것 모두 확인할 수 있습니다. 등록 기능을 완성했으니 수정/조회 기능도 만들어 보겠습니다.
