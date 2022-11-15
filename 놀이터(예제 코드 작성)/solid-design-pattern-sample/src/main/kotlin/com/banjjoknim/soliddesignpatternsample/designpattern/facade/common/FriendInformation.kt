package com.banjjoknim.soliddesignpatternsample.designpattern.facade.common

data class FriendInformation(
    val name: String,
    val age: Int
) : Information {
    constructor(friend: Friend) : this(
        name = friend.name,
        age = friend.age
    )
}
