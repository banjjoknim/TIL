package com.banjjoknim.soliddesignpatternsample.designpattern.templatemethod.after

class SportsInstructorApplicationAfter

fun main() {
    val swimmingInstructor = SwimmingInstructorAfter()
    swimmingInstructor.startLesson()
    val soccerInstructor = SoccerInstructorAfter()
    soccerInstructor.startLesson()
}
