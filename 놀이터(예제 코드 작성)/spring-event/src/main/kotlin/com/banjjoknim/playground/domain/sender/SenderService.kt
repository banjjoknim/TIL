package com.banjjoknim.playground.domain.sender

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class SenderService {
    private val log = LoggerFactory.getLogger(this::class.java)

    fun sendEmail(email: String) {
        log.info("환영 이메일 발송 성공 : {}", email)
    }

    fun sendSMS(phoneNumber: String) {
        log.info("환영 SMS 발송 성공 : {}", phoneNumber)
    }
}
