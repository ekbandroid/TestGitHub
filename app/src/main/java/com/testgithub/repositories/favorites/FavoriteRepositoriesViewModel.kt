package com.testgithub.repositories.favorites

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.testgithub.repositories.RepositoriesSearchUseCase
import com.testgithub.repositories.model.Repository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class FavoriteRepositoriesViewModel(
    private val repositoriesSearchUseCase: RepositoriesSearchUseCase
) : ViewModel() {
    val repositoriesListLiveData = MutableLiveData<Pair<String, List<Repository>>>()

    var repositoriesList: ArrayList<Repository> = ArrayList()
    private var searchRepositoriesDisposable: Disposable? = null
    private var getFavoriteRepositoriesDisposable: Disposable

    init {
        getFavoriteRepositoriesDisposable =
            repositoriesSearchUseCase.getFavoriteRepositories()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { repositoryList ->
                        repositoriesList.clear()
                        Timber.d("getFavoriteRepositories result $repositoryList")
                        repositoriesList.addAll(repositoryList)
                        if (repositoriesListLiveData.value == null) {
                            repositoriesListLiveData.postValue("" to repositoriesList)
                        } else {
                            repositoriesListLiveData.value?.let { (text, list) ->
                                repositoriesListLiveData.postValue(text to repositoriesList)
                            }
                        }
                    },
                    {
                        Timber.e(it, "Error searchRepositories")
                    }
                )
    }

    fun onDeleteRepository(repository: Repository) {
        searchRepositoriesDisposable?.dispose()
        searchRepositoriesDisposable =
            repositoriesSearchUseCase.deleteFavoriteRepository(repository)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        Timber.d("deleteFavoriteRepository success $repository")
                    },
                    {
                        Timber.e(it, "Error deleteFavoriteRepository")
                    }
                )
    }

    override fun onCleared() {
        super.onCleared()
        getFavoriteRepositoriesDisposable.dispose()
        searchRepositoriesDisposable?.dispose()
    }

    fun onSearch(text: String) {
        repositoriesListLiveData.value?.let { (_, list) ->
            repositoriesListLiveData.postValue(text to list)
        }
    }
}