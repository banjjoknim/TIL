package com.banjjoknim.soliddesignpatternsample.designpattern.facade.before

import com.banjjoknim.soliddesignpatternsample.designpattern.facade.common.FriendFinder
import com.banjjoknim.soliddesignpatternsample.designpattern.facade.common.MemberFinder
import com.banjjoknim.soliddesignpatternsample.designpattern.facade.common.MemberInformation

class MemberApplication

fun main() {
    val memberFinder = MemberFinder()
    val friendFinder = FriendFinder()

    val member = memberFinder.findMember()
    val friends = friendFinder.findFriends(member)

    val memberInformation = MemberInformation(member, friends)
    println(memberInformation)
}
