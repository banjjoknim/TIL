package com.banjjoknim.playground.config

import org.springframework.context.annotation.Bean
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

@EnableWebSecurity // 스프링 시큐리티 필터가 스프링 필터체인에 등록되도록 해준다.
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true) // 스프링 시큐리티 관련 특정 어노테이션에 대한 활성화 설정을 할 수 있다.
class SecurityConfiguration : WebSecurityConfigurerAdapter() {

    @Bean // passwordEncoder() 메서드에서 리턴해주는 PasswordEncoder 를 스프링 빈으로 등록한다.
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    override fun configure(http: HttpSecurity) {
        http.csrf().disable()
        http.authorizeRequests() // 인증만 되면 들어갈 수 있는 주소 설정
            .antMatchers("/user/**").hasRole("USER")
            .antMatchers("/manager/**").hasAnyRole("MANAGER", "ADMIN")
            .antMatchers("/admin/**").hasRole("ADMIN")
            .anyRequest().permitAll()
            .and()
            .formLogin()
            .loginPage("/loginPage")
            .loginProcessingUrl("/login") // /login url이 호출되면 Security 가 요청을 낚아채서 대신 로그인을 진행해준다.
            .defaultSuccessUrl("/")
    }
}
