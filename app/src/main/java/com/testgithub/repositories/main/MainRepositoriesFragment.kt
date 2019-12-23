package com.testgithub.repositories.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import com.testgithub.R
import com.testgithub.extention.hideKeyboard
import kotlinx.android.synthetic.main.fragment_repositories_main.*


class MainRepositoriesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_repositories_main, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.setNavigationOnClickListener { activity?.onBackPressed() }
        val repositoriesPagerAdapter =
            RepositoriesPagerAdapter(requireFragmentManager(), requireContext())
        viewPager.adapter = repositoriesPagerAdapter
        repositoriesTabLayout.setupWithViewPager(viewPager)
        searchImageButton.setOnClickListener {
            activity?.hideKeyboard()
            (repositoriesPagerAdapter.getItem(0) as OnSearchTextListener).onSearchText(
                searchEditText.text.toString()
            )
            viewPager.setCurrentItem(0, true)
        }
        searchEditText.doAfterTextChanged { text ->
            (repositoriesPagerAdapter.getItem(1) as OnSearchTextListener).onSearchText(text.toString())
        }

    }
}

interface OnSearchTextListener {
    fun onSearchText(text: String)
}