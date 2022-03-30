package com.banjjoknim.playground.jwt.config.filter

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.banjjoknim.playground.jwt.config.security.PrincipalDetails
import com.banjjoknim.playground.jwt.domain.user.JwtUser
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import java.util.Date
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * Spring Security 에는 UsernamePasswordAuthenticationFilter 가 있다.
 *
 * 기본적으로는 /login 요청에서 username, password 를 전송하면 (post 요청) UsernamePasswordAuthenticationFilter 가 동작한다.
 *
 * 하지만 우리는 formLogin().disable() 설정을 해주었기 때문에 직접 Filter 를 만들어서 Security 설정에 등록해주어야 한다. 그래야 Security 에서 UserDetailsService 를 호출할 수 있다.
 *
 * 단, Security 에 등록해줄 때 AuthenticationManager 와 함께 등록해주어야 한다. AuthenticationManager 를 통해서 로그인이 진행되기 때문이다.
 *
 * 참고로, AuthenticationManager 는 WebSecurityConfigurerAdapter 가 들고 있고, 그 녀석을 사용하면 된다.
 *
 * AuthenticationManager 는 AbstractAuthenticationProcessingFilter 또한 가지고 있으므로
 * UsernamePasswordAuthenticationFilter 를 상속받아서 사용하는 대신 AbstractAuthenticationProcessingFilter 를 상속받아서 사용해도 된다.
 *
 * @see org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
 * @see org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
 * @see org.springframework.security.authentication.AuthenticationManager
 * @see org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
 * @see com.banjjoknim.playground.jwt.config.security.JwtSecurityConfiguration
 */
