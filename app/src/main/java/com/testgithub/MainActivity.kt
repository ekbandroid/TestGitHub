package com.testgithub

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.testgithub.authorisation.AuthorizationFragment
import com.testgithub.common.GlideApp
import com.testgithub.db.FavoriteRepositoriesDao
import com.testgithub.extention.replaceFragment
import com.testgithub.repositories.main.MainRepositoriesFragment
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject

const val RC_SIGN_IN = 1

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private val favoriteRepositoriesDao: FavoriteRepositoriesDao by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        firebaseUser = auth.currentUser

        if (firebaseUser != null) {
            firebaseUser?.let {
                showCurrentUser(it)
            }
            replaceMainRepositoriesFragment()
        } else {
            replaceAuthorizationFragment()
        }

        userAvatarImageView.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle(R.string.sign_out_alert_dialog_title)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    auth.signOut()
                    googleSignInClient.revokeAccess().addOnCompleteListener(this) {
                        showCurrentUser(null)
                        Single.fromCallable {
                            favoriteRepositoriesDao.deleteAll()
                        }
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                {},
                                {}
                            )

                        replaceAuthorizationFragment()
                    }
                }
                .setNegativeButton(android.R.string.cancel) { _, _ -> }
                .show()
        }
    }

    var firebaseUser: FirebaseUser? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == Activity.RESULT_OK) {
                firebaseUser = FirebaseAuth.getInstance().currentUser
                firebaseUser?.let {
                    showCurrentUser(it)
                }
                replaceMainRepositoriesFragment()
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }

    private fun replaceMainRepositoriesFragment() {
        replaceFragment(
            MainRepositoriesFragment(),
            container.id,
            false
        )
    }

    private fun replaceAuthorizationFragment() {
        replaceFragment(
            AuthorizationFragment(),
            container.id,
            false
        )
    }

    private fun showCurrentUser(firebaseUser: FirebaseUser?) {
        if (firebaseUser != null) {
            userAvatarImageView.isVisible = true
            GlideApp.with(this)
                .load(firebaseUser.photoUrl)
                .apply(RequestOptions.circleCropTransform())
                .into(userAvatarImageView)
        } else {
            userAvatarImageView.setImageDrawable(null)
            userAvatarImageView.isVisible = false
        }
    }
}
