package com.banjjoknim.playground.config.filter

import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse

/**
 * @see javax.servlet.Filter
 * @see com.banjjoknim.playground.config.security.JwtSecurityConfiguration
 */
class CustomFilter1 : Filter {
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        println("필터1")
        chain.doFilter(request, response) // 필터체인에 request, response 를 등록해주어야 한다. 그렇지 않으면 현재 필터가 진행되고 이 이후의 필터들은 더이상 동작하지 않게 된다.
    }
}

class CustomFilter2 : Filter {
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        println("필터2")
        chain.doFilter(request, response) // 필터체인에 request, response 를 등록해주어야 한다. 그렇지 않으면 현재 필터가 진행되고 이 이후의 필터들은 더이상 동작하지 않게 된다.
    }
}

class CustomFilter3 : Filter {
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        println("필터3")
        chain.doFilter(request, response) // 필터체인에 request, response 를 등록해주어야 한다. 그렇지 않으면 현재 필터가 진행되고 이 이후의 필터들은 더이상 동작하지 않게 된다.
    }
}
