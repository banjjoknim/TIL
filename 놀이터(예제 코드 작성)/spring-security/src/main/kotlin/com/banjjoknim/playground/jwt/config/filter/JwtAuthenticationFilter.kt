package com.banjjoknim.playground.jwt.config.filter

import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
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
class JwtAuthenticationFilter(authenticationManager: AuthenticationManager) : UsernamePasswordAuthenticationFilter() {

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
     * 로그인시 Filter의 동작 순서 및 구현해줘야 하는 것들은 아래와 같다.
     *
     * 1. username & password 를 받는다.
     * 2. 포함하고 있는 AuthenticationManager로 정상인지 로그인 시도를 한다.
     * 3. 로그인 시도를 하면 우리가 만든 PrincipalDetailsService#loadUserByUsername(String) 이 호출된다.
     * 4. 정상적으로 로직이 수행되어서 PrincipalDetails 가 리턴되면 해당 PrincipalDetails 를 세션에 담는다.
     *     - 만약 세션에 PrincipalDetails 를 담지 않으면 Spring Security 에서 권한관리가 동작하지 않는다.
     *     - Spring Security 는 세션에 PrincipalDetails 객체가 존재해야 권한관리를 해준다.
     *     - 굳이 권한관리를 안할거면 PrincipalDetails 객체를 세션에 담을 필요가 없다.
     * 5. 마지막으로 JWT 토큰을 만들어서 응답해준다.
     *
     * @see org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
     * @see org.springframework.security.authentication.AuthenticationManager
     * @see org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
     * @see org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter
     * @see com.banjjoknim.playground.jwt.config.security.PrincipalDetailsService
     */
    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication {
        println("JwtAuthenticationFilter : 로그인 시도중")
        return super.attemptAuthentication(request, response)
    }
}
