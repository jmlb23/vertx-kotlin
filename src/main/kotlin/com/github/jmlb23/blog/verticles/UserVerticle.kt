package com.github.jmlb23.blog.verticles

import com.github.jmlb23.blog.domain.User
import com.github.jmlb23.blog.repositories.MysqlUserRepository
import com.github.jmlb23.blog.verticles.helpers.AddUserRequestPayload
import com.github.jmlb23.blog.verticles.helpers.UserResponses
import io.reactivex.Completable
import io.vertx.core.http.HttpMethod
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import io.vertx.reactivex.core.AbstractVerticle
import io.vertx.reactivex.ext.jdbc.JDBCClient
import io.vertx.reactivex.ext.web.Router

class UserVerticle : AbstractVerticle() {


  override fun rxStart(): Completable {
    val router = Router.router(vertx)
    val mysqlUserRepository by lazy {
      MysqlUserRepository(JDBCClient.createShared(vertx, JsonObject(mapOf(
        "driver_class" to "com.mysql.cj.jdbc.Driver",
        "url" to "jdbc:mysql://127.0.0.1:3306/blog",
        "user" to "root",
        "password" to "my-secret-pw",
        "max_pool_size" to 30
      ))))
    }

    router
      .route(HttpMethod.GET, "/user").blockingHandler{ rc ->
        mysqlUserRepository.getAll().subscribe{ t1 -> rc
          .response()
          .putHeader("content-type","application/json")
          .setStatusCode(200)
          .end(Json.encode(UserResponses.GetAllResponse(t1))) }
      }

    router
      .route(HttpMethod.GET, "/user/:id").blockingHandler{ rc ->
        val param= rc.pathParam("id").toLong()
        mysqlUserRepository.getElement(param).subscribe { t1 -> rc
          .response()
          .putHeader("content-type","application/json")
          .setStatusCode(200)
          .end(Json.encode(UserResponses.GetElementResponse(t1))) }
      }

    router
      .route(HttpMethod.DELETE, "/user/:id").blockingHandler{ rc ->
        val param= rc.pathParam("id").toLong()
        mysqlUserRepository.remove(param).subscribe ({ t1 -> rc
          .response()
          .putHeader("content-type","application/json")
          .setStatusCode(200)
          .end(Json.encode(UserResponses.RemoveResponse(UserResponses.CDUBodyResponse(param.toInt(),"Deleted with success")))) },{
          rc.response().putHeader("content-type","application/json").setStatusCode(404).end(Json.encode(UserResponses.ErrorResponse(UserResponses.ErrorM("ID $param not found","404"))))
        })
      }

    router
      .route(HttpMethod.POST, "/user").blockingHandler{ rc ->
        val param= rc.bodyAsJson.mapTo(AddUserRequestPayload::class.java)
        mysqlUserRepository.add(User(0,param.username, emptyList())).subscribe { t1 -> rc
          .response()
          .putHeader("content-type","application/json")
          .setStatusCode(201)
          .end(Json.encode(UserResponses.AddResponse(UserResponses.CDUBodyResponse(t1,"created with success")))) }
      }

    return vertx
      .createHttpServer()
      .requestHandler(router)
      .rxListen(8888,"127.0.0.1").ignoreElement()

  }


  override fun rxStop(): Completable = vertx.rxClose().doOnComplete { println("Clean up") }
}
