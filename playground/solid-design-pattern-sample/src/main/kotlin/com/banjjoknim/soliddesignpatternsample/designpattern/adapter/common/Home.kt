package com.banjjoknim.soliddesignpatternsample.designpattern.adapter.common

class Home(
    private var electric: HomeElectric = HomeElectric(0)
) {
    fun takeElectric(electric: HomeElectric) {
        this.electric = electric
    }
}
