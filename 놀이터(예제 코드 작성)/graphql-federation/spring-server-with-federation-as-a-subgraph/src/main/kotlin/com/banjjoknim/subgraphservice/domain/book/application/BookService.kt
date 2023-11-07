package com.banjjoknim.subgraphservice.domain.book.application

import com.banjjoknim.subgraphservice.domain.book.api.BookResponse
import com.banjjoknim.subgraphservice.domain.book.infrastructure.datasource.BookDataSource
import com.banjjoknim.subgraphservice.domain.book.infrastructure.messaging.MessageChannel
import com.banjjoknim.subgraphservice.domain.book.infrastructure.messaging.PubSubMessage
import org.springframework.stereotype.Service

@Service
class BookService(
    private val messageChannel: MessageChannel
) {

    companion object {
        private const val destination = "pickupBook"
    }

    fun getBook(number: Int): BookResponse {
        val book = BookDataSource.getBook(number)
        val content = "book has picked up with number: $number. it's title is [${book.title}]"
        val pubsubMessage = PubSubMessage(destination, content)
        messageChannel.sendMessage(pubsubMessage)
        return BookResponse(book)
    }
}
