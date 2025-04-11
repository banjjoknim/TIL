package com.banjjoknim.springcloudgatewaymvc.configuration

import org.slf4j.LoggerFactory
import org.springframework.cloud.gateway.server.mvc.common.Shortcut
import org.springframework.cloud.gateway.server.mvc.filter.FilterSupplier
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.web.servlet.function.HandlerFilterFunction
import org.springframework.web.servlet.function.ServerRequest
import org.springframework.web.servlet.function.ServerResponse
import java.lang.reflect.Method

/**
 * `Spring Cloud Gateway MVC`는 `@Configuration` 어노테이션이 없어도 동작한다.
 *
 * `Spring Cloud Gateway MVC`는 애초에 필터를 Bean으로 인식해서 동작시키는 구조가 아니라, `FilterSupplier`를 통해 정적 메서드를 리플렉션으로 직접 탐색해서 사용하는 구조이기 때문.
 *
 * `resources/META-INF/spring.factories` 참조.
 *
 * @see org.springframework.cloud.gateway.server.mvc.GatewayServerMvcAutoConfiguration
 */
@Configuration
class GatewayConfiguration {

    /**
     * @see org.springframework.cloud.gateway.server.mvc.common.Shortcut
     */
    class AuthorizationHeaderFilterFunctions {

        companion object {

            private const val SECRET_HEADER_NAME = "Secret"

            private val logger = LoggerFactory.getLogger(this::class.java)

            /**
             * Kotlin의 Companion object 내부에 있는 함수는 JVM에서 실제로는 `진짜 static`이 아니다.
             *
             * Companion Object 내의 함수는 `AuthorizationHeaderFilterFunctions$Companion`라는 내부 클래스로 변환되고, 해당 메서드들은 INSTANCE 객체를 통해 접근한다.
             *
             * 즉, Java처럼 `진짜 static`이 아니기 때문에 `Spring Cloud Gateway MVC`에서 기대하는 `@Shortcut` 필터로 인식되지 않는다(`@Shortcut` 필터로 인식되려면 `진짜 static` 함수 여야 한다).
             *
             * 따라서 `진짜 static`으로 만들어주기 위해 `@JvmStatic` 선언이 필요하다.
             *
             * @see kotlin.jvm.JvmStatic
             */
            @JvmStatic
            /**
             * `@Shortcut` 어노테이션은 `Spring Cloud Gateway MVC`가 이 어노테이션이 선언된 함수의 이름과 인자를 순서대로 이용하여 필터 메서드로 인식하게 해준다.
             *
             * 인자가 없을 시에는 굳이 함수에 명시하지 않아도 동작한다. 단, 인자가 있을 시에는 반드시 명시해줘야 한다.
             *
             * - 인자가 없는 경우 : e.g. AuthorizationHeaderFilter
             *
             * @see org.springframework.web.servlet.function.HandlerFilterFunction
             * @see org.springframework.web.servlet.function.ServerRequest
             * @see org.springframework.web.servlet.function.ServerResponse
             * @see org.springframework.cloud.gateway.server.mvc.common.Shortcut
             * @see org.springframework.cloud.gateway.server.mvc.config.NormalizedOperationMethod
             * @see org.springframework.cloud.gateway.server.mvc.config.NormalizedOperationMethod.normalizeArgs
             */
//            @Shortcut
            fun authorizationHeaderFilter(): HandlerFilterFunction<ServerResponse, ServerResponse> {
                val requestProcessor = java.util.function.Function<ServerRequest, ServerRequest> { request ->
                    val authorization = request.headers().header(HttpHeaders.AUTHORIZATION).first()
                        ?: throw RuntimeException("인증 정보가 존재하지 않습니다.")
                    logger.info("request.remoteAddress: ${request.remoteAddress()}, request.path: ${request.uri().path}")
                    ServerRequest.from(request).header(SECRET_HEADER_NAME, authorization.toSecret()).build()
                }
                return HandlerFilterFunction.ofRequestProcessor(requestProcessor)
            }

            /**
             * Kotlin의 Companion object 내부에 있는 함수는 JVM에서 실제로는 `진짜 static`이 아니다.
             *
             * Companion Object 내의 함수는 `AuthorizationHeaderFilterFunctions$Companion`라는 내부 클래스로 변환되고, 해당 메서드들은 INSTANCE 객체를 통해 접근한다.
             *
             * 즉, Java처럼 `진짜 static`이 아니기 때문에 `Spring Cloud Gateway MVC`에서 기대하는 `@Shortcut` 필터로 인식되지 않는다(`@Shortcut` 필터로 인식되려면 `진짜 static` 함수 여야 한다).
             *
             * 따라서 `진짜 static`으로 만들어주기 위해 `@JvmStatic` 선언이 필요하다.
             *
             * @see kotlin.jvm.JvmStatic
             */
            @JvmStatic
            /**
             * `@Shortcut` 어노테이션은 `Spring Cloud Gateway MVC`가 이 어노테이션이 선언된 함수의 이름과 인자를 순서대로 이용하여 필터 메서드로 인식하게 해준다.
             *
             * 인자가 없을 시에는 굳이 함수에 명시하지 않아도 동작한다. 단, 인자가 있을 시에는 반드시 명시해줘야 한다.
             *
             * - 인자가 있는 경우 : e.g. MyHeaderFilter=X-my-header
             *
             * @see org.springframework.web.servlet.function.HandlerFilterFunction
             * @see org.springframework.web.servlet.function.ServerRequest
             * @see org.springframework.web.servlet.function.ServerResponse
             * @see org.springframework.cloud.gateway.server.mvc.common.Shortcut
             * @see org.springframework.cloud.gateway.server.mvc.config.NormalizedOperationMethod
             * @see org.springframework.cloud.gateway.server.mvc.config.NormalizedOperationMethod.normalizeArgs
             */
            @Shortcut
            fun serverHeaderFilter(serverHeaderName: String): HandlerFilterFunction<ServerResponse, ServerResponse> {
                val requestProcessor = java.util.function.Function<ServerRequest, ServerRequest> { request ->
                    val authorization = request.headers().header(serverHeaderName).first()
                        ?: throw RuntimeException("헤더 정보가 존재하지 않습니다.")
                    logger.info("request.remoteAddress: ${request.remoteAddress()}, request.path: ${request.uri().path}")
                    ServerRequest.from(request).header(SECRET_HEADER_NAME, authorization.toSecret()).build()
                }
                return HandlerFilterFunction.ofRequestProcessor(requestProcessor)
            }

            private fun String.toSecret(): String {
                return "Secret-$this" // 필요할 경우 원하는 작업을 수행하게끔 구현한다.
            }
        }
    }

    /**
     * Define in `spring.factories` file
     *
     * [How to Register Custom Predicates and Filters for Configuration](https://docs.spring.io/spring-cloud-gateway/reference/spring-cloud-gateway-server-mvc/writing-custom-predicates-and-filters.html#_how_to_register_custom_predicates_and_filters_for_configuration)
     *
     * @see org.springframework.cloud.gateway.server.mvc.filter.FilterSupplier
     */
    class AuthorizationHeaderFilterSupplier : FilterSupplier {
        override fun get(): Collection<Method> {
            return AuthorizationHeaderFilterFunctions::class.java.methods.toList()
        }
    }
}