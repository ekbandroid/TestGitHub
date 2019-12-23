package com.testgithub

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.bumptech.glide.request.RequestOptions
import com.testgithub.authorisation.AuthorizationFragment
import com.testgithub.authorisation.AuthorizationViewModel
import com.testgithub.authorisation.ShowFragmentEvent
import com.testgithub.common.GlideApp
import com.testgithub.extention.replaceFragment
import com.testgithub.extention.toast
import com.testgithub.repositories.main.MainRepositoriesFragment
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel

const val RC_SIGN_IN = 1

class MainActivity : AppCompatActivity() {

    private val viewModel: AuthorizationViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        userAvatarImageView.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle(R.string.sign_out_alert_dialog_title)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    viewModel.onLogout()
                }
                .setNegativeButton(android.R.string.cancel) { _, _ -> }
                .show()
        }

        viewModel.showAvatarLiveData.observe(
            this,
            Observer { url ->
                showUserAvatar(url)
            }
        )

        viewModel.showFragmentLiveData.observe(
            this,
            Observer { event ->
                when (event) {
                    ShowFragmentEvent.AUTH ->
                        replaceFragment(
                            AuthorizationFragment(),
                            container.id,
                            false
                        )
                    ShowFragmentEvent.SEARCH ->
                        replaceFragment(
                            MainRepositoriesFragment(),
                            container.id,
                            false
                        )
                    else -> {
                        //do nothing
                    }
                }
            }
        )

        viewModel.showErrorLiveData.observe(
            this,
            Observer { error ->
                toast(error)
            }
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN && resultCode == Activity.RESULT_OK) {
            viewModel.onSignIn()
        }

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == Activity.RESULT_OK) {
                viewModel.onSignIn()
            } else {
                toast("Error sign in")
            }
        }
    }

    private fun showUserAvatar(uri: Uri?) {
        if (uri != null) {
            userAvatarImageView.isVisible = true
            GlideApp.with(this)
                .load(uri)
                .apply(RequestOptions.circleCropTransform())
                .into(userAvatarImageView)
        } else {
            userAvatarImageView.setImageDrawable(null)
            userAvatarImageView.isVisible = false
        }
    }
}
