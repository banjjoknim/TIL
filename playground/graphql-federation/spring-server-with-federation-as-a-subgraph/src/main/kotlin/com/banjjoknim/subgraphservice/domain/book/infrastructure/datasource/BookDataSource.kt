package com.banjjoknim.subgraphservice.domain.book.infrastructure.datasource

import com.banjjoknim.subgraphservice.domain.book.model.Book

object BookDataSource {

    private val books = mapOf(0 to "어린왕자", 1 to "보물섬", 2 to "피터팬")

    fun getBook(number: Int): Book {
        val title = books[number] ?: throw RuntimeException("번호에 해당하는 책의 정보를 찾지 못했습니다. number: $number")
        return Book(number, title)
    }
}
