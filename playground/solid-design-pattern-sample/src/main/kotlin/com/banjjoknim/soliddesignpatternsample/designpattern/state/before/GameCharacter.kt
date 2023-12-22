package com.banjjoknim.soliddesignpatternsample.designpattern.state.before

class GameCharacter(
    private var state: State = State.STOPPING
) {
    fun move() {
        when (state) {
            State.STOPPING -> {
                println("캐릭터가 이동한다.")
                this.state = State.MOVING
            }
            State.MOVING -> {
                println("캐릭터가 점프한다.")
                this.state = State.JUMPING
            }
            State.JUMPING -> {
                println("캐릭터가 착지한 뒤 정지한다.")
                this.state = State.STOPPING
            }
        }
    }
}
