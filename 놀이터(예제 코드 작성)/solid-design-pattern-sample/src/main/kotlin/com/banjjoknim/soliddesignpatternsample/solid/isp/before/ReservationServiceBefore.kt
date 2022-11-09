package com.banjjoknim.soliddesignpatternsample.solid.isp.before

import com.banjjoknim.soliddesignpatternsample.solid.isp.common.Reservation
import com.banjjoknim.soliddesignpatternsample.solid.isp.common.SeatStatus
import org.springframework.stereotype.Service

@Service
class ReservationServiceBefore : CinemaService {
    override fun makeReservation(): Reservation {
        return Reservation(seat = "A1", id = 1L)
    }

    override fun getSeatStatus(): SeatStatus {
        throw UnsupportedOperationException("좌석 현황 조회는 지원하지 않습니다.") // 불필요함에도 불구하고 구현해주어야 한다.
    }
}
