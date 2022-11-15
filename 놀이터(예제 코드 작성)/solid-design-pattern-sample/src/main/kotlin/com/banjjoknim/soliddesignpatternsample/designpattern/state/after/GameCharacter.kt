package com.banjjoknim.soliddesignpatternsample.designpattern.state.after

class GameCharacter(
    private var state: State = StoppingState()
) {
    fun move() {
        state.move(this)
    }

    fun changeState(newState: State) {
        this.state = newState
    }
}
