package com.banjjoknim.soliddesignpatternsample.solid.isp.after

import com.banjjoknim.soliddesignpatternsample.solid.isp.common.SeatStatus
import com.banjjoknim.soliddesignpatternsample.solid.isp.common.SeatStatusType
import org.springframework.stereotype.Service

@Service
class SeatStatusServiceAfter : SeatStatusService {
    override fun getSeatStatus(): SeatStatus {
        return SeatStatus(SeatStatusType.CAN_RESERVATION)
    }
}
