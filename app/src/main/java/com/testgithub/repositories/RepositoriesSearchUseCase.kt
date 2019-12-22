package com.testgithub.repositories

import com.testgithub.db.FavoriteRepositoriesDao
import com.testgithub.db.RepositoryConverter
import com.testgithub.repositories.model.Repository
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import timber.log.Timber

class RepositoriesSearchUseCase(
    private val gitHubApi: GitHubApi,
    private val favoriteRepositoriesDao: FavoriteRepositoriesDao
) {

    fun searchRepositories(text: String, page: Int, countPerPage: Int): Single<List<Repository>> {
        Timber.d("searchRepositories request $text $page")
        return gitHubApi.searchRepositories(text, page, countPerPage)
            .map { it.data }
            .toFlowable()
            .flatMapIterable { it }
            .flatMapMaybe {
                favoriteRepositoriesDao.getFavoriteRepositoryById(it.id)
                    .map { _ -> it.copy(isFavorited = true) }
                    .defaultIfEmpty(it)
            }
            .toList()
    }

    fun saveFavoriteRepository(repository: Repository): Completable =
        Completable.fromCallable {
            favoriteRepositoriesDao.insert(
                RepositoryConverter.toDatabase(
                    repository
                )
            )
        }

    fun deleteFavoriteRepository(repository: Repository): Completable =
        Completable.fromCallable {
            favoriteRepositoriesDao.delete(repository.id)
        }

    fun getFavoriteRepositories(): Maybe<List<Repository>> =
        favoriteRepositoriesDao.getAllFavoriteRepositories()
            .map { RepositoryConverter.fromDatabase(it) }

}