package com.banjjoknim.graphqlkotlin.person

interface PersonRepository {

    fun findPerson(name: String): Person?
}
