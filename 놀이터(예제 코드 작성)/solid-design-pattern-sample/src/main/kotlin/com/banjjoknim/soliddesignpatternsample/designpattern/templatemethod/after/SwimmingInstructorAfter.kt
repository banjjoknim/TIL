package com.banjjoknim.soliddesignpatternsample.designpattern.templatemethod.after

class SwimmingInstructorAfter : SportsInstructor() {
    override fun showMovements() {
        println("수영 동작을 보여준다.")
    }
}
