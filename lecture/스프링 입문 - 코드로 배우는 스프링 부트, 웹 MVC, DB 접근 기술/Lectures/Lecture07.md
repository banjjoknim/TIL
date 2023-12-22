# API
- API방식은 데이터를 바로 내려주는 방식이다.

---

## @ResponseBody
- `http`의 통신 프로토콜에서 `header`와 `body` 중에서 `body`에 응답 데이터를 직접 넣어주겠다는 의미이다.
- 메서드에서 리턴하는 문자가 요청하는 클라이언트에 그대로 전달된다.
- 템플릿 엔진과의 차이는 `View`와 같은 것들이 없이 문자가 그대로 전달되는 것이다.
- 즉, `html` 태그와 같은 것들이 하나도 없다...

---

## static class
- `static class`로 만들면 `class` 내부에서 사용할 수 있다.

---

## 인텔리제이 단축키
- `ctrl + shift + enter`를 누르면 코드의 세미콜론을 자동완성해준다.

---

## JSON
- `키(key)`와 `값(value)`으로 이루어진 구조이다.
- `{키 : 값}`의 형식을 가진다.
- 과거에는 `XML`도 많이 쓰였다(`<HTML></HTML>`과 같은 것들).
- 최근에는 `JSON`으로 거의 통일되었다(거의 `json`이 `default`로 세팅되어 있다).

---

## 자바 빈 규약
- `getter`, `setter`를 자바 빈 규약이라고 하며, 프로퍼티 접근 방식이라고 하기도 한다.
- `private` 필드값인 `name`에 메서드를 통해서 접근하게 된다.

## @ResponseBody 사용 원리(보통 API를 사용하는 방식)
![@ResponseBody 사용 원리](https://user-images.githubusercontent.com/68052095/102773119-0d45ce00-43cc-11eb-9f0a-64d094196964.PNG)
- 웹 브라우저에서 `localhost:8080/hello-api`로 접근.
- 톰캣 내장 서버가 스프링에 던짐.
- 스프링이 `hello-api`에 매핑된 메서드를 찾음.
- 근데 `@ResponseBody`라는 어노테이션이 붙어있네? 이럴 경우에는 리턴된 데이터를 응답으로 그대로 `HTTP`의 `BODY`에 반환하도록 동작한다(`view` 없이).
    - 그런데! 문자가 아니라 객체일 경우에는 `json` 형식으로 데이터를 만들어서 `HTTP`응답에 반환하는 것이 기본정책이다.
- `@ResponseBody`가 있으면 우선, `HttpMessageConverter`라는 녀석이 동작한다(기존에는 `viewResolver`가 동작했었다).
    - 단순 문자일 경우에는 `StringConverter`가 동작한다.
        - 기본 문자처리 : `StringHttpMessageConverter`
    - 객체일 경우에는 `JsonConverter`가 동작해서 `json` 형식으로 바꿔서 요청한 웹 브라우저 또는 서버로 반환한다.
        - 기본 객체처리 : `MappingJackson2HttpMessageConverter`
- 실무에서는 `Jackson`, `Gson` 라이브러리를 자주 보게되는데, 스프링에서는 `Jackson` 라이브러리를 기본으로 사용한다(물론 변경 가능하다).
- 객체를 `Jackson`이라는 라이브러리로 `json`으로 바꾸는 것을 `MappingJackson2HttpMessageConverter`가 수행한다.
- 이렇게 나오는 `json` 데이터를 `HTTP`의 `BODY`에 실어서 웹 브라우저 또는 클라이언트 등에 반환한다.
- `byte` 처리 등등 기타 여러 `HttpMessageConverter`가 기본으로 등록되어 있다.

---

## 참고사항
- 클라이언트의 `HTTP Accept 헤더`와 `서버의 컨트롤러 반환 타입 정보` 둘을 조합해서 `HttpMessageConverter`가 선택된다.

---