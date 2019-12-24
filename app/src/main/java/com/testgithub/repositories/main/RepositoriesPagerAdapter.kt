package com.testgithub.repositories.main

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.testgithub.R
import com.testgithub.repositories.favorites.FavoriteRepositoriesFragment
import com.testgithub.repositories.search.RepositoriesSearchFragment

@Suppress("DEPRECATION")
class RepositoriesPagerAdapter(
    fm: FragmentManager,
    context: Context
) : FragmentStatePagerAdapter(fm) {

    private val fragments = listOf(
        FragmentItem(
            RepositoriesSearchFragment(),
            context.getString(R.string.tab_search)
        ),
        FragmentItem(
            FavoriteRepositoriesFragment(),
            context.getString(R.string.tab_favorite)
        )
    )

    override fun getItem(position: Int) = fragments[position].fragment

    override fun getCount() = fragments.size

    override fun getPageTitle(position: Int) = fragments[position].title
}

private data class FragmentItem(val fragment: Fragment, val title: String)