class JwtAuthenticationFilter(
    private val authenticationManagerFromSecurityConfiguration: AuthenticationManager // authenticationManager 로 변수명을 지으면 이름이 겹쳐서 컴파일 에러가 발생하여 변수명 변경.
) : UsernamePasswordAuthenticationFilter() {

    /**
     * 기존의 /login URL로 요청을 하면 로그인 시도를 위해 호출되는 함수이다.
     *
     * 추상 메서드로, AbstractAuthenticationProcessingFilter 에 포함되어 있으며,
     * AbstractAuthenticationProcessingFilter 를 상속받은 UsernamePasswordAuthenticationFilter, OAuth2LoginAuthenticationFilter 등이 구현하고 있다.
     *
     * AbstractAuthenticationProcessingFilter#doFilter(HttpServletRequest, HttpServletResponse) 에서 내부적으로 호출하고 있다.
     *
     * /login URL로 요청을 하면 UsernamePasswordAuthenticationFilter 가 해당 요청을 낚아채서 아래의 함수가 자동으로 실행된다.
     *
     * 따라서 구현해줘야 하는 것들은 아래와 같다.
     *
     * 1. username & password 를 받는다.
     * 2. 포함하고 있는 AuthenticationManager로 정상인지 로그인 시도를 한다.
     * 3. 로그인 시도를 하면 우리가 만든 PrincipalDetailsService#loadUserByUsername(String) 이 호출된다.
     *     - 데이터베이스로부터 일치하는 id, password 가 있는지 검사한다.
     *     - 로직이 정상적으로 완료되면 로그인을 시도한 유저의 정보를 담고 있는 Authentication 객체가 반환된다.
     * 4. 정상적으로 로직이 수행되어서 Authentication 객체가 리턴되면 해당 객체를 리턴해서 Spring Security 세션에 담는다.
     *     - 만약 세션에 Authentication 객체를 담지 않으면 Spring Security 에서의 권한관리가 동작하지 않는다.
     *     - Spring Security 는 세션에 Authentication 객체가 존재해야 권한관리를 해준다.
     *     - 만약 Spring Security 를 통해 권한관리를 안할거면 Authentication 객체를 세션에 담을 필요가 없다.
     * 5. 마지막으로 JWT 토큰을 만들어서 응답으로 돌려주면 된다(선택-successfulAuthentication() 을 override 해서 구현해줘도 됨).
     *
     * @see org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
     * @see org.springframework.security.authentication.AuthenticationManager
     * @see org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
     * @see org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter
     * @see com.banjjoknim.playground.jwt.config.security.PrincipalDetailsService
     * @see org.springframework.security.authentication.UsernamePasswordAuthenticationToken
     */
    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication {
        println("JwtAuthenticationFilter : 로그인 시도중")

//        println(request.inputStream) // username, password 가 담겨있다. request의 inputStream 은 Request 당 1회만 호출할 수 있으므로 주석처리.

//        val bufferedReader = request.reader
//        bufferedReader.lineSequence().forEach(::println) // request 데이터 확인

        val objectMapper = ObjectMapper().registerKotlinModule()
        val jwtUser = objectMapper.readValue(request.inputStream, JwtUser::class.java)
//        println(jwtUser)

        // 로그인 시도를 위해서 id, password 를 이용해서 직접 토큰을 만든다.
        // UsernamePasswordAuthenticationFilter#attemptAuthentication() 함수를 참고하도록 한다.
        // 즉, 우리가 직접 토큰을 만들어서 호출을 대신 수행해준다고 보면 될듯.
        val authenticationToken = UsernamePasswordAuthenticationToken(jwtUser.username, jwtUser.password)

        // 직접 만든 토큰을 인자로 넣고 AuthenticationManager#authenticate(Token) 을 호출하면
        // 내부적으로 로직이 돌면서 우리가 만든 PrincipalDetailsService#loadUserByUsername(String) 함수가 호출된다.
        // 그 결과로 User의 로그인 정보가 담긴 Authentication 객체를 얻을 수 있다.
        // Authentication 객체를 얻어다는 것은 데이터베이스에 있는 username 과 password 가 일치한다는 뜻이다.
        val authentication = authenticationManagerFromSecurityConfiguration.authenticate(authenticationToken)

        // 위 처럼 인증이 정상적으로 진행되어 Authentication 객체를 얻었다면
        // 아래처럼 Authentication 객체 내부의 PrincipalDetails 객체를 꺼내어 정보 확인이 가능하다.
        // 즉, 로그인이 정상적으로 되었다는 뜻이다.
        val principalDetails = authentication.principal as PrincipalDetails
        println("로그인 완료됨: ${principalDetails.user.username}")

//        return super.attemptAuthentication(request, response)

        // 로그인이 정상적으로 되었으므로 Authentication 객체를 Session 영역에 저장해야 한다.
        // Authentication 객체를 Session 영역에 저장하는 방법은 Authentication 객체를 return 해주는 것이다.
        // Authentication 객체를 return 해주면 Spring Security 가 자동으로 Authentication 객체를 Security Session 영역에 저장해준다.
        // Authentication 객체를 return 해서 Session 영역에 저장하는 이유는 권한 관리를 Spring Security 가 대신 해주어 관리가 편해지기 때문이다(원하지 않으면 Session 영역에 저장을 안하면 된다).
        // JWT 토큰을 사용한다면 Session 영역을 굳이 만들 필요가 없다. 다만, 권한 처리 때문에 Session 에 저장하는 것이다.
        // 기본적으로 Authentication 객체를 세션에 저장하는 로직은 AbstractAuthenticationProcessingFilter#successfulAuthentication() 함수에서 수행하고 있다.
        // Security Session 영역에 저장되는 정보들은 잠시 사용하고 응답이 끝났을 때 버리면 된다(세션 정보는 시간이 지나면 자동으로 사라진다).
        return authentication
    }

    /**
     * 본 함수는 attemptAuthentication() 함수를 통한 인증이 성공적으로 이루어져서 Authentication 객체를 얻을 수 있는 경우 그 다음으로 호출되는 함수다.
     *
     * AbstractAuthenticationProcessingFilter#successfulAuthentication() 함수에는 Security Session 영역에 Authentication 객체를 저장하는 로직이 포함되어 있다.
     *
     * 자세한 내용은 AbstractAuthenticationProcessingFilter#successfulAuthentication() 에 달린 javadoc 을 참고하도록 하자.
     *
     * 따라서, 여기서 JWT 토큰을 만들어서 Request 요청한 사용자에게 JWT 토큰을 응답해주면 된다(선택사항).
     *
     * 기존의 username, password 방식의 로그인을 사용할 경우, 스프링 시큐리티는 세션이 유효할 경우 인증이 필요한 페이지의 권한을 체크해서 알아서 인증이 필요한 페이지로 이동시켜준다.
     *
     * 기존의 서버는 세션의 유효성 검증을 할 때 Session.getAttribute("세션값 확인") 와 같은 방식으로 확인하기만 하면 된다.
     *
     * 하지만 토큰 방식을 사용하게되면, 세션ID도 만들지 않고, 쿠키도 응답에 제공해주지 않는다(세션에 데이터를 담아둘게 아니다).
     *
     * 대신, JWT 토큰을 생성하고 클라이언트쪽으로 JWT 토큰을 응답해준다. 따라서 요청할 때마다 JWT 토큰을 가지고 요청해야 한다.
     *
     * 따라서 서버는 JWT 토큰이 유효한지를 판단해야하는데, 이 부분에 대한 필터를 따로 만들어주어야 한다.
     */
    override fun successfulAuthentication(
        request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain,
        authResult: Authentication
    ) {
        val principalDetails = authResult.principal as PrincipalDetails
        println("successfulAuthentication 실행됨 : ${principalDetails.user.username}의 인증이 완료되었다는 뜻.")
        val jwtExpireSecond = 1000 * 60 * 10

        // RSA 방식은 아니다. Hash 암호 방식.
        val jwtToken = JWT.create()
            .withSubject("banjjoknim 토큰")
            .withExpiresAt(Date(System.currentTimeMillis() + jwtExpireSecond))
            .withClaim("id", principalDetails.user.id)
            .withClaim("username", principalDetails.user.username)
            .sign(Algorithm.HMAC512("banjjoknim")) // 서버에서만 알고 있는 비밀 키를 사용한다.

        response.addHeader(HttpHeaders.AUTHORIZATION, "Bearer $jwtToken")

        super.successfulAuthentication(request, response, chain, authResult)
    }
}
