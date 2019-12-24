package com.testgithub.db

import com.testgithub.repositories.model.Owner
import com.testgithub.repositories.model.Repository

object RepositoryConverter {
    fun fromDatabase(repository: FavoriteRepositoryEntity): Repository =
        Repository(
            id = repository.id,
            name = repository.name,
            owner = Owner(
                id = repository.ownerId,
                login = repository.ownerLogin,
                avatarUrl = repository.ownerAvatarUrl
            ),
            description = repository.description,
            forks = repository.forks,
            stars = repository.score,
            dateCreate = repository.createdAt
        )

    fun fromDatabase(repositories: List<FavoriteRepositoryEntity>): List<Repository> =
        repositories.map {
            Repository(
                id = it.id,
                name = it.name,
                owner = Owner(
                    id = it.ownerId,
                    login = it.ownerLogin,
                    avatarUrl = it.ownerAvatarUrl
                ),
                description = it.description,
                forks = it.forks,
                stars = it.score,
                dateCreate = it.createdAt,
                isFavorite = true
            )
        }

    fun toDatabase(repository: Repository): FavoriteRepositoryEntity =
        FavoriteRepositoryEntity(
            id = repository.id,
            name = repository.name,
            ownerId = repository.owner.id,
            ownerLogin = repository.owner.login,
            ownerAvatarUrl = repository.owner.avatarUrl,
            description = repository.description ?: "",
            forks = repository.forks,
            score = repository.stars,
            createdAt = repository.dateCreate
        )

}