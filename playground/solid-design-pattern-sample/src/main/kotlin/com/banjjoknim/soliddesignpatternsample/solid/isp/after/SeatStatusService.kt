package com.banjjoknim.soliddesignpatternsample.solid.isp.after

import com.banjjoknim.soliddesignpatternsample.solid.isp.common.SeatStatus

interface SeatStatusService {
    fun getSeatStatus(): SeatStatus
}
