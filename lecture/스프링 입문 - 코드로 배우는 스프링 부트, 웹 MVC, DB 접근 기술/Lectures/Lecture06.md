# MVC와 템플릿 엔진
- `MVC` : `Model`, `View`, `Controller`
- 과거에는 `View`에서 모든 것을 다 했다(`Model 1 방식`).
- 개발을 할 때는 관심사를 분리해야 한다. 역할과 책임.
- `View`는 화면을 그리는데 모든 역량을 집중해야 한다.
- `Model`, `Controller`과 관련된 부분들은 비즈니스 로직과 관련이 있거나 내부적인 것들을 처리하는데 집중해야 한다.
- `View`는 화면에 관계된 일만.
- 비즈니스 로직과 서버 뒷단에 관련된 것은 `Controller`나 뒷단 비즈니스 로직에서 처리하고 `Model`에 화면에 필요한 것들을 담아서 화면쪽에 넘겨준다.

---

## @RequestParam
- 외부에서 파라미터를 받을 때 사용한다.

---

## Thymeleaf
- `html`을 그대로 쓰고 그 파일을 서버 없이 바로 열어봐도 껍데기를 볼 수 있다는 장점이 있다.
- 다음 `html` 코드는 템플릿 엔진으로 동작을 하면 내용(`hello! empty`)이 서버에서 전달해준 데이터로 치환(`'hello ' + ${name}`)된다.
    - `<p th:text="'hello ' + ${name}">hello! empty</p>`

---

## 인텔리제이 옵션보기
- 원하는 부분을 선택한 뒤 `ctrl + p`를 누르면 파라미터 정보 옵션을 볼 수 있다.
- `@RequestParam`의 `required` 옵션의 기본값은 `true`이기 때문에 파라미터를 무조건 전달해줘야 한다(`false`로 지정하면 값을 넘기지 않아도 된다).

---

## MVC, 템플릿 엔진 이미지
![MVC,-템플릿-엔진-이미지](https://user-images.githubusercontent.com/68052095/102771216-e2a64600-43c8-11eb-97a2-c7504eebcb01.PNG)
- 웹 브라우저에서 `localhost:8080/hello-mvc`로 접근하면 내장 톰캣 서버가 스프링에게 요청을 던진다.
- 스프링은 `helloController`에 해당 url에 매핑되어 있는 메서드를 호출해준다.
- 그리고 리턴을 해줄때 `hello-template`와 `model(name:spring)`을 스프링한테 리턴해준다.
- 스프링의 `viewResolver`가 뷰를 찾고 템플릿 엔진을 연결시켜준다. templates/`hello-template`.html(메서드가 리턴한 값인 `hello-template`)를 찾아서 `Thymeleaf` 템플릿 엔진한테 처리해달라고 넘긴다.
- `Thymeleaf` 템플릿 엔진이 랜더링해서 변환한 `html`을 웹 브라우저에 반환한다.

---