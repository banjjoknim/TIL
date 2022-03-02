package com.banjjoknim.playground.config

import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableAsync

/**
 * ```
 * @EnableAsync 어노테이션을 이용해서 비동기 설정을 활성화할 수 있다.
 *
 * 비동기 설정을 활성화했다면, 비동기로 실행할 이벤트 리스너에 @Async 어노테이션을 붙이면 된다.
 * ```
 *
 * @see EnableAsync
 * @see Async
 */
@EnableAsync
@Configuration
class AsyncConfiguration {
}
