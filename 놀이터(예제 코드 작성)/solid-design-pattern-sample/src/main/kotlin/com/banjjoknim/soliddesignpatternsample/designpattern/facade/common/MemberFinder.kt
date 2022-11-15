package com.banjjoknim.soliddesignpatternsample.designpattern.facade.common

import com.banjjoknim.soliddesignpatternsample.designpattern.facade.common.Member

class MemberFinder {
    fun findMember(): Member {
        return Member("banjjoknim", 29)
    }
}
