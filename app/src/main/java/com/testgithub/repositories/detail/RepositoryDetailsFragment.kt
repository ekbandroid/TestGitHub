package com.testgithub.repositories.detail

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.testgithub.R
import com.testgithub.common.GlideApp
import com.testgithub.common.TextUtils
import com.testgithub.repositories.model.Repository
import kotlinx.android.synthetic.main.fragment_repository_details.*
import kotlin.math.roundToInt

class RepositoryDetailsFragment : Fragment() {

    companion object {
        var selectedRepository: Repository? = null

        fun create(repository: Repository): RepositoryDetailsFragment {
            selectedRepository = repository
            return RepositoryDetailsFragment()
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
        selectedRepository?.let { repository ->
            nameTextView.text = getString(
                R.string.login_repository_name_template,
                repository.owner.login,
                repository.name
            )
            forksTextView.text = repository.forks.toString()
            starsTextView.text = repository.stars.roundToInt().toString()
            dateCreateTextView.text = TextUtils.convertServerDate(repository.dateCreate)
            detailsTextView.text = repository.description
        }
        GlideApp.with(requireContext())
            .load(Uri.parse(selectedRepository?.owner?.avatarUrl))
            .placeholder(R.drawable.ic_launcher_background)
            .into(repositoryImageView)
    }
}