package com.banjjoknim.playground.jwt.config.filter

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter

/**
 * ```kotlin
 * corsConfiguration.allowCredentials = true
 *
 * user credentials 을 허용한다. 즉, 서버가 응답을 할 때 json 을 자바스크립트에서 처리할 수 있게(응답을 받을 수 있게) 할건지 말건지를 설정한다.
 * 만약 false 로 설정할 경우, 자바스크립트로 어떤 요청을 했을 때 서버로부터 응답이 오지 않는다.
 * ```
 *
 * ```kotlin
 * corsConfiguration.addAllowedOrigin("*")
 *
 * 모든 IP에 응답을 허용한다는 설정.
 * ```
 *
 * ```kotlin
 * corsConfiguration.addAllowedHeader("*")
 *
 * 모든 Header에 응답을 허용한다는 설정.
 * ```
 *
 * ```kotlin
 * corsConfiguration.addAllowedMethod("*")
 *
 * 모든 HTTP METHOD 요청을 허용한다는 설정.
 * ```
 *
 * CorsFilter 대신 `@CrossOrigin` 어노테이션은 사용하더라도 Security 인증이 필요한 요청은 전부 거부된다.
 *
 * `@CrossOrigin` 어노테이션은 인증이 필요하지 않은 요청만 허용해준다. 예를 들어, 로그인을 해야만 할 수 있는 요청들은 모두 거부된다.
 *
 * 인증이 필요한 경우는 CorsFilter 를 Security Filter 에 등록해주어야 하고, 인증이 필요 없는 경우는 `@CrossOrigin` 어노테이션을 사용할 수 있다.
 *
 * @see org.springframework.web.cors.CorsConfiguration
 * @see org.springframework.web.cors.UrlBasedCorsConfigurationSource
 * @see org.springframework.web.filter.CorsFilter
 * @see org.springframework.web.bind.annotation.CrossOrigin
 */
@Configuration
class CorsFilterConfiguration {

    /**
     * Spring 에서 관리하는 Bean 으로 등록한 CorsFilter 를 Security Filter 에 등록해주어야 한다.
     *
     * 단순히 Bean 으로만 등록해서는 동작하지 않는다.
     *
     * @see com.banjjoknim.playground.jwt.config.security.JwtSecurityConfiguration
     */
    @Bean
    fun corsFilter(): CorsFilter {
        val corsConfigurationSource = UrlBasedCorsConfigurationSource() // URL 을 이용한 CORS 설정을 담아두는 객체.
        val corsConfiguration = CorsConfiguration() // CORS 설정 객체.
        corsConfiguration.allowCredentials = true
        corsConfiguration.addAllowedOrigin("*")
        corsConfiguration.addAllowedHeader("*")
        corsConfiguration.addAllowedMethod("*")
        corsConfigurationSource.registerCorsConfiguration("/api/**", corsConfiguration) // corsSource 에 corsConfiguration 을 등록한다.
        return CorsFilter(corsConfigurationSource)
    }
}
