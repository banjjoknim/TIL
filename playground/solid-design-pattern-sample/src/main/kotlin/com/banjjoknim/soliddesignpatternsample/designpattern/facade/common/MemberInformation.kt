package com.banjjoknim.soliddesignpatternsample.designpattern.facade.common

data class MemberInformation(
    val memberName: String,
    val memberAge: Int,
    val friendsInformation: List<FriendInformation>
): Information {
    constructor(member: Member, friends: List<Friend>) : this(
        memberName = member.name,
        memberAge = member.age,
        friendsInformation = friends.map { FriendInformation(it) }
    )
}
