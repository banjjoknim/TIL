# 프로젝트 생성

---

## spring initializr
- [spring initializr](https://start.spring.io/)에서 스프링부트 기반으로 스프링 프로젝트를 만들어주는 사이트이며, 스프링이 운영하고 있다.

---

## Maven, Gradle?
- 필요한 라이브러리를 가져오고 빌드하는 라이프사이클까지 관리해주는 툴이다.
- 과거에는 `Maven`을 많이 사용했으나 요즘 추세는 `Gradle`로 넘어가는 추세이다.
- 레거시 프로젝트나 과거 프로젝트는 `Maven`으로 남아있는 경우가 있다.
- 심지어 스프링 라이브러리 관리 자체도 요즘에는 `Gradle`로 넘어온 상황이다.
- 따라서 `Gradle`을 추천한다.

---

## Language
- Java

---

## Spring Boot
- `SNAPSHOT`은 아직 만들고 있는 버전이다
- `M1`은 정식 릴리즈된 버전이 아니다.
- 강의상으로는 `2.3.1` 버전을 선택했으나 버전이 바뀌었을 때의 대처는 강의교재를 참고하자.

---

## Project Metadata
- `Group`에 보통 기업 도메인명 같은 것들을 적어준다.
- `Artifact`는 빌드되어 나올 때의 결과물이라고 보면 된다(프로젝트명 같은 것).
- 나머지(`Name`, `Description` 등)는 그대로 둔다.

---

## Dependencies
- 어떤 라이브러리를 가져와서 사용할 것인지를 선택하는 것.
- `Spring Web`, HTML을 만들어주는 템플릿 엔진이 필요한데 이것으로 `Thymeleaf`선택(회사마다 다름).

---

## GENERATE
- 아래의 `GENERATE` 버튼 누르면 `zip` 파일이 다운로드 되는데 이 것을 압축 푼 다음 인텔리제이에서 `Open or import`로 `build.gradle`을 열면된다.
- `Open as Project` 선택.

---

## 프로젝트 내부 구조

### `.gradle`
- 무시..

### `.idea`
- 인텔리제이가 사용하는 설정파일이다.

### `gradle` -> `wrapper`
- `gradle`과 관련해서 `gradle`을 사용하는 폴더이다.

### `src` -> `main`, `test`
- `main` : `java`(하위에 실제 패키지와 소스파일 존재), `resources`(실제 자바 코드파일을 제외한 `xml`이나 `properties`, `html`등의 설정 파일 등이 존재 -> 자바 파일을 제외한 나머지는 `resources`라 보면됨)

- `test` : `java`(테스트 코드들과 관련된 소스들이 존재 -> 테스트 코드라는 것이 요즘 개발 트랜드에서는 중요하다는 뜻이다)
### `build.gradle` 
- 중요!! 예전에는 한땀한땀 입력해야 했으나... 앞서 `start.spring.io`에서 선택한 설정들이 지정되어 있는 파일이다. 
- 버전 설정하고 라이브러리를 가져오는구나.. 정도로 이해하면 된다.

### `dependencies`
- 앞서 설정한 라이브러리들이 입력되어 있고, 여기에 입력하면 해당 라이브러리를 사용할 수 있다.
- 요즘에는 기본적으로 테스트 라이브러리가 들어가는데 `JUnit5`가 기본적으로 들어간다.

### `repositories`
- 라이브러리를 사용하기 위해서는 다운로드를 받아야하는데, 어디서(입력된 사이트에서) 다운로드 받을지 설정을 해놓는 항목이다.

### `.gitignore`
- 소스코드를 관리해준다.
- `git`에는 필요한 소스코드 파일만 올라가야 하며 빌드된 결과물등은 올라가면 안된다.
- 기본적으로 어느정도는 `start.spring.io`에서 다 해준다.

### `gradlew`, `gradlew.bat`
- `gradle`로 빌드할 때 사용한다.

---

## Application
- `springboot`는 톰캣 웹 서버를 내장하고 있다.
- 그래서 `@SpringBootApplication` 어노테이션이 붙어있는 클래스에서 `main` 메서드 내에 `SpringApplication.run()`의 인자로 해당 클래스와 `main`메서드의 `args`를 넣고 실행하면 내장 톰캣 웹 서버를 자체적으로 띄우면서 스프링부트가 같이 올라온다(?).

---

## 번외
- 인텔리제이를 사용하면 빌드가 자바를 직접 실행하는 것이 아니라 `gradle`을 통해 실행될때가 있다.
- 이럴 때는 설정의 `Build, Execution, Deployment`에서 `Build and run using` 항목과 `Run tests using` 항목을 `IntelliJ IDEA`로 바꿔준다.
- `gradle`을 통해서 실행하면 느릴 때가 있는데, 이렇게 설정해주면 인텔리제이에서 `gradle`을 통하지 않고 바로 자바를 띄워서 실행하므로 훨씬 빠르다.