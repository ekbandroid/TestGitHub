package com.testgithub.repositories.search

import androidx.annotation.IntegerRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.testgithub.R
import com.testgithub.repositories.favorite.FavoriteRepositoriesInteractor
import com.testgithub.repositories.model.Repository
import com.testgithub.repositories.search.paging.LoadRepositoriesError
import com.testgithub.repositories.search.paging.NetworkState
import com.testgithub.repositories.search.paging.RepoSearchResult
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class SearchRepositoriesViewModel(
    private val searchRepositoriesInteractor: SearchRepositoriesInteractor,
    private val favoriteRepositoriesInteractor: FavoriteRepositoriesInteractor
) : ViewModel() {

    private val _repositoriesList = MutableLiveData<PagedList<Repository>?>()
    val repositoriesList: LiveData<Pair<PagedList<Repository>?, String>>
        get() = Transformations.map(_repositoriesList) { it to (query ?: "") }

    private val _networkState = MutableLiveData<NetworkState>()
    val networkState: LiveData<NetworkState>
        get() = _networkState

    private val _showErrorToastLiveData = MutableLiveData<@IntegerRes Int>()
    val showErrorToastLiveData: LiveData<Int>
        get() = _showErrorToastLiveData

    private val _swipeRefreshLiveData = MutableLiveData<Boolean>()
    val swipeRefreshLiveData: LiveData<Boolean>
        get() = _swipeRefreshLiveData

    private var query: String? = null
    private var retryCallback: (() -> Unit)? = null

    private var searchRepositoriesDisposable: Disposable? = null
    private var repositoryLikedDisposable: Disposable? = null
    private var resultCompositeDisposable = CompositeDisposable()

    fun searchRepo(queryString: String) {
        query = queryString
        searchRepositoriesDisposable?.dispose()
        this._repositoriesList.postValue(null)
        searchRepositoriesDisposable =
            searchRepositoriesInteractor.clearSearchedRepositoriesTable()
                .andThen(Single.fromCallable {
                    searchRepositoriesInteractor.search(queryString)
                })
                .subscribeOn(Schedulers.io())
                .subscribe(
                    { handleRepoSearchResult(it) },
                    {
                        Timber.d(it, "Error searchRepo")
                    }
                )
    }

    fun refreshList() {
        _swipeRefreshLiveData.postValue(false)
        query?.let { searchedText ->
            if (searchedText.isNotEmpty()) {
                searchRepo(searchedText)
            }
        }
    }

    fun onRepositoryLiked(repository: Repository) {
        repositoryLikedDisposable?.dispose()
        repositoryLikedDisposable =
            if (!repository.isFavorite) {
                favoriteRepositoriesInteractor.saveFavoriteRepository(repository)
            } else {
                favoriteRepositoriesInteractor.deleteFavoriteRepository(repository)
            }
                .andThen(
                    searchRepositoriesInteractor.replaceRepository(
                        repository.copy(isFavorite = !repository.isFavorite),
                        query
                    )
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        Timber.d("RepositoryLiked $repository")
                    },
                    {
                        Timber.e(it, "Error onRepositoryLiked")
                        _showErrorToastLiveData.postValue(R.string.add_repository_to_favorites_error)
                    }
                )
    }

    fun retry() {
        retryCallback?.invoke()
    }

    private fun handleRepoSearchResult(result: RepoSearchResult) {
        resultCompositeDisposable.clear()
        resultCompositeDisposable.addAll(
            result.data
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        this._repositoriesList.postValue(it)
                    },
                    {
                        Timber.d(it, "Error observe paged list")
                    }
                ),
            result.networkState
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        _networkState.postValue(it)
                        it.error?.let { error ->
                            if (error is LoadRepositoriesError) {
                                _showErrorToastLiveData.postValue(R.string.load_repositories_error)
                            } else {
                                _showErrorToastLiveData.postValue(R.string.unknown_error)
                            }
                        }
                    },
                    {
                        Timber.d(it, "Error observe networkState")
                    }
                )
        )
        retryCallback = result.retryCallback
    }

    override fun onCleared() {
        super.onCleared()
        searchRepositoriesDisposable?.dispose()
        repositoryLikedDisposable?.dispose()
        resultCompositeDisposable.clear()
    }
}