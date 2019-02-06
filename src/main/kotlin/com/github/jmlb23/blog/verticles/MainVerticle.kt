package com.github.jmlb23.blog.verticles

import io.reactivex.Completable
import io.vertx.reactivex.core.AbstractVerticle

class MainVerticle : AbstractVerticle() {
  override fun rxStart(): Completable =
    vertx.rxDeployVerticle(UserVerticle()).ignoreElement()

}

