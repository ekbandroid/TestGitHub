package com.testgithub.authorisation

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber


class AuthorizationViewModel(
    private val authorizationUseCase: AuthorizationUseCase
) : ViewModel() {

    val showAvatarLiveData = MutableLiveData<Uri?>()
    val showFragmentLiveData = MutableLiveData<ShowFragmentEvent>()

    private var logoutDisposable: Disposable? = null

    init {
        setupView()
    }

    fun onLogout() {
        logoutDisposable =
            authorizationUseCase.signOut()
                .andThen(authorizationUseCase.clearFavoriteRepositoriesTable())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        setupView()
                    },
                    { Timber.e("onLogout error") }
                )
    }

    fun onSignIn() = setupView()


    private fun setupView() {
        authorizationUseCase.getUser()?.let {
            showAvatarLiveData.postValue(it.photoUrl)
            showFragmentLiveData.postValue(ShowFragmentEvent.SEARCH)
            return
        }
        showAvatarLiveData.postValue(null)
        showFragmentLiveData.postValue(ShowFragmentEvent.AUTH)
    }


    override fun onCleared() {
        super.onCleared()
        logoutDisposable?.dispose()
    }

}

enum class ShowFragmentEvent { AUTH, SEARCH; }