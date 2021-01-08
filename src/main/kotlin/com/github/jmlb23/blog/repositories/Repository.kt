package com.github.jmlb23.blog.repositories

import com.github.jmlb23.blog.domain.User

interface Repository<E>{
  suspend fun getElement(id: Long): E?
  suspend fun getAll(): List<E>
  suspend fun remove(id: Long): Int
  suspend fun update(id: Long, entity: E): Int
  suspend fun add(entity: E): Int
  suspend fun filter(pred: (E) -> Boolean): List<User>
}
