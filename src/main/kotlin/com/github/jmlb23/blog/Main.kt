package com.github.jmlb23.blog

import com.github.jmlb23.blog.verticles.MainVerticle
import io.vertx.core.Vertx

fun main(args: Array<String>) {
  val vertx = Vertx.vertx()
  vertx.deployVerticle(MainVerticle())
}
