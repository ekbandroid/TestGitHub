package com.testgithub.repositories

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.testgithub.repositories.model.Repository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class RepositoriesSearchViewModel(
    private val repositoriesSearchUseCase: RepositoriesSearchUseCase
) : ViewModel() {
    val repositoriesListLiveData = MutableLiveData<Pair<String, List<Repository>>>()
    var searchRepositoriesDisposable: Disposable? = null
    fun searchRepositories(searchText: String) {
        if (searchText.isBlank()) return
        searchRepositoriesDisposable?.dispose()
        searchRepositoriesDisposable =
            repositoriesSearchUseCase.searchRepositories(searchText)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        repositoriesListLiveData.postValue(searchText to it.data)
                    },
                    {
                        Timber.e(it, "Error searchRepositories")
                    }
                )
    }
}