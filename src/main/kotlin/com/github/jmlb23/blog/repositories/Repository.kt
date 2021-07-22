package com.github.jmlb23.blog.repositories

import arrow.core.Either

sealed class RepoErrors {
    object NotFound : RepoErrors()
    object AlreadyContained : RepoErrors()
}

interface Repository<Env, S, E : RepoErrors> {
    suspend fun Env.getElement(id: Long): Either<E, S>
    suspend fun Env.getAll(): Either<E, List<S>>
    suspend fun Env.remove(id: Long): Either<E, Int>
    suspend fun Env.update(id: Long, entity: S): Either<E, Int>
    suspend fun Env.add(entity: S): Either<E, Int>
    suspend fun Env.filter(pred: (S) -> Boolean): Either<E,List<S>>
}
