package com.banjjoknim.soliddesignpatternsample.designpattern.state.after

class StoppingState : State {
    override fun move(gameCharacter: GameCharacter) {
        println("캐릭터가 이동한다.")
        gameCharacter.changeState(MovingState())
    }
}

class MovingState : State {
    override fun move(gameCharacter: GameCharacter) {
        println("캐릭터가 점프한다.")
        gameCharacter.changeState(JumpingState())
    }
}

class JumpingState : State {
    override fun move(gameCharacter: GameCharacter) {
        println("캐릭터가 착지한 뒤 정지한다.")
        gameCharacter.changeState(StoppingState())
    }
}
