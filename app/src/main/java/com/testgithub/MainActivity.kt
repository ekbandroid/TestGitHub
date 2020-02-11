package com.testgithub

import android.app.Activity
import android.content.Intent
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

const val REQUEST_CODE_SIGN_IN = 1

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
                    if (url != null) {
                        userAvatarImageView.isVisible = true
                        GlideApp.with(this@MainActivity)
                            .load(url)
                            .apply(RequestOptions.circleCropTransform())
                            .into(userAvatarImageView)
                    } else {
                        userAvatarImageView.setImageDrawable(null)
                        userAvatarImageView.isVisible = false
                    }
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
            showErrorToastLiveData.observe(
                this@MainActivity,
                Observer { toast(it) }
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SIGN_IN) {
            if (resultCode == Activity.RESULT_OK) {
                viewModel.onSignIn()
            } else {
                toast(R.string.sign_in_error)
            }
        }
    }

}
