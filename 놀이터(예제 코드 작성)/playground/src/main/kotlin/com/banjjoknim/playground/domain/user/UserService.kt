package com.banjjoknim.playground.domain.user

import com.banjjoknim.playground.domain.admin.AdminService
import com.banjjoknim.playground.domain.admin.CouponService
import com.banjjoknim.playground.domain.sender.SenderService
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class UserService(
    private val userRepository: UserRepository,
    private val adminService: AdminService,
    private val senderService: SenderService,
    private val couponService: CouponService
) {
    fun createUser(request: CreateUserRequest) {
        val user = request.toUser()
        userRepository.save(user)
        adminService.alarm(user.name)
        couponService.registerCoupon(user.email)
        senderService.sendSMS(user.phoneNumber)
        senderService.sendEmail(user.email)
    }

    fun retrieveUser(userId: Long): RetrieveUserResponse {
        val user = userRepository.findByIdOrNull(userId)
            ?: throw NoSuchElementException("회원이 존재하지 않습니다. [userId: $userId]")
        return RetrieveUserResponse(user)
    }
}
