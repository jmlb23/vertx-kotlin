package com.github.jmlb23.blog.repositories

import com.github.jmlb23.blog.domain.Post
import com.github.jmlb23.blog.domain.User
import io.reactivex.Maybe
import io.vertx.reactivex.ext.jdbc.JDBCClient

class MysqlUserRepository(val client: JDBCClient ) : Repository<User> {
  val connection = client.rxGetConnection()
  override fun getElement(id: Long): Maybe<User> =
    connection.flatMapMaybe{ cnn ->
      cnn.rxCall("select users.id as userId, users.username as username, posts.id as postId, content, date_created  from users inner join posts on (users.id = posts.id_user) where users.id = $id")
      .map { t -> t.rows }
      .map { e ->
        val first = e.first()
        User(first.getInteger("userId").toLong(), first.getString("username"), e.map { Post(it.getLong("postId"), it.getString("content"), it.getString("date_created"), listOf()) })
      }.toMaybe()
    }



  override fun getAll(): Maybe<List<User>> = connection.flatMapMaybe{ cnn ->
    cnn.rxCall("select posts.id_user as userId, users.username as username, posts.id as postId, content, date_created  from users inner join posts on (users.id = posts.id_user)")
    .map { t -> t.rows }
    .map { e ->

      val user = e.asSequence().map {
        User(
          it.getLong("userId"),
          it.getString("username"),
          listOf())
      }
      val post = e.asSequence().map {
        Post(it.getLong("postId"), it.getString("content"), it.getString("date_created"), emptyList())
      }

      val userWithPost = user.map { x -> x.copy(posts = post.filter { x.id == it.id }.toList()) }
      val group = userWithPost.groupBy { it.id }.map { (it.key to it.value.flatMap { it.posts }) }.asSequence()
      user.map { u -> u.copy(posts = group.filter { it.first == u.id }.flatMap { it.second.asSequence() }.toList()) }.distinct().toList()
    }.toMaybe()
  }

  override fun remove(id: Long): Maybe<Int> =
    connection.flatMapMaybe{ cnn ->
      cnn.rxUpdate("delete from users where id = $id").toMaybe().map { if (it.updated ==0) throw Exception("") else it.updated }
    }

  override fun update(id: Long, entity: User): Maybe<Int> =
    connection.flatMapMaybe{ cnn ->
      cnn.rxUpdate("update users set username=${entity.username} where id = $id").toMaybe().map { if (it.updated ==0) throw Exception("") else it.updated }
    }

  override fun add(entity: User): Maybe<Int> =
    connection.flatMapMaybe{ cnn ->
      cnn.rxUpdate("insert into users(username) values(${entity.username})").toMaybe().map { it.keys.first().toString().toInt() }
    }

  override fun filter(pred: (User) -> Boolean): Maybe<List<User>> = getAll().map{it.filter(pred)}


}
