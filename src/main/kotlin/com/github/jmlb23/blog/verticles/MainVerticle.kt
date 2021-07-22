package com.github.jmlb23.blog.verticles

import com.github.jmlb23.blog.mockEnv
import com.github.jmlb23.blog.repositories.InMemoryUserRepository
import io.vertx.core.AbstractVerticle

class MainVerticle : AbstractVerticle() {
    override fun start() {
        val userController =
            UserController(
                vertx,
                InMemoryUserRepository()
            )
        vertx.createHttpServer().exceptionHandler { throw it }.requestHandler(with(userController) {
            with(mockEnv) {
                createRouter()
            }
        }).listen(8080, "127.0.0.1")
    }

}

