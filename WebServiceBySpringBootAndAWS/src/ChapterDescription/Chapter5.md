# Chapter5. 스프링 시큐리티와 OAuth 2.0으로 로그인 기능 구현하기

스프링 시큐리티는 막강한 인증과 인가 기능을 가진 프레임워크입니다. 스프링 기반의 애플리케이션에서는 보안을 위한 표준이라고 보면 됩니다. 인터셉터, 필터 기반의 보안 기능을 구현하는 것보다 스프링 시큐리티를 통해 구현하는 것을 적극적으로 권장하고 있습니다.

---

## 5.1 스프링 시큐리티와 스프링 시큐리티 OAuth2 클라이언트
소셜 로그인 기능을 이용하지 않고 직접 로그인 기능을 구현하려면 다음을 전부 구현해야 합니다. `OAuth`를 써도 구현해야 하는 것은 제외했습니다.

- **로그인 시 보안**
- **회원가입 시 이메일 혹은 전화번호 인증**
- **비밀번호 찾기**
- **비밀번호 변경**
- **회원정보 변경**

`OAuth` 로그인 구현 시 앞선 목록의 것들을 모두 구글, 페이스북, 네이버 등에 맡기면 되니 서비스 개발에 집중할 수 있습니다.

### 스프링 부트 1.5 vs 스프링 부트 2.0
`spring-security-oauth2-autoconfigure` 라이브러리를 사용할 경우 스프링 부트 2에서도 1.5에서 쓰던 설정을 그대로 사용할 수 있습니다. 새로운 방법을 쓰기보다는 **기존에 안전하게 작동하던 코드**를 사용하는 것이 아무래도 더 확실하므로 많은 개발자가 이 방식을 사용해 왔습니다.

하지만 여기서는 스프링 부트 2 방식인 `Spring Security Oauth2 Client` 라이브러리를 사용해서 진행합니다. 이유는 다음과 같습니다.

- **스프링 팀에서 기존 1.5에서 사용되던 `spring-security-oauth` 프로젝트는 유지 상태로 결정했으며 더는 신규 기능은 추가하지 않고 버그 수정 정도의 기능만 추가될 예정, 신규 기능은 새 oauth2 라이브러리에서만 지원하겠다고 선언**
- **스프링 부트용 라이브러리(`starter`) 출시**
- **기존에 사용되던 방식은 확장 포인트가 적절하게 오픈되어 있지 않아 직접 상속하거나 오버라이딩 해야 하고 신규 라이브러리의 경우 확장 포인트를 고려해서 설계된 상태**

스프링 부트 1.5 방식에서는 `url` 주소를 모두 명시해야 하지만, **2.0 방식에서는 client 인증 정보**만 입력하면 됩니다. 1.5버전에서 직접 입력했던 값들은 2.0버전으로 오면서 모두 **enum으로 대체되었습니다.**
`CommonOAuth2Provider`라는 `enum`이 새롭게 추가되어 구글, 깃허브, 페이스북, 옥타의 기본 설정값은 모두 여기서 제공합니다.

```java
public enum CommonOAuth2Provider {
    GOOGLE {

        @Override
        public Builder getBuilder(String registrationId) {
            ClientRegistration.Builder builder = getBuilder(registrationId, ClientAuthenticationMethod.BASIC, DEFAULT_REDIRECT_URL);
            builder.scope("openin", "profile", "email");
            builder.authorizationUri("https://accounts.google.com/o/oauth2/v2/auth");
            builder.tokenUri("https://www.googleapis.com/oauth2/v4/token");
            builder.jwkSetUri("https://www.googleapis.com/oauth2/v3/certs");
            builder.userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo");
            builder.userNameAttributeName(IdTokenClaimNames.SUB);
            builder.clientName("Google");
            return builder;
        }
    },

    ...
}
```
이외에 다른 소셜 로그인(네이버, 카카오 등)을 추가한다면 직접 다 추가해 주어야 합니다.

---

## 5.2 구글 서비스 등록
먼저 구글 서비스에 신규 서비스를 생성합니다. 여기서 발급된 인증 정보를 통해서 로그인 기능과 소셜 서비스 기능을 사용할 수 있으니 무조건 발급받고 시작해야 합니다. 구글 클라우드 플랫폼 주소(`https://console.cloud.google.com`)로 이동합니다.

