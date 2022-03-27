package com.banjjoknim.playground.jwt.config.filter

import org.springframework.http.HttpMethod
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * @see javax.servlet.Filter
 * @see com.banjjoknim.playground.jwt.config.security.JwtSecurityConfiguration
 */
class CustomFilter1 : Filter {
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        println("필터1")
        chain.doFilter(request, response) // request, response 와 함께 doFilter 를 호출해주어야 한다. 그렇지 않으면 현재 필터가 진행되고나서 이 이후의 필터들은 더이상 동작하지 않게 된다.
    }
}

class CustomFilter2 : Filter {
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        println("필터2")
        chain.doFilter(request, response) // request, response 와 함께 doFilter 를 호출해주어야 한다. 그렇지 않으면 현재 필터가 진행되고나서 이 이후의 필터들은 더이상 동작하지 않게 된다.
    }
}

class CustomFilter3 : Filter {
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        println("필터3")
        chain.doFilter(request, response) // request, response 와 함께 doFilter 를 호출해주어야 한다. 그렇지 않으면 현재 필터가 진행되고나서 이 이후의 필터들은 더이상 동작하지 않게 된다.
    }
}

class AuthorizationFilter : Filter {
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val httpServletRequest = request as HttpServletRequest
        val httpServletResponse = response as HttpServletResponse

        if (httpServletRequest.method == HttpMethod.POST.name) {
            println("POST 요청됨")
            val headerAuthorization = httpServletRequest.getHeader("Authorization")
            println(headerAuthorization)

            // banjjoknim 이 정상적인 토큰이라고 가정한다.
            // 따라서 banjjoknim 이라는 토큰을 id, password 가 정상적으로 입력되었을 때 토큰을 만들어주고 응답에 실어보낸다.
            // 클라이언트는 요청할 때마다 해당 토큰을 Header 중 Authorization 의 값으로 포함하여 요청한다.
            // 따라서 클라이언트가 요청할 때 Authorization 의 값으로 포함되어 온 토큰이 우리 서버에서 만든 토큰이 맞는지 검증만 하면 된다(RSA, HS256).
            if (headerAuthorization == "banjjoknim") {
                chain.doFilter(httpServletRequest, httpServletResponse) // request, response 와 함께 doFilter 를 호출해주어야 한다. 그렇지 않으면 현재 필터가 진행되고나서 이 이후의 필터들은 더이상 동작하지 않게 된다.
            } else {
                val printWriter = httpServletResponse.writer
                printWriter.println("인증안됨") // 응답에 '인증안됨' 을 쓴다.
            }
        }
    }
}
