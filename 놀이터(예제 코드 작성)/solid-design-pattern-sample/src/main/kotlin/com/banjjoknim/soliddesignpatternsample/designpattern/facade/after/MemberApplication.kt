package com.banjjoknim.soliddesignpatternsample.designpattern.facade.after

import com.banjjoknim.soliddesignpatternsample.designpattern.facade.common.FriendFinder
import com.banjjoknim.soliddesignpatternsample.designpattern.facade.common.MemberFinder

class MemberApplication

fun main() {
    val memberFinder = MemberFinder()
    val friendFinder = FriendFinder()

    val memberInformationFinder = MemberInformationFinder(memberFinder, friendFinder)

    val memberInformation = memberInformationFinder.findInformation()
    println(memberInformation)
}
