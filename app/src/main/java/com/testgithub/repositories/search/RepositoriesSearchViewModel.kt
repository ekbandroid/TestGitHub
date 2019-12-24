package com.testgithub.repositories.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.testgithub.common.MyError
import com.testgithub.repositories.RepositoriesUseCase
import com.testgithub.repositories.model.Repository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.processors.PublishProcessor
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.concurrent.TimeUnit

private const val FIRST_PAGE = 1
private const val PAGE_ITEMS_COUNT = 100
private const val ITEMS_COUNT_LIMIT = 1000
private const val DEBOUNCE_MS = 1000L

class RepositoriesSearchViewModel(
    private val repositoriesUseCase: RepositoriesUseCase
) : ViewModel() {
    val repositoriesListLiveData = MutableLiveData<Pair<String, List<Repository?>>>()
    val showProgressLiveData = MutableLiveData<Boolean>()
    val showSwipeRefreshLiveData = MutableLiveData<Boolean>()
    val showErrorLiveData = MutableLiveData<MyError>()

    private var page = FIRST_PAGE
    private var repositoriesList: ArrayList<Repository> = ArrayList()
    private var searchRepositoriesDisposable: Disposable? = null
    private var searchEventsDisposable: Disposable
    private var getFavoriteRepositoriesDisposable: Disposable
    private var repositoryLikedDisposable: Disposable? = null
    private val searchEventsProcessor =
        PublishProcessor.create<NextEvent>()

    init {
        searchEventsDisposable =
            searchEventsProcessor
                .debounce(DEBOUNCE_MS, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        getNext(it)
                    },
                    {
                        Timber.d(it, "Error searchEventsProcessor")
                    }
                )

        getFavoriteRepositoriesDisposable =
            repositoriesUseCase.getFavoriteRepositories()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { repositoryList ->
                        repositoriesList = ArrayList(repositoriesList.map {
                            it.copy(isFavorite = false)
                        })

                        repositoryList.forEach { favoriteRepository ->
                            repositoriesList.filter {
                                it.id == favoriteRepository.id
                            }
                                .forEach {
                                    repositoriesList[repositoriesList.indexOf(it)] =
                                        it.copy(isFavorite = true)
                                }
                        }
                        repositoriesListLiveData.value?.first?.let {
                            repositoriesListLiveData.postValue(it to repositoriesList)
                        }
                    },
                    {
                        Timber.e(it, "Error searchRepositories")
                    }
                )
        showProgressLiveData.postValue(false)
        showSwipeRefreshLiveData.postValue(false)
    }

    fun searchRepositories(searchText: String) {
        if (searchText.isBlank()) return
        repositoriesListLiveData.postValue(searchText to emptyList())
        showProgressLiveData.postValue(true)
        loadRepositories(searchText)
    }

    fun updateRepositories() {
        showSwipeRefreshLiveData.postValue(false)
        repositoriesListLiveData.value?.first?.let {
            showProgressLiveData.postValue(true)
            loadRepositories(it)
        }
    }

    private fun loadRepositories(searchText: String) {
        searchRepositoriesDisposable?.dispose()
        searchRepositoriesDisposable =
            repositoriesUseCase.searchRepositories(
                searchText,
                FIRST_PAGE,
                PAGE_ITEMS_COUNT
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { repositoryList ->
                        showProgressLiveData.postValue(false)
                        repositoriesList.clear()
                        page = FIRST_PAGE
                        Timber.d("searchRepositories result $repositoryList")
                        repositoriesList.addAll(repositoryList)
                        repositoriesListLiveData.postValue(searchText to repositoriesList)
                    },
                    {
                        showProgressLiveData.postValue(false)
                        Timber.e(it, "Error searchRepositories")
                        showErrorLiveData.postValue(MyError.LOAD_REPOSITORIES_ERROR)
                    }
                )
    }

    fun listScrolledToEnd() {
        Timber.d("listScrolledTEnd")
        repositoriesListLiveData.value?.let { (searchText, list) ->
            if (list.size < PAGE_ITEMS_COUNT || list.size == ITEMS_COUNT_LIMIT) return
            if (list[list.size - 1] != null) {
                repositoriesListLiveData.postValue(searchText to list + listOf<Repository?>(null))
            }
        }
        repositoriesListLiveData.value?.first?.let { searchText ->
            searchEventsProcessor.onNext(
                NextEvent(
                    searchText,
                    page + 1,
                    PAGE_ITEMS_COUNT
                )
            )
        }
    }

    private fun getNext(event: NextEvent) {
        if (repositoriesList.isEmpty()) return
        searchRepositoriesDisposable?.dispose()
        searchRepositoriesDisposable =
            repositoriesUseCase.searchRepositories(
                event.searchText,
                event.page,
                PAGE_ITEMS_COUNT
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { repositoryList ->
                        repositoriesList.addAll(repositoryList)
                        page = event.page
                        Timber.d("searchRepositories repositoriesList.size ${repositoriesList.size}")
                        repositoriesListLiveData.postValue(event.searchText to repositoriesList)
                    },
                    {
                        Timber.e(it, "Error getNextPage")
                        repositoriesListLiveData.value?.let { (searchText, list) ->
                            if (list.size >= PAGE_ITEMS_COUNT) {
                                if (list[list.size - 1] == null) {
                                    repositoriesListLiveData.postValue(
                                        searchText to list.subList(0, list.size - 2)
                                    )
                                }
                            }
                        }
                    }
                )

    }

    fun onRepositoryLiked(repository: Repository) {
        val repositoryCopy: Repository
        repositoryLikedDisposable?.dispose()
        repositoryLikedDisposable =
            if (!repository.isFavorite) {
                repositoryCopy = repository.copy(isFavorite = true)
                repositoriesUseCase.saveFavoriteRepository(repository)
            } else {
                repositoryCopy = repository.copy(isFavorite = false)
                repositoriesUseCase.deleteFavoriteRepository(repository)

            }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        val index = repositoriesList.indexOf(repository)
                        repositoriesList[index] = repositoryCopy
                        repositoriesListLiveData.value?.first?.let {
                            repositoriesListLiveData.postValue(it to repositoriesList)
                        }
                    },
                    {
                        Timber.e(it, "Error onRepositoryLiked")
                    }
                )
    }

    override fun onCleared() {
        super.onCleared()
        searchEventsDisposable.dispose()
        searchRepositoriesDisposable?.dispose()
        getFavoriteRepositoriesDisposable.dispose()
        repositoryLikedDisposable?.dispose()
    }
}

data class NextEvent(val searchText: String, val page: Int, val itemsCount: Int)