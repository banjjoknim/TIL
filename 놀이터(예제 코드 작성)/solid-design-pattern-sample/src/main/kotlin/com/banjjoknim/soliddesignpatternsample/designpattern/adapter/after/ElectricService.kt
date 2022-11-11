package com.banjjoknim.soliddesignpatternsample.designpattern.adapter.after

import com.banjjoknim.soliddesignpatternsample.designpattern.adapter.common.HomeElectric

interface ElectricService {
    fun generateElectric(current: Int): HomeElectric
}
