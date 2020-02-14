package com.testgithub.repositories.search

import androidx.paging.RxPagedListBuilder
import com.testgithub.repositories.favorite.db.FavoriteRepositoriesDatabaseGateway
import com.testgithub.repositories.model.Repository
import com.testgithub.repositories.search.api.GitHubService
import com.testgithub.repositories.search.db.SearchRepositoriesDatabaseGateway
import com.testgithub.repositories.search.paging.RepoBoundaryCallback
import com.testgithub.repositories.search.paging.RepoSearchResult
import io.reactivex.BackpressureStrategy
import io.reactivex.Completable

class SearchRepositoriesInteractor(
    private val gitHubService: GitHubService,
    private val searchRepositoriesDatabaseGateway: SearchRepositoriesDatabaseGateway,
    private val favoriteRepositoriesDatabaseGateway: FavoriteRepositoriesDatabaseGateway
) {

    companion object {
        private const val DATABASE_PAGE_SIZE = 50
    }

    fun search(query: String): RepoSearchResult {
        val dataSourceFactory =
            searchRepositoriesDatabaseGateway.reposByName(query)

        val boundaryCallback =
            RepoBoundaryCallback(
                query,
                gitHubService,
                searchRepositoriesDatabaseGateway,
                favoriteRepositoriesDatabaseGateway
            )

        val data = RxPagedListBuilder(dataSourceFactory, DATABASE_PAGE_SIZE)
            .setBoundaryCallback(boundaryCallback)
            .buildFlowable(BackpressureStrategy.BUFFER)

        return with(boundaryCallback) {
            RepoSearchResult(
                data,
                networkStateFlowable
            ) {
                boundaryCallback.retry()
            }
        }
    }

    fun clearSearchedRepositoriesTable(): Completable =
        searchRepositoriesDatabaseGateway.deleteAll()

    fun replaceRepository(repository: Repository, searchText: String?): Completable =
        searchRepositoriesDatabaseGateway.update(repository, searchText)

}