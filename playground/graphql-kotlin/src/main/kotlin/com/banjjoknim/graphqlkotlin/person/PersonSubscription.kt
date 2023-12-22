package com.banjjoknim.graphqlkotlin.person

import com.expediagroup.graphql.server.operations.Subscription
import org.reactivestreams.Publisher
import org.springframework.stereotype.Component

@Component
class PersonSubscription : Subscription {

    fun changeName(person: Person, newName: String): Publisher<Person> {
        return Publisher { println("change name published") }
    }
}
