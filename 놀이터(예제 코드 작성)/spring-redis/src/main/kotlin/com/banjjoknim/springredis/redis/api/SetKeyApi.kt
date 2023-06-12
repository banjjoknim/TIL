package com.banjjoknim.springredis.redis.api

import com.banjjoknim.springredis.redis.application.SetKeyService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/redis")
@RestController
class SetKeyApi(
    private val setKeyService: SetKeyService
) {

    @PostMapping("/keys")
    fun setKey(@RequestBody request: SetKeyRequest): SetKeyResponse {
        return setKeyService.setKey(request)
    }
}

