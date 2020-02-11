package com.testgithub.repositories.favorites

import com.testgithub.repositories.favorites.db.FavoriteRepositoriesDatabaseGateway
import com.testgithub.repositories.model.Repository
import com.testgithub.repositories.search.db.SearchedRepositoriesDatabaseGateway
import io.reactivex.Completable
import io.reactivex.Flowable

class FavoriteRepositoriesInteractor(
    private val favoriteRepositoriesDatabaseGateway: FavoriteRepositoriesDatabaseGateway,
    private val searchedRepositoriesDatabaseGateway: SearchedRepositoriesDatabaseGateway
) {

    fun saveFavoriteRepository(repository: Repository): Completable =
        favoriteRepositoriesDatabaseGateway.saveFavoriteRepository(repository)


    fun deleteFavoriteRepository(repository: Repository): Completable =
        favoriteRepositoriesDatabaseGateway.delete(repository.id)
            .andThen (searchedRepositoriesDatabaseGateway.cleanFavoriteStatus(repository.id))

    fun getFavoriteRepositories(): Flowable<List<Repository>> =
        favoriteRepositoriesDatabaseGateway.getAllFavoriteRepositories()

}