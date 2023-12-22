package com.banjjoknim.soliddesignpatternsample.solid.isp.before

import com.banjjoknim.soliddesignpatternsample.solid.isp.common.Reservation
import com.banjjoknim.soliddesignpatternsample.solid.isp.common.SeatStatus

interface CinemaService {
    fun makeReservation(): Reservation

    fun getSeatStatus(): SeatStatus
}
