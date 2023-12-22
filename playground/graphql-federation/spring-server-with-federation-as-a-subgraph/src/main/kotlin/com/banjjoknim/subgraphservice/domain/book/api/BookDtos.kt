package com.banjjoknim.subgraphservice.domain.book.api

import com.banjjoknim.subgraphservice.domain.book.model.Book

data class BookResponse(
    val number: Int,
    val title: String,
) {
    constructor(book: Book) : this(
        number = book.number,
        title = book.title,
    )
}
