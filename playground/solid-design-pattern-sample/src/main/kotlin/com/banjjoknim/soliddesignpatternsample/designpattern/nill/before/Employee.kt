package com.banjjoknim.soliddesignpatternsample.designpattern.nill.before

class Employee(
    val name: String,
    var workingDays: List<Int> = listOf()
) {
    fun memorizeWorkingDays(workingDays: List<Int>) {
        this.workingDays = workingDays
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Employee

        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}
