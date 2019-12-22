package com.testgithub.repositories.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.testgithub.repositories.RepositoriesSearchUseCase
import com.testgithub.repositories.model.Repository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.processors.PublishProcessor
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.concurrent.TimeUnit

private const val FIRST_PAGE = 1
private const val PAGE_ITEMS_COUNT = 100
private const val DEBOUNCE_MS = 1000L

class RepositoriesSearchViewModel(
    private val repositoriesSearchUseCase: RepositoriesSearchUseCase
) : ViewModel() {
    val repositoriesListLiveData = MutableLiveData<Pair<String, List<Repository>>>()

    var page = FIRST_PAGE
    var repositoriesList: ArrayList<Repository> = ArrayList()
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
            repositoriesSearchUseCase.getFavoriteRepositories()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { repositoryList ->
                        repositoriesList = ArrayList(repositoriesList.map {
                            it.copy(isFavorited = false)
                        })

                        repositoryList.forEach { favoriteRepository ->
                            repositoriesList.filter {
                                it.id == favoriteRepository.id
                            }
                                .forEach {
                                    repositoriesList[repositoriesList.indexOf(it)] =
                                        it.copy(isFavorited = true)
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
    }

    fun searchRepositories(searchText: String) {
        if (searchText.isBlank()) return
        searchRepositoriesDisposable?.dispose()
        searchRepositoriesDisposable =
            repositoriesSearchUseCase.searchRepositories(
                searchText,
                FIRST_PAGE,
                PAGE_ITEMS_COUNT
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { repositoryList ->
                        repositoriesList.clear()
                        page = FIRST_PAGE
                        Timber.d("searchRepositories result $repositoryList")
                        repositoriesList.addAll(repositoryList)
                        repositoriesListLiveData.postValue(searchText to repositoriesList)
                    },
                    {
                        Timber.e(it, "Error searchRepositories")
                    }
                )
    }

    fun listScrolledToEnd() {
        Timber.d("listScrolledTEnd")
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
            repositoriesSearchUseCase.searchRepositories(
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
                    }
                )

    }

    fun onRepositoryLiked(repository: Repository) {
        var repositoryCopy: Repository
        repositoryLikedDisposable?.dispose()
        repositoryLikedDisposable =
            if (!repository.isFavorited) {
                repositoryCopy = repository.copy(isFavorited = true)
                repositoriesSearchUseCase.saveFavoriteRepository(repository)
            } else {
                repositoryCopy = repository.copy(isFavorited = false)
                repositoriesSearchUseCase.deleteFavoriteRepository(repository)

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