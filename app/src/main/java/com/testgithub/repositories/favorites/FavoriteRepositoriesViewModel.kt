package com.testgithub.repositories.favorites

import androidx.annotation.IntegerRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.testgithub.R
import com.testgithub.repositories.model.Repository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class FavoriteRepositoriesViewModel(
    private val favoriteRepositoriesInteractor: FavoriteRepositoriesInteractor
) : ViewModel() {

    private val _repositoriesListLiveData = MutableLiveData<Pair<String, List<Repository>>>()
    val repositoriesListLiveData: LiveData<Pair<String, List<Repository>>>
            get() = _repositoriesListLiveData

    private val _showErrorToastLiveData = MutableLiveData<@IntegerRes Int>()
    val showErrorToastLiveData: LiveData<Int>
        get() = _showErrorToastLiveData

    private var searchRepositoriesDisposable: Disposable? = null
    private var getFavoriteRepositoriesDisposable: Disposable

    init {
        getFavoriteRepositoriesDisposable =
            favoriteRepositoriesInteractor.getFavoriteRepositories()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { repositoryList ->
                        Timber.d("getFavoriteRepositories result $repositoryList")
                        with(_repositoriesListLiveData) {
                            postValue((value?.first ?: "") to repositoryList)
                        }
                    },
                    {
                        Timber.e(it, "Error getFavoriteRepositories")
                        _showErrorToastLiveData.postValue(R.string.load_favorite_repositories_error)
                    }
                )
    }

    fun onDeleteRepository(repository: Repository) {
        searchRepositoriesDisposable?.dispose()
        searchRepositoriesDisposable =
            favoriteRepositoriesInteractor.deleteFavoriteRepository(repository)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        Timber.d("deleteFavoriteRepository success $repository")
                    },
                    {
                        Timber.e(it, "Error deleteFavoriteRepository")
                        _showErrorToastLiveData.postValue(R.string.delete_favorite_repository_error)
                    }
                )
    }

    fun onSearch(text: String) {
        _repositoriesListLiveData.value?.let { (_, list) ->
            _repositoriesListLiveData.postValue(text to list)
        }
    }

    override fun onCleared() {
        super.onCleared()
        getFavoriteRepositoriesDisposable.dispose()
        searchRepositoriesDisposable?.dispose()
    }
}