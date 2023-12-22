package com.banjjoknim.soliddesignpatternsample.designpattern.templatemethod.after

abstract class SportsInstructor {
    fun startLesson() {
        showStretching()
        showMovements()
        greeting()
    }

    private fun showStretching() {
        println("스트레칭을 한다.")
    }

    protected abstract fun showMovements()

    private fun greeting() {
        println("인사를 한다.")
    }
}
