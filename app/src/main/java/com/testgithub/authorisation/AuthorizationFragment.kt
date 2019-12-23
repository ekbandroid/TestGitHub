package com.testgithub.authorisation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.firebase.ui.auth.AuthUI
import com.testgithub.R
import com.testgithub.RC_SIGN_IN
import kotlinx.android.synthetic.main.fragment_authorization.*

class AuthorizationFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_authorization, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        signInButton.setOnClickListener {
            val providers = arrayListOf(AuthUI.IdpConfig.GoogleBuilder().build())
            activity?.startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .build(),
                RC_SIGN_IN
            )
        }
    }
}
