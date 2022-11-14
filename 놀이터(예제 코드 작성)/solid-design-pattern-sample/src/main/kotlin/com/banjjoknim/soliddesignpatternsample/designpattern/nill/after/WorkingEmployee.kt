package com.banjjoknim.soliddesignpatternsample.designpattern.nill.after

class WorkingEmployee(
    override val name: String,
    override var workingDays: List<Int> = listOf()
) : Employee {
    override fun memorizeWorkingDays(workingDays: List<Int>) {
        this.workingDays = workingDays
    }
}
