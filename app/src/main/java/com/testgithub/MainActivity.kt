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
import com.testgithub.authorization.AuthorizationFragment
import com.testgithub.authorization.AuthorizationViewModel
import com.testgithub.authorization.ShowFragmentEvent
import com.testgithub.common.*
import com.testgithub.repositories.main.MainRepositoriesFragment
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel

const val RC_SIGN_IN = 1

class MainActivity : AppCompatActivity() {

    private val viewModel: AuthorizationViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        userAvatarImageView.setDebouncedOnClickListener {
            AlertDialog.Builder(this)
                .setTitle(R.string.sign_out_alert_dialog_title)
                .setMessage(R.string.sign_out_alert_dialog)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    viewModel.onLogout()
                }
                .setNegativeButton(android.R.string.cancel) { _, _ -> }
                .show()
        }

        with(viewModel) {
            showAvatarLiveData.observe(
                this@MainActivity,
                Observer { url ->
                    showUserAvatar(url)
                }
            )
            showFragmentLiveData.observe(
                this@MainActivity,
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
            showErrorLiveData.observe(
                this@MainActivity,
                Observer { error ->
                    when (error) {
                        MyError.SIGN_OUT_ERROR -> toast(R.string.sign_out_error)
                        else -> toast(R.string.unknown_error)
                    }
                }
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == Activity.RESULT_OK) {
                viewModel.onSignIn()
            } else {
                toast(R.string.sign_in_error)
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
