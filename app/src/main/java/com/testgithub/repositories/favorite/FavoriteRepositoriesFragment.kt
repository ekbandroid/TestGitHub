package com.testgithub.repositories.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
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

class FavoriteRepositoriesFragment : Fragment(), OnSearchTextListener {

    private val viewModel: FavoriteRepositoriesViewModel by viewModel()

    private val repositoriesAdapter =
        RepositoriesAdapter()

    private var searchListener: ((text: String) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_repositories_favorites, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(repositoriesRecyclerView) {
            itemAnimator = null
            adapter = repositoriesAdapter
            layoutManager = LinearLayoutManager(context)
        }

        with(repositoriesAdapter) {
            favoriteClickListener =
                { repository ->
                    AlertDialog.Builder(requireContext())
                        .setTitle(R.string.delete_alert_dialog_title)
                        .setMessage(R.string.delete_alert_dialog)
                        .setPositiveButton(android.R.string.ok) { _, _ ->
                            viewModel.onDeleteRepository(repository)
                        }
                        .setNegativeButton(android.R.string.cancel) { _, _ -> }
                        .show()
                }
            itemClickListener = { repository ->
                addFragment(RepositoryDetailsFragment.create(repository))
            }
        }

        with(viewModel) {
            repositoriesListLiveData.observe(
                viewLifecycleOwner,
                Observer { (searchText, repositoriesList) ->
                    repositoriesAdapter.highlightedText = searchText
                    repositoriesAdapter.submitList(repositoriesList)
                    repositoriesAdapter.notifyDataSetChanged()
                }
            )
            showErrorToastLiveData.observe(
                viewLifecycleOwner,
                Observer { toast(it) }
            )
        }

        searchListener = { text -> viewModel.onSearch(text) }
    }

    override fun onSearchText(text: String) {
        searchListener?.invoke(text)
    }
}