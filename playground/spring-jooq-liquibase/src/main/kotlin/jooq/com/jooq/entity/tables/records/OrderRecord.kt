/*
 * This file is generated by jOOQ.
 */
package com.jooq.entity.tables.records


import com.jooq.entity.tables.Order

import java.time.LocalDateTime

import org.jooq.Field
import org.jooq.Record1
import org.jooq.Record6
import org.jooq.Row6
import org.jooq.impl.UpdatableRecordImpl


/**
 * This class is generated by jOOQ.
 */
@Suppress("UNCHECKED_CAST")
open class OrderRecord() : UpdatableRecordImpl<OrderRecord>(Order.ORDER), Record6<Int?, String?, Int?, LocalDateTime?, LocalDateTime?, LocalDateTime?> {

    var orderId: Int?
        set(value): Unit = set(0, value)
        get(): Int? = get(0) as Int?

    var productName: String?
        set(value): Unit = set(1, value)
        get(): String? = get(1) as String?

    var productPrice: Int?
        set(value): Unit = set(2, value)
        get(): Int? = get(2) as Int?

    var createdAt: LocalDateTime?
        set(value): Unit = set(3, value)
        get(): LocalDateTime? = get(3) as LocalDateTime?

    var updatedAt: LocalDateTime?
        set(value): Unit = set(4, value)
        get(): LocalDateTime? = get(4) as LocalDateTime?

    var deletedAt: LocalDateTime?
        set(value): Unit = set(5, value)
        get(): LocalDateTime? = get(5) as LocalDateTime?

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    override fun key(): Record1<Int?> = super.key() as Record1<Int?>

    // -------------------------------------------------------------------------
    // Record6 type implementation
    // -------------------------------------------------------------------------

    override fun fieldsRow(): Row6<Int?, String?, Int?, LocalDateTime?, LocalDateTime?, LocalDateTime?> = super.fieldsRow() as Row6<Int?, String?, Int?, LocalDateTime?, LocalDateTime?, LocalDateTime?>
    override fun valuesRow(): Row6<Int?, String?, Int?, LocalDateTime?, LocalDateTime?, LocalDateTime?> = super.valuesRow() as Row6<Int?, String?, Int?, LocalDateTime?, LocalDateTime?, LocalDateTime?>
    override fun field1(): Field<Int?> = Order.ORDER.ORDER_ID
    override fun field2(): Field<String?> = Order.ORDER.PRODUCT_NAME
    override fun field3(): Field<Int?> = Order.ORDER.PRODUCT_PRICE
    override fun field4(): Field<LocalDateTime?> = Order.ORDER.CREATED_AT
    override fun field5(): Field<LocalDateTime?> = Order.ORDER.UPDATED_AT
    override fun field6(): Field<LocalDateTime?> = Order.ORDER.DELETED_AT
    override fun component1(): Int? = orderId
    override fun component2(): String? = productName
    override fun component3(): Int? = productPrice
    override fun component4(): LocalDateTime? = createdAt
    override fun component5(): LocalDateTime? = updatedAt
    override fun component6(): LocalDateTime? = deletedAt
    override fun value1(): Int? = orderId
    override fun value2(): String? = productName
    override fun value3(): Int? = productPrice
    override fun value4(): LocalDateTime? = createdAt
    override fun value5(): LocalDateTime? = updatedAt
    override fun value6(): LocalDateTime? = deletedAt

    override fun value1(value: Int?): OrderRecord {
        this.orderId = value
        return this
    }

    override fun value2(value: String?): OrderRecord {
        this.productName = value
        return this
    }

    override fun value3(value: Int?): OrderRecord {
        this.productPrice = value
        return this
    }

    override fun value4(value: LocalDateTime?): OrderRecord {
        this.createdAt = value
        return this
    }

    override fun value5(value: LocalDateTime?): OrderRecord {
        this.updatedAt = value
        return this
    }

    override fun value6(value: LocalDateTime?): OrderRecord {
        this.deletedAt = value
        return this
    }

    override fun values(value1: Int?, value2: String?, value3: Int?, value4: LocalDateTime?, value5: LocalDateTime?, value6: LocalDateTime?): OrderRecord {
        this.value1(value1)
        this.value2(value2)
        this.value3(value3)
        this.value4(value4)
        this.value5(value5)
        this.value6(value6)
        return this
    }

    /**
     * Create a detached, initialised OrderRecord
     */
    constructor(orderId: Int? = null, productName: String? = null, productPrice: Int? = null, createdAt: LocalDateTime? = null, updatedAt: LocalDateTime? = null, deletedAt: LocalDateTime? = null): this() {
        this.orderId = orderId
        this.productName = productName
        this.productPrice = productPrice
        this.createdAt = createdAt
        this.updatedAt = updatedAt
        this.deletedAt = deletedAt
    }

    /**
     * Create a detached, initialised OrderRecord
     */
    constructor(value: com.jooq.entity.tables.pojos.Order?): this() {
        if (value != null) {
            this.orderId = value.orderId
            this.productName = value.productName
            this.productPrice = value.productPrice
            this.createdAt = value.createdAt
            this.updatedAt = value.updatedAt
            this.deletedAt = value.deletedAt
        }
    }
}
