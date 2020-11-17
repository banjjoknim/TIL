# Chapter2. 스프링 부트에서 테스트 코드를 작성하자

---

## 2.1 테스트 코드 소개

`TDD`와 단위 테스트는 다른 이야기이다. `TDD`는 **테스트가 주도하는 개발**을 이야기하는데, **테스트 코드를 먼저 작성**하는 것부터 시작합니다.

>**레드 그린 사이클**
>- 항상 실패하는 테스트를 먼저 작성하고(`Red`)
>- 테스트가 통과하는 프로덕션 코드를 작성하고(`Green`)
>- 테스트가 통과하면 프로덕션 코드를 리팩토링 합니다(`Refactor`).

반면 단위 테스트는 `TDD`의 첫 번째 단계인 **기능 단위의 테스트 코드를 작성**하는 것을 이야기한다. `TDD`와 달리 테스트 코드를 꼭 먼저 작성해야 하는 것도 아니고, 리팩토링도 포함되지 않는다. 순수하게 테스크 코드만 작성하는 것을 말한다.

>**단위 테스트 코드를 작성함으로써 얻는 이점**
>- 단위 테스트는 개발단계 초기에 문제를 발견하게 도와줍니다.
>- 단위 테스트는 개발자가 나중에 코드를 리팩토링하거나 라이브러리 업그레이드 등에서 기존 기능이 올바르게 작동하는지 확인할 수 있습니다(예, 회귀테스트).
>- 단위 테스트는 기능에 대한 불확실성을 감소시킬 수 있습니다.
>- 단위 테스트는 시스템에 대한 실제 문서를 제공합니다. 즉, 단위 테스트 자체가 문서로 사용할 수 있습니다.

---

## 2.2 Hello Controller 테스트 코드 작성하기

일반적으로 패키지 명은 **웹 사이트 주소의 역순**으로 합니다. 예를 들어 `admin.jojoldu.com`이라는 사이트라면 패키지명은 `com.jojoldu.admin`으로 하면 됩니다.

>#### Application
>```java
>package com.banjjoknim.book.springboot;
>
>import org.springframework.boot.SpringApplication;
>import org.springframework.boot.autoconfigure.SpringBootApplication;
>
>@SpringBootApplication
>public class Application {
>    public static void main(String[] args) {
>        SpringApplication.run(Application.class, args);
>    }
>}
>```
>
>- `@SpringBootApplication` 어노테이션으로 인해 스프링 부트의 자동 설정, 스프링 `Bean` 읽기와 생성이 모두 자동으로 설정됩니다.
>- 특히, `@SpringBootApplication`이 있는 위치부터 설정을 읽어가기 때문에 이 어노테이션이 선언된 클래스는 항상 **프로젝트의 최상단에 위치**해야만 합니다.
>- 내장 WAS(`Web Application Server`)란 별도로 외부에 WAS를 두지 않고 애플리케이션을 실행할 때 내부에서 WAS를 실행하는 것을 이야기합니다. `SpringApplication.run`으로 인해 내장 WAS를 실행합니다. 이렇게 되면 항상 서버에 **톰캣을 설치할 필요가 없게 되고,** 스프링 부트로 만들어진 `Jar` 파일(실행 가능한 Java 패키징 파일)로 실행하면 됩니다.
>- 스프링 부트에서는 **내장 WAS를 사용하는 것을 권장**한다. **언제 어디서나 같은 환경에서 스프링 부트를 배포**할 수 있기 때문이다.

>#### HelloController
>```java
>package com.banjjoknim.book.springboot.web;
>
>import org.springframework.web.bind.annotation.GetMapping;
>import org.springframework.web.bind.annotation.RestController;
>
>@RestController // 1.
>public class HelloController {
>
>    @GetMapping("/hello") // 2.
>    public String hello() {
>        return "hello";
>    }
>}
>```
>
>**1. @RestController**
>- 컨트롤러를 `JSON`을 반환하는 컨트롤러로 만들어 줍니다.
>- 예전에는 `@ResponseBody`를 각 메소드마다 선언했던 것을 한번에 사용할 수 있게 해준다고 생각하면 됩니다.
>
>**2. @GetMapping**
>- `HTTP Method`인 `Get`의 요청을 받을 수 있는 API를 만들어 줍니다.
>- 예전에는 `@RequestMapping(method = RequestMethod.GET)`으로 사용되었습니다. 

