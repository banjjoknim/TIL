package com.banjjoknim.soliddesignpatternsample.solid.isp.before

import com.banjjoknim.soliddesignpatternsample.solid.isp.common.Reservation
import com.banjjoknim.soliddesignpatternsample.solid.isp.common.SeatStatus
import com.banjjoknim.soliddesignpatternsample.solid.isp.common.SeatStatusType
import org.springframework.stereotype.Service

@Service
class SeatStatusServiceBefore : CinemaService {
    override fun makeReservation(): Reservation {
        throw UnsupportedOperationException("영화표 예매는 지원하지 않습니다.") // 불필요함에도 불구하고 구현해주어야 한다.
    }

    override fun getSeatStatus(): SeatStatus {
        return SeatStatus(SeatStatusType.CAN_RESERVATION)
    }
}
