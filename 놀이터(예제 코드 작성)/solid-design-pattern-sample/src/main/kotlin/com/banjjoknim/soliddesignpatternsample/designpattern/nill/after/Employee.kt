package com.banjjoknim.soliddesignpatternsample.designpattern.nill.after

interface Employee {
    companion object {
        val NOT_FOUND = object : Employee {
            override val name: String
                get() = "존재하지 않음"

            override fun memorizeWorkingDays(workingDays: List<Int>) {
                // 아무 작업도 하지 않는다.
            }
        }
    }

    val name: String
    var workingDays: List<Int>
        get() = listOf()
        set(workingDays) {
            memorizeWorkingDays(workingDays)
        }

    fun memorizeWorkingDays(workingDays: List<Int>)
}
