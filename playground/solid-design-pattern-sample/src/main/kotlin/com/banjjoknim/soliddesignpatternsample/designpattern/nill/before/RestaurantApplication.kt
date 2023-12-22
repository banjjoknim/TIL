package com.banjjoknim.soliddesignpatternsample.designpattern.nill.before

class RestaurantApplication

fun main() {
    val restaurant = Restaurant()
    restaurant.addEmployee(Employee("banjjoknim"))

    restaurant.assignWorkingDays(listOf(1, 4, 7, 14, 20, 27), "banjjoknim")
    restaurant.assignWorkingDays(listOf(2, 5, 10, 17, 23, 29), "colt")

    restaurant.showAllEmployeesWorkingDays()
}
