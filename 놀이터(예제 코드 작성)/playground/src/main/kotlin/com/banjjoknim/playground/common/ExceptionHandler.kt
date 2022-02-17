package com.banjjoknim.playground.common

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
class ExceptionHandler : ResponseEntityExceptionHandler() {
    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        logger.error("message", ex)
        val errors = ex.fieldErrors.map { fieldError ->
            mapOf(
                ("propertyName" to fieldError.field),
                ("reason" to fieldError.defaultMessage)
            )
        }
        return ResponseEntity.badRequest().body(errors)
    }

    @ExceptionHandler(value = [Exception::class])
    fun handleException(ex: Exception): ResponseEntity<Unit> {
        logger.error("message", ex)
        return ResponseEntity.internalServerError().build()
    }
}
