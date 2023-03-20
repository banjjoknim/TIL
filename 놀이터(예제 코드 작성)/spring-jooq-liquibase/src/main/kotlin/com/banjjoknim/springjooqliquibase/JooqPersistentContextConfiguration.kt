package com.banjjoknim.springjooqliquibase

import org.jooq.ConnectionProvider
import org.jooq.DSLContext
import org.jooq.ExecuteContext
import org.jooq.ExecuteListenerProvider
import org.jooq.SQLDialect
import org.jooq.conf.Settings
import org.jooq.impl.DSL
import org.jooq.impl.DataSourceConnectionProvider
import org.jooq.impl.DefaultConfiguration
import org.jooq.impl.DefaultDSLContext
import org.jooq.impl.DefaultExecuteListener
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy
import java.time.LocalDateTime
import javax.sql.DataSource


/**
 * @see org.springframework.boot.autoconfigure.jooq.JooqAutoConfiguration
 * @see org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
 */
//@ImportAutoConfiguration(JooqAutoConfiguration::class)
@Configuration
class JooqPersistentContextConfiguration(
    private val dataSource: DataSource,
) {

    /**
     * @see org.jooq.DSLContext
     * @see org.jooq.impl.DefaultDSLContext
     */
    @Bean
    fun dsl(): DSLContext {
        return DefaultDSLContext(jooqConfiguration())
    }

    /**
     * @see org.jooq.Configuration
     * @see org.jooq.impl.DefaultConfiguration
     */
    private fun jooqConfiguration(): org.jooq.Configuration {
        val jooqConfiguration = DefaultConfiguration()

        val settings = jooqConfiguration.settings()
            .withExecuteWithOptimisticLocking(true)
            .withExecuteLogging(false)
            .withRenderSchema(false)

        jooqConfiguration.set(settings)
        jooqConfiguration.set(connectionProvider())
        jooqConfiguration.set(executeListenerProvider())
        jooqConfiguration.setSQLDialect(SQLDialect.MYSQL)

        return jooqConfiguration
    }

    /**
     * @see org.jooq.ConnectionProvider
     * @see org.jooq.impl.DataSourceConnectionProvider
     * @see org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy
     */
    private fun connectionProvider(): ConnectionProvider {
        return DataSourceConnectionProvider(TransactionAwareDataSourceProxy(dataSource))
    }

    /**
     * @see org.jooq.ExecuteListenerProvider
     * @see org.jooq.impl.DefaultExecuteListenerProvider
     */
    private fun executeListenerProvider(): ExecuteListenerProvider {
//        return DefaultExecuteListenerProvider(DefaultExecuteListener())
        return ExecuteListenerProvider { CustomExecuteListener() }
    }

    /**
     * [DefaultExecuteListener Docs.](https://www.jooq.org/javadoc/3.16.x/org.jooq/org/jooq/impl/DefaultExecuteListener.html)
     * @see org.jooq.impl.DefaultExecuteListener
     * @see org.jooq.impl.BatchCRUD
     * @see org.jooq.impl.FetchServerOutputListener
     * @see org.jooq.tools.LoggerListener
     * @see org.springframework.boot.autoconfigure.jooq.JooqExceptionTranslator
     */
    private class CustomExecuteListener : DefaultExecuteListener() {
        private val logger = LoggerFactory.getLogger("org.jooq.CustomExecuteListener")

        /**
         * Hook into the query execution lifecycle before executing queries
         */
        override fun executeStart(ctx: ExecuteContext) {

            // Create a new DSLContext for logging rendering purposes
            // This DSLContext doesn't need a connection, only the SQLDialect...
            val dslForLogging = DSL.using(
                ctx.dialect(),  // ... and the flag for pretty-printing
                Settings().withRenderFormatted(false)
            )

            // If we're executing a query
            if (ctx.query() != null) {
                logger.info("start execute query at time: ${LocalDateTime.now()}")
                logger.info(dslForLogging.renderInlined(ctx.query()))
            } else if (ctx.routine() != null) {
                logger.info("start execute query at time: ${LocalDateTime.now()}")
                logger.info(dslForLogging.renderInlined(ctx.routine()))
            }
        }

        override fun executeEnd(ctx: ExecuteContext) {
            logger.info("end execute query at time: ${LocalDateTime.now()}")
            super.executeEnd(ctx)
        }

        override fun exception(ctx: ExecuteContext) {
            if (ctx.sqlException() != null) {
                ctx.sqlException()?.printStackTrace()
                val exceptionMessage = ctx.sqlException()?.message ?: "sql error has occured, but has not message."
                ctx.exception(RuntimeException(exceptionMessage))
//                val dialect = ctx.configuration().dialect()
//                val translator = if (dialect != null) {
//                    SQLErrorCodeSQLExceptionTranslator(dialect.name)
//                } else {
//                    SQLStateSQLExceptionTranslator()
//                }
//                ctx.exception(translator.translate("JOOQ", ctx.sql(), ctx.sqlException()!!))
            }
        }
    }
}
