package com.banjjoknim.graphqlkotlin.person

import org.json.JSONObject
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PersonQueryTest(
    @Autowired
    private val webTestClient: WebTestClient
) {

    @DisplayName("getPerson Query Tests")
    @Nested
    inner class GetPersonTestCases {
        @Test
        fun `인자로 넣은 이름을 가진 Person 객체를 얻는다`() {
            val query = """
            query {
              getPerson(name: "colt") {
                name
              }
            }
        """.trimIndent()
            val json = JSONObject().put("query", query).toString()
            webTestClient.post()
                .uri("/graphql")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectBody().json("""{"data":{"getPerson":{"name":"colt"}}}""")
                .consumeWith {
                    println(it.responseHeaders)
                }
        }
    }

    @DisplayName("findPerson Query Tests")
    @Nested
    inner class FindPersonTestCases {

        @Test
        fun `메모리에 존재하는 Person 객체 중에서 인자와 이름이 일치하는 객체를 얻는다`() {
            val query = """
            
            query {
              findPerson(name: "banjjoknim") {
                name
              }
            }

        """.trimIndent()
            val json = JSONObject().put("query", query).toString()
            webTestClient.post()
                .uri("/graphql")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectBody().json("""{"data":{"findPerson":{"name":"banjjoknim"}}}""")
                .consumeWith {
                    println(it.responseHeaders)
                }
        }

        @Test
        fun `인자와 이름이 일치하는 객체가 메모리에 없으면 null을 얻는다`() {
            val query = """
            
            query {
              findPerson(name: "invalid") {
                name
              }
            }

        """.trimIndent()
            val json = JSONObject().put("query", query).toString()
            webTestClient.post()
                .uri("/graphql")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectBody().json("""{"data":{"findPerson":null}}""")
                .consumeWith {
                    println(it.responseHeaders)
                }
        }
    }
}