>#### HelloControllerTest
>```java
>package com.banjjoknim.book.springboot.web;
>
>import org.junit.Test;
>import org.junit.runner.RunWith;
>import org.springframework.beans.factory.annotation.Autowired;
>import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
>import org.springframework.test.context.junit4.SpringRunner;
>import org.springframework.test.web.servlet.MockMvc;
>
>import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
>import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
>import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
>
>@RunWith(SpringRunner.class) // 1.
>@WebMvcTest(controllers = HelloController.class) // 2.
>public class HelloControllerTest {
>
>    @Autowired // 3.
>    private MockMvc mvc; // 4.
>
>    @Test
>    public void hello가_리턴된다() throws Exception {
>        String hello = "hello";
>
>        mvc.perform(get("/hello")) // 5.
>                .andExpect(status().isOk()) // 6.
>                .andExpect(content().string(hello)); // 7.
>    }
>}
>```
>
>**1. @RunWith(SpringRunner.class)**
>- 테스트를 진행할 때 `JUnit`에 내장된 실행자 외에 다른 실행자를 실행시킵니다.
>- 여기서는 `SpringRunner`라는 스프링 실행자를 사용합니다.
>- 즉, 스프링 부트 테스트와 `JUnit` 사이에 연결자 역할을 합니다.
>
>**2. @WebMvcTest**
>- 여러 스프링 테스트 어노테이션 중, `Web(Spring MVC)`에 집중할 수 있는 어노테이션입니다.
>- 선언할 경우 `@Controller`, `@ControllerAdvice` 등을 사용할 수 있습니다.
>- 단, `@Service`, `@Component`, `@Repository` 등은 사용할 수 없습니다.
>- 여기서는 컨트롤러만 사용하기 때문에 선언합니다.
>
>**3. @Autowired**
>- 스프링이 관리하는 빈(`Bean`)을 주입 받습니다.
>
>**4. private MockMvc mvc**
>- 웹 API를 테스트할 때 사용합니다.
>- 스프링 `MVC` 테스트의 시작점입니다.
>- 이 클래스를 통해 `HTTP GET`, `POST` 등에 대한 API 테스트를 할 수 있습니다.
>
>**5. mvc.perform(get("/hello"))**
>- `MockMvc`를 통해 `/hello` 주소로 `HTTP GET` 요청을 합니다.
>- 체이닝이 지원되어 아래와 같이 여러 검증 기능을 이어서 선언할 수 있습니다.
>
>**6. .andExpect(status().isOk())**
>- `mvc.perform`의 결과를 검증합니다.
>- `HTTP Header`의 `Status`를 검증합니다.
>- 우리가 흔히 알고 있는 `200`, `404`, `500` 등의 상태를 검증합니다.
>- 여기선 `OK` 즉, `200`인지 아닌지를 검증합니다.
>
>**7. .andExpect(content().string(hello))**
>- `mvc.perform`의 결과를 검증합니다.
>- 응답 본문의 내용을 검증합니다.
>- `Controller`에서 `"hello"`를 리턴하기 때문에 이 값이 맞는지 검증합니다.

---

## 2.3 롬복 소개 및 설치하기

**자바 개발자들의 필수 라이브러리 롬복**
- 롬복은 자바 개발할 때 자주 사용하는 코드 `Getter`, `Setter`, 기본생성자, `toString` 등을 어노테이션으로 자동 생성해 줍니다.


- `build.gradle`에 다음의 코드를 추가하여 의존성(라이브러리)을 추가합니다.
```java
compile('org.projectlombok:lombok')
```

- `plugins`에서 `lombok` 플러그인을 검색하여 설치합니다.

- 롬복은 프로젝트마다 설정해야 합니다. 플러그인 설치는 한 번만 하면 되지만, `build.gradle`에 라이브러리를 추가하는 것과 `Enable annotation processing`를 체크하는 것은 프로젝트마다 진행해야 합니다.

---

## 2.4 Hello Controller 코드를 롬복으로 전환하기

