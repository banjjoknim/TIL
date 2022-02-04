package com.banjjoknim.playground.config

import com.banjjoknim.playground.domain.user.User
import com.banjjoknim.playground.domain.user.UserRepository
import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@EnableWebSecurity // 스프링 시큐리티 필터가 스프링 필터체인에 등록되도록 해준다.
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true) // 스프링 시큐리티 관련 특정 어노테이션에 대한 활성화 설정을 할 수 있다.
class SecurityConfiguration : WebSecurityConfigurerAdapter() {

    @Bean // passwordEncoder() 메서드에서 리턴해주는 PasswordEncoder 를 스프링 빈으로 등록한다.
    fun passwordEncoder(): PasswordEncoder { // Security 로 로그인을 하려면 비밀번호는 암호화되어 있어야 하므로 PasswordEncoder 가 필요하다.
        return BCryptPasswordEncoder()
    }

    override fun configure(http: HttpSecurity) {
        http.csrf().disable()
        http.authorizeRequests() // 인증만 되면 들어갈 수 있는 주소 설정
            .antMatchers("/user/**").hasRole("USER")
            .antMatchers("/manager/**").hasAnyRole("MANAGER", "ADMIN")
            .antMatchers("/admin/**").hasRole("ADMIN")
            .anyRequest().permitAll() // 위에서 명시한 주소 외에는 모든 접근을 허용한다.
            .and()
            .formLogin()
            .loginPage("/loginPage")
            .usernameParameter("username") // 기본적으로 인증을 위해 사용자를 찾을 때 username 을 사용하는데, 이에 사용되는 파라미터 이름을 바꿔주고 싶을때 사용한다.
            .loginProcessingUrl("/login") // /login url이 호출되면 Security 가 요청을 낚아채서 대신 로그인을 진행해준다.
            .defaultSuccessUrl("/") // loginPage 의 url을 통해서 로그인을 하면 / 로 보내줄건데, 특정 페이지로 요청해서 로그인하게 되면 그 페이지를 그대로 보여주겠다는 의미.
    }
}

/**
 * 시큐리티가 /login 주소 요청이 오면 낚아채서 로그인을 진행해준다.
 *
 * 로그인 진행이 완료되면 시큐리티 session을 만들어준다 (Security ContextHolder)
 *
 * Security ContextHolder에 들어갈 수 있는 객체 타입은 Authentication 이다.
 *
 * Authentication 객체 안에는 User 정보가 있어야 한다. 이때 User 객체 타입은 UserDetails 이다.
 *
 * Security Session -> Authentication -> UserDetails
 *
 * Security ContextHolder 내부에 Authentication이 있고, 그 속에 UserDetails가 있는 형태.
 *
 * ```kotlin
 * ex) 1. SecurityContextHolder.getContext().authentication.details
 *     2. SecurityContextHolder.getContext().authentication.principal
 *     3. SecurityContextHolder.getContext().authentication.authorities
 *     4. ...
 * ```
 */
class PrincipalDetails(private val user: User) : UserDetails {
    override fun getAuthorities(): Collection<out GrantedAuthority> {
        return listOf(GrantedAuthority { user.role })
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
 * 스프링 시큐리티 설정에서 loginProcessingUrl("/login") 설정을 해주었기 때문에
 *
 * /login url로 요청이 오면 자동으로 시큐리티가 로그인 과정을 낚아챈다.
 *
 * 이때 UserDetailsService 타입으로 등록되어 있는 빈을 찾아서 해당 빈에 정의된 loadUserByUsername() 을 실행한다.
 *
 * @see DaoAuthenticationProvider
 * @see AbstractUserDetailsAuthenticationProvider
 */
@Service
class PrincipalDetailService(private val userRepository: UserRepository) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        val user = (userRepository.findByUsername(username)
            ?: throw UsernameNotFoundException("can not found user by username. username: $username"))
        return PrincipalDetails(user)
    }
}
