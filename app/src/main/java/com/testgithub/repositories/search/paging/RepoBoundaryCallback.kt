package com.testgithub.repositories.search.paging

import androidx.paging.PagedList
import com.testgithub.repositories.favorites.db.FavoriteRepositoriesDatabaseGateway
import com.testgithub.repositories.model.Repository
import com.testgithub.repositories.search.api.GitHubService
import com.testgithub.repositories.search.db.SearchedRepositoriesDatabaseGateway
import io.reactivex.Flowable
import io.reactivex.disposables.Disposable
import io.reactivex.processors.BehaviorProcessor
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class RepoBoundaryCallback(
    private val query: String,
    private val gitHubService: GitHubService,
    private val searchedRepositoriesDatabaseGateway: SearchedRepositoriesDatabaseGateway,
    private val favoriteRepositoriesDatabaseGateway: FavoriteRepositoriesDatabaseGateway
) : PagedList.BoundaryCallback<Repository?>() {

    companion object {
        private const val NETWORK_PAGE_SIZE = 100
    }

    private var lastRequestedPage = 1

    private val _networkStateProcessor: BehaviorProcessor<NetworkState> = BehaviorProcessor.create()
    val networkStateFlowable: Flowable<NetworkState>
        get() = _networkStateProcessor

    private var requestDisposable: Disposable? = null

    override fun onZeroItemsLoaded() {
        Timber.d("RepoBoundaryCallback onZeroItemsLoaded")
        requestAndSaveData(query)
    }

    override fun onItemAtEndLoaded(itemAtEnd: Repository) {
        Timber.d("RepoBoundaryCallback onItemAtEndLoaded")
        requestAndSaveData(query)
    }

    fun retry() {
        requestAndSaveData(query)
    }

    private fun requestAndSaveData(query: String) {
        if (_networkStateProcessor.value == NetworkState.LOADING) return
        _networkStateProcessor.onNext(NetworkState.LOADING)
        requestDisposable?.dispose()
        requestDisposable =
            gitHubService.searchRepositories(
                query,
                lastRequestedPage,
                NETWORK_PAGE_SIZE
            )
                .toFlowable()
                .flatMapIterable { it }
                .flatMapMaybe {
                    favoriteRepositoriesDatabaseGateway.getFavoriteRepositoryById(it.id)
                        .map { _ -> it.copy(isFavorite = true) }
                        .defaultIfEmpty(it)
                }
                .toList()
                .flatMapCompletable {
                    searchedRepositoriesDatabaseGateway.insertList(it, query)
                }
                .subscribeOn(Schedulers.io())
                .subscribe(
                    {
                        Timber.d("lastRequestedPage $lastRequestedPage")
                        _networkStateProcessor.onNext(NetworkState.LOADED)
                        lastRequestedPage++
                    },
                    { error ->
                        Timber.d(error, "Error requestAndSaveData")
                        if (error is java.net.UnknownHostException) {
                            _networkStateProcessor.onNext(
                                NetworkState.error(LoadRepositoriesError())
                            )
                        } else {
                            _networkStateProcessor.onNext(NetworkState.LOADED)
                        }
                    }
                )
    }
}

class LoadRepositoriesError : Throwable()