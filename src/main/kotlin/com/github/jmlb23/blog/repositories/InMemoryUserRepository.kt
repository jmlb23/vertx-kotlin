package com.github.jmlb23.blog.repositories

import arrow.core.*
import com.github.jmlb23.blog.Enviroment
import com.github.jmlb23.blog.domain.User


class InMemoryUserRepository : Repository<Enviroment, User, RepoErrors> {
    val memory = mutableListOf<User>()
    override suspend fun Enviroment.getElement(id: Long): Either<RepoErrors, User> =
        memory.firstOrNull { it.id == id }.toOption().toEither { RepoErrors.NotFound }


    override suspend fun Enviroment.getAll(): Either<RepoErrors, List<User>> = memory.right()

    override suspend fun Enviroment.remove(id: Long): Either<RepoErrors, Int> =
        (if (memory.removeIf { it.id == id }) id.toInt().right() else RepoErrors.NotFound.left())

    override suspend fun Enviroment.update(id: Long, entity: User): Either<RepoErrors, Int> =
        remove(id).flatMap { add(entity) }

    override suspend fun Enviroment.add(entity: User): Either<RepoErrors, Int> =
        (if (memory.contains(entity)) RepoErrors.AlreadyContained.left() else memory.add(entity)
            .let { entity.id.toInt().right() })

    override suspend fun Enviroment.filter(pred: (User) -> Boolean): Either<RepoErrors, List<User>> =
        getAll().map { it.filter(pred) }


}
