package com.banjjoknim.soliddesignpatternsample.designpattern.templatemethod.before

class SportsInstructorApplicationBefore

fun main() {
    val swimmingTeacher = SwimmingInstructorBefore()
    swimmingTeacher.startLesson()
    val soccerTeacher = SoccerInstructorBefore()
    soccerTeacher.startLesson()
}
