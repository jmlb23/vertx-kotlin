package com.github.jmlb23.blog.repositories

import com.github.jmlb23.blog.domain.User
import io.reactivex.Flowable
import io.reactivex.Maybe

interface Repository<E>{
  fun getElement(id: Long): Maybe<E>
  fun getAll(): Maybe<List<E>>
  fun remove(id: Long): Maybe<Int>
  fun update(id: Long, entity: E): Maybe<Int>
  fun add(entity: E): Maybe<Int>
  fun filter(pred: (E) -> Boolean): Maybe<List<User>>
}
