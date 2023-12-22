package com.banjjoknim.soliddesignpatternsample.solid.isp.after

import com.banjjoknim.soliddesignpatternsample.solid.isp.common.Reservation
import com.banjjoknim.soliddesignpatternsample.solid.isp.common.SeatStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/cinemas")
@RestController
class CinemaControllerAfter(
    private val seatStatusService: SeatStatusServiceAfter,
    private val reservationService: ReservationServiceAfter
) {
    @GetMapping("/with-isp-get-seat-status")
    fun getSeatStatus(): SeatStatus {
        return seatStatusService.getSeatStatus()
    }

    @PostMapping("/with-isp-make-reservation")
    fun makeReservation(): Reservation {
        return reservationService.makeReservation()
    }
}
