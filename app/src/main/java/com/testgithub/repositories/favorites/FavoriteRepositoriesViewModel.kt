package com.testgithub.repositories.favorites

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.testgithub.common.MyError
import com.testgithub.repositories.RepositoriesUseCase
import com.testgithub.repositories.model.Repository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class FavoriteRepositoriesViewModel(
    private val repositoriesUseCase: RepositoriesUseCase
) : ViewModel() {
    val repositoriesListLiveData = MutableLiveData<Pair<String, List<Repository>>>()
    val showErrorLiveData = MutableLiveData<MyError>()

    private var searchRepositoriesDisposable: Disposable? = null
    private var getFavoriteRepositoriesDisposable: Disposable

    init {
        getFavoriteRepositoriesDisposable =
            repositoriesUseCase.getFavoriteRepositories()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { repositoryList ->
                        Timber.d("getFavoriteRepositories result $repositoryList")
                        with(repositoriesListLiveData) {
                            postValue((value?.first ?: "") to repositoryList)
                        }
                    },
                    {
                        Timber.e(it, "Error getFavoriteRepositories")
                        showErrorLiveData.postValue(MyError.LOAD_FAVORITE_REPOSITORIES_ERROR)
                    }
                )
    }

    fun onDeleteRepository(repository: Repository) {
        searchRepositoriesDisposable?.dispose()
        searchRepositoriesDisposable =
            repositoriesUseCase.deleteFavoriteRepository(repository)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        Timber.d("deleteFavoriteRepository success $repository")
                    },
                    {
                        Timber.e(it, "Error deleteFavoriteRepository")
                        showErrorLiveData.postValue(MyError.DELETE_FAVORITE_REPOSITORY_ERROR)
                    }
                )
    }

    fun onSearch(text: String) {
        repositoriesListLiveData.value?.let { (_, list) ->
            repositoriesListLiveData.postValue(text to list)
        }
    }

    override fun onCleared() {
        super.onCleared()
        getFavoriteRepositoriesDisposable.dispose()
        searchRepositoriesDisposable?.dispose()
    }
}