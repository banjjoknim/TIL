# Chapter4. 머스테치로 화면 구성하기

---

## 4.1 서버 템플릿 엔진과 머스테치 소개

일반적으로 웹 개발에 있어 템플릿 엔진이란, **지정된 템플릿 양식과 데이터**가 합쳐져 HTML 문서를 출력하는 소프트웨어를 이야기합니다.

- 서버 템플릿 엔진 : `JSP`, `Freemarker` ...
- 클라이언트 템플릿 엔진 : `리액트(React)`, `뷰(Vue)`의 `View` 파일 ...

```java
<script type="text/javascript">

$(document).ready(function(){
    if(a == "1"){
        <% System.out.println("test"); %>
    }
});
```
실제로 위 코드는 **if문과 관계없이 무조건 test를 콘솔에 출력합니다.** 이유는 프론트엔드의 자바스크립트가 작동하는 영역과 JSP가 작동하는 영역이 다르기 때문인데 JSP를 비롯한 서버 템플릿 엔진은 **서버에서 구동**됩니다.

서버 템플릿 엔진을 이용한 화면 생성은 **서버에서 Java 코드로 문자열**을 만든 뒤 이 문자열을 HTML로 변환하여 **브라우저로 전달**합니다. 앞선 코드는 HTML을 만드는 과정에서 `System.out.println("test");`를 실행할 뿐이며, 이때의 자바스크립트 코드는 **단순한 문자열일 뿐입니다.**

반면에 자바스크립트는 **브라우저 위에서 작동**합니다. 앞에서 작성된 자바스크립트 코드가 실행되는 장소는 서버가 아닌 **브라우저**입니다. 즉, 브라우저에서 작동될 때는 서버 템플릿 엔진의 손을 벗어나 제어할 수가 없습니다. 흔히 이야기하는 `Vue.js`나 `React.js`를 이용한 SPA는 **브라우저에서 화면을 생성**합니다. 즉, **서버에서 이미 코드가 벗어난 경우**입니다. 그래서 서버에는 `Json` 혹은 `Xml` 형식의 데이터만 전달하고 클라이언트에서 조립합니다.

**머스테치란**

머스테치는 **수많은 언어를 지원하는 가장 심플한 템플릿 엔진**입니다. 루비, 자바스크립트, 파이썬, PHP, 자바, 펄, Go, ASP 등 현존하는 대부분 언어를 지원하고 있습니다. 그러다 보니 자바에서 사용될 때는 서버 템플릿 엔진으로, 자바스크립트에서 사용될 때는 클라이언트 템플릿 엔진으로 모두 사용할 수 있습니다.

자바 진영에서는 `JSP`, `Velocity`, `Freemarker`, `Thymeleaf` 등 다양한 서버 템플릿 엔진이 존재합니다.

템플릿 엔진들의 단점은 다음과 같습니다.
- **JSP, Velocity** : **스프링 부트에서는 권장하지 않는 템플릿 엔진입니다.**
- **Freemarker : 템플릿 엔진으로는 너무 과하게 많은 기능을 지원합니다. 높은 자유도로 인해 숙련도가 낮을수록 Freemarker 안에 비즈니스 로직이 추가될 확률이 높습니다.**
- **Thymeleaf : 스프링 진영에서 적극적으로 밀고 있지만 문법이 어렵습니다. HTML 태그에 속성으로 템플릿 기능을 사용하는 방식이 기존 개발자분들께 높은 허들로 느껴지는 경우가 많습니다. 실제로 사용해 본 분들은 자바스크립트 프레임워크를 배우는 기분이라고 후기를 이야기하기도 합니다. 물론 Vue.js를 사용해 본 경험이 있어 태그 속성 방식이 익숙한 분이라면 Thymeleaf를 선택해도 됩니다.**

