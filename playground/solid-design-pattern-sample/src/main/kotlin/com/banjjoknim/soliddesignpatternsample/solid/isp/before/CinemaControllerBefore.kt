package com.banjjoknim.soliddesignpatternsample.solid.isp.before

import com.banjjoknim.soliddesignpatternsample.solid.isp.common.Reservation
import com.banjjoknim.soliddesignpatternsample.solid.isp.common.SeatStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/cinemas")
@RestController
class CinemaControllerBefore(
    private val seatStatusService: SeatStatusServiceBefore,
    private val reservationService: ReservationServiceBefore,
) {
    @GetMapping("/none-isp-get-seat-status")
    fun getSeatStatus(): SeatStatus {
        return seatStatusService.getSeatStatus()
    }

    @PostMapping("/none-isp-make-reservation")
    fun makeReservation(): Reservation {
        return reservationService.makeReservation()
    }
}
