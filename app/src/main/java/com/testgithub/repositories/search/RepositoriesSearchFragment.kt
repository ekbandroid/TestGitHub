package com.testgithub.repositories.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.testgithub.R
import com.testgithub.extention.addFragment
import com.testgithub.extention.toast
import com.testgithub.repositories.OnBottomReachedListener
import com.testgithub.repositories.RepositoriesAdapter
import com.testgithub.repositories.detail.RepositoryDetailsFragment
import com.testgithub.repositories.main.OnSearchTextListener
import kotlinx.android.synthetic.main.fragment_repositories_search.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class RepositoriesSearchFragment : Fragment(), OnSearchTextListener {

    private val viewModel: RepositoriesSearchViewModel by viewModel()

    private val repositoriesAdapter = RepositoriesAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_repositories_search, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        repositoriesAdapter.onBottomReachedListener = object :
            OnBottomReachedListener {
            override fun onBottomReached(position: Int) {
                viewModel.listScrolledToEnd()
            }
        }
        repositoriesRecyclerView.itemAnimator = null
        repositoriesRecyclerView.adapter = repositoriesAdapter
        repositoriesRecyclerView.layoutManager = LinearLayoutManager(context)
        repositoriesAdapter.favoriteClickListener =
            { repository -> viewModel.onRepositoryLiked(repository) }
        repositoriesAdapter.itemClickListener =
            { repository ->
                addFragment(RepositoryDetailsFragment.create(repository))
            }
        swipeRefreshLayout.setOnRefreshListener {
            viewModel.updateRepositories()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.repositoriesListLiveData.observe(
            this,
            Observer { (searchText, repositoriesList) ->
                repositoriesAdapter.highligtedText = searchText
                repositoriesAdapter.submitList(repositoriesList)
                repositoriesAdapter.notifyDataSetChanged()
            }
        )

        viewModel.showProgressLiveData.observe(
            this,
            Observer { isShow ->
                loadingStateView.isVisible = isShow
            }
        )

        viewModel.showSwipeRefreshLiveData.observe(
            this,
            Observer { isShow ->
                swipeRefreshLayout.isRefreshing = isShow
            }
        )

        viewModel.showErrorLiveData.observe(
            this,
            Observer { error ->
                toast(error)
            }
        )
    }

    override fun onSearchText(text: String) {
        viewModel.searchRepositories(text)
    }
}