다음의 자세한 내용은 책을 참조할 것.

클라이언트를 성공적으로 만든 이후, 클라이언트 ID와 클라이언트 보안 비밀 코드를 프로젝트에서 설정합니다.

#### application-oauth 등록
`application.properties`가 있는 `src/main/resources/` 디렉토리에 **application-oauth.properties** 파일을 생성합니다.
그리고 해당 파일에 클라이언트 ID와 클라이언트 보안 비밀코드를 다음과 같이 등록합니다.
```java
spring.security.oauth2.client.registration.google.client-id=클라이언트 ID
spring.security.oauth2.client.registration.google.client-secret=클라이언트 보안 비밀
spring.security.oauth2.client.registration.google.scope=profile,email
```

`scope=profile,email`
- **많은 예제에서는 이 `scope`를 별도로 등록하지 않고 있습니다.**
- **기본값이 `openid,profile,email`이기 때문입니다.**
- **강제로 `profile,email`를 등록한 이유는 `openid`라는 `scope`가 있으면 `OpenId Provider`로 인식하기 때문입니다.**
- **이렇게 되면 `OpenId Provider`인 서비스(구글)와 그렇지 않은 서비스(네이버/ 카카오 등)로 나눠서 각각 `OAuth2Service`를 만들어야 합니다.**
- **하나의 `OAuth2Service`로 사용하기 위해 일부러 `openid scope`를 빼고 등록합니다.**

스프링 부트에서는 `properties`의 이름을 `application-xxx.properties` 로 만들면 `xxx`라는 이름의 `profile`이 생성되어 이를 통해 관리할 수 있습니다. 즉, `profile=xxx`라는 식으로 호출하면 **해당 properties의 설정들을 가져올 수 있습니다.** 호출하는 방식은 여러 방식이 있지만 여기서는 스프링 부트의 기본 설정 파일인 `application.properties`에서 `application-oauth.properties`를 포함하도록 구성합니다.
`application.properties`에 다음과 같이 코드를 추가합니다.

```java
spring.profiles.include=oauth
```
이제 이 설정값을 사용할 수 있게 되었습니다.

#### .gitignore 등록
구글 로그인을 위한 클라이언트 ID와 클라이언트 보안 비밀은 보안이 중요한 정보들입니다. 이들이 외부에 노출될 경우 언제든 개인정보를 가져갈 수 있는 취약점이 될 수 있습니다. 보안을 위해 깃허브에 `application-oauth.properties` 파일이 올라가는 것을 방지하기 위해, `.gitignore`에 다음과 같이 한 줄의 코드를 추가합니다.

```java
application-oauth.properties
```

---

## 5.3 구글 로그인 연동하기
구글의 로그인 인증정보를 발급 받았으니 프로젝트 구현을 해봅니다. 먼저 사용자 정보를 담당할 도메인인 `User` 클래스를 생성합니다. 패키지는 `domain` 아래에 `user` 패키지를 생성합니다.

```java
package com.banjjoknim.book.springboot.domain.user;

import com.banjjoknim.book.springboot.domain.BaseTimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String picture;

    @Enumerated(EnumType.STRING) // 1.
    @Column(nullable = false)
    private Role role;

    @Builder
    public User(String name, String email, String picture, Role role) {
        this.name = name;
        this.email = email;
        this.picture = picture;
        this.role = role;
    }

    public User update(String name, String picture) {
        this.name = name;
        this.picture = picture;

        return this;
    }

    public String getRoleKey() {
        return this.role.getKey();
    }
}
```

**1. @Enumerated(EnumType.STRING)**
- `JPA`로 데이터베이스로 저장할 때 `Enum` 값을 어떤 형태로 저장할지를 결정합니다.
- 기본적으로는 `int`로 된 숫자가 저장됩니다.
- 숫자로 저장되면 데이터베이스로 확인할 때 그 값이 무슨 코드를 의미하는지 알 수가 없습니다.
- 그래서 문자열 (`EnumType.STRING`)로 저장될 수 있도록 선언합니다.

각 사용자의 권한을 관리할 `Enum` 클래스 `Role`을 생성합니다.

```java
package com.banjjoknim.book.springboot.domain.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {

    GUEST("ROLE_GUEST", "손님"),
    USER("ROLE_USER", "일반 사용자");

    private final String key;
    private final String title;

}
```
스프링 시큐리티에서는 권한 코드에 항상 **`ROLE_`이 앞에 있어야만 합니다.** 그래서 코드별 키 값을 `ROLE_GUEST`, `ROLE_USER` 등으로 지정합니다.

마지막으로 `User`의 `CRUD`를 책임질 `UserRepository`도 생성합니다.

```java
package com.banjjoknim.book.springboot.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email); // 1.
}
```

**1. findByEamil**
- 소셜 로그인으로 반환되는 값 중 `email`을 통해 이미 생성된 사용자인지 처음 가입하는 사용자인지 판단하기 위한 메소드입니다.

### 스프링 시큐리티 설정
먼저 `build.gradle`에 스프링 시큐리티 관련 의존성 하나를 추가합니다.

```java
compile('org.springframework.boot:spring-boot-starter-oauth2-client') // 1.
```

**1. spring-boot-starter-oauth2-client**
- 소셜 로그인 등 클라이언트 입장에서 소셜 기능 구현 시 필요한 의존성입니다.
- `spring-security-oauth2-client`와 `spring-security-oauth2-jose`를 기본으로 관리해줍니다.

`build.gradle` 설정이 끝났으면 `OAuth` 라이브러리를 이용한 소셜 로그인 설정 코드를 작성합니다.
`config.auth` 패키지를 생성하고 `SecurityConfig` 클래스를 생성합니다.

```java
package com.banjjoknim.book.springboot.config.auth;

import com.banjjoknim.book.springboot.domain.user.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@RequiredArgsConstructor
@EnableWebSecurity // 1.
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomOAuth2UserService customOAuth2UserService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().headers().frameOptions().disable() // 2.
                .and()
                .authorizeRequests() // 3.
                .antMatchers("/", "/css/**", "/images/**", "/js/**", "/h2-console/**").permitAll()
                .antMatchers("/api/v1/**").hasRole(Role.USER.name()) // 4.
                .anyRequest().authenticated() // 5.
                .and()
                .logout()
                .logoutSuccessUrl("/") // 6.
                .and()
                .oauth2Login() // 7.
                .userInfoEndpoint() // 8.
                .userService(customOAuth2UserService); // 9.
    }
}
```

**1. @EnableWebSecurity**
- `Spring Security` 설정들을 활성화시켜 줍니다.

**2. csrf().disable().headers().frameOptions().disable()**
- `h2-console` 화면을 사용하기 위해 해당 옵션들을 `disable` 합니다.

**3. authorizeRequests**
- URL별 권한 관리를 설정하는 옵션의 시작점입니다.
- `authorizeRequests`가 선언되어야만 `antMatchers` 옵션을 사용할 수 있습니다.

**4. antMatchers**
- 권한 관리 대상을 지정하는 옵션입니다.
- URL, HTTP 메소드별로 관리가 가능합니다.
- `"/"` 등 지정된 URL들은 `permitAll()` 옵션을 통해 전체 열람 권한을 주었습니다.
- `"/api/v1/**"` 주소를 가진 API는 `USER` 권한을 가진 사람만 가능하도록 했습니다.

**5. anyRequest**
- 설정된 값들 이외 나머지 URL들을 나타냅니다.
- 여기서는 `authenticated()`을 추가하여 나머지 URL들은 모두 인증된 사용자들에게만 허용하게 합니다.
- 인증된 사용자 즉, 로그인한 사용자들을 이야기합니다.

**6. logout().logoutSuccessUrl("/")**
- 로그아웃 기능에 대한 여러 설정의 진입점입니다.
- 로그아웃 성공시 `/` 주소로 이동합니다.

**7. oauth2Login**
- `OAuth 2` 로그인 기능에 대한 여러 설정의 진입점입니다.

**8. userInfoEndpoint**
- `OAuth 2` 로그인 성공 이후 사용자 정보를 가져올 때의 설정들을 담당합니다.

**9. userService**
- 소셜 로그인 성공 시 후속 조치를 진행할 `UserService` 인터페이스의 구현체를 등록합니다.
- 리소스 서버(즉, 소셜 서비스들)에서 사용자 정보를 가져온 상태에서 추가로 진행하고자 하는 기능을 명시할 수 있습니다.

설정 코드 작업이 끝났다면 `CustomOAuth2UserService` 클래스를 생성합니다. 이 클래스에서는 구글 로그인 이후 가져온 사용자의 정보(`email`, `name`, `picture`)들을 기반으로 가입 및 정보수정, 세션 저장 등의 기능을 지원합니다.

```java
package com.banjjoknim.book.springboot.config.auth;

import com.banjjoknim.book.springboot.domain.user.User;
import com.banjjoknim.book.springboot.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Collections;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService implements OAuth2UserService {
    private final UserRepository userRepository;
    private final HttpSession httpSession;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId(); // 1.
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName(); // 2.

        OAuthAttributes attributes = OAuthAttributes
        .of(registrationId, userNameAttributeName, oAuth2User.getAttributes()); // 3.

        User user = saveOrUpdate(attributes);
        httpSession.setAttribute("user", new SessionUser(user)); // 4.

        return new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority(user.getRoleKey())),
                attributes.getAttributes(),
                attributes.getNameAttributeKey());
    }

    private User saveOrUpdate(OAuthAttributes attributes) {
        User user = userRepository.findByEmail(attributes.getEmail())
                .map(entity -> entity.update(attributes.getName(), attributes.getPicture()))
                .orElse(attributes.toEntity());

        return userRepository.save(user);
    }
}
```

**1. registrationId**
- 현재 로그인 진행 중인 서비스를 구분하는 코드입니다.
- 지금은 구글만 사용하는 불필요한 값이지만, 이후 네이버 로그인 연동 시에 네이버 로그인인지, 구글 로그인인지 구분하기 위해 사용합니다.

**2. userNameAttributeName**
- `OAuth2` 로그인 진행 시 키가 되는 필드값을 이야기합니다. `Primary Key`와 같은 의미입니다.
- 구글의 경우 기본적으로 코드를 지원하지만, 네이버 카카오 등은 기본 지원하지 않습니다. 구글의 기본 코드는 `"sub"` 입니다.
이후 네이버 로그인과 구글 로그인을 동시 지원할 때 사용됩니다.

**3. OAuthAttributes**
- `OAuth2UserService`를 통해 가져온 `OAuth2User`의 `attribute`를 담을 클래스입니다.
- 이후 네이버 등 다른 소셜 로그인도 이 클래스를 사용합니다.

**4. SessionUser**
- 세션에 사용자 정보를 저장하기 위한 `Dto` 클래스입니다.

구글 사용자 정보가 업데이트 되었을 때를 대비하여 `update` 기능도 같이 구현되었습니다. 사용자의 이름이나 프로필 사진이 변경되면 `User` 엔티티에도 반영됩니다.

`CustomOAuth2UserService` 클래스까지 생성되었다면 `OAuthAttributes` 클래스를 생성합니다.

```java
package com.banjjoknim.book.springboot.config.auth.dto;

import com.banjjoknim.book.springboot.domain.user.Role;
import com.banjjoknim.book.springboot.domain.user.User;
import lombok.Builder;

import java.util.Map;

@Getter
public class OAuthAttributes {
    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String name;
    private String email;
    private String picture;

    @Builder
    public OAuthAttributes(Map<String, Object> attributes, String nameAttributeKey, String name, String email, String picture) {
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.name = name;
        this.email = email;
        this.picture = picture;
    }

    // 1.
    public static OAuthAttributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        return ofGoogle(userNameAttributeName, attributes);
    }

    private static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .picture((String) attributes.get("picture"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    // 2.
    public User toEntity() {
        return User.builder()
                .name(name)
                .email(email)
                .picture(picture)
                .role(Role.GUEST)
                .build();
    }
}
```

**1. of()**
- `OAuth2User`에서 반환하는 사용자 정보는 `Map`이기 때문에 값 하나하나를 변환해야만 합니다.

**2. toEntity()**
- `User` 엔티티를 생성합니다.
- `OAuthAttributes`에서 엔티티를 생성하는 시점은 처음 가입할 때입니다.
- 가입할 때의 기본 권한을 `GUEST`로 주기 위해서 `role` 빌더값에는 `Role.GUEST`를 사용합니다.

`OAuthAttributes` 클래스 생성이 끝났으면 같은 패키지에 `SessionUser` 클래스를 생성합니다.

```java
package com.banjjoknim.book.springboot.config.auth.dto;

import com.banjjoknim.book.springboot.domain.user.User;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class SessionUser implements Serializable {
    private String name;
    private String email;
    private String picture;

    public SessionUser(User user) {
        this.name = user.getName();
        this.email = user.getEmail();
        this.picture = user.getPicture();
    }
}
```
`SessionUser`에는 **인증된 사용자 정보**만 필요합니다. 그 외에 필요한 정보들은 없으니 `name`, `email`, `picture`만 필드로 선언합니다.

### 로그인 테스트
스프링 시큐리티가 잘 적용되었는지 확인하기 위해 화면에 로그인 버튼을 추가합니다. `index.mustache`에 로그인 버튼과 로그인 성공 시 사용자 이름을 보여주는 코드입니다.

```java
<h1>스프링 부트로 시작하는 웹 서비스 Ver.2</h1>
<div class="col-md-12">
    <div class="row">
        <div class="col-md-6">
            <a href="/posts/save" role="button" class="btn btn-primary">글 등록</a>
            {{#userName}} // 1.
                Logged in as: <span id="user">{{userName}}</span>
            <a href="/logout" class="btn btn-info active" role="button">Logout</a> // 2.
            {{/userName}}
            {{^userName}} // 3.
                <a href="/oauth2/authorization/google" class="btn btn-success active" role="button">Google Login</a> // 4.
            {{/userName}}
        </div>
    </div>
```

