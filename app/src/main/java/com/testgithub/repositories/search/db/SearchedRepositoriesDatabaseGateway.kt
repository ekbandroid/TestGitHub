package com.testgithub.repositories.search.db

import androidx.paging.DataSource
import com.testgithub.repositories.model.Repository
import io.reactivex.Completable

class SearchedRepositoriesDatabaseGateway(private val searchedRepositoriesDao: SearchedRepositoriesDao) {

    fun reposByName(query: String): DataSource.Factory<Int, Repository> =
        searchedRepositoriesDao.reposByName(query)
            .map { SearchedRepositoryConverter.fromDatabase(it) }

    fun insertList(posts: List<Repository>, query: String): Completable =
        searchedRepositoriesDao.insertList(
            SearchedRepositoryConverter.listToDatabase(
                posts,
                query
            )
        )

    fun update(repository: Repository, searchText: String?): Completable =
        searchedRepositoriesDao.update(
            SearchedRepositoryConverter.toDatabase(
                repository, searchText ?: ""
            )
        )

    fun deleteAll(): Completable =
        Completable.fromCallable {
            searchedRepositoriesDao.deleteAll()
        }

    fun cleanFavoriteStatus(id: String): Completable =
        searchedRepositoriesDao.getSearchedRepositoryById(id)
            .flatMapCompletable { searchedRepositoriesDao.update(it.copy(isFavorite = false)) }

}