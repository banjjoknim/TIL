# 정적 컨텐츠

---

## 간단한 설명

### 정적 컨텐츠
- `Welcome Page`와 같이 서버에서 파일을 그대로 웹 브라우저에 내려주는 것을 말한다.

### MVC와 템플릿 엔진
- `HTML`을 그냥 주는 것이 아니라 서버에서 프로그래밍(변형)을 해서 `HTML`을 동적으로 바꿔서 내려주는 것을 말한다.
- `Model`, `View`, `Controller`의 세 가지를 `MVC`라고 한다.


### API
- 최근에는 `json`이라는 데이터 구조 포맷을 이용하여 클라이언트에게 데이터를 전달하는 방식을 말한다.
- 최근에는 `vue.js`, `react`등을 사용할 때도 API로 데이터만 내려주면 화면은 클라이언트가 알아서 그리고 정리하는 방식을 이용할 때도 사용한다.
- 서버끼리 통신할 때에도 사용한다.

---

## 스프링 부트에서의 정적 컨텐츠

- 스프링 부트는 정적 컨텐츠 기능을 제공한다.
- [스프링 Static Content](https://docs.spring.io/spring-boot/docs/2.3.1.RELEASE/reference/html/spring-boot-features.html#boot-features-spring-mvc-static-content)
- 스프링 부트는 기본적으로 정적 컨텐츠는 `/static` 폴더에서 찾아서 제공한다.
- 원하는 파일을 넣으면(`/static` 폴더 내부에) 정적 파일이 그대로 반환되지만, 어떠한 프로그래밍을 할 수는 없다.

### 정적 컨텐츠 이미지
![정적-컨텐츠-이미지](https://user-images.githubusercontent.com/68052095/102767711-580f1800-43c3-11eb-84a2-562cfca57fb5.PNG)
- 웹 브라우저에서 `localhost:8080/hello-static.html` 으로 접근하면 내장 톰캣 서버가 요청을 받아서 스프링한테 넘긴다.
- 컨트롤러쪽에서 `hello-static`이 있는지 먼저 찾아본다(컨트롤러가 우선 순위에 있다는 뜻).
- 하지만 `hello-static`와 매핑된 컨트롤러 메서드는 없다.
- 따라서 `resources/static/hello-static.html`을 찾아서 있으면 그대로 반환한다.

---