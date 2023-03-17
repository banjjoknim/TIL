/*
 * This file is generated by jOOQ.
 */
package com.jooq.entity.keys


import com.jooq.entity.tables.Databasechangeloglock
import com.jooq.entity.tables.records.DatabasechangeloglockRecord

import org.jooq.UniqueKey
import org.jooq.impl.DSL
import org.jooq.impl.Internal



// -------------------------------------------------------------------------
// UNIQUE and PRIMARY KEY definitions
// -------------------------------------------------------------------------

val PK_DATABASECHANGELOGLOCK: UniqueKey<DatabasechangeloglockRecord> = Internal.createUniqueKey(Databasechangeloglock.DATABASECHANGELOGLOCK, DSL.name("PK_DATABASECHANGELOGLOCK"), arrayOf(Databasechangeloglock.DATABASECHANGELOGLOCK.ID), true)
