package com.github.jmlb23.blog

import com.mchange.v2.log.MLevel
import com.mchange.v2.log.log4j2.Log4j2MLog
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.sql.SQLClient
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.util.logging.Logger
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

suspend fun SQLClient.update(sql: String): Int =
    suspendCoroutine {
        update(sql) { x ->
            if (x.failed())
                it.resumeWithException(x.cause())
            else it.resume(x.result().updated)
        }
    }

suspend fun SQLClient.call(sql: String): List<JsonObject> =
    suspendCoroutine {
        call(sql) { x ->
            if (x.failed())
                it.resumeWithException(x.cause())
            else it.resume(x.result().rows)
        }
    }

fun Vertx.dispatcherWithErrorHandler() = dispatcher() + CoroutineExceptionHandler { coroutineContext, throwable -> throw throwable }

inline fun <reified T> RoutingContext.bodyTyped(): T? = bodyAsString?.let{Json.decodeFromString<T>(it)}