반면 머스테치의 장점은 다음과 같습니다.
- **문법이 다른 템플릿 엔진보다 심플합니다.**
- **로직 코드를 사용할 수 없어 View의 역할과 서버의 역할이 명확하게 분리됩니다.**
- **Mustache.js와 Mustache.java 2가지가 다 있어, 하나의 문법으로 클라이언트/서버 템플릿을 모두 사용 가능합니다.**

**템플릿 엔진은 화면 역할에만 충실한 것이 좋습니다.** 너무 많은 기능을 제공하면 API와 템플릿 엔진, 자바스크립트가 서로 로직을 나눠 갖게 되어 유지보수하기가 굉장히 어려워집니다.

**머스테치 플러그인 설치**

`IntelliJ` 플러그인에서 `mustache`를 검색해서 해당 플러그인을 설치한 후 인텔리제이를 재시작하여 플러그인이 작동하는 것을 확인하면 됩니다.

---

## 4.2 기본 페이지 만들기

가장 먼저 스프링 부트 프로젝트에서 머스테치를 편하게 사용할 수 있도록 머스테치 스타터 의존성을 `build.gradle`에 등록합니다.

`compile('org.springframework.boot:spring-boot-starter-mustache')`

머스테치는 **스프링 부트에서 공직 지원하는 템플릿 엔진**입니다. 의존성 하나만 추가하면 다른 스타터 패키지와 마찬가지로 추가 설정 없이 설치가 끝나며, 별도로 스프링 부트 버전을 신경 쓰지 않아도 되는 장점도 있습니다.

머스테치의 파일 위치는 기본적으로 `src/main/resources/templates`입니다. 이 위치에 머스테치 파일을 두면 스프링 부트에서 자동으로 로딩합니다.

첫 페이지를 담당할 `index.mustache`를 `src/main/resources/templates`에 생성합니다.

**index.mustache**
```java
<!DOCTYPE HTML>
<html>
<head>
    <title>스프링 부트 웹서비스</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
</head>
    <body>
        <h1>스프링 부트로 시작하는 웹 서비스</h1>
    </body>
</html>
```

생덩된 머스테치에 URL을 매핑합니다. URL 매핑은 당연하게 `Controller`에서 진행합니다. `web` 패키지 안에 `IndexController`를 생성합니다.

```java
package com.banjjoknim.book.springboot.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @GetMapping("/")
    public String index() {
        return "index";
    }
}
```
머스테치 스타터 덕분에 컨트롤러에서 문자열을 반환할 때 **앞의 경로와 뒤의 파일 확장자는 자동으로 지정**됩니다. 앞의 경로는 `src/main/resources/templates`로, 뒤의 파일 확장자는 `.mustache`가 붙는 것입니다. 즉 여기선 `index`를 반환하므로, `src/main/resources/templates/index.mustache`로 전환되어 `View Resolver`가 처리하게 됩니다.

>`ViewResolver`는 URL 요청의 결과를 전달할 타입과 값을 지정하는 관리자 격으로 볼 수 있습니다.

테스트 코드로 검증해 보겠습니다. `test` 패키지에 `IndexControllerTest` 클래스를 생성합니다.

```java
package com.banjjoknim.book.springboot.web;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class IndexControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void 메인페이지_로딩() {
        // when
        String body = this.restTemplate.getForObject("/", String.class);

        // then
        assertThat(body).contains("스프링 부트로 시작하는 웹 서비스");
    }
}
```
이번 테스트는 실제로 URL 호출 시 페이지의 내용이 제대로 호출되는지에 대한 테스트입니다. HTML도 결국은 **규칙이 있는 문자열**입니다. `TestRestTemplate`를 통해 `"/"`로 호출했을 때 `index.mustache`에 포함된 코드들이 있는지 확인하면 됩니다.

---

## 4.3 게시글 등록 화면 만들기

오픈 소스인 부트스트랩을 이용하여 화면을 만들어 봅니다. 부트스트랩, 제이쿼리 등 프론트엔드 라이브러리를 사용할 수 있는 방법은 크게 2가지가 있습니다. 하나는 **외부 CDN**을 사용하는 것이고, 다른 하나는 **직접 라이브러리를 받아서 사용**하는 방법입니다.

