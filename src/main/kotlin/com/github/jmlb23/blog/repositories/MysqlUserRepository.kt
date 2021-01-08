package com.github.jmlb23.blog.repositories

import com.github.jmlb23.blog.call
import com.github.jmlb23.blog.domain.Post
import com.github.jmlb23.blog.domain.User
import com.github.jmlb23.blog.update
import io.vertx.ext.jdbc.JDBCClient


class MysqlUserRepository(client: JDBCClient) : Repository<User> {
    val connection = client.getConnection { }

    override suspend fun getElement(id: Long): User? =
        connection.call("select users.id as userId, users.username as username, posts.id as postId, content, date_created  from users inner join posts on (users.id = posts.id_user) where users.id = $id")
            .let { e ->
                val first = e.firstOrNull()
                first?.let { first ->
                    User(
                        first.getInteger("userId").toLong(),
                        first.getString("username"),
                        e.map {
                            Post(
                                it.getLong("postId"),
                                it.getString("content"),
                                it.getString("date_created"),
                                listOf()
                            )
                        })
                }
            }


    override suspend fun getAll(): List<User> = connection.call("select posts.id_user as userId, users.username as username, posts.id as postId, content, date_created  from users inner join posts on (users.id = posts.id_user)")
    .let { e ->

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
    }

    override suspend fun remove(id: Long): Int =
        connection.update("delete from users where id = $id")

    override suspend fun update(id: Long, entity: User): Int =
        connection.update("update users set username=${entity.username} where id = $id")

    override suspend fun add(entity: User): Int =
        connection.update("insert into users(username) values(${entity.username})")

    override suspend fun filter(pred: (User) -> Boolean): List<User> = getAll().filter(pred)

}
