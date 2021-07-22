package com.github.jmlb23.blog.verticles

import com.github.jmlb23.blog.Enviroment
import com.github.jmlb23.blog.bodyTyped
import com.github.jmlb23.blog.dispatcherWithErrorHandler
import com.github.jmlb23.blog.domain.User
import com.github.jmlb23.blog.repositories.RepoErrors
import com.github.jmlb23.blog.repositories.Repository
import com.github.jmlb23.blog.verticles.helpers.AddUserRequestPayload
import com.github.jmlb23.blog.verticles.helpers.UserResponses
import io.vertx.core.Vertx
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class UserController(val vertx: Vertx, val repository: Repository<Enviroment, User, RepoErrors>) {
    val router: Router = Router.router(vertx)

    fun Enviroment.createRouter(): Router {
        router.get("/user").blockingHandler { rc ->
            GlobalScope.launch(vertx.dispatcherWithErrorHandler()) {
                with(repository) {
                    getAll().let { t1 ->
                        t1.fold({
                            rc
                                .response()
                                .putHeader("content-type", "application/json")
                                .setStatusCode(404)
                                .end("""{"message": "Not Found"}""")
                        }, {
                            rc
                                .response()
                                .putHeader("content-type", "application/json")
                                .setStatusCode(200)
                                .end(Json.encodeToString(UserResponses.GetAllResponse(it)))
                        })

                    }
                }
            }

        }

        router.get("/user/:id").handler { rc ->
            val param = rc.pathParam("id").toLong()
            GlobalScope.launch(vertx.dispatcherWithErrorHandler()) {
                with(repository) {
                    getElement(param).let { t1 ->
                        t1.fold({
                            rc
                                .response()
                                .putHeader("content-type", "application/json")
                                .setStatusCode(404)
                                .end("""{"message": "Not Found"}""")
                        }, {
                            rc
                                .response()
                                .putHeader("content-type", "application/json")
                                .setStatusCode(200)
                                .end(Json.encodeToString(UserResponses.GetElementResponse(it)))
                        })
                    }
                }
            }
        }

        router.delete("/user/:id").handler { rc ->
            val param = rc.pathParam("id").toLong()
            GlobalScope.launch(vertx.dispatcherWithErrorHandler()) {
                with(repository) {
                    remove(param).let { t1 ->
                        t1.fold({
                            rc
                                .response()
                                .putHeader("content-type", "application/json")
                                .setStatusCode(404)
                                .end("""{"message": "Not Found"}""")
                        }, {
                            rc
                                .response()
                                .putHeader("content-type", "application/json")
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
                        })
                    }
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
                        with(repository) {
                            add(User(1, it.username, emptyList())).let { t1 ->
                                t1.fold({
                                    rc
                                        .response()
                                        .putHeader("content-type", "application/json")
                                        .setStatusCode(400)
                                        .end("""{"message": "Not Found"}""")
                                }, {
                                    rc
                                        .response()
                                        .putHeader("content-type", "application/json")
                                        .setStatusCode(200)
                                        .end(
                                            Json.encodeToString(
                                                UserResponses.AddResponse(
                                                    UserResponses.CDUBodyResponse(
                                                        it,
                                                        "created with success"
                                                    )
                                                )
                                            )
                                        )
                                })
                            }
                        }
                    }
                }
            }

        return router

    }
}
