package com.banjjoknim.soliddesignpatternsample.designpattern.facade.after

import com.banjjoknim.soliddesignpatternsample.designpattern.facade.common.FriendFinder
import com.banjjoknim.soliddesignpatternsample.designpattern.facade.common.Information
import com.banjjoknim.soliddesignpatternsample.designpattern.facade.common.MemberFinder
import com.banjjoknim.soliddesignpatternsample.designpattern.facade.common.MemberInformation

class MemberInformationFinder(
    private val memberFinder: MemberFinder,
    private val friendFinder: FriendFinder,
): InformationFinder {
    override fun findInformation(): Information {
        val member = memberFinder.findMember()
        val friends = friendFinder.findFriends(member)
        return MemberInformation(member, friends)
    }
}
