package com.banjjoknim.playground.jwt.config.security

import com.banjjoknim.playground.jwt.config.filter.CustomAuthorizationFilter
import com.banjjoknim.playground.jwt.config.filter.CustomFilter3
import com.banjjoknim.playground.jwt.config.filter.JwtAuthenticationFilter
import com.banjjoknim.playground.jwt.config.filter.JwtAuthorizationFilter
import com.banjjoknim.playground.jwt.domain.user.JwtUser
import com.banjjoknim.playground.jwt.domain.user.JwtUserRepository
import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.context.SecurityContextPersistenceFilter
import org.springframework.stereotype.Service
import org.springframework.web.filter.CorsFilter

/**
 * SessionCreationPolicy 설정을 STATELESS 로 사용하면 세션을 만드는 방식을 사용하지 않게 된다.
 *
 * 토큰 기반(JWT)을 사용한 설정에서는 기본이며, 상태가 없는 서버를 만들 때 사용한다.
 *
 * ```kotlin
 * Spring Filter Chain 에 존재하는 BasicAuthenticationFilter의 동작 이전에 MySecurityFilter1 을 추가한다. 하지만 반드시 SecurityFilter 에 Filter 를 추가할 필요는 없다.
 *
 * http.addFilterBefore(MySecurityFilter1(), BasicAuthenticationFilter::class.java)
 * ```
 * @see org.springframework.security.config.http.SessionCreationPolicy
 */
@EnableWebSecurity // 시큐리티 활성화 -> 시큐리티 설정을 기본 스프링 필터체인에 등록한다.
class JwtSecurityConfiguration(
    private val corsFilter: CorsFilter, // CorsConfiguration 에서 Bean 으로 등록해준 CorsFilter 를 Spring 으로부터 DI 받는다.
    private val jwtUserRepository: JwtUserRepository
) : WebSecurityConfigurerAdapter() {

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    override fun configure(http: HttpSecurity) {
        // Spring Filter Chain 에 존재하는 BasicAuthenticationFilter의 동작 이전에 MySecurityFilter1 을 추가한다. 하지만 반드시 SecurityFilter 에 Filter 를 추가할 필요는 없다.
//        http.addFilterBefore(MySecurityFilter1(), BasicAuthenticationFilter::class.java)

        // 우리가 원하는 위치에 Filter 를 등록한다. 만약 Spring Security Filter 보다도 먼저 실행되게 하고 싶다면 SecurityContextPersistenceFilter 보다 먼저 실행되도록 아래처럼 등록해주면 된다.
        http.addFilterBefore(CustomFilter3(), SecurityContextPersistenceFilter::class.java)
        http.addFilterBefore(CustomAuthorizationFilter(), SecurityContextPersistenceFilter::class.java)

        http.csrf().disable()
        // 기본적으로 웹은 STATELESS 인데, STATEFUL 처럼 쓰기 위해서 세션과 쿠키를 만든다. 이때, 그걸(세션과 쿠키) 사용하지 않도록 설정하는 것이다.
        http.sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 세션을 사용하지 않겠다는 설정. 토큰 기반에서는 기본 설정이다. 상태가 없는 서버를 만든다.
            .and()
            .addFilter(corsFilter) // filter 에 Bean 으로 등록해준 CorsFilter 를 추가한다. 따라서 모든 요청은 추가된 CorsFilter 를 거치게 된다. 이렇게 하면 내 서버는 CORS 정책에서 벗어날 수 있다(Cross-origin 요청이 와도 다 허용될 것이다).
            .formLogin().disable() // Form 태그 방식 로그인을 사용하지 않는다.
            .httpBasic().disable() // HttpBasic 방식 로그인을 사용하지 않는다.

            // formLogin().disable() 로 인해 직접 만든 필터를 등록해주어야 Security 가 UserDetailsService 를 호출할 수 있다.
            // 이때, WebSecurityConfigurerAdapter 에 포함되어 있는 AuthenticationManager 라는 녀석과 함께 등록해주어야 한다.
            .addFilter(JwtAuthenticationFilter(super.authenticationManager()))
            .addFilter(JwtAuthorizationFilter(super.authenticationManager(), jwtUserRepository))

            .authorizeRequests()
            .antMatchers("/api/v1/user/**").hasAnyRole("USER", "MANAGER", "ADMIN")
            .antMatchers("/api/v1/manager/**").hasAnyRole("MANAGER", "ADMIN")
            .antMatchers("/api/v1/admin/**").hasAnyRole("ADMIN")
            .anyRequest().permitAll()
    }
}

class PrincipalDetails(
    val user: JwtUser
) : UserDetails {
    override fun getAuthorities(): Collection<GrantedAuthority> {
        val authorities = mutableListOf<GrantedAuthority>()
        for (role in user.getRoles()) {
            authorities.add(GrantedAuthority { role })
//            authorities + GrantedAuthority { role }
        }
        return authorities
    }

    override fun getPassword(): String {
        return user.password
    }

    override fun getUsername(): String {
        return user.username
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }
}

/**
 * 원래는 http://localhost:8080/login 요청이 올 때 동작한다(Spring Security 의 기본 로그인 url).
 *
 * 하지만 우리는 formLogin().disable() 했기 때문에 위 url로 요청이 들어올 때 직접 PrincipalDetailsService 를 호출할 Filter 를 만들어줘야 한다.
 */
@Service
class PrincipalDetailsService(
    private val jwtUserRepository: JwtUserRepository
) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        val jwtUser = jwtUserRepository.findByUsername(username)
            ?: throw UsernameNotFoundException("can not found user by username. username: $username")
        return PrincipalDetails(jwtUser)
    }
}
