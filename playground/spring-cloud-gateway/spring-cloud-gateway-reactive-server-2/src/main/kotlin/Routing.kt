package com.banjjoknim

import io.ktor.http.*
import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        get("/hello") {
            val authorization = call.request.header(HttpHeaders.Authorization)
            val secret = call.request.header(SECRET_HEADER_NAME)
            call.respond("hello reactive server 2! authorization: $authorization, secret: $secret")
        }
    }
}
