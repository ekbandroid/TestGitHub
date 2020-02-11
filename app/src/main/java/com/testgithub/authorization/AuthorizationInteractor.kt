package com.testgithub.authorization

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.testgithub.repositories.favorites.db.FavoriteRepositoriesDao
import io.reactivex.Completable

class AuthorizationInteractor(
    private val favoriteRepositoriesDao: FavoriteRepositoriesDao,
    private val fireBaseAuth: FirebaseAuth
) {

    fun clearFavoriteRepositoriesTable(): Completable =
        Completable.fromCallable {
            favoriteRepositoriesDao.deleteAll()
        }

    fun signOut(): Completable =
        Completable.fromCallable {
            fireBaseAuth.signOut()
        }

    fun getUser(): FirebaseUser? = fireBaseAuth.currentUser
}