package com.testgithub.repositories.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import com.testgithub.R
import com.testgithub.common.hideKeyboard
import kotlinx.android.synthetic.main.fragment_repositories_main.*


class MainRepositoriesFragment : Fragment() {

    private lateinit var repositoriesPagerAdapter: RepositoriesPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_repositories_main, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        repositoriesPagerAdapter =
            RepositoriesPagerAdapter(requireFragmentManager(), requireContext())
        viewPager.adapter = repositoriesPagerAdapter
        repositoriesTabLayout.setupWithViewPager(viewPager)
        searchImageButton.setOnClickListener {
            searchClicked()
        }

        with(searchEditText) {
            setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchClicked()
                    true
                } else {
                    false
                }
            }
            doAfterTextChanged { text ->
                // динамическое выделение текста при посимвольном вводе в searchEditText - эта фича
                // будет доступна только для списка сохраненных репозиториев
                (repositoriesPagerAdapter.getItem(1) as OnSearchTextListener).onSearchText(text.toString())
            }
        }
    }

    private fun searchClicked() {
        activity?.hideKeyboard()
        (repositoriesPagerAdapter.getItem(0) as OnSearchTextListener).onSearchText(
            searchEditText.text.toString()
        )
        viewPager.setCurrentItem(0, true)
    }
}

interface OnSearchTextListener {
    fun onSearchText(text: String)
}