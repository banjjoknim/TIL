/*
 * This file is generated by jOOQ.
 */
package com.jooq.entity.tables.pojos


import java.io.Serializable
import java.time.LocalDateTime


/**
 * This class is generated by jOOQ.
 */
@Suppress("UNCHECKED_CAST")
data class Order(
    val orderId: Int? = null,
    val productName: String? = null,
    val productPrice: Int? = null,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
    val deletedAt: LocalDateTime? = null
): Serializable {

    override fun toString(): String {
        val sb = StringBuilder("Order (")

        sb.append(orderId)
        sb.append(", ").append(productName)
        sb.append(", ").append(productPrice)
        sb.append(", ").append(createdAt)
        sb.append(", ").append(updatedAt)
        sb.append(", ").append(deletedAt)

        sb.append(")")
        return sb.toString()
    }
}