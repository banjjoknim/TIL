package com.banjjoknim.playground.jwt.config.filter

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.banjjoknim.playground.jwt.config.security.JwtSecurityProperties
import com.banjjoknim.playground.jwt.config.security.PrincipalDetails
import com.banjjoknim.playground.jwt.domain.user.JwtUserRepository
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 로그인을 통해 발행된 JWT 토큰의 전자서명을 이용해서 개인정보에 접근할 수 있게 하기 위한 커스텀 필터.
 *
 * Security 가 가진 Filter 중에서 BasicAuthenticationFilter 라는 것이 있다.
 *
 * 권한이나 인증이 필요한 특정 URL 을 요청했을 때 위 BasicAuthenticationFilter 를 무조건 거치게 되어 있다.
 *
 * 만약 권한이나 인증이 필요한 주소가 아니라면 위 필터를 거치지 않는다. 따라서 BasicAuthenticationFilter 를 상속받아서 필요한 로직을 구현해준다.
 *
 * @see org.springframework.security.web.authentication.www.BasicAuthenticationFilter
 */
class JwtAuthorizationFilter(
    private val authenticationManagerFromSecurityConfiguration: AuthenticationManager,
    private val jwtUserRepository: JwtUserRepository
) :
    BasicAuthenticationFilter(authenticationManagerFromSecurityConfiguration) {
    /**
     * 인증이나 권한이 필요한 URL 요청이 있을 때 BasicAuthenticationFilter#doFilterInternal() 함수를 거치게 된다.
     *
     * 따라서 이 함수에서 Header 의 JWT 토큰에 대한 처리를 진행해주면 된다.
     */
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        println("인증이나 권한이 필요한 주소가 요청됨.")

        val jwtHeader = request.getHeader(HttpHeaders.AUTHORIZATION)
        println("jwtHeader: $jwtHeader")

        // Header의 JWT 토큰이 정상적인지 검사한다.
        if (jwtHeader == null || !jwtHeader.startsWith("Bearer")) {
            chain.doFilter(request, response)
            return
        }

        // JWT 토큰을 검증해서 정상적인 사용자인지 검사한다.
        val jwtToken = jwtHeader.replace(JwtSecurityProperties.BEARER_TOKEN_PREFIX, "")
        val jwtVerifier = JWT.require(Algorithm.HMAC512(JwtSecurityProperties.SECRET)).build()
        val username = jwtVerifier.verify(jwtToken).getClaim("username").asString()

        // username 이 null 이 아니라면 서명이 정상적으로 된 것이다.
        if (username != null) {
            println("username 정상. username: $username")
            val jwtUser = jwtUserRepository.findByUsername(username)
                ?: throw IllegalArgumentException("can not found jwtUser. username: $username")
            val principalDetails = PrincipalDetails(jwtUser)

            // Authentication 객체를 강제로 만든다. password 의 경우는 null 을 사용해도 무방하다. 우리가 직접 Authentication 객체를 만들기 때문이다.
            // 이게 가능한 이유는, username 이 null 이 아니기 때문인데, username 이 null 이 아니라는 것은 인증이 정상적으로 진행되었다는 뜻이기 때문.
            // 단, 이렇게 Authentication 객체를 만들 때는 권한을 직접 알려주어야(지정해주어야) 한다.
            // 즉, JWT 토큰 서명을 통해서 서명이 정상이면 Authentication 객체를 만들어준다.
            val authenticationToken =
                UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.authorities)

            // SecurityContext 는 Security 의 세션 공간이다.
            val securityContext = SecurityContextHolder.getContext()

            // 강제로 Security 의 세션에 접근하여 Authentication 객체를 저장한다. 만약 세션에 저장이 제대로 되면 Authentication 객체를 Controller 단에서 가져올 수 있다.
            securityContext.authentication = authenticationToken
        }

        //        super.doFilterInternal(request, response, chain) // 아래의 chain.doFilter(request, response) 에서도 응답을 하기 때문에 응답을 총 2번하게 되어 오류가 나므로 지워줘야 한다.
        chain.doFilter(request, response)
    }
}

