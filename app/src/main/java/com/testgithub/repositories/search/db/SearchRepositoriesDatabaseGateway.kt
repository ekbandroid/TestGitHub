package com.testgithub.repositories.search.db

import androidx.paging.DataSource
import com.testgithub.repositories.model.Repository
import io.reactivex.Completable

class SearchRepositoriesDatabaseGateway(private val searchRepositoriesDao: SearchRepositoriesDao) {

    fun reposByName(query: String): DataSource.Factory<Int, Repository> =
        searchRepositoriesDao.reposByName(query)
            .map { SearchRepositoryConverter.fromDatabase(it) }

    fun insertList(posts: List<Repository>, query: String): Completable =
        searchRepositoriesDao.insertList(
            SearchRepositoryConverter.listToDatabase(
                posts,
                query
            )
        )

    fun update(repository: Repository, searchText: String?): Completable =
        searchRepositoriesDao.update(
            SearchRepositoryConverter.toDatabase(
                repository, searchText ?: ""
            )
        )

    fun deleteAll(): Completable =
        Completable.fromCallable {
            searchRepositoriesDao.deleteAll()
        }

    fun cleanFavoriteStatus(id: String): Completable =
        searchRepositoriesDao.getSearchedRepositoryById(id)
            .flatMapCompletable { searchRepositoriesDao.update(it.copy(isFavorite = false)) }

}