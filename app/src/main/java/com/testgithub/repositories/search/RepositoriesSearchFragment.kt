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
import com.testgithub.common.MyError
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
        with(repositoriesAdapter) {
            onBottomReachedListener = object : OnBottomReachedListener {
                override fun onBottomReached(position: Int) {
                    viewModel.listScrolledToEnd()
                }
            }
            favoriteClickListener =
                { repository -> viewModel.onRepositoryLiked(repository) }
            itemClickListener =
                { repository -> addFragment(RepositoryDetailsFragment.create(repository)) }
        }

        with(repositoriesRecyclerView) {
            itemAnimator = null
            adapter = repositoriesAdapter
            layoutManager = LinearLayoutManager(context)
        }

        swipeRefreshLayout.setOnRefreshListener { viewModel.updateRepositories() }

        with(viewModel) {
            repositoriesListLiveData.observe(
                viewLifecycleOwner,
                Observer { (searchText, repositoriesList) ->
                    repositoriesAdapter.highligtedText = searchText
                    repositoriesAdapter.submitList(repositoriesList)
                    repositoriesAdapter.notifyDataSetChanged()
                }
            )
            showProgressLiveData.observe(
                viewLifecycleOwner,
                Observer { isShow ->
                    loadingStateView.isVisible = isShow
                }
            )
            showSwipeRefreshLiveData.observe(
                viewLifecycleOwner,
                Observer { isShow ->
                    swipeRefreshLayout.isRefreshing = isShow
                }
            )
            showErrorLiveData.observe(
                viewLifecycleOwner,
                Observer { error ->
                    when (error) {
                        MyError.LOAD_REPOSITORIES_ERROR -> toast(R.string.load_repositories_error)
                        else -> toast(R.string.unknown_error)
                    }
                }
            )
        }
    }

    override fun onSearchText(text: String) {
        viewModel.searchRepositories(text)
    }
}