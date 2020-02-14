package com.testgithub.repositories.search.db

import com.testgithub.repositories.model.Owner
import com.testgithub.repositories.model.Repository

object SearchRepositoryConverter {
    fun fromDatabase(repository: SearchRepositoryEntity): Repository =
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
            dateCreate = repository.createdAt,
            isFavorite = repository.isFavorite
        )

    fun fromDatabase(repositories: List<SearchRepositoryEntity>): List<Repository> =
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

    fun listToDatabase(
        repositoryList: List<Repository>,
        searchText: String
    ): List<SearchRepositoryEntity> =
        repositoryList.map {
            SearchRepositoryEntity(
                id = it.id,
                name = it.name,
                ownerId = it.owner.id,
                ownerLogin = it.owner.login,
                ownerAvatarUrl = it.owner.avatarUrl,
                description = it.description ?: "",
                forks = it.forks,
                score = it.stars,
                createdAt = it.dateCreate,
                isFavorite = it.isFavorite,
                searchText = searchText
            )
        }

    fun toDatabase(repository: Repository, searchText: String): SearchRepositoryEntity =
        SearchRepositoryEntity(
            id = repository.id,
            name = repository.name,
            ownerId = repository.owner.id,
            ownerLogin = repository.owner.login,
            ownerAvatarUrl = repository.owner.avatarUrl,
            description = repository.description ?: "",
            forks = repository.forks,
            score = repository.stars,
            createdAt = repository.dateCreate,
            isFavorite = repository.isFavorite,
            searchText = searchText
        )
}