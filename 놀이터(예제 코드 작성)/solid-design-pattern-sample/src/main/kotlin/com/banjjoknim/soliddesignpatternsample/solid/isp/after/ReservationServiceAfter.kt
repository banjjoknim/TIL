package com.banjjoknim.soliddesignpatternsample.solid.isp.after

import com.banjjoknim.soliddesignpatternsample.solid.isp.common.Reservation
import org.springframework.stereotype.Service

@Service
class ReservationServiceAfter : ReservationService {
    override fun makeReservation(): Reservation {
        return Reservation(seat = "A1", id = 1L)
    }
}
