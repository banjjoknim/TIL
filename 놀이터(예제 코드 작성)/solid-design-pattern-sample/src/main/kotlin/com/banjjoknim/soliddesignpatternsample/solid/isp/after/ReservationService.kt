package com.banjjoknim.soliddesignpatternsample.solid.isp.after

import com.banjjoknim.soliddesignpatternsample.solid.isp.common.Reservation

interface ReservationService {
    fun makeReservation(): Reservation
}