>#### HelloResponseDto
>```java
>package com.banjjoknim.book.springboot.web.dto;
>
>import lombok.Getter;
>import lombok.RequiredArgsConstructor;
>
>@Getter // 1.
>@RequiredArgsConstructor // 2.
>public class HelloResponseDto {
>
>    private final String name;
>    private final int amount;
>}
>```
>
>**1. @Getter**
>- 선언된 모든 필드의 `get` 메소드를 생성해줍니다.
>
>**2. @RequiredArgsConstructor**
>- 선언된 모든 `final` 필드가 포함된 생성자를 생성해 줍니다.
>- `final`이 없는 필드는 생성자에 포함되지 않습니다.

>#### HelloResponseDtoTest
>```java
>package com.banjjoknim.book.springboot.web.dto;
>
>import org.junit.Test;
>
>import static org.assertj.core.api.Assertions.assertThat;
>
>public class HelloResponseDtoTest {
>
>    @Test
>    public void 롬복_기능_테스트() {
>        // given
>        String name = "test";
>        int amount = 1000;
>
>        // when
>        HelloResponseDto dto = new HelloResponseDto(name, amount);
>
>        // then
>        assertThat(dto.getName()).isEqualTo(name); // 1. , 2.
>        assertThat(dto.getAmount()).isEqualTo(amount);
>    }
>}
>```
>
>**1. assertThat**
>- `assertj`라는 테스트 검증 라이브러리의 검증 메소드입니다.
>- 검증하고 싶은 대상을 메소드 인자로 받습니다.
>- 메소드 체이닝이 지원되어 `isEqualTo`와 같이 메소드를 이어서 사용할 수 있습니다.
>
>**2. isEqualTo**
>- `assertj`의 동등 비교 메소드입니다.
>- `assertThat`에 있는 값과 `isEqualTo`의 값을 비교해서 같을 때만 성공입니다.
>
>**Junit과 비교하여 assertj의 장점은 다음과 같습니다.**
>- `CoreMatchers`와 달리 추가적으로 라이브러리가 필요하지 않습니다.
>  - `JUnit`의 `assertThat`을 쓰게 되면 `is()`와 같이 `CoreMatchers` 라이브러리가 필요합니다.
>- 자동완성이 좀 더 확실하게 지원됩니다.
>  - IDE에서는 `CoreMatchers`와 같은 `Matcher` 라이브러리의 자동완성 지원이 약합니다.

>#### HelloResponseDto
>```java
>@GetMapping("/hello/dto")
>    public HelloResponseDto helloDto(@RequestParam("name") String name, @RequestParam("amount") int amount) { // 1.
>        return new HelloResponseDto(name, amount);
>    }
>```
>
>**1. @RequestParam**
>- 외부에서 API로 넘긴 파라미터를 가져오는 어노테이션입니다.
>- 여기서는 외부에서 `name (@RequestParam("name"))`이란 이름으로 넘긴 파라미터를 메소드 파라미터 `name (String name)`에 저장하게 됩니다.

`name`과 `amount`는 API를 호출하는 곳에서 넘겨준 값들입니다. 추가된 API를 테스트하는 코드를 `HelloControllerTest`에 추가합니다.

>#### HelloResponseDtoTest
>```java
>@Test
>    public void helloDto가_리턴된다() throws Exception {
>        String name = "hello";
>        int amount = 1000;
>
>        mvc.perform(get("/hello/dto")
>                .param("name", name) // 1.
>                .param("amount", String.valueOf(amount)))
>                .andExpect(status().isOk())
>                .andExpect(jsonPath("$.name", is(name))) // 2.
>                .andExpect(jsonPath("$.amount", is(amount)));
>    }
>```
>
>**1. param**
>- API 테스트할 때 사용될 요청 파라미터를 설정합니다.
>- 단, 값은 `String`만 허용됩니다.
>- 그래서 숫자/날짜 등의 데이터도 등록할 때는 문자열로 변경해야만 가능합니다.
>
>**2. jsonPath**
>- `JSON` 응답값을 필드별로 검증할 수 있는 메소드입니다.
>- `$`를 기준으로 필드명을 명시합니다.
>- 여기서는 `name`과 `amount`를 검증하니 `$.name`, `$.amount`로 검증합니다.

---