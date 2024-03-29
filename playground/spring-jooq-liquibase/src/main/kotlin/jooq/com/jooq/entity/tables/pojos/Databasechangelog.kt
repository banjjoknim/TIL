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
data class Databasechangelog(
    val id: String? = null,
    val author: String? = null,
    val filename: String? = null,
    val dateexecuted: LocalDateTime? = null,
    val orderexecuted: Int? = null,
    val exectype: String? = null,
    val md5sum: String? = null,
    val description: String? = null,
    val comments: String? = null,
    val tag: String? = null,
    val liquibase: String? = null,
    val contexts: String? = null,
    val labels: String? = null,
    val deploymentId: String? = null
): Serializable {

    override fun toString(): String {
        val sb = StringBuilder("Databasechangelog (")

        sb.append(id)
        sb.append(", ").append(author)
        sb.append(", ").append(filename)
        sb.append(", ").append(dateexecuted)
        sb.append(", ").append(orderexecuted)
        sb.append(", ").append(exectype)
        sb.append(", ").append(md5sum)
        sb.append(", ").append(description)
        sb.append(", ").append(comments)
        sb.append(", ").append(tag)
        sb.append(", ").append(liquibase)
        sb.append(", ").append(contexts)
        sb.append(", ").append(labels)
        sb.append(", ").append(deploymentId)

        sb.append(")")
        return sb.toString()
    }
}
