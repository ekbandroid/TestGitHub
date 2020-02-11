package com.testgithub.authorization

import android.net.Uri
import androidx.annotation.IntegerRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.testgithub.R
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class AuthorizationViewModel(
    private val authorizationUseCase: AuthorizationInteractor
) : ViewModel() {

    private val _showAvatarLiveData = MutableLiveData<Uri?>()
    val showAvatarLiveData: LiveData<Uri?>
        get() = _showAvatarLiveData

    private val _showFragmentLiveData = MutableLiveData<ShowFragmentEvent>()
    val showFragmentLiveData: LiveData<ShowFragmentEvent>
        get() = _showFragmentLiveData

    private val _showErrorToastLiveData = MutableLiveData<@IntegerRes Int>()
    val showErrorToastLiveData: LiveData<Int>
        get() = _showErrorToastLiveData

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
                    {
                        Timber.e("onLogout error")
                        _showErrorToastLiveData.postValue(R.string.sign_out_error)
                    }
                )
    }

    fun onSignIn() = setupView()

    private fun setupView() {
        authorizationUseCase.getUser()?.let {
            _showAvatarLiveData.postValue(it.photoUrl)
            _showFragmentLiveData.postValue(ShowFragmentEvent.SEARCH)
            return
        }
        _showAvatarLiveData.postValue(null)
        _showFragmentLiveData.postValue(ShowFragmentEvent.AUTH)
    }

    override fun onCleared() {
        super.onCleared()
        logoutDisposable?.dispose()
    }

}

enum class ShowFragmentEvent { AUTH, SEARCH; }