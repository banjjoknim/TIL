package com.banjjoknim.soliddesignpatternsample.designpattern.templatemethod.before

class SwimmingInstructorBefore {
    fun startLesson() {
        showStretching()
        showSwimming()
        greeting()
    }

    private fun showStretching() {
        println("스트레칭을 한다.")
    }

    private fun showSwimming() {
        println("수영 동작을 보여준다.")
    }

    private fun greeting() {
        println("인사를 한다.")
    }
}
