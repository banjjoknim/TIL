/*
 * This file is generated by jOOQ.
 */
package com.jooq.entity.tables


import com.jooq.entity.Public
import com.jooq.entity.tables.records.OrderRecord

import java.time.LocalDateTime

import org.jooq.Field
import org.jooq.ForeignKey
import org.jooq.Identity
import org.jooq.Name
import org.jooq.Record
import org.jooq.Row6
import org.jooq.Schema
import org.jooq.Table
import org.jooq.TableField
import org.jooq.TableOptions
import org.jooq.impl.DSL
import org.jooq.impl.Internal
import org.jooq.impl.SQLDataType
import org.jooq.impl.TableImpl


/**
 * This class is generated by jOOQ.
 */
@Suppress("UNCHECKED_CAST")
open class Order(
    alias: Name,
    child: Table<out Record>?,
    path: ForeignKey<out Record, OrderRecord>?,
    aliased: Table<OrderRecord>?,
    parameters: Array<Field<*>?>?
): TableImpl<OrderRecord>(
    alias,
    Public.PUBLIC,
    child,
    path,
    aliased,
    parameters,
    DSL.comment(""),
    TableOptions.table()
) {
    companion object {

        /**
         * The reference instance of <code>PUBLIC.ORDER</code>
         */
        val ORDER: Order = Order()
    }

    /**
     * The class holding records for this type
     */
    override fun getRecordType(): Class<OrderRecord> = OrderRecord::class.java

    /**
     * The column <code>PUBLIC.ORDER.ORDER_ID</code>.
     */
    val ORDER_ID: TableField<OrderRecord, Int?> = createField(DSL.name("ORDER_ID"), SQLDataType.INTEGER.nullable(false).identity(true), this, "")

    /**
     * The column <code>PUBLIC.ORDER.PRODUCT_NAME</code>.
     */
    val PRODUCT_NAME: TableField<OrderRecord, String?> = createField(DSL.name("PRODUCT_NAME"), SQLDataType.VARCHAR(1000000000), this, "")

    /**
     * The column <code>PUBLIC.ORDER.PRODUCT_PRICE</code>.
     */
    val PRODUCT_PRICE: TableField<OrderRecord, Int?> = createField(DSL.name("PRODUCT_PRICE"), SQLDataType.INTEGER, this, "")

    /**
     * The column <code>PUBLIC.ORDER.CREATED_AT</code>.
     */
    val CREATED_AT: TableField<OrderRecord, LocalDateTime?> = createField(DSL.name("CREATED_AT"), SQLDataType.LOCALDATETIME(6).defaultValue(DSL.field("CURRENT_TIMESTAMP", SQLDataType.LOCALDATETIME)), this, "")

    /**
     * The column <code>PUBLIC.ORDER.UPDATED_AT</code>.
     */
    val UPDATED_AT: TableField<OrderRecord, LocalDateTime?> = createField(DSL.name("UPDATED_AT"), SQLDataType.LOCALDATETIME(6).defaultValue(DSL.field("CURRENT_TIMESTAMP", SQLDataType.LOCALDATETIME)), this, "")

    /**
     * The column <code>PUBLIC.ORDER.DELETED_AT</code>.
     */
    val DELETED_AT: TableField<OrderRecord, LocalDateTime?> = createField(DSL.name("DELETED_AT"), SQLDataType.LOCALDATETIME(6), this, "")

    private constructor(alias: Name, aliased: Table<OrderRecord>?): this(alias, null, null, aliased, null)
    private constructor(alias: Name, aliased: Table<OrderRecord>?, parameters: Array<Field<*>?>?): this(alias, null, null, aliased, parameters)

    /**
     * Create an aliased <code>PUBLIC.ORDER</code> table reference
     */
    constructor(alias: String): this(DSL.name(alias))

    /**
     * Create an aliased <code>PUBLIC.ORDER</code> table reference
     */
    constructor(alias: Name): this(alias, null)

    /**
     * Create a <code>PUBLIC.ORDER</code> table reference
     */
    constructor(): this(DSL.name("ORDER"), null)

    constructor(child: Table<out Record>, key: ForeignKey<out Record, OrderRecord>): this(Internal.createPathAlias(child, key), child, key, ORDER, null)
    override fun getSchema(): Schema? = if (aliased()) null else Public.PUBLIC
    override fun getIdentity(): Identity<OrderRecord, Int?> = super.getIdentity() as Identity<OrderRecord, Int?>
    override fun `as`(alias: String): Order = Order(DSL.name(alias), this)
    override fun `as`(alias: Name): Order = Order(alias, this)

    /**
     * Rename this table
     */
    override fun rename(name: String): Order = Order(DSL.name(name), null)

    /**
     * Rename this table
     */
    override fun rename(name: Name): Order = Order(name, null)

    // -------------------------------------------------------------------------
    // Row6 type methods
    // -------------------------------------------------------------------------
    override fun fieldsRow(): Row6<Int?, String?, Int?, LocalDateTime?, LocalDateTime?, LocalDateTime?> = super.fieldsRow() as Row6<Int?, String?, Int?, LocalDateTime?, LocalDateTime?, LocalDateTime?>
}
