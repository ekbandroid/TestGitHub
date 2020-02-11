package com.testgithub.repositories.detail

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.testgithub.R
import com.testgithub.common.GlideApp
import com.testgithub.common.TextUtils
import com.testgithub.repositories.model.Repository
import kotlinx.android.synthetic.main.fragment_repository_details.*
import kotlin.math.roundToInt

private const val REPOSITORY_KEY = "REPOSITORY_KEY"

class RepositoryDetailsFragment : Fragment() {

    companion object {
        fun create(repository: Repository): RepositoryDetailsFragment {
            return RepositoryDetailsFragment().apply {
                arguments = bundleOf(REPOSITORY_KEY to repository)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_repository_details, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.setNavigationOnClickListener { activity?.onBackPressed() }
        val selectedRepository: Repository? = arguments?.get(REPOSITORY_KEY) as Repository
        selectedRepository?.let { repository ->
            with(repository) {
                nameTextView.text = getString(
                    R.string.login_repository_name_template,
                    owner.login,
                    name
                )
                forksTextView.text = forks.toString()
                starsTextView.text = stars.roundToInt().toString()
                dateCreateTextView.text = TextUtils.convertServerDate(dateCreate)
                detailsTextView.text = description
            }
        }
        GlideApp.with(requireContext())
            .load(Uri.parse(selectedRepository?.owner?.avatarUrl))
            .placeholder(R.drawable.ic_launcher_background)
            .into(repositoryImageView)
    }
}