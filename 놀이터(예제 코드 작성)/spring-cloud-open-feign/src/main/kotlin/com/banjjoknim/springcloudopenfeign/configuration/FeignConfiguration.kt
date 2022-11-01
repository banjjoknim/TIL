package com.banjjoknim.springcloudopenfeign.configuration

import com.fasterxml.jackson.databind.DeserializationFeature
import feign.Logger
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder

@Configuration
class FeignConfiguration : Jackson2ObjectMapperBuilderCustomizer {
    override fun customize(jacksonObjectMapperBuilder: Jackson2ObjectMapperBuilder) {
        jacksonObjectMapperBuilder
            .featuresToEnable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)
            .featuresToDisable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    }
}

/**
 * For each Feign client, a logger is created by default.
 *
 * To enable logging, we should declare it in the application.properties file using the package name of the client interfaces:
 *
 * > logging.level.com.baeldung.cloud.openfeign.client: DEBUG
 *
 * Or, if we want to enable logging only for one particular client in a package, we can use the full class name:
 *
 * > logging.level.com.baeldung.cloud.openfeign.client.JSONPlaceHolderClient: DEBUG
 *
 * **Note that Feign logging responds only to the DEBUG level.**
 *
 * The ***Logger.Level*** that we may configure per client indicates how much to log:
 * ```java
 * public class ClientConfiguration {
 *
 *     @Bean
 *     Logger.Level feignLoggerLevel() {
 *         return Logger.Level.BASIC;
 *     }
 * }
 * ```
 *
 * There are four logging levels to choose from:
 *
 * - NONE – no logging, which is the default
 * - BASIC – log only the request method, URL and response status
 * - HEADERS – log the basic information together with request and response headers
 * - FULL – log the body, headers and metadata for both request and response
 */
@Configuration
class LoggerConfiguration {
    @Bean
    fun feignLoggerLevel(): Logger.Level {
        return Logger.Level.FULL
    }
}