**레이아웃 방식 : 공통 영역을 별도의 파일로 분리하여 필요한 곳에서 가져다 쓰는 방식**

`src/main/resources/templates` 디렉토리에 `layout` 디렉토리를 추가로 생성합니다. 그리고 `footer.mustache`, `header.mustache` 파일을 생성합니다.

레이아웃 파일들에 각각 공통 코드를 추가합니다.

**header.mustache**
```java
<!DOCTYPE HTML>
<html>
<head>
    <title>스프링 부트 웹 서비스</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>

    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css">
</head>
<body>

```

**footer.mustache**
```java
<script src="https://code.jquery.com/jquery-3.3.1.min.js"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js"></script>
</body>
</html>
```

**페이지 로딩속도를 높이기 위해** `css`는 `header`에, `js`는 `footer`에 두었습니다. HTML은 위에서부터 코드가 실행되기 때문에 **head가 다 실행되고서야 body가 실행**됩니다. 즉, `head`가 다 불러지지 않으면 사용자 쪽에선 백지 화면만 노출됩니다. 특히 `js`의 용량이 크면 클수록 `body` 부분의 실행이 늦어지기 때문에 `js`는 `body` 하단에 두어 화면이 다 그려진 뒤에 호출하는 것이 좋습니다.

반면 `css`는 화면을 그리는 역할이므로 `head`에서 불러오는 것이 좋습니다. 그렇지 않으면 `css`가 적용되지 않은 깨진 화면을 사용자가 볼 수 있기 때문입니다. 추가로, `bootstrap.js`의 경우 **제이쿼리가 꼭 있어야**만 하기 때문에 부트스트랩보다 먼저 호출되도록 코드를 작성해야 합니다. 보통 이런 경우를 `bootstrap.js`가 **제이쿼리에 의존**한다고 합니다.

라이브러리를 비롯해 기타 HTML 태그들이 모두 레이아웃에 추가되었으므로 `index.mustache`의 코드는 다음과 같이 변경됩니다.

```java
{{>layout/header}} // 1.

<h1>스프링 부트로 시작하는 웹 서비스</h1>

{{>layout/footer}}
```

**1. {{>layout>header}}**
- `{{>}}`는 현재 머스테치 파일(`index.mustache`)을 기준으로 다른 파일을 가져옵니다.

레이아웃으로 파일을 분리했으니 `index.mustache`에 글 등록 버튼을 추가합니다.

```java
{{>layout/header}}

<h1>스프링 부트로 시작하는 웹 서비스</h1>
<div class="col-md-12">
    <div class="row">
        <div class="col-md-6">
            <a href="/posts/save" role="button" class="btn btn-primary">글 등록</a>
        </div>
    </div>
</div>

{{>layout/footer}}
```
여기서는 `<a>` 태그를 이용해 글 등록 페이지로 이동하는 글 등록 버튼이 생성되었습니다. 이동할 페이지의 주소는 `/posts/save`입니다.

이 주소에 해당하는 컨트롤러를 생성합니다.

```java
@RequiredArgsConstructor
@Controller
public class IndexController {
    ...

    @GetMapping("/posts/save")
    public String postsSave() {
        return "posts-sava";
    }
}
```
`index.mustache`와 마찬가지로 `/posts/save`를 호출하면 `posts-save.mustache`를 호출하는 메소드가 추가되었습니다. `posts-save.mustache` 파일을 생성합니다. 파일의 위치는 `index.mustache`와 같습니다.

