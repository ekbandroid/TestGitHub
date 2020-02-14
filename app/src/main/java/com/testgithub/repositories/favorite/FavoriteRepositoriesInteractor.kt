package com.testgithub.repositories.favorite

import com.testgithub.repositories.favorite.db.FavoriteRepositoriesDatabaseGateway
import com.testgithub.repositories.model.Repository
import com.testgithub.repositories.search.db.SearchRepositoriesDatabaseGateway
import io.reactivex.Completable
import io.reactivex.Flowable

class FavoriteRepositoriesInteractor(
    private val favoriteRepositoriesDatabaseGateway: FavoriteRepositoriesDatabaseGateway,
    private val searchRepositoriesDatabaseGateway: SearchRepositoriesDatabaseGateway
) {

    fun saveFavoriteRepository(repository: Repository): Completable =
        favoriteRepositoriesDatabaseGateway.saveFavoriteRepository(repository)

    fun deleteFavoriteRepository(repository: Repository): Completable =
        favoriteRepositoriesDatabaseGateway.delete(repository.id)
            .andThen (searchRepositoriesDatabaseGateway.cleanFavoriteStatus(repository.id))

    fun getFavoriteRepositories(): Flowable<List<Repository>> =
        favoriteRepositoriesDatabaseGateway.getAllFavoriteRepositories()

}