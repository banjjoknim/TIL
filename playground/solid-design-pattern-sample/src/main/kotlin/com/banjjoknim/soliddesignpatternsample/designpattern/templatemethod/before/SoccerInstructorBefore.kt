package com.banjjoknim.soliddesignpatternsample.designpattern.templatemethod.before

class SoccerInstructorBefore {
    fun startLesson() {
        showStretching()
        showSoccerMovements()
        greeting()
    }

    private fun showStretching() {
        println("스트레칭을 한다.")
    }

    private fun showSoccerMovements() {
        println("축구 동작을 보여준다.")
    }

    private fun greeting() {
        println("인사를 한다.")
    }
}
