package com.banjjoknim.soliddesignpatternsample.designpattern.nill.before

class Restaurant(
    private val employees: MutableList<Employee> = mutableListOf()
) {
    fun addEmployee(employee: Employee) {
        employees.add(employee)
    }

    fun removeEmployee(employee: Employee) {
        employees.remove(employee)
    }

    fun showAllEmployeesWorkingDays() {
        for (employee in employees) {
            println("${employee.name}의 근무일은 ${employee.workingDays}일 입니다.")
        }
    }

    fun assignWorkingDays(workingDays: List<Int>, employeeName: String) {
        val employee = findEmployee(employeeName)
        if (employee != null) {
            employee.memorizeWorkingDays(workingDays)
        }
    }

    private fun findEmployee(employeeName: String): Employee? {
        return employees.find { it.name == employeeName }
    }
}