**posts-save.mustache**
```java
{{>layout/header}}

<h1>게시글 등록</h1>

<div class="col-md-12">
    <div class="col-md-4">
        <form>
            <div class="form-group">
                <label for="title">제목</label>
                <input type="text" class="form-control" id="title" placeholder="제목을 입력하세요">
            </div>
            <div class="form-group">
                <label for="content">내용</label>
                <textarea class="form-control" id="content" placeholder="내용을 입력하세요"></textarea>
            </div>
        </form>
        <a href="/" role="button" class="btn btn-secondary">취소</a>
        <button type="button" class="btn btn-primary" id="btn-save">등록</button>
    </div>
</div>

{{>layout/footer}}
```
아직 게시글 등록 화면에 **등록 버튼은 기능이 없습니다.** API를 호출하는 `JS`가 전혀 없기 때문입니다. 그래서 `src/main/resources`에 `static/js/app` 디렉토리를 생성합니다.

**index.js**
```java
var main = {
    init : function () {
        var _this = this;
        $('#btn-save').on('click', function () {
            _this.save();
        });
    },
    save : function () {
        var data = {
            title: $('#title').val(),
            author: $('#author').val(),
            content: $('#content').val()
        };

        $.ajax({
            type: 'POST',
            url: '/api/v1/posts',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            data: JSON.stringify(data)
        }).done(function () {
            alert('글이 등록되었습니다.');
            window.location.href = '/'; // 1.
        }).fail(function (error) {
            alert(JSON.stringify(error));
        });
    }
};

main.init();
```
**1. window.location.href = '/'**
- 글 등록이 성공하면 메인페이지(`/`)로 이동합니다.

`index.js`의 첫 문장에 `var main = { ... }`라는 코드를 선언했습니다. 굳이 `main`라는 변수의 속성으로 `function`을 추가한 이유는, 예를 들면 `index.js`가 다음과 같이 `function`을 작성한 상황이라고 가정하겠습니다.

```java
var init = function() {
    ...
};

var save = function() {
    ...
};

init();
```
`index.mustache`에서 `a.js`가 추가되어 `a.js`도 **a.js만의 init과 save function이 있다**면?
브라우저의 스코프는 **공용 공간**으로 쓰이기 때문에 나중에 로딩된 `js`의 `init`, `save`가 먼저 로딩된 `js`의 `function`을 **덮어쓰게 됩니다.**
여러 사람이 참여하는 프로젝트에서는 **중복된 함수 이름**은 자주 발생할 수 있습니다. 모든 `function` 이름을 확인하면서 만들 수는 없습니다. 그래서 이런 문제를 피하려고 `index.js`만의 유효범위를 만들어 사용합니다.

방법은 `var index`이란 객체를 만들어 해당 객체에서 필요한 모든 `function`을 선언하는 것입니다. 이렇게 하면 **index 객체 안에서만 function이 유효**하기 때문에 다른 `JS`와 겹칠 위험이 사라집니다.

생성된 `index.js`를 머스테치 파일이 쓸 수 있게 `footer.mustache`에 추가합니다.

```java
<script src="https://code.jquery.com/jquery-3.3.1.min.js"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/js/bootstrap.min.js"></script>

<!--index.js 추가-->
<script src="/js/app/index.js"></script>
</body>
</html>
```
`index.js` 호출 코드를 보면 **절대 경로**(`/`)로 바로 시작합니다. 스프링 부트는 기본적으로 `src/main/resources/static`에 위치한 자바스크립트, CSS, 이미지 등 정적 파일들은 URL에서 `/`로 설정됩니다.

그래서 다음과 같이 파일이 위치하면 위치에 맞게 호출이 가능합니다.

- `src/main/resources/static/js/...(http://도메인/js/...)`
- `src/main/resources/static/css/...(http://도메인/css/...)`
- `src/main/resources/static/image/...(http://도메인/image/...)`

등록 기능이 완성되었으므로 직접 브라우저에서 테스트하고 `h2` DB에 데이터가 등록되었는지도 확인해봅니다.

---

## 4.4 전체 조회 화면 만들기
전체 조회를 위해 `index.mustache`의 UI를 변경합니다.

