package com.banjjoknim.playground.config.security

import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.web.filter.CorsFilter

/**
 * SessionCreationPolicy 설정을 STATELESS 로 사용하면 세션을 만드는 방식을 사용하지 않게 된다.
 *
 * 토큰 기반(JWT)을 사용한 설정에서는 기본이며, 상태가 없는 서버를 만들 때 사용한다.
 *
 * @see org.springframework.security.config.http.SessionCreationPolicy
 */
@EnableWebSecurity // 시큐리티 활성화 -> 시큐리티 설정을 기본 스프링 필터체인에 등록한다.
class JwtSecurityConfiguration(
    private val corsFilter: CorsFilter // CorsConfiguration 에서 Bean 으로 등록해준 CorsFilter 를 Spring 으로부터 DI 받는다.
) : WebSecurityConfigurerAdapter() {
    override fun configure(http: HttpSecurity) {
        http.csrf().disable()
        // 기본적으로 웹은 STATELESS 인데, STATEFUL 처럼 쓰기 위해서 세션과 쿠키를 만든다. 이때, 그걸(세션과 쿠키) 사용하지 않도록 설정하는 것이다.
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 세션을 사용하지 않겠다는 설정. 토큰 기반에서는 기본 설정이다. 상태가 없는 서버를 만든다.
            .and()
            .addFilter(corsFilter) // filter 에 Bean 으로 등록해준 CorsFilter 를 추가한다. 따라서 모든 요청은 추가된 CorsFilter 를 거치게 된다. 이렇게 하면 내 서버는 CORS 정책에서 벗어날 수 있다(Cross-origin 요청이 와도 다 허용될 것이다).
            .formLogin().disable() // Form 태그 방식 로그인을 사용하지 않는다.
            .httpBasic().disable() // HttpBasic 방식 로그인을 사용하지 않는다.
            .authorizeRequests()
            .antMatchers("/api/v1/user/**").hasAnyRole("USER", "MANAGER", "ADMIN")
            .antMatchers("/api/v1/manager/**").hasAnyRole("MANAGER", "ADMIN")
            .antMatchers("/api/v1/admin/**").hasAnyRole("ADMIN")
            .anyRequest().permitAll()
    }
}
