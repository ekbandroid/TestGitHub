package com.testgithub.repositories.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.testgithub.R
import com.testgithub.common.addFragment
import com.testgithub.common.toast
import com.testgithub.repositories.detail.RepositoryDetailsFragment
import com.testgithub.repositories.main.OnSearchTextListener
import kotlinx.android.synthetic.main.fragment_repositories_search.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class SearchRepositoriesFragment : Fragment(), OnSearchTextListener {

    private val viewModel: SearchRepositoriesViewModel by viewModel()

    private val repositoriesAdapter =
        SearchedRepositoriesAdapter {
            viewModel.retry()
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_repositories_search, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(repositoriesAdapter) {
            favoriteClickListener = { repository ->
                viewModel.onRepositoryLiked(repository)
            }
            itemClickListener = { repository ->
                addFragment(RepositoryDetailsFragment.create(repository))
            }
        }

        with(repositoriesRecyclerView) {
            itemAnimator = null
            adapter = repositoriesAdapter
            layoutManager = LinearLayoutManager(context)
        }

        swipeRefreshLayout.setOnRefreshListener { viewModel.refreshList() }

        with(viewModel) {
            networkState.observe(viewLifecycleOwner, Observer {
                repositoriesAdapter.setNetworkState(it)
            })
            repositoriesList.observe(viewLifecycleOwner, Observer { (pagedList, searchText) ->
                Timber.d("repositoriesAdapter.submitList size = ${pagedList?.size}")
                repositoriesAdapter.addItems(pagedList, searchText)
            })
            swipeRefreshLiveData.observe(
                viewLifecycleOwner,
                Observer { isShow ->
                    swipeRefreshLayout.isRefreshing = isShow
                }
            )
            showErrorToastLiveData.observe(
                viewLifecycleOwner,
                Observer { toast(it) }
            )
        }
    }

    override fun onSearchText(text: String) {
        viewModel.searchRepo(text)
    }
}