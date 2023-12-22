package com.banjjoknim.graphqlkotlin.person

import org.springframework.stereotype.Repository

@Repository
class PersonRepositoryImpl : PersonRepository {

    companion object {
        private val people = mapOf(
            "banjjoknim" to Person("banjjoknim"),
            "colt" to Person("colt")
        )
    }

    override fun findPerson(name: String): Person? {
        return people[name]
    }
}
