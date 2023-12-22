package com.banjjoknim.soliddesignpatternsample.designpattern.facade.common

import com.banjjoknim.soliddesignpatternsample.designpattern.facade.common.Friend
import com.banjjoknim.soliddesignpatternsample.designpattern.facade.common.Member

class FriendFinder {
    fun findFriends(member: Member): List<Friend> {
        return listOf(Friend("colt", 29))
    }
}
