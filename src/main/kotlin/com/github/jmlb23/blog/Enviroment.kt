package com.github.jmlb23.blog

import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.ext.sql.SQLClient
import io.vertx.ext.sql.SQLConnection

data class Enviroment(val sqlClient: SQLClient)

val mockEnv = Enviroment(object : SQLClient {
    override fun getConnection(handler: Handler<AsyncResult<SQLConnection>>?): SQLClient {
        TODO("Not yet implemented")
    }

    override fun close(handler: Handler<AsyncResult<Void>>?) {
        TODO("Not yet implemented")
    }

    override fun close() {
        TODO("Not yet implemented")
    }

})