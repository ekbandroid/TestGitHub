package com.testgithub.repositories.favorite.db

import com.testgithub.repositories.model.Repository
import io.reactivex.Completable
import io.reactivex.Completable.fromCallable
import io.reactivex.Flowable

class FavoriteRepositoriesDatabaseGateway(private val favoriteRepositoriesDao: FavoriteRepositoriesDao) {

    fun saveFavoriteRepository(repository: Repository): Completable =
        fromCallable {
            favoriteRepositoriesDao.insert(
                RepositoryConverter.toDatabase(
                    repository
                )
            )
        }

    fun delete(id: String): Completable =
        fromCallable { favoriteRepositoriesDao.delete(id) }

    fun getAllFavoriteRepositories(): Flowable<List<Repository>> =
        favoriteRepositoriesDao.getAllFavoriteRepositories()
            .map { RepositoryConverter.fromDatabase(it) }

    fun getFavoriteRepositoryById(id: String) =
        favoriteRepositoriesDao.getFavoriteRepositoryById(id)
            .map { RepositoryConverter.fromDatabase(it) }

}