**index.mustache**
```java
{{>layout/header}}

<h1>스프링 부트로 시작하는 웹 서비스 Ver.2</h1>
<div class="col-md-12">
    <div class="row">
        <div class="col-md-6">
            <a href="/posts/save" role="button" class="btn btn-primary">글 등록</a>
        </div>
    </div>
    <br>
    <!--목록 출력 영역-->
    <table class="table table-horizontal table-bordered">
        <thead class="thead-strong">
        <tr>
            <th>게시글번호</th>
            <th>제목</th>
            <th>작성자</th>
            <th>최종수정일</th>
        </tr>
        </thead>
        <tbody id="tbody">
        {{#posts}} // 1.
            <tr>
                <td>{{id}}</td> // 2.
                <td>{{title}}</td>
                <td>{{author}}</td>
                <td>{{modifiedDate}}</td>
            </tr>
        {{/posts}}
        </tbody>
    </table>
</div>

{{>layout/footer}}
```
**1. {{#posts}}**
- `posts`라는 `List`를 순회합니다.
- `Java`의 `for`문과 동일하게 생각하면 됩니다.

**2. {{id}} 등의 {{변수명}}**
- `List`에서 뽑아낸 객체의 필드를 사용합니다.

다음으로 `Controller`, `Service`, `Repository` 코드를 작성합니다.
기존에 있던 `PostsRepository` 인터페이스에 쿼리가 추가됩니다.

```java
package com.banjjoknim.book.springboot.domain.posts;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostsRepository extends JpaRepository<Posts, Long> {

    @Query("SELECT p FROM Posts p ORDER BY p.id DESC ")
    List<Posts> findAllDesc();
}
```
`SpringDataJpa`에서 제공하지 않는 메소드는 위처럼 `@Query`를 사용하여 쿼리로 작성해도 됩니다.

`Repository` 다음으로 `PostsService`에 코드를 추가합니다.

```java
@RequiredArgsConstructor
@Service
public class PostsService {
    private final PostsRepository postsRepository;

    ...

    @Transactional(readOnly = true)
    public List<PostsListResponseDto> findAllDesc() {
        return postsRepository.findAllDesc().stream()
                .map(PostsListResponseDto::new)
                .collect(toList());
    }
}
```
`findAllDesc` 메소드의 트랙잭션 어노테이션(`@Transaction`)에 옵션이 하나 추가되었습니다. `(readOnly = true)`를 주면 **트랜잭션 범위는 유지**하되, 조회 기능만 남겨두어 **조회 속도가 개선**되기 때문에 등록, 수정, 삭제 기능이 전혀 없는 서비스 메소드에서 사용하는 것을 추천합니다.

아직 `PostsListResponseDto` 클래스가 없기 때문에 이 클래스 역시 생성합니다.

```java
package com.banjjoknim.book.springboot.web.dto;

import com.banjjoknim.book.springboot.domain.posts.Posts;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostsListResponseDto {
    private Long id;
    private String title;
    private String author;
    private LocalDateTime modifiedDate;

    public PostsListResponseDto(Posts entity) {
        this.id = entity.getId();
        this.title = entity.getTitle();
        this.author = entity.getAuthor();
        this.modifiedDate = entity.getModifiedDate();
    }
}
```
마지막으로 `Controller`를 변경합니다.

```java
package com.banjjoknim.book.springboot.web;


import org.springframework.ui.Model;


@RequiredArgsConstructor
@Controller
public class IndexController {

    private final PostsService postsService;

    @GetMapping("/")
    public String index(Model model) { // 1.
        model.addAttribute("posts", postsService.findAllDesc());
        return "index";
    }

    @GetMapping("/posts/save")
    public String postsSave() {
        return "posts-save";
    }

}
```
**1. Model**
- 서버 템플릿 엔진에서 사용할 수 있는 객체를 저장할 수 있습니다.
- 여기서는 `postsService.findAllDesc()`로 가져온 결과를 `posts`로 `index.mustache`에 전달합니다.

`Controller`까지 모두 완성되었으므로, `http://localhost:8080/`로 접속한 뒤 등록 화면을 이용해 정상적으로 기능이 동작하는지 확인합니다.

--- 

## 4.5 게시글 수정, 삭제 화면 만들기

게시글 수정 API는 이미 만들어둔 `PostsApiController`의 `update` 메소드를 이용합니다.

#### 게시글 수정

게시글 수정 화면 머스테치 파일을 생성합니다.

```java
{{>layout/header}}

<h1>게시글 수정</h1>

<div class="col-md-12">
    <div class="col-md-4">
        <form>
            <div class="form-group">
                <label for="id">글 번호</label>
                <input type="text" class="form-control" id="id" value="{{post.id}}" readonly>
            </div>
            <div class="form-group">
                <label for="title">제목</label>
                <input type="text" class="form-control" id="title" value="{{post.title}}">
            </div>
            <div class="form-group">
                <label for="author">작성자</label>
                <input type="text" class="form-control" id="author" value="{{post.author}}" readonly>
            </div>
            <div class="form-group">
                <label for="content">내용</label>
                <textarea class="form-control" id="content">{{post.content}}</textarea>
            </div>
        </form>
        <a href="/" role="button" class="btn btn-secondary">취소</a>
        <button type="button" class="btn btn-primary" id="btn-update">수정 완료</button>
        <button type="button" class="btn btn-danger" id="btn-delete">삭제</button>
    </div>
</div>

{{>layout/footer}}
```
**1. {{post.id}}**
- 머스테치는 객체의 필드 접근 시 점(`Dot`)으로 구분합니다.
- 즉, `Post` 클래스의 `id`에 대한 접근은 `post.id`로 사용할 수 있습니다.

**2. readonly**
- `input` 태그에 읽기 기능만 허용하는 속성입니다.
- `id`와 `author`는 수정할 수 없도록 읽기만 허용하도록 추가합니다.

그리고 `btn-update` 버튼을 클릭하면 `update` 기능을 호출할 수 있게 `index.js` 파일에도 `update function`을 하나 추가합니다.

```java
var main = {
    init: function () {
        var _this = this;
        ...

        $('#btn-update').on('click', function () { // 1.
            _this.update();
        })
    },
    save: function () {
        ...
    },
    update: function () { // 2.
        var data = {
            title: $('#title').val(),
            content: $('#content').val()
        };

        var id = $('#id').val();

        $.ajax({
            type: 'PUT', // 3.
            url: '/api/v1/posts/' + id, // 4.
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            data: JSON.stringify(data)
        }).done(function () {
            alert('글이 수정되었습니다.');
            window.location.href = '/';
        }).fail(function (error) {
            alert(JSON.stringify(error));
        });
    }
};

main.init();
```
**1. $('#btn-update').on('click')**
- `btn-update`란 `id`를 가진 HTML 엘리먼트에 `click` 이벤트가 발생할 때 `update function`을 실행하도록 이벤트를 등록합니다.

**2. update: function ()**
- 신규로 추가될 `update function`입니다.

**3. type: 'PUT'**
- 여러 `HTTP Method` 중 PUT 메소드를 선택합니다.
- `PostApiController`에 있는 API에서 이미 `@PutMapping`으로 선언했기 때문에 `PUT`을 사용해야 합니다. 참고로 이는 `REST` 규약에 맞게 설정된 것입니다.
- `REST`에서 `CRUD`는 다음과 같이 `HTTP Method`에 매핑됩니다.
  - 생성 (Create) - POST
  - 읽기 (Read) - GET
  - 수정 (Update) - PUT
  - 삭제 (Delete) - DELETE

**4. url: '/api/v1/posts/' + id**
- 어느 게시글을 수정할지 `URL Path`로 구분하기 위해 `Path`에 `id`를 추가합니다.

마지막으로 전체 목록에서 **수정 페이지로 이동할 수 있게** 페이지 이동 기능을 추가해 보겠습니다. `index.mustache` 코드를 '살짝' 수정합니다.

**index.mustache**
```java
<tbody id="tbody">
    {{#posts}}
        <tr>
            <td>{{id}}</td>
            <td><a href="/posts/update/{{id}}">{{title}}</a></td> // 1.
            <td>{{author}}</td>
            <td>{{modifiedDate}}</td>
        </tr>
    {{/posts}}
</tbody>
```
**1. < a href="/posts/update/{{id}}"></a>**
- 타이틀(`title`)에 `a tag`를 추가합니다.
- 타이틀을 클릭하면 해당 게시글의 수정 화면으로 이동합니다.

`IndexController`에 다음과 같이 메소드를 추가합니다.

```java
@RequiredArgsConstructor
@Controller
public class IndexController {

    private final PostsService postsService;

    ...

    @GetMapping("/posts/update/{id}")
    public String postsUpdate(@PathVariable Long id, Model model) {
        PostsResponseDto dto = postsService.findById(id);
        model.addAttribute("post", dto);

        return "posts-update";
    }
}
```
브라우저에서 수정 기능이 제대로 동작하는지 확인합니다.

#### 게시글 삭제
삭제 버튼은 본문을 확인하고 진행해야 하므로, 수정 화면에 추가합니다.

**posts-update.mustache**
```java
<div class="col-md-12">
    <div class="col-md-4">
        ...
        <a href="/" role="button" class="btn btn-secondary">취소</a>
        <button type="button" class="btn btn-primary" id="btn-update">수정 완료</button>
        <button type="button" class="btn btn-danger" id="btn-delete">삭제</button>
    </div>
</div>
```
**1. btn-delete**
- 삭제 버튼을 수정 완료 버튼 옆에 추가합니다.
- 해당 버튼 클릭시 `JS`에서 이벤트를 수신할 예정입니다.

삭제 이벤트를 진행할 `JS` 코드도 추가합니다.

**index.js**
```java
var main = {
    init: function () {
        var _this = this;
        ...

        $('#btn-delete').on('click', function () {
            _this.delete();
        });
    },
    ...
    delete: function () {
        var id = $('#id').val();

        $.ajax({
            type: 'DELETE',
            url: '/api/v1/posts/' + id,
            dataType: 'json',
            contentType: 'application/json; charset=utf-8'
        }).done(function () {
            alert('글이 삭제되었습니다.');
            window.location.href = '/';
        }).fail(function (error) {
            alert(JSON.stringify(error));
        })
    }
};

main.init();
```
`type`은 `DELETE`를 제외하고는 `update function`과 크게 차이 나진 않습니다. 다음으로는 삭제 API를 만듭니다. 먼저 서비스 메소드입니다.

**PostsService**
```java
@RequiredArgsConstructor
@Service
public class PostsService {
    private final PostsRepository postsRepository;

    ...

    @Transactional
    public void delete(Long id) {
        Posts posts = postsRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id=" + id));

        postsRepository.delete(posts); // 1.
    }
}
```
**1. postsRepository.delete(posts);**
- `JpaRepository`에서 이미 `delete` 메소드를 지원하고 있으니 이를 활용합니다.
- 엔티티를 파라미터로 삭제할 수도 있고, `deleteById` 메소드를 이용하면 `id`로 삭제할 수도 있습니다.
- 존재하는 `Posts`인지 확인을 위해 엔티티 조회 후 그대로 삭제합니다.

서비스에서 만든 `delete` 메소드를 컨트롤러가 사용하도록 코드를 추가합니다.

**PostApiController**
```java
@RequiredArgsConstructor
@RestController
public class PostsApiController {

    private final PostsService postsService;

    ...

    @DeleteMapping("/api/v1/posts/{id}")
    public Long delete(@PathVariable Long id) {
        postsService.delete(id);
        return id;
    }
}
```
컨트롤러까지 생성되었으니 브라우저에서 기능이 잘 동작하는지 테스트를 해봅니다.

---