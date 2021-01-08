package com.github.jmlb23.blog.verticles

import com.github.jmlb23.blog.bodyTyped
import com.github.jmlb23.blog.dispatcherWithErrorHandler
import com.github.jmlb23.blog.domain.User
import com.github.jmlb23.blog.repositories.Repository
import com.github.jmlb23.blog.verticles.helpers.AddUserRequestPayload
import com.github.jmlb23.blog.verticles.helpers.UserResponses
import io.vertx.core.Vertx
import io.vertx.core.http.HttpMethod
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class UserController(val vertx: Vertx, val repository: Repository<User>) {

    val router: Router = Router.router(vertx)

    fun createRouter(): Router {

        router.get("/user").blockingHandler { rc ->
            GlobalScope.launch(vertx.dispatcherWithErrorHandler()) {
                repository.getAll().let { t1 ->
                    rc
                        .response()
                        .putHeader("content-type", "application/json")
                        .setStatusCode(200)
                        .end(Json.encodeToString(UserResponses.GetAllResponse(t1)))
                }
            }

        }

        router.get("/user/:id").handler { rc ->
            val param = rc.pathParam("id").toLong()
            GlobalScope.launch(vertx.dispatcherWithErrorHandler()) {

                repository.getElement(param).let { t1 ->
                    rc
                        .response()
                        .putHeader("content-type", "application/json")
                        .setStatusCode(200)
                        .end(Json.encodeToString(UserResponses.GetElementResponse(t1)))
                }
            }
        }

        router.delete("/user/:id").handler { rc ->
            val param = rc.pathParam("id").toLong()
            GlobalScope.launch(vertx.dispatcherWithErrorHandler()) {
                repository.remove(param).let { t1 ->
                    rc
                        .response()
                        .putHeader("Content-Type", "application/json")
                        .setStatusCode(200)
                        .end(
                            Json.encodeToString(
                                UserResponses.RemoveResponse(
                                    UserResponses.CDUBodyResponse(
                                        param.toInt(),
                                        "Deleted with success"
                                    )
                                )
                            )
                        )
                }
            }
        }

        router.post("/user/")
            .handler(BodyHandler.create())
            .failureHandler {
                throw it.failure()
            }.handler { rc ->
                GlobalScope.launch(vertx.dispatcherWithErrorHandler()) {
                    val param = rc.bodyTyped<AddUserRequestPayload>()
                    param?.let {
                        repository.add(User(1, it.username, emptyList())).let { t1 ->
                            rc
                                .response()
                                .putHeader("content-type", "application/json")
                                .setStatusCode(200)
                                .end(
                                    Json.encodeToString(
                                        UserResponses.AddResponse(
                                            UserResponses.CDUBodyResponse(
                                                t1,
                                                "created with success"
                                            )
                                        )
                                    )
                                )
                        }
                    }
                }
            }

        return router

    }
}
