package com.banjjoknim.graphqlkotlin.person

import com.expediagroup.graphql.server.operations.Mutation
import org.springframework.stereotype.Component

@Component
class PersonMutation : Mutation {

    fun changeName(person: Person, newName: String): Person {
        return person.apply {
            name = newName
        }
    }
}
