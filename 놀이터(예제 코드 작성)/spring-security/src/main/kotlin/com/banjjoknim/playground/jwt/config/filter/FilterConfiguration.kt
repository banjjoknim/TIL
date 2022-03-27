package com.banjjoknim.playground.jwt.config.filter

import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * 기본 설정시 Spring Security 는 일련의 Servlet Filter Chain(FilterChainProxy 라는 클래스로 등록되어 있다. 하나의 Filter 로 등록 되어있지만 내부적으로는 여러개의 Filter 가 동작하고 있다) 을 자동으로 구성한다(web tier 에 있는 Spring Security 는 Servlet Filter 에 기반을 두고 있다).
 *
 * 일반적인 웹 환경에서 브라우저가 서버에게 요청을 보내게 되면, DispatcherServlet(Controller)가 요청을 받기 이전에 많은 ServletFilter(서블릿 필터)를 거치게 된다.
 *
 * Spring Security 역시 Servlet Filter 로써 작동하며, 인증 또는 권한과 관련한 처리를 진행하게 된다.
 *
 * 본래 Servlet Filter 는 WAS(Web Application Server)에서 담당하는데 Spring 은 이 Servlet Filter 들을 직접 관리하기 위해서 DelegatingFilterProxy 를 web.xml 에 설정한다.
 *
 * 이를 통해 Spring 에서 설정된 Servlet Filter Bean 객체를 거치게 된다.
 *
 * 여기서는 스프링 시큐리티 필터체인에 필터를 추가하는 대신, 직접 필터를 만들어서 사용한다.
 *
 * 굳이 Security Filter Chain 에 필터를 추가할 필요가 없고, 이렇게 따로 만들어서 사용해도 된다.
 *
 * Filter 를 Bean 으로 등록해놓으면, 요청이 들어왔을 때 등록된 Filter 가 동작하게 된다.
 *
 * 이때, Security Filter Chain 이 우리가 직접 만든 Filter 보다 먼저 동작한다.
 *
 * 만약 우리가 만든 Filter 를 원하는 위치에서 동작하도록 하고 싶다면 원하는 위치에 Filter 를 추가하면 된다.
 *
 * 이때, Security Filter Chain 의 순서는 com.banjjoknim.playground.config.filter.SecurityFilterChain.png 이미지를 참고하자.
 *
 * - Filter type의 Bean에는 @Order 어노테이션으로 순서를 정할 수 있다.
 * - FilterRegistrationBean을 이용하여 순서를 정할 수 있다
 *
 * ```kotlin
 * http.addFilterBefore(MySecurityFilter3(), SecurityContextPersistenceFilter::class.java)
 * ```
 *
 * @see org.springframework.boot.web.servlet.FilterRegistrationBean
 * @see com.banjjoknim.playground.config.security.JwtSecurityConfiguration
 * @see org.springframework.web.filter.DelegatingFilterProxy
 * @see org.springframework.security.web.FilterChainProxy
 */
@Configuration
class FilterConfiguration {

    @Bean
    fun customFilter1(): FilterRegistrationBean<CustomFilter1> {
        val bean = FilterRegistrationBean(CustomFilter1())
        bean.addUrlPatterns("/*") // 모든 요청에 대해 필터가 동작하도록 설정한다.
        bean.order = 0 // 필터의 순서를 정할 수 있는데, 낮은 번호가 필터중에서 가장 먼저 실행된다.
        return bean
    }

    @Bean
    fun customFilter2(): FilterRegistrationBean<CustomFilter2> {
        val bean = FilterRegistrationBean(CustomFilter2())
        bean.addUrlPatterns("/*") // 모든 요청에 대해 필터가 동작하도록 설정한다.
        bean.order = 1 // 필터의 순서를 정할 수 있는데, 낮은 번호가 필터중에서 가장 먼저 실행된다.
        return bean
    }
}
