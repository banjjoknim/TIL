package com.banjjoknim.playground.config

import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter

@EnableWebSecurity
class SecurityConfiguration : WebSecurityConfigurerAdapter() {
    override fun configure(http: HttpSecurity) {
        http.csrf().disable()
        http.authorizeRequests()
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
