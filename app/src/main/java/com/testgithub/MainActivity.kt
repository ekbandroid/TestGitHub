package com.testgithub

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.testgithub.extention.replaceFragment
import com.testgithub.repositories.main.MainRepositoriesFragment
import kotlinx.android.synthetic.main.activity_main.*

private const val RC_SIGN_IN = 1

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        // [END config_signin]

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        firebaseUser = auth.currentUser

        toolbar.isVisible = firebaseUser != null
        toolbar.setNavigationOnClickListener {
            if (firebaseUser != null) {
                replaceFragment(MainRepositoriesFragment())
            }
        }
        if (firebaseUser != null) {
            currentUserTextView.text = firebaseUser?.displayName
            replaceFragment(MainRepositoriesFragment())
        } else {
            signInButton.isVisible = true
        }
        signInButton.setOnClickListener {
            val providers = arrayListOf(AuthUI.IdpConfig.GoogleBuilder().build())
            startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .build(),
                RC_SIGN_IN
            )
        }
        signOutButton.setOnClickListener {
            auth.signOut()
            googleSignInClient.revokeAccess().addOnCompleteListener(this) {
                currentUserTextView.text = ""
                signInButton.isVisible = true
                toolbar.isVisible = false
            }
        }
    }

    var firebaseUser: FirebaseUser? = null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                firebaseUser = FirebaseAuth.getInstance().currentUser
                currentUserTextView.text = firebaseUser?.displayName
                signInButton.isVisible = false
                toolbar.isVisible = firebaseUser != null

                replaceFragment(MainRepositoriesFragment())

                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }
}
