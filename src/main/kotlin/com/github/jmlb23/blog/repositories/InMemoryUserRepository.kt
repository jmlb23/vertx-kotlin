package com.github.jmlb23.blog.repositories

import com.github.jmlb23.blog.call
import com.github.jmlb23.blog.domain.Post
import com.github.jmlb23.blog.domain.User
import com.github.jmlb23.blog.update
import io.vertx.ext.jdbc.JDBCClient


class InMemoryUserRepository : Repository<User> {
    val memory = mutableListOf<User>()

    override suspend fun getElement(id: Long): User? = memory.firstOrNull { it.id == id }


    override suspend fun getAll(): List<User> = memory

    override suspend fun remove(id: Long): Int = memory.removeIf { it.id == id }.compareTo(true)

    override suspend fun update(id: Long, entity: User): Int =
        (memory.removeIf { it.id == id}.takeIf { it }?.let { memory.add(entity) } ?: false).compareTo(true)

    override suspend fun add(entity: User): Int =
        memory.add(entity).compareTo(true)

    override suspend fun filter(pred: (User) -> Boolean): List<User> = getAll().filter(pred)

}
