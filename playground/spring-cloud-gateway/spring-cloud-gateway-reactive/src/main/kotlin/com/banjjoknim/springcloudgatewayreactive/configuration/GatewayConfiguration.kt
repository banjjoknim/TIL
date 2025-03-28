package com.banjjoknim.springcloudgatewayreactive.configuration

import org.slf4j.LoggerFactory
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

/**
 * @see org.springframework.cloud.gateway.config.GatewayAutoConfiguration
 */
@Configuration
class GatewayConfiguration {

    /**
     * @see org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
     * @see org.springframework.cloud.gateway.filter.factory.GatewayFilterFactory
     */
    class AuthorizationHeaderGatewayFilterFactory :
        AbstractGatewayFilterFactory<GatewayConfiguration>(GatewayConfiguration::class.java) {
        companion object {
            private const val SECRET_HEADER_NAME = "Secret"
        }

        private val logger = LoggerFactory.getLogger(this::class.java)

        override fun apply(config: GatewayConfiguration): GatewayFilter {
            return GatewayFilter { exchange: ServerWebExchange, chain: GatewayFilterChain ->
                val request: ServerHttpRequest = exchange.request
                val response: ServerHttpResponse = exchange.response
                val authorizedExchange = exchange.toAuthorizedExchange(request)

                chain.filter(authorizedExchange).then(Mono.fromRunnable {
                    logger.info("request.remoteAddress: ${request.remoteAddress}, request.path: ${request.uri.path}, response.statusCode: ${response.statusCode}")
                })
            }
        }

        /**
         * 인증 정보를 검사하고, 인증 필터 처리에 사용할 ServerWebExchange 생성한다.
         *
         * @see org.springframework.web.server.ServerWebExchange
         */
        private fun ServerWebExchange.toAuthorizedExchange(request: ServerHttpRequest): ServerWebExchange {
            val authorization = request.headers[HttpHeaders.AUTHORIZATION]?.first()
                ?: throw RuntimeException("인증 정보가 존재하지 않습니다.")
            val authorizedRequest = request.mutate()
                .headers {
                    /**
                     * 수신한 요청에 이미 존재하는 헤더이므로 굳이 작성할 필요는 없으나, 예시용으로 작성하였음.
                     * `add()` 사용시 라우팅되는 서버에는 중복으로 값이 부여된 Authorization 헤더가 전달됨에 유의할 것.
                     */
                    it.set(HttpHeaders.AUTHORIZATION, authorization)
                    it.set(SECRET_HEADER_NAME, authorization.toSecret())
                }
                .build()
            val authorizedExchange = this.mutate().request(authorizedRequest).build()
            return authorizedExchange
        }

        private fun String.toSecret(): String {
            return "Secret-$this" // 필요할 경우 원하는 작업을 수행하게끔 구현한다.
        }
    }

    /**
     * `NameUtils.normalizeFilterFactoryName(GatewayConfiguration.AuthorizationHeaderGatewayFilterFactory::class.java)`의 결과 값이 `RouteLocator`에 등록되는 GatewayFilter Map의 Key로 등록된다.
     * (e.g. AuthorizationHeaderGatewayFilterFactory -> AuthorizationHeader) 만약 application.yaml에 커스텀 필터를 등록하고 싶다면 이 값을 사용해야 한다.
     * `RouteDefinitionRouteLocator.loadGatewayFilters` 에서 해당 Key 값으로 필터를 찾아오기 때문이다.
     *
     * @see org.springframework.cloud.gateway.config.GatewayAutoConfiguration.routeDefinitionRouteLocator
     * @see org.springframework.cloud.gateway.filter.factory.GatewayFilterFactory.name - Key 생성 함수. 오버라이드 한 결과값이 GatewayFilter Map의 Key로 사용된다.
     * @see org.springframework.cloud.gateway.support.NameUtils.normalizeFilterFactoryName
     * @see org.springframework.cloud.gateway.route.RouteDefinitionRouteLocator
     * @see org.springframework.cloud.gateway.route.RouteDefinitionRouteLocator.loadGatewayFilters
     */
    @Bean
    fun authorizationHeaderGatewayFilterFactory(): AuthorizationHeaderGatewayFilterFactory {
        return AuthorizationHeaderGatewayFilterFactory()
    }
}
