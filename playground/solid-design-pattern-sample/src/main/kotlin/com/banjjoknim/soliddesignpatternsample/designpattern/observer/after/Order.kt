package com.banjjoknim.soliddesignpatternsample.designpattern.observer.after

import com.banjjoknim.soliddesignpatternsample.designpattern.observer.common.OrderState

class Order(
    private var state: OrderState = OrderState.RECEIVED,
    private val teams: MutableList<Team> = mutableListOf()
) {
    fun addTeam(team: Team) {
        this.teams.add(team)
    }

    fun removeTeam(team: Team) {
        this.teams.remove(team)
    }

    fun changeState(state: OrderState) {
        this.state = state
        teams.forEach { it.onNotice(state) }
    }
}
