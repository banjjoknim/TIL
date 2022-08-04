package com.banjjoknim.graphqlkotlin.person

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.expediagroup.graphql.generator.annotations.GraphQLName
import com.expediagroup.graphql.server.operations.Query
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
class PersonQuery(
    /**
     * # Spring Beans
     *
     * Since the top level objects are Spring components, Spring will automatically autowire dependent beans as normal.
     *
     * Refer to [Spring Documentation](https://docs.spring.io/spring-framework/docs/current/reference/html/) for details.
     */
    private val personRepository: PersonRepository
) : Query {

    @GraphQLDescription("get Person Instance")
    fun getPerson(name: String): Person = Person(name)

    /**
     * # Spring Beans in Arguments
     *
     * graphql-kotlin-spring-server provides Spring-aware data fetcher that automatically autowires Spring beans when they are specified as function arguments.
     *
     * `@Autowired` arguments should be explicitly excluded from the GraphQL schema by also specifying @GraphQLIgnore.
     *
     * ```
     * NOTE
     * If you are using custom data fetcher make sure that you extend SpringDataFetcher instead of the base FunctionDataFetcher to keep this functionallity.
     * ```
     */
    @GraphQLDescription("find Person Instance")
    fun findPerson(@GraphQLIgnore @Autowired personRepository: PersonRepository, name: String): Person? {
        return personRepository.findPerson(name)
    }

    @GraphQLDescription("@GraphQLName example")
    @GraphQLName("somePerson")
    fun randomPerson(name: String): Person = Person(name = name, age = Random.nextLong())
}
