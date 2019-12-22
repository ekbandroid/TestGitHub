package com.testgithub.repositories.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.testgithub.R
import com.testgithub.extention.addFragment
import com.testgithub.repositories.OnBottomReachedListener
import com.testgithub.repositories.RepositoriesAdapter
import com.testgithub.repositories.detail.RepositoryDetailsFragment
import com.testgithub.repositories.main.OnSearchTextListener
import com.testgithub.repositories.model.Repository
import kotlinx.android.synthetic.main.fragment_repositories_search.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class RepositoriesSearchFragment : Fragment(), OnSearchTextListener {

    private val viewModel: RepositoriesSearchViewModel by viewModel()

    private val repositoriesAdapter =
        RepositoriesAdapter()

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
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.repositoriesListLiveData.observe(
            this,
            Observer<Pair<String, List<Repository>>> { (searchText, repositoriesList) ->
                repositoriesAdapter.highligtedText = searchText
                repositoriesAdapter.submitList(repositoriesList)
                repositoriesAdapter.notifyDataSetChanged()
            }
        )
    }

    override fun onSearchText(text: String) {
        repositoriesAdapter.submitList(emptyList())
        repositoriesAdapter.notifyDataSetChanged()
        viewModel.searchRepositories(text)
    }
}