**1. {{#userName}}**
- 머스테치는 다른 언어와 같은 `if문(if userName != null 등)`을 제공하지 않습니다.
- `true/false` 여부만 판단할 뿐입니다.
- 그래서 머스테치에서는 항상 최종값을 넘겨줘야 합니다.
- 여기서는 `userName`이 있다면 `userName`을 노출시킵니다.

**2. a href="/logout"**
- 스프링 시큐리티에서 기본적으로 제공하는 로그아웃 URL입니다.
- 즉, 개발자가 별도로 저 URL에 해당하는 컨트롤러를 만들 필요가 없습니다.
- `SecurityConfig` 클래스에서 URL을 변경할 순 있지만 기본 URL을 사용해도 충분하니 여기서는 그대로 사용합니다.

**3. {{^userName}}**
- 머스테치에서 해당 값이 존재하지 않는 경우에는 `^`를 사용합니다.
- 여기서는 `userName`이 없다면 로그인 버튼을 노출시킵니다.

**4. a href="/oauth2/authorization/google"**
- 스프링 시큐리티에서 기본적으로 제공하는 로그인 URL입니다.
- 로그아웃 URL과 마찬가지로 개발자가 별도의 컨트롤러를 생성할 필요가 없습니다.

`index.mustache`에서 `userName`을 사용할 수 있게 `IndexController`에서 `userName`을 `model`에 저장하는 코드를 추가합니다.

```java
@RequiredArgsConstructor
@Controller
public class IndexController {

    private final PostsService postsService;
    private final HttpSession httpSession;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("posts", postsService.findAllDesc());

        SessionUser user = (SessionUser) httpSession.getAttribute("user"); // 1.

        if (user != null) { // 2.
            model.addAttribute("userName", user.getName());
        }
        return "index";
    }
}
```
**1. (SessionUser) httpSession.getAttribute("user")**
- 앞서 작성된 `CustomOAuth2UserService`에서 로그인 성공 시 세션에 `SessionUser`를 저장합니다.
- 즉, 로그인 성공 시 `httpSession.getAttribute("user")`에서 값을 가져올 수 있습니다.

**2. if (user != null)**
- 세션에 저장된 값이 있을 때만 `model`에 `userName`으로 등록합니다.
- 세션에 저장된 값이 없으면 `model`엔 아무런 값이 없는 상태이니 로그인 버튼이 보이게 됩니다.

이제 프로젝트를 실행해서 `Google Login` 버튼이 잘 노출되는지, 권한 관리도 잘되는지 확인해 봅니다.

---

## 5.4 어노테이션 기반으로 개선하기

`SessionUser user = (SessionUser) httpSession.getAttribute("user");`

`index` 메소드 외에 다른 컨트롤러와 메소드에서 세션값이 필요하면 그때마다 직접 세션에서 값을 가져와야 합니다. 같은 코드가 계속해서 반복되는 것은 불필요합니다. 따라서 이 부분을 **메소드 인자로 세션값을 바로 받을 수 있도록** 변경합니다.

`config.auth` 패키지에 다음과 같이 `@LoginUser` 어노테이션을 생성합니다.

```java
package com.banjjoknim.book.springboot.config.auth;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER) // 1.
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginUser { // 2.
}
```
**1. @Target(ElementType.PARAMETER)**
- 이 어노테이션이 생성될 수 있는 위치를 지정합니다.
- `PARAMETER`로 지정했으니 메소드의 파라미터로 선언된 객체에서만 사용할 수 있습니다.
- 이 외에도 클래스 선언문에 쓸 수 있는 `TYPE` 등이 있습니다.

**2. @interface**
- 이 파일을 어노테이션 클래스로 지정합니다.
- `LoginUser`라는 이름을 가진 어노테이션이 생성되었다고 보면 됩니다.

그리고 같은 위치에 `LoginUserArgumentResolver`를 생성합니다. `LoginUserArgumentResolver`라는 `HandlerMethodArgumentResolver` 인터페이스를 구현한 클래스입니다.

`HandlerMethodArgumentResolver`는 한가지 기능을 지원합니다. 바로 조건에 맞는 경우 메소드가 있다면 `HandlerMethodArgumentResolver`의 구현체가 지정한 값으로 해당 메소드의 파라미터로 넘길 수 있습니다.

```java
package com.banjjoknim.book.springboot.config.auth;

import com.banjjoknim.book.springboot.config.auth.dto.SessionUser;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpSession;

@RequiredArgsConstructor
@Component
public class LoginUserArgumentResolver implements HandlerMethodArgumentResolver {

    private final HttpSession httpSession;

    @Override
    public boolean supportsParameter(MethodParameter parameter) { // 1.
        boolean isLoginUserAnnotation = parameter.getParameterAnnotation(LoginUser.class) != null;
        boolean isUserClass = SessionUser.class.equals(parameter.getParameterType());
        return isLoginUserAnnotation && isUserClass;
    }

    @Override // 2.
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        return httpSession.getAttribute("user");
    }
}
```
**1. supportsParameter()**
- 컨트롤러 메서드의 특정 파라미터를 지원하는지 판단합니다.
- 여기서는 파라미터에 `@LoginUser` 어노테이션이 붙어 있고, 파라미터 클래스 타입이 `SessionUser.class`인 경우 `true`를 반환합니다.

**2. resolveArgument()**
- 파라미터에 전달할 객체를 생성합니다.
- 여기서는 세션에서 객체를 가져옵니다.

`@LoginUser`를 사용하기 위한 환경은 구성되었습니다. 이제 이렇게 생성된 `LoginUserArgumentResolver`가 **스프링에서 인식될 수 있도록** `WebMvcConfigurer`에 추가합니다. `config` 패키지에 `WebConfig` 클래스를 생성하여 다음과 같이 설정을 추가합니다.

```java
package com.banjjoknim.book.springboot.config;

import com.banjjoknim.book.springboot.config.auth.LoginUserArgumentResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@RequiredArgsConstructor
@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final LoginUserArgumentResolver loginUserArgumentResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(loginUserArgumentResolver);
    }
}
```
`HandlerMethodArgumentResolver`는 항상 `WebMvcConfigurer`의 `addArgumentResolvers()`를 통해 추가해야 합니다. 다른 `HandlerMethodArgumentResolver`가 필요하다면 같은 방식으로 추가해 주면 됩니다.

모든 설정이 끝났으니 `IndexController`의 코드에서 반복되는 부분들을 모두 `@LoginUser`로 개선합니다.

```java
@RequiredArgsConstructor
@Controller
public class IndexController {

    private final PostsService postsService;
    private final HttpSession httpSession;

    @GetMapping("/")
    public String index(Model model, @LoginUser SessionUser user) {
        model.addAttribute("posts", postsService.findAllDesc());

        if (user != null) {
            model.addAttribute("userName", user.getName());
        }
        return "index";
    }
}
```
**1. @LoginUser SessionUser user**
- 기존에 `(User) httpSession.getAttribute("user")`로 가져오던 세션 정보 값이 개선되었습니다.
- 이제는 어느 컨트롤러든지 `@LoginUser`만 사용하면 세션 정보를 가져올 수 있게 되었습니다.

다시 애플리케이션을 실행해 로그인 기능이 정상적으로 작동하는지 확인합니다.

---

## 5.5 세션 저장소로 데이터베이스 사용하기
현재 우리가 만든 서비스는 **애플리케이션을 재실행**하면 로그인이 풀립니다. 이는 세션이 **내장 톰캣 메모리에 저장**되기 때문입니다. 기본적으로 세션은 실행되는 WAS(Web Application Server)의 메모리에서 저장되고 호출됩니다. 메모리에 저장되다 보니 **내장 톰캣처럼 애플리케이션 실행 시 실행되는 구조에선 항상 초기화**가 됩니다. 즉, **배포할 때마다 톰캣이 재시작**되는 것입니다. 게다가, 2대 이상의 서버에서 서비스하고 있다면 **톰캣마다 세션 동기화** 설정을 해야만 합니다. 그래서 실제 현업에서는 세션 저장소에 대해 다음의 3가지 중 한 가지를 선택합니다.

1. 톰캣 세션을 사용한다.
    - 일반적으로 별다른 설정을 하지 않을 때 기본적으로 선택되는 방식입니다.
    - 이렇게 될 경우 톰캣(`WAS`)에 세션이 저장되기 때문에 2대 이상의 `WAS`가 구동되는 환경에서는 톰캣들 간의 세션 공유를 위한 추가 설정이 필요합니다.

2. `MySQL`과 같은 데이터베이스를 세션 저장소로 사용한다.
    - 여러 `WAS` 간의 공용 세션을 사용할 수 있는 가장 쉬운 방법입니다.
    - 많은 설정이 필요 없지만, 결국 로그인 요청마다 `DB IO`가 발생하여 성능상 이슈가 발생할 수 있습니다.
    - 보통 로그인 요청이 많이 없는 백오피스, 사내 시스템 용도에서 사용합니다.

3. `Redis`, `Memcached`와 같은 메모리 `DB`를 세션 저장소로 사용한다.
    - `B2C` 서비스에서 가장 많이 사용하는 방식입니다.
    - 실제 서비스로 사용하기 위해서는 `Embedded Redis`와 같은 방식이 아닌 외부 메모리 서버가 필요합니다.

여기서는 두 번째 방식인 **데이터베이스를 세션 저장소**로 사용하는 방식을 사용합니다. 이유는 **설정이 간단**하고 사용자가 많은 서비스가 아니며 비용 절감을 위해서입니다.

#### spring-session-jdbc 등록
먼저 `build.gradle`에 다음과 같이 의존성을 등록합니다. `spring-session-jdbc` 역시 의존성이 추가되어 있어야 사용할 수 있습니다.

```java
compile('org.springframework.session:spring-session-jdbc')
```

그리고 `application.properties`에 세션 저장소를 `jdbc`로 선택하도록 코드를 추가합니다. 설정은 다음 코드가 전부입니다. 이 외에 설정할 것이 없습니다.

```java
spring.session.store-type=jdbc
```
모두 변경하였으니 다시 애플리케이션을 실행해서 로그인을 테스트한 뒤, `h2-console`로 접속하여 세션을 위한 테이블이 생성되었는지 확인합니다. **JPA로 인해 세션 테이블이 자동 생성**되었기 때문에 별도로 해야 할 일은 없습니다. 방금 로그인했기 때문에 한 개의 세션이 등록돼있는 것을 볼 수 있습니다.

세션 저장소를 데이터베이스로 교체했습니다. 지금은 기존과 동일하게 **스프링을 재시작하면 세션이 풀립니다.** 이유는 `H2` 기반으로 스프링이 재실행될 때 **H2도 재시작되기 때문입니다.** 이후 `AWS`로 배포하게 되면 `AWS`의 데이터베이스 서비스인 `RDS(Relational Database Service)`를 사용하게 되며, 이때부터는 세션이 풀리지 않습니다.

---

## 5.6 네이버 로그인
마지막으로 네이버 로그인을 추가해 봅니다.

### 네이버 API 등록
먼저 네이버 오픈 API로 이동합니다.

>https://developers.naver.com/apps/#/register?api=nvlogin

