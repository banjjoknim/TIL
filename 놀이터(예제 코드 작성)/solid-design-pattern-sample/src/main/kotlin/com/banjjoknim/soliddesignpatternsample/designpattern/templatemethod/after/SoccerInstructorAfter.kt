package com.banjjoknim.soliddesignpatternsample.designpattern.templatemethod.after

class SoccerInstructorAfter : SportsInstructor() {
    override fun showMovements() {
        println("축구 동작을 보여준다.")
    }
}
