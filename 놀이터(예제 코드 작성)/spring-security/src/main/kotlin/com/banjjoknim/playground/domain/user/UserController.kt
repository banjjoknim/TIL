package com.banjjoknim.playground.domain.user

import com.banjjoknim.playground.config.security.PrincipalDetails
import com.banjjoknim.playground.config.security.PrincipalOAuth2UserService
import org.springframework.security.core.Authentication
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController {

    /**
     * ```
     * 스프링 시큐리티는 스프링 시큐리티 세션을 들고 있다.
     *
     * 그러면 원래 서버 세션 영역 안에 시큐리티가 관리하는 세션이 따로 존재하게 된다.
     *
     * 시큐리티 세션에는 무조건 Authentication 객체 만 들어갈 수 있다.
     *
     * Authentication 가 시큐리티세션 안에 들어가 있다는 것은 로그인된 상태라는 의미이다.
     *
     * Authentication 에는 2개의 타입이 들어갈 수 있는데 UserDetails, OAuth2User 이다.
     *
     * 문제점 :
     *
     * 이때 세션이 2개의 타입으로 나눠졌기 때문에 컨트롤러에서 처리하기 복잡해진다는 문제점이 발생한다!
     *
     * 왜냐하면 일반적인 로그인을 할 때엔 UserDetails 타입으로 Authentication 객체가 만들어지고,
     *
     * 구글 로그인처럼 OAuth 로그인을 할 때엔 OAuth2User 타입으로 Authentication 객체가 만들어지기 때문이다.
     *
     * 해결방법 :
     *
     * PrincipalDetails 에 UserDetails, OAuth2User 를 implements 한다.
     *
     * 그렇게 하면 PrincipalDetails 타입은 UserDetails, OAuth2User 타입이 되므로 우리는 오직 PrincipalDetails 만 활용하면 된다.
     *
     * 추가로, @AuthenticationPrincipal 어노테이션으로 세션 정보를 DI 받아서 바로 접근할 수 있다.
     *
     * 이는 스프링 시큐리티가 갖고 있는 세션에서 Authentication 객체를 갖고 있기 때문이다.
     *
     * 그에 따라 결과적으로는 시큐리티 세션에 존재하는 Authentication 객체를 PrincipalDetails 으로 다운 캐스팅 하지 않아도 된다.
     * ```
     *
     * ```
     * @AuthenticationPrincipal 어노테이션이 활성화되는 시점?
     *
     * PrincipalOAuth2UserService, PrincipalDetailService 를 만들지 않아도(오버라이드 하지 않아도)
     *
     * loadUser(), loadUserByUsername() 은 기본적으로 실행되어 대신 스프링 시큐리티가 로그인을 진행해준다.
     *
     * 하지만 굳이 오버라이드 하면서 PrincipalOAuth2UserService, PrincipalDetailService 를 만든 이유는 로그인시 PrincipalDetails 객체를 반환하기 위해서다.
     *
     * 이는 로그인시 반환되는 객체가 Authentication 객체 내부에 저장되기 때문이며, 이렇게 하는게 더 편하다.
     *
     * ```
     *
     * @see PrincipalDetails
     * @see AuthenticationPrincipal
     * @see PrincipalOAuth2UserService
     * @see PrincipalDetailsService
     *
     */
    @GetMapping("/login") // OAuth2 로그인 및 일반 로그인 모두 principalDetails 로 세션 정보를 얻어올 수 있다(다운 캐스팅을 하지 않아도 된다!).
    fun login(@AuthenticationPrincipal principalDetails: PrincipalDetails) { // DI(의존성 주입)
        println("principalDetailsUser : ${principalDetails.user}")
    }

    @GetMapping("/test/login")
    fun testLogin(authentication: Authentication, @AuthenticationPrincipal userDetails: UserDetails) { // DI(의존성 주입)
        val principalDetailsFromAuthentication = authentication.principal as PrincipalDetails // 다운 캐스팅
        println("principalDetailsFromAuthentication : ${principalDetailsFromAuthentication.user}")
        println("principalDetailsFromAuthentication : ${principalDetailsFromAuthentication.username}")
        val principalDetailsFromUserDetails = userDetails as PrincipalDetails // 다운 캐스팅
        println("principalDetailsFromUserDetails : ${principalDetailsFromUserDetails.user}")
        println("principalDetailsFromUserDetails : ${principalDetailsFromUserDetails.username}")
    }

    @GetMapping("/test/oauth2/login")
    fun testOAuth2Login(authentication: Authentication, @AuthenticationPrincipal oauth: OAuth2User) { // DI(의존성 주입)
        val oAuth2User = authentication.principal as OAuth2User // 다운 캐스팅
        println("authentication : ${oAuth2User.attributes}") // OAuth2Service 의 super.loadUser(userRequest).attributes 와 같다.
        println("oAuth2User : ${oauth.attributes}")
    